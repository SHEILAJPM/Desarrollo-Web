package desarrolloWeb.backend.ventas.controller;

import desarrolloWeb.backend.ventas.dto.TicketPromedioResponse;
import desarrolloWeb.backend.ventas.dto.VentaRequest;
import desarrolloWeb.backend.ventas.dto.VentaResponse;
import desarrolloWeb.backend.ventas.dto.VentasPorComprobante;
import desarrolloWeb.backend.ventas.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<VentaResponse> registrar(@Valid @RequestBody VentaRequest request) {
        VentaResponse creada = ventaService.registrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @GetMapping
    public ResponseEntity<List<VentaResponse>> listar() {
        return ResponseEntity.ok(ventaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VentaResponse>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ventaService.listarPorCliente(clienteId));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<VentaResponse>> listarPorPeriodo(@RequestParam LocalDate desde,
                                                                @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(ventaService.listarPorPeriodo(desde, hasta));
    }

    @GetMapping("/igv-periodo")
    public ResponseEntity<BigDecimal> calcularIgvPeriodo(@RequestParam LocalDate desde,
                                                         @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(ventaService.calcularTotalIgvPeriodo(desde, hasta));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<VentaResponse> anular(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.anular(id));
    }

    @GetMapping("/reportes/por-comprobante")
    public ResponseEntity<List<VentasPorComprobante>> reportePorComprobante() {
        return ResponseEntity.ok(ventaService.contarPorComprobante());
    }

    @GetMapping("/reportes/ticket-promedio")
    public ResponseEntity<TicketPromedioResponse> reporteTicketPromedio(@RequestParam LocalDate desde,
                                                                        @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(ventaService.calcularTicketPromedio(desde, hasta));
    }
}
