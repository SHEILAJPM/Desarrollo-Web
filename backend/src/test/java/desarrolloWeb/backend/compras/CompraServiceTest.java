package desarrolloWeb.backend.compras;

import desarrolloWeb.backend.pagos.Pago;
import desarrolloWeb.backend.pagos.PagoService;
import desarrolloWeb.backend.personal.trabajadores.PersonalService;
import desarrolloWeb.backend.proveedores.Proveedor;
import desarrolloWeb.backend.proveedores.ProveedorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CompraServiceTest {

    private ProveedorService proveedorService;
    private PagoService pagoService;
    private CompraService compraService;
    private Proveedor proveedor;

    @BeforeEach
    void setUp() {
        proveedorService = new ProveedorService();
        pagoService = new PagoService(new PersonalService(), proveedorService);
        compraService = new CompraService(proveedorService, pagoService);
        proveedor = proveedorService.registrar("20123456789", "Distribuidora ACME S.A.C.");
    }

    @Test
    void registrarConRucInexistenteLanzaExcepcion() {
        assertThrows(NoSuchElementException.class,
                () -> compraService.registrar("00000000000", "F001-1", 100.0, LocalDate.now()));
    }

    @Test
    void registrarCalculaIgvYMontoTotal() {
        Compra compra = compraService.registrar(
                proveedor.getRuc(), "F001-1", 100.0, LocalDate.now());

        assertNotNull(compra.getId());
        assertEquals(proveedor.getId(), compra.getProveedorId());
        assertEquals(18.0, compra.getIgv(), 0.001);
        assertEquals(118.0, compra.getMontoTotal(), 0.001);
    }

    @Test
    void registrarCompraCreaPagoPendienteAlProveedor() {
        Compra compra = compraService.registrar(
                proveedor.getRuc(), "F001-1", 100.0, LocalDate.now());

        List<Pago> pendientes = pagoService.listarPendientes();

        assertEquals(1, pendientes.size());
        assertEquals("PROVEEDOR", pendientes.get(0).getTipoBeneficiario());
        assertEquals(proveedor.getId(), pendientes.get(0).getProveedorId());
        assertEquals(compra.getMontoTotal(), pendientes.get(0).getMonto(), 0.001);
    }

    @Test
    void listarPorProveedorDevuelveSoloSusCompras() {
        compraService.registrar(proveedor.getRuc(), "F001-1", 100.0, LocalDate.now());
        compraService.registrar(proveedor.getRuc(), "F001-2", 200.0, LocalDate.now());

        List<Compra> compras = compraService.listarPorProveedor(proveedor.getId());

        assertEquals(2, compras.size());
    }

    @Test
    void calcularTotalIgvDelPeriodoSumaSoloLasFechasDentroDelRango() {
        compraService.registrar(proveedor.getRuc(), "F001-1", 100.0, LocalDate.of(2026, 8, 15));
        compraService.registrar(proveedor.getRuc(), "F001-2", 200.0, LocalDate.of(2026, 8, 20));
        compraService.registrar(proveedor.getRuc(), "F001-3", 300.0, LocalDate.of(2026, 9, 1));

        double totalIgvAgosto = compraService.calcularTotalIgvPeriodo(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31));

        assertEquals(54.0, totalIgvAgosto, 0.001);
    }
}
