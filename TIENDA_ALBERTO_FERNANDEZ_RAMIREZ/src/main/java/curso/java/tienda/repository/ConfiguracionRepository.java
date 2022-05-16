package curso.java.tienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import curso.java.tienda.model.Configuracion;

public interface ConfiguracionRepository extends JpaRepository<Configuracion, Integer>{
	Configuracion findByClave(String clave);
}
