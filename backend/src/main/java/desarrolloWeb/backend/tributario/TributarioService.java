package desarrolloWeb.backend.tributario;

import desarrolloWeb.backend.compras.Compra;
import desarrolloWeb.backend.compras.CompraService;
import desarrolloWeb.backend.ventas.Venta;
import desarrolloWeb.backend.ventas.VentaService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TributarioService {

    private final CompraService compraService;
    private final VentaService ventaService;

    public TributarioService(CompraService compraService, VentaService ventaService) {
        this.compraService = compraService;
        this.ventaService = ventaService;
    }

    public ResumenTributario generarResumen(LocalDate desde, LocalDate hasta) {
        List<Compra> compras = compraService.listarPorPeriodo(desde, hasta);
        List<Venta> ventas = ventaService.listarPorPeriodo(desde, hasta);

        double totalComprasSinIgv = compras.stream().mapToDouble(Compra::getMontoSinIgv).sum();
        double igvCompras = compras.stream().mapToDouble(Compra::getIgv).sum();
        double totalVentasSinIgv = ventas.stream().mapToDouble(Venta::getMontoSinIgv).sum();
        double igvVentas = ventas.stream().mapToDouble(Venta::getIgv).sum();

        return new ResumenTributario(
                desde, hasta, totalComprasSinIgv, igvCompras, totalVentasSinIgv, igvVentas, igvVentas - igvCompras);
    }
}
