package curso.java.tienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import curso.java.tienda.model.Categoria;
import curso.java.tienda.model.Configuracion;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer>{
	Categoria getByNombre(String nombre);
}
