package desarrolloWeb.backend.ventas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClienteServiceTest {

    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        clienteService = new ClienteService();
    }

    @Test
    void registrarAsignaIdAlCliente() {
        Cliente creado = clienteService.registrar("10203040", "Carlos Mendoza");

        assertNotNull(creado.getId());
        assertEquals("10203040", creado.getDocumento());
    }

    @Test
    void registrarConDocumentoDuplicadoLanzaExcepcion() {
        clienteService.registrar("10203040", "Carlos Mendoza");

        assertThrows(IllegalStateException.class,
                () -> clienteService.registrar("10203040", "Otro Cliente"));
    }

    @Test
    void buscarPorDocumentoEncuentraAlClienteRegistrado() {
        clienteService.registrar("10203040", "Carlos Mendoza");

        Optional<Cliente> encontrado = clienteService.buscarPorDocumento("10203040");

        assertTrue(encontrado.isPresent());
        assertEquals("Carlos Mendoza", encontrado.get().getNombre());
    }
}
