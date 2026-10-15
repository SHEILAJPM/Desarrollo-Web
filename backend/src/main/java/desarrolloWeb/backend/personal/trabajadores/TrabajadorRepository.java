
package desarrolloWeb.backend.personal.trabajadores;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {

    boolean existsByDocumentoIdentidad(String documentoIdentidad);

    @Query("SELECT t FROM Trabajador t WHERE LOWER(t.area) = LOWER(:area)")
    List<Trabajador> buscarPorArea(@Param("area") String area);
}
