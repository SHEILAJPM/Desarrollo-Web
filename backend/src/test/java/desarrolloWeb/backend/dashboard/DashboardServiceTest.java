package desarrolloWeb.backend.dashboard;

import desarrolloWeb.backend.personal.asistencia.AsistenciaService;
import desarrolloWeb.backend.personal.asistencia.TipoMarcacion;
import desarrolloWeb.backend.compras.CompraService;
import desarrolloWeb.backend.proveedores.ProveedorService;
import desarrolloWeb.backend.personal.documentos.DocumentoService;
import desarrolloWeb.backend.pagos.PagoService;
import desarrolloWeb.backend.personal.trabajadores.PersonalService;
import desarrolloWeb.backend.personal.trabajadores.Trabajador;
import desarrolloWeb.backend.ventas.ClienteService;
import desarrolloWeb.backend.ventas.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DashboardServiceTest {

    private DashboardService dashboardService;
    private PersonalService personalService;

    @BeforeEach
    void setUp() {

        personalService = new PersonalService();

        AsistenciaService asistenciaService =
                new AsistenciaService(personalService);

        ProveedorService proveedorService =
                new ProveedorService();

        PagoService pagoService =
                new PagoService(personalService, proveedorService);

        DocumentoService documentoService =
                new DocumentoService(personalService);

        CompraService compraService =
                new CompraService(proveedorService, pagoService);

        ClienteService clienteService =
                new ClienteService();

        VentaService ventaService =
                new VentaService(clienteService);

        dashboardService = new DashboardService(
                personalService,
                asistenciaService,
                pagoService,
                documentoService,
                compraService,
                ventaService
        );

        Trabajador trabajador = personalService.registrar(
                new Trabajador(
                        null,
                        "Ana Torres",
                        "70001234",
                        "Analista",
                        "Ventas",
                        null
                )
        );

        asistenciaService.marcar(
                trabajador.getDocumentoIdentidad(),
                TipoMarcacion.ENTRADA,
                -12.04,
                -77.04
        );

        pagoService.registrar(
                trabajador.getDocumentoIdentidad(),
                "Sueldo agosto",
                1500.0,
                LocalDate.now()
        );

        documentoService.registrar(
                trabajador.getDocumentoIdentidad(),
                "DNI",
                LocalDate.now().plusDays(10)
        );

        proveedorService.registrar(
                "20123456789",
                "Distribuidora ACME S.A.C."
        );

        compraService.registrar(
                "20123456789",
                "F001-1",
                100.0,
                LocalDate.now()
        );

        clienteService.registrar(
                "10203040",
                "Carlos Mendoza"
        );

        ventaService.registrar(
                "10203040",
                "B001-1",
                300.0,
                LocalDate.now()
        );
    }

    @Test
    void obtenerMetricasReflejaLoRegistradoEnCadaModulo() {

        DashboardMetricas metricas =
                dashboardService.obtenerMetricas();

        assertEquals(1, metricas.totalTrabajadores());
        assertEquals(1, metricas.trabajadoresActivos());
        assertEquals(1, metricas.totalMarcaciones());
        assertEquals(2, metricas.pagosPendientes());
        assertEquals(1, metricas.documentosProximosAVencer());
        assertEquals(1, metricas.totalCompras());
        assertEquals(1, metricas.totalVentas());
    }
}