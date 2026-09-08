package desarrolloWeb.backend.compras;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody RegistrarCompraRequest request) {
        try {
            Compra creada = compraService.registrar(
                    request.ruc(), request.numeroComprobante(), request.montoSinIgv(), request.fecha());
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Compra>> listar() {
        return ResponseEntity.ok(compraService.listarTodas());
    }

    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<Compra>> listarPorProveedor(@PathVariable Long proveedorId) {
        return ResponseEntity.ok(compraService.listarPorProveedor(proveedorId));
    }

    @GetMapping("/igv-periodo")
    public ResponseEntity<Double> calcularIgvPeriodo(
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(compraService.calcularTotalIgvPeriodo(desde, hasta));
    }
}
