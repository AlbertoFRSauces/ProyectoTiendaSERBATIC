package curso.java.tienda.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import curso.java.tienda.model.Proveedor;
import curso.java.tienda.repository.ProveedorRepository;

@Service
public class ProveedorService {
	@Autowired
	ProveedorRepository pr;
	
	public ArrayList<Proveedor> getProveedores(){
		ArrayList<Proveedor> proveedores = new ArrayList<Proveedor>();
		proveedores = (ArrayList<Proveedor>)pr.findAll();
		
		return proveedores;
	}
	
	public Proveedor getProveedor(int id) {
		Proveedor proveedor = pr.getById(id);
		return proveedor;
	}
	
	public void crearProveedor(Proveedor proveedor) {
		pr.save(proveedor);
	}
	
	public void editarProveedor(Proveedor proveedor) {
		pr.save(proveedor);
	}
	
	public void borrarProveedor(Proveedor proveedor) {
		pr.delete(proveedor);
	}
}
