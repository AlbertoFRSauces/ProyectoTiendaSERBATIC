package curso.java.tienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import curso.java.tienda.model.Descuento;

public interface DescuentoRepository extends JpaRepository<Descuento, Integer>{
	
}
