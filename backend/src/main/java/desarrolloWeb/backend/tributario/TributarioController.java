package desarrolloWeb.backend.tributario;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/tributario")
public class TributarioController {

    private final TributarioService tributarioService;

    public TributarioController(TributarioService tributarioService) {
        this.tributarioService = tributarioService;
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenTributario> resumen(
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(tributarioService.generarResumen(desde, hasta));
    }
}
