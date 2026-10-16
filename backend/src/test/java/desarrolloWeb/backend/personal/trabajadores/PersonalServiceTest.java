package desarrolloWeb.backend.personal.trabajadores;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonalServiceTest {

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @InjectMocks
    private PersonalService personalService;

    @Test
    void registrarAsignaEstadoActivoYDejaElIdALaBaseDeDatos() {
        Trabajador nuevo = new Trabajador(99L, "Ana Torres", "70001234", "Analista", "Ventas", null);
        when(trabajadorRepository.existsByDocumentoIdentidad("70001234")).thenReturn(false);
        when(trabajadorRepository.save(any(Trabajador.class))).thenAnswer(inv -> {
            Trabajador t = inv.getArgument(0);
            assertNull(t.getId());
            t.setId(1L);
            return t;
        });

        Trabajador creado = personalService.registrar(nuevo);

        assertEquals(1L, creado.getId());
        assertEquals("ACTIVO", creado.getEstado());
    }

    @Test
    void registrarSinNombreLanzaExcepcion() {
        Trabajador sinNombre = new Trabajador(null, " ", "70001234", "Analista", "Ventas", null);

        assertThrows(IllegalArgumentException.class, () -> personalService.registrar(sinNombre));
        verify(trabajadorRepository, never()).save(any());
    }

    @Test
    void registrarConDocumentoDuplicadoLanzaExcepcion() {
        when(trabajadorRepository.existsByDocumentoIdentidad("70001234")).thenReturn(true);
        Trabajador duplicado = new Trabajador(null, "Otro Nombre", "70001234", "Tecnico", "Almacen", null);

        assertThrows(IllegalStateException.class, () -> personalService.registrar(duplicado));
    }

    @Test
    void buscarPorDocumentoEncuentraAlTrabajadorRegistrado() {
        when(trabajadorRepository.findAll()).thenReturn(List.of(
                new Trabajador(1L, "Ana Torres", "70001234", "Analista", "Ventas", "ACTIVO")));

        Optional<Trabajador> encontrado = personalService.buscarPorDocumento("70001234");

        assertTrue(encontrado.isPresent());
        assertEquals("Ana Torres", encontrado.get().getNombres());
    }

    @Test
    void buscarPorIdInexistenteRetornaVacio() {
        when(trabajadorRepository.findById(999L)).thenReturn(Optional.empty());

        assertTrue(personalService.buscarPorId(999L).isEmpty());
    }

    @Test
    void listarFiltraPorAreaYEstado() {
        when(trabajadorRepository.findAll()).thenReturn(List.of(
                new Trabajador(1L, "Ana Torres", "70001234", "Analista", "Ventas", "ACTIVO"),
                new Trabajador(2L, "Luis Ramos", "70005678", "Tecnico", "Almacen", "INACTIVO"),
                new Trabajador(3L, "Rosa Diaz", "70009999", "Tecnico", "Almacen", "ACTIVO")));

        List<Trabajador> ventas = personalService.listar("Ventas", null);
        List<Trabajador> almacenActivos = personalService.listar("Almacen", "ACTIVO");

        assertEquals(1, ventas.size());
        assertEquals(1, almacenActivos.size());
        assertEquals("Rosa Diaz", almacenActivos.get(0).getNombres());
    }

    @Test
    void eliminarDevuelveTrueSiExisteYFalseSiNo() {
        when(trabajadorRepository.existsById(1L)).thenReturn(true);
        when(trabajadorRepository.existsById(2L)).thenReturn(false);

        assertTrue(personalService.eliminar(1L));
        assertFalse(personalService.eliminar(2L));
        verify(trabajadorRepository).deleteById(1L);
        verify(trabajadorRepository, never()).deleteById(2L);
    }
}
