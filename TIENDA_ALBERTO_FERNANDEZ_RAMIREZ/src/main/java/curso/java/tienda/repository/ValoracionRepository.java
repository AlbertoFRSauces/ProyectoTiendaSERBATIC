package curso.java.tienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import curso.java.tienda.model.Valoracion;

public interface ValoracionRepository extends JpaRepository<Valoracion, Integer>{
	
}
