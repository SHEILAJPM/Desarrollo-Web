package desarrolloWeb.backend.personal.trabajadores;

import desarrolloWeb.backend.personal.trabajadores.PersonalService;
import desarrolloWeb.backend.personal.trabajadores.Trabajador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PersonalServiceTest {

    private PersonalService personalService;

    @BeforeEach
    void setUp() {
        personalService = new PersonalService();
    }

    @Test
    void registrarAsignaIdYEstadoActivo() {
        Trabajador nuevo = new Trabajador(null, "Ana Torres", "70001234", "Analista", "Ventas", null);

        Trabajador creado = personalService.registrar(nuevo);

        assertNotNull(creado.getId());
        assertEquals("ACTIVO", creado.getEstado());
        assertEquals(1, personalService.listar(null, null).size());
    }

    @Test
    void registrarSinNombreLanzaExcepcion() {
        Trabajador sinNombre = new Trabajador(null, " ", "70001234", "Analista", "Ventas", null);

        assertThrows(IllegalArgumentException.class, () -> personalService.registrar(sinNombre));
    }

    @Test
    void registrarConDocumentoDuplicadoLanzaExcepcion() {
        personalService.registrar(new Trabajador(null, "Ana Torres", "70001234", "Analista", "Ventas", null));
        Trabajador duplicado = new Trabajador(null, "Otro Nombre", "70001234", "Tecnico", "Almacen", null);

        assertThrows(IllegalStateException.class, () -> personalService.registrar(duplicado));
    }

    @Test
    void buscarPorDocumentoEncuentraAlTrabajadorRegistrado() {
        personalService.registrar(new Trabajador(null, "Ana Torres", "70001234", "Analista", "Ventas", null));

        Optional<Trabajador> encontrado = personalService.buscarPorDocumento("70001234");

        assertTrue(encontrado.isPresent());
        assertEquals("Ana Torres", encontrado.get().getNombres());
    }

    @Test
    void buscarPorIdInexistenteRetornaVacio() {
        Optional<Trabajador> resultado = personalService.buscarPorId(999L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarFiltraPorAreaYEstado() {
        personalService.registrar(new Trabajador(null, "Ana Torres", "70001234", "Analista", "Ventas", null));
        Trabajador enAlmacen = personalService.registrar(
                new Trabajador(null, "Luis Ramos", "70005678", "Tecnico", "Almacen", null));
        personalService.eliminar(enAlmacen.getId());
        personalService.registrar(new Trabajador(null, "Rosa Diaz", "70009999", "Tecnico", "Almacen", null));

        List<Trabajador> ventas = personalService.listar("Ventas", null);
        List<Trabajador> almacenActivos = personalService.listar("Almacen", "ACTIVO");

        assertEquals(1, ventas.size());
        assertEquals(1, almacenActivos.size());
        assertEquals("Rosa Diaz", almacenActivos.get(0).getNombres());
    }

    @Test
    void eliminarQuitaAlTrabajadorDeLaLista() {
        Trabajador creado = personalService.registrar(
                new Trabajador(null, "Luis Ramos", "70005678", "Tecnico", "Almacen", null));

        boolean eliminado = personalService.eliminar(creado.getId());

        assertTrue(eliminado);
        assertTrue(personalService.buscarPorId(creado.getId()).isEmpty());
    }
}
