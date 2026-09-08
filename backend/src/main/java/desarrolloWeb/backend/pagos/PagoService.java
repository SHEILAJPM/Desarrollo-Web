package desarrolloWeb.backend.pagos;

import desarrolloWeb.backend.personal.PersonalService;
import desarrolloWeb.backend.personal.Trabajador;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PagoService {

    private final PersonalService personalService;
    private final List<Pago> pagos = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(0);

    public PagoService(PersonalService personalService) {
        this.personalService = personalService;
    }

    public Pago registrar(String documentoIdentidad, String concepto, Double monto, LocalDate fechaProgramada) {
        Trabajador trabajador = personalService.buscarPorDocumento(documentoIdentidad)
                .orElseThrow(() -> new NoSuchElementException(
                        "No existe un trabajador con documento " + documentoIdentidad));

        Pago pago = new Pago();
        pago.setId(contadorId.incrementAndGet());
        pago.setTrabajadorId(trabajador.getId());
        pago.setConcepto(concepto);
        pago.setMonto(monto);
        pago.setFechaProgramada(fechaProgramada);
        pago.setEstado("PENDIENTE");

        pagos.add(pago);
        return pago;
    }

    public Pago marcarComoPagado(Long id) {
        Pago pago = buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("No existe un pago con id " + id));

        if ("PAGADO".equals(pago.getEstado())) {
            throw new IllegalStateException("El pago " + id + " ya estaba marcado como pagado");
        }

        pago.setEstado("PAGADO");
        pago.setFechaPago(LocalDateTime.now());
        return pago;
    }

    public Optional<Pago> buscarPorId(Long id) {
        return pagos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public List<Pago> listarTodos() {
        return pagos;
    }

    public List<Pago> listarPendientes() {
        return pagos.stream()
                .filter(p -> "PENDIENTE".equals(p.getEstado()))
                .toList();
    }

    public List<Pago> listarPorTrabajador(Long trabajadorId) {
        return pagos.stream()
                .filter(p -> p.getTrabajadorId().equals(trabajadorId))
                .toList();
    }
}
