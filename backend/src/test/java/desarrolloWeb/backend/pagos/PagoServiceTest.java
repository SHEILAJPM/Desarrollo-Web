package desarrolloWeb.backend.pagos;

import desarrolloWeb.backend.personal.trabajadores.PersonalService;
import desarrolloWeb.backend.personal.trabajadores.Trabajador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class PagoServiceTest {

    private PersonalService personalService;
    private PagoService pagoService;
    private Trabajador trabajador;

    @BeforeEach
    void setUp() {
        personalService = new PersonalService();
        pagoService = new PagoService(personalService);
        trabajador = personalService.registrar(
                new Trabajador(null, "Ana Torres", "70001234", "Analista", "Ventas", null));
    }

    @Test
    void registrarConDocumentoInexistenteLanzaExcepcion() {
        assertThrows(NoSuchElementException.class,
                () -> pagoService.registrar("99999999", "Sueldo agosto", 1500.0, LocalDate.of(2026, 8, 31)));
    }

    @Test
    void registrarQuedaComoPendienteLigadoAlTrabajador() {
        Pago pago = pagoService.registrar(
                trabajador.getDocumentoIdentidad(), "Sueldo agosto", 1500.0, LocalDate.of(2026, 8, 31));

        assertNotNull(pago.getId());
        assertEquals(trabajador.getId(), pago.getTrabajadorId());
        assertEquals("PENDIENTE", pago.getEstado());
        assertNull(pago.getFechaPago());
    }

    @Test
    void marcarComoPagadoActualizaEstadoYFecha() {
        Pago pago = pagoService.registrar(
                trabajador.getDocumentoIdentidad(), "Sueldo agosto", 1500.0, LocalDate.of(2026, 8, 31));

        Pago pagado = pagoService.marcarComoPagado(pago.getId());

        assertEquals("PAGADO", pagado.getEstado());
        assertNotNull(pagado.getFechaPago());
    }

    @Test
    void marcarComoPagadoDosVecesLanzaExcepcion() {
        Pago pago = pagoService.registrar(
                trabajador.getDocumentoIdentidad(), "Sueldo agosto", 1500.0, LocalDate.of(2026, 8, 31));
        pagoService.marcarComoPagado(pago.getId());

        assertThrows(IllegalStateException.class, () -> pagoService.marcarComoPagado(pago.getId()));
    }

    @Test
    void listarPendientesExcluyeLosYaPagados() {
        Pago pendiente = pagoService.registrar(
                trabajador.getDocumentoIdentidad(), "Sueldo agosto", 1500.0, LocalDate.of(2026, 8, 31));
        Pago pagado = pagoService.registrar(
                trabajador.getDocumentoIdentidad(), "Sueldo julio", 1500.0, LocalDate.of(2026, 7, 31));
        pagoService.marcarComoPagado(pagado.getId());

        List<Pago> pendientes = pagoService.listarPendientes();

        assertEquals(1, pendientes.size());
        assertEquals(pendiente.getId(), pendientes.get(0).getId());
    }

    @Test
    void listarPorTrabajadorDevuelveSoloSusPagos() {
        pagoService.registrar(trabajador.getDocumentoIdentidad(), "Sueldo agosto", 1500.0, LocalDate.of(2026, 8, 31));
        pagoService.registrar(trabajador.getDocumentoIdentidad(), "Sueldo julio", 1500.0, LocalDate.of(2026, 7, 31));

        List<Pago> pagos = pagoService.listarPorTrabajador(trabajador.getId());

        assertEquals(2, pagos.size());
    }
}
