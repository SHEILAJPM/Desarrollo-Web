package desarrolloWeb.backend.ventas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class VentaServiceTest {

    private ClienteService clienteService;
    private VentaService ventaService;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        clienteService = new ClienteService();
        ventaService = new VentaService(clienteService);
        cliente = clienteService.registrar("10203040", "Carlos Mendoza");
    }

    @Test
    void registrarConDocumentoInexistenteLanzaExcepcion() {
        assertThrows(NoSuchElementException.class,
                () -> ventaService.registrar("00000000", "B001-1", 100.0, LocalDate.now()));
    }

    @Test
    void registrarCalculaIgvYMontoTotal() {
        Venta venta = ventaService.registrar(cliente.getDocumento(), "B001-1", 100.0, LocalDate.now());

        assertNotNull(venta.getId());
        assertEquals(cliente.getId(), venta.getClienteId());
        assertEquals(18.0, venta.getIgv(), 0.001);
        assertEquals(118.0, venta.getMontoTotal(), 0.001);
    }

    @Test
    void listarPorClienteDevuelveSoloSusVentas() {
        ventaService.registrar(cliente.getDocumento(), "B001-1", 100.0, LocalDate.now());
        ventaService.registrar(cliente.getDocumento(), "B001-2", 200.0, LocalDate.now());

        List<Venta> ventas = ventaService.listarPorCliente(cliente.getId());

        assertEquals(2, ventas.size());
    }

    @Test
    void calcularTotalIgvDelPeriodoSumaSoloLasFechasDentroDelRango() {
        ventaService.registrar(cliente.getDocumento(), "B001-1", 100.0, LocalDate.of(2026, 8, 15));
        ventaService.registrar(cliente.getDocumento(), "B001-2", 200.0, LocalDate.of(2026, 8, 20));
        ventaService.registrar(cliente.getDocumento(), "B001-3", 300.0, LocalDate.of(2026, 9, 1));

        double totalIgvAgosto = ventaService.calcularTotalIgvPeriodo(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31));

        assertEquals(54.0, totalIgvAgosto, 0.001);
    }
}
