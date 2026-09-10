package desarrolloWeb.backend.tributario;

import desarrolloWeb.backend.compras.CompraService;
import desarrolloWeb.backend.pagos.PagoService;
import desarrolloWeb.backend.personal.trabajadores.PersonalService;
import desarrolloWeb.backend.proveedores.ProveedorService;
import desarrolloWeb.backend.ventas.ClienteService;
import desarrolloWeb.backend.ventas.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TributarioServiceTest {

    private TributarioService tributarioService;

    @BeforeEach
    void setUp() {
        ProveedorService proveedorService = new ProveedorService();
        PagoService pagoService = new PagoService(new PersonalService(), proveedorService);
        CompraService compraService = new CompraService(proveedorService, pagoService);
        ClienteService clienteService = new ClienteService();
        VentaService ventaService = new VentaService(clienteService);
        tributarioService = new TributarioService(compraService, ventaService);

        proveedorService.registrar("20123456789", "Distribuidora ACME S.A.C.");
        compraService.registrar("20123456789", "F001-1", 100.0, LocalDate.of(2026, 8, 10));

        clienteService.registrar("10203040", "Carlos Mendoza");
        ventaService.registrar("10203040", "B001-1", 300.0, LocalDate.of(2026, 8, 15));
    }

    @Test
    void generarResumenSumaComprasYVentasDelPeriodo() {
        ResumenTributario resumen = tributarioService.generarResumen(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31));

        assertEquals(100.0, resumen.totalComprasSinIgv(), 0.001);
        assertEquals(18.0, resumen.igvCompras(), 0.001);
        assertEquals(300.0, resumen.totalVentasSinIgv(), 0.001);
        assertEquals(54.0, resumen.igvVentas(), 0.001);
        assertEquals(36.0, resumen.igvPorPagar(), 0.001);
    }

    @Test
    void generarResumenFueraDeRangoDaCero() {
        ResumenTributario resumen = tributarioService.generarResumen(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

        assertEquals(0.0, resumen.totalComprasSinIgv(), 0.001);
        assertEquals(0.0, resumen.totalVentasSinIgv(), 0.001);
    }
}
