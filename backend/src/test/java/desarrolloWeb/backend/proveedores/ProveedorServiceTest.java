package desarrolloWeb.backend.proveedores;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProveedorServiceTest {

    private ProveedorService proveedorService;

    @BeforeEach
    void setUp() {
        proveedorService = new ProveedorService();
    }

    @Test
    void registrarAsignaIdAlProveedor() {
        Proveedor creado = proveedorService.registrar("20123456789", "Distribuidora ACME S.A.C.");

        assertNotNull(creado.getId());
        assertEquals("20123456789", creado.getRuc());
    }

    @Test
    void registrarConRucDuplicadoLanzaExcepcion() {
        proveedorService.registrar("20123456789", "Distribuidora ACME S.A.C.");

        assertThrows(IllegalStateException.class,
                () -> proveedorService.registrar("20123456789", "Otro Nombre S.A.C."));
    }

    @Test
    void buscarPorRucEncuentraAlProveedorRegistrado() {
        proveedorService.registrar("20123456789", "Distribuidora ACME S.A.C.");

        Optional<Proveedor> encontrado = proveedorService.buscarPorRuc("20123456789");

        assertTrue(encontrado.isPresent());
        assertEquals("Distribuidora ACME S.A.C.", encontrado.get().getRazonSocial());
    }
}
