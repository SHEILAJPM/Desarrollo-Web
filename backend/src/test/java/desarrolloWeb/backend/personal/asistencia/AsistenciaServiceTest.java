package desarrolloWeb.backend.personal.asistencia;

import desarrolloWeb.backend.personal.trabajadores.PersonalService;
import desarrolloWeb.backend.personal.trabajadores.Trabajador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class AsistenciaServiceTest {

    private PersonalService personalService;
    private AsistenciaService asistenciaService;
    private Trabajador trabajador;

    @BeforeEach
    void setUp() {
        personalService = new PersonalService();
        asistenciaService = new AsistenciaService(personalService);

        trabajador = personalService.registrar(
                new Trabajador(
                        null,
                        "Ana Torres",
                        "70001234",
                        "Analista",
                        "Ventas",
                        null
                )
        );
    }

    @Test
    void marcarConDocumentoInexistenteLanzaExcepcion() {
        assertThrows(
                NoSuchElementException.class,
                () -> asistenciaService.marcar(
                        "99999999",
                        TipoMarcacion.ENTRADA,
                        -12.0464,
                        -77.0428
                )
        );
    }

    @Test
    void marcarConDocumentoValidoRegistraLaMarcacionLigadaAlTrabajador() {
        Marcacion registrada = asistenciaService.marcar(
                trabajador.getDocumentoIdentidad(),
                TipoMarcacion.ENTRADA,
                -12.0464,
                -77.0428
        );

        assertNotNull(registrada.getId());
        assertEquals(trabajador.getId(), registrada.getTrabajadorId());
        assertEquals(TipoMarcacion.ENTRADA, registrada.getTipo());
        assertNotNull(registrada.getFechaHora());
    }

    @Test
    void listarPorTrabajadorDevuelveSoloSusMarcaciones() {
        asistenciaService.marcar(
                trabajador.getDocumentoIdentidad(),
                TipoMarcacion.ENTRADA,
                -12.0464,
                -77.0428
        );

        asistenciaService.marcar(
                trabajador.getDocumentoIdentidad(),
                TipoMarcacion.SALIDA,
                -12.0464,
                -77.0428
        );

        List<Marcacion> marcaciones =
                asistenciaService.listarPorTrabajador(trabajador.getId());

        assertEquals(2, marcaciones.size());
    }
}