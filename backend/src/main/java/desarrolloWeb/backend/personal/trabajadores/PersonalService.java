package desarrolloWeb.backend.personal.trabajadores;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonalService {

    private final TrabajadorRepository trabajadorRepository;

    public PersonalService(TrabajadorRepository trabajadorRepository) {
        this.trabajadorRepository = trabajadorRepository;
    }

    public List<Trabajador> listar(String area, String estado) {
        return trabajadorRepository.findAll().stream()
                .filter(t -> area == null || area.isBlank()
                        || (t.getArea() != null
                        && t.getArea().equalsIgnoreCase(area)))
                .filter(t -> estado == null || estado.isBlank()
                        || (t.getEstado() != null
                        && t.getEstado().equalsIgnoreCase(estado)))
                .toList();
    }
    
    
public List<Trabajador> listarPorArea(String area) {
    if (area == null || area.isBlank()) {
        return trabajadorRepository.findAll();
    }

    return trabajadorRepository.buscarPorArea(area);
}


    public Optional<Trabajador> buscarPorId(Long id) {
        return trabajadorRepository.findById(id);
    }

    public Optional<Trabajador> buscarPorDocumento(String documentoIdentidad) {
        return trabajadorRepository.findAll().stream()
                .filter(t -> t.getDocumentoIdentidad() != null
                        && t.getDocumentoIdentidad().equals(documentoIdentidad))
                .findFirst();
    }

    @Transactional
    public Trabajador registrar(Trabajador nuevo) {
        if (nuevo.getNombres() == null || nuevo.getNombres().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del trabajador es obligatorio");
        }

        if (nuevo.getDocumentoIdentidad() == null
                || nuevo.getDocumentoIdentidad().isBlank()) {
            throw new IllegalArgumentException(
                    "El documento de identidad es obligatorio");
        }

        if (trabajadorRepository.existsByDocumentoIdentidad(
                nuevo.getDocumentoIdentidad())) {
            throw new IllegalStateException(
                    "Ya existe un trabajador registrado con el documento "
                    + nuevo.getDocumentoIdentidad());
        }

        // El ID lo genera MySQL.
        nuevo.setId(null);
        nuevo.setEstado("ACTIVO");

        return trabajadorRepository.save(nuevo);
    }

    @Transactional
    public Optional<Trabajador> actualizar(Long id, Trabajador cambios) {
        return trabajadorRepository.findById(id).map(existente -> {
            existente.setNombres(cambios.getNombres());
            existente.setCargo(cambios.getCargo());
            existente.setArea(cambios.getArea());

            return trabajadorRepository.save(existente);
        });
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!trabajadorRepository.existsById(id)) {
            return false;
        }

        trabajadorRepository.deleteById(id);
        return true;
    }
}