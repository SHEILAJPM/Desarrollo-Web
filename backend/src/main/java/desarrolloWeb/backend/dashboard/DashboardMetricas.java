package desarrolloWeb.backend.dashboard;

public record DashboardMetricas(
        int totalTrabajadores,
        int trabajadoresActivos,
        int totalMarcaciones,
        int pagosPendientes,
        int documentosProximosAVencer,
        int documentosVencidos,
        int totalCompras,
        int totalVentas) {
}
