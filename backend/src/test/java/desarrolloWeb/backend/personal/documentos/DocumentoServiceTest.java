package desarrolloWeb.backend.personal.documentos;

import desarrolloWeb.backend.personal.trabajadores.PersonalService;
import desarrolloWeb.backend.personal.trabajadores.Trabajador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class DocumentoServiceTest {

    private PersonalService personalService;
    private DocumentoService documentoService;
    private Trabajador trabajador;

    @BeforeEach
    void setUp() {
        personalService = new PersonalService();
        documentoService = new DocumentoService(personalService);

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
    void registrarConDocumentoInexistenteLanzaExcepcion() {
        assertThrows(
                NoSuchElementException.class,
                () -> documentoService.registrar(
                        "99999999",
                        "DNI",
                        LocalDate.now().plusDays(10)
                )
        );
    }

    @Test
    void registrarQuedaLigadoAlTrabajador() {
        Documento documento = documentoService.registrar(
                trabajador.getDocumentoIdentidad(),
                "Licencia de conducir",
                LocalDate.now().plusDays(10)
        );

        assertNotNull(documento.getId());
        assertEquals(trabajador.getId(), documento.getTrabajadorId());
        assertEquals("Licencia de conducir", documento.getTipo());
    }

    @Test
    void listarProximosAVencerIncluyeSoloLosDentroDelRango() {
        Documento porVencer = documentoService.registrar(
                trabajador.getDocumentoIdentidad(),
                "Licencia de conducir",
                LocalDate.now().plusDays(5)
        );

        documentoService.registrar(
                trabajador.getDocumentoIdentidad(),
                "Contrato",
                LocalDate.now().plusDays(90)
        );

        List<Documento> proximos =
                documentoService.listarProximosAVencer(30);

        assertEquals(1, proximos.size());
        assertEquals(porVencer.getId(), proximos.get(0).getId());
    }

    @Test
    void listarVencidosIncluyeSoloFechasPasadas() {
        Documento vencido = documentoService.registrar(
                trabajador.getDocumentoIdentidad(),
                "DNI",
                LocalDate.now().minusDays(1)
        );

        documentoService.registrar(
                trabajador.getDocumentoIdentidad(),
                "Contrato",
                LocalDate.now().plusDays(10)
        );

        List<Documento> vencidos =
                documentoService.listarVencidos();

        assertEquals(1, vencidos.size());
        assertEquals(vencido.getId(), vencidos.get(0).getId());
    }

    @Test
    void listarPorTrabajadorDevuelveSoloSusDocumentos() {
        documentoService.registrar(
                trabajador.getDocumentoIdentidad(),
                "DNI",
                LocalDate.now().plusDays(30)
        );

        documentoService.registrar(
                trabajador.getDocumentoIdentidad(),
                "Contrato",
                LocalDate.now().plusDays(60)
        );

        List<Documento> documentos =
                documentoService.listarPorTrabajador(trabajador.getId());

        assertEquals(2, documentos.size());
    }
}