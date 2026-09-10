package desarrolloWeb.backend.dashboard;

import desarrolloWeb.backend.personal.asistencia.AsistenciaService;
import desarrolloWeb.backend.compras.CompraService;
import desarrolloWeb.backend.personal.documentos.DocumentoService;
import desarrolloWeb.backend.pagos.PagoService;
import desarrolloWeb.backend.personal.trabajadores.PersonalService;
import desarrolloWeb.backend.ventas.VentaService;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final PersonalService personalService;
    private final AsistenciaService asistenciaService;
    private final PagoService pagoService;
    private final DocumentoService documentoService;
    private final CompraService compraService;
    private final VentaService ventaService;

    public DashboardService(
            PersonalService personalService,
            AsistenciaService asistenciaService,
            PagoService pagoService,
            DocumentoService documentoService,
            CompraService compraService,
            VentaService ventaService) {
        this.personalService = personalService;
        this.asistenciaService = asistenciaService;
        this.pagoService = pagoService;
        this.documentoService = documentoService;
        this.compraService = compraService;
        this.ventaService = ventaService;
    }

    public DashboardMetricas obtenerMetricas() {
        return new DashboardMetricas(
                personalService.listar(null, null).size(),
                personalService.listar(null, "ACTIVO").size(),
                asistenciaService.listarTodas().size(),
                pagoService.listarPendientes().size(),
                documentoService.listarProximosAVencer(30).size(),
                documentoService.listarVencidos().size(),
                compraService.listarTodas().size(),
                ventaService.listarTodas().size());
    }
}
