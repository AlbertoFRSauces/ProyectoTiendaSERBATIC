package curso.java.tienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import curso.java.tienda.model.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer>{

}
