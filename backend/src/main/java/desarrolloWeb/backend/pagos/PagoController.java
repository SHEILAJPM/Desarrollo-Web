package desarrolloWeb.backend.pagos;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<?> marcarComoPagado(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pagoService.marcarComoPagado(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/trabajador/{trabajadorId}")
    public ResponseEntity<List<Pago>> listarPorTrabajador(
            @PathVariable Long trabajadorId) {

        return ResponseEntity.ok(
                pagoService.listarPorTrabajador(trabajadorId)
        );
    }

    @GetMapping
    public ResponseEntity<List<Pago>> listar() {
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<?> registrar(
            @RequestBody RegistrarPagoRequest request) {

        try {
            Pago creado = pagoService.registrar(
                    request.documentoIdentidad(),
                    request.concepto(),
                    request.monto(),
                    request.fechaProgramada()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(creado);

        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<Pago>> listarPendientes() {
        return ResponseEntity.ok(pagoService.listarPendientes());
    }
}