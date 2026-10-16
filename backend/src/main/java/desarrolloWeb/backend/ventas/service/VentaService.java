package desarrolloWeb.backend.ventas.service;

import desarrolloWeb.backend.exception.DocumentoInvalidoException;
import desarrolloWeb.backend.exception.RecursoNoEncontradoException;
import desarrolloWeb.backend.exception.ReglaNegocioException;
import desarrolloWeb.backend.tributario.CalculadoraTributaria;
import desarrolloWeb.backend.tributario.TipoComprobante;
import desarrolloWeb.backend.tributario.ValidadorDocumento;
import desarrolloWeb.backend.tributario.service.ConfiguracionTributariaService;
import desarrolloWeb.backend.ventas.dto.TicketPromedioResponse;
import desarrolloWeb.backend.ventas.dto.VentaRequest;
import desarrolloWeb.backend.ventas.dto.VentaResponse;
import desarrolloWeb.backend.ventas.dto.VentasPorComprobante;
import desarrolloWeb.backend.ventas.entity.Cliente;
import desarrolloWeb.backend.ventas.entity.EstadoVenta;
import desarrolloWeb.backend.ventas.entity.Venta;
import desarrolloWeb.backend.ventas.repository.ClienteRepository;
import desarrolloWeb.backend.ventas.repository.VentaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ConfiguracionTributariaService configuracionService;
    private final CalculadoraTributaria calculadora = new CalculadoraTributaria();
    private final ValidadorDocumento validador = new ValidadorDocumento();

    public VentaService(VentaRepository ventaRepository,
                        ClienteRepository clienteRepository,
                        ConfiguracionTributariaService configuracionService) {
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.configuracionService = configuracionService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRACION')")
    @Transactional
    public VentaResponse registrar(VentaRequest request) {
        Cliente cliente = clienteRepository.findByDocumento(request.documentoCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un cliente con documento " + request.documentoCliente()));

        TipoComprobante tipo = validador.tipoComprobantePara(cliente.getDocumento());
        if (!request.numeroComprobante().startsWith(tipo.getPrefijo())) {
            throw new DocumentoInvalidoException("El cliente con " + cliente.getTipoDocumento()
                    + " requiere " + tipo + ": el comprobante debe empezar con '" + tipo.getPrefijo() + "'");
        }

        if (ventaRepository.existsByNumeroComprobante(request.numeroComprobante())) {
            throw new ReglaNegocioException("Ya existe una venta con el comprobante " + request.numeroComprobante());
        }

        BigDecimal tasa = configuracionService.obtenerTasaVigente(ConfiguracionTributariaService.CODIGO_IGV);
        BigDecimal base = request.montoSinIgv().setScale(2, RoundingMode.HALF_UP);

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setTipoComprobante(tipo);
        venta.setNumeroComprobante(request.numeroComprobante());
        venta.setFecha(request.fecha());
        venta.setMontoSinIgv(base);
        venta.setIgv(calculadora.calcularIgv(base, tasa));
        venta.setMontoTotal(calculadora.calcularTotal(base, tasa));
        venta.setTasaAplicada(tasa);
        venta.setEstado(EstadoVenta.EMITIDA);

        return toResponse(ventaRepository.save(venta));
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> listarTodas() {
        return mapear(ventaRepository.findAll());
    }

    @Transactional(readOnly = true)
    public VentaResponse buscarPorId(Long id) {
        return toResponse(obtenerEntidad(id));
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> listarPorCliente(Long clienteId) {
        return mapear(ventaRepository.buscarPorCliente(clienteId));
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> listarPorPeriodo(LocalDate desde, LocalDate hasta) {
        validarPeriodo(desde, hasta);
        return mapear(ventaRepository.buscarPorPeriodo(desde, hasta));
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalIgvPeriodo(LocalDate desde, LocalDate hasta) {
        validarPeriodo(desde, hasta);
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (Venta venta : ventaRepository.buscarPorEstadoYPeriodo(EstadoVenta.EMITIDA, desde, hasta)) {
            total = total.add(venta.getIgv());
        }
        return total;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CONTABILIDAD')")
    @Transactional
    public VentaResponse anular(Long id) {
        Venta venta = obtenerEntidad(id);
        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new ReglaNegocioException("La venta " + venta.getNumeroComprobante() + " ya está anulada");
        }

        venta.setEstado(EstadoVenta.ANULADA);
        return toResponse(venta);
    }

    @Transactional(readOnly = true)
    public List<VentasPorComprobante> contarPorComprobante() {
        List<VentasPorComprobante> reporte = new ArrayList<>();
        for (TipoComprobante tipo : TipoComprobante.values()) {
            reporte.add(new VentasPorComprobante(tipo,
                    ventaRepository.contarPorTipoComprobante(tipo, EstadoVenta.EMITIDA)));
        }
        return reporte;
    }

    @Transactional(readOnly = true)
    public TicketPromedioResponse calcularTicketPromedio(LocalDate desde, LocalDate hasta) {
        validarPeriodo(desde, hasta);
        Double promedio = ventaRepository.promedioMontoSinIgv(EstadoVenta.EMITIDA, desde, hasta);

        BigDecimal ticket = promedio == null
                ? BigDecimal.ZERO.setScale(2)
                : BigDecimal.valueOf(promedio).setScale(2, RoundingMode.HALF_UP);
        return new TicketPromedioResponse(desde, hasta, ticket);
    }

    private Venta obtenerEntidad(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una venta con id " + id));
    }

    private void validarPeriodo(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha 'desde' no puede ser posterior a 'hasta'");
        }
    }

    private List<VentaResponse> mapear(List<Venta> ventas) {
        List<VentaResponse> respuesta = new ArrayList<>();
        for (Venta venta : ventas) {
            respuesta.add(toResponse(venta));
        }
        return respuesta;
    }

    private VentaResponse toResponse(Venta venta) {
        return new VentaResponse(
                venta.getId(),
                venta.getCliente().getId(),
                venta.getCliente().getDocumento(),
                venta.getCliente().getNombre(),
                venta.getTipoComprobante(),
                venta.getNumeroComprobante(),
                venta.getFecha(),
                venta.getMontoSinIgv(),
                venta.getIgv(),
                venta.getMontoTotal(),
                venta.getTasaAplicada(),
                venta.getEstado());
    }
}
