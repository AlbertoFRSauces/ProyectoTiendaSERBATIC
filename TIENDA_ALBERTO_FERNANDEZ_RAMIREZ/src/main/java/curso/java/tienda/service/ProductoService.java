package curso.java.tienda.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import curso.java.tienda.model.Categoria;
import curso.java.tienda.model.Producto;
import curso.java.tienda.repository.ProductoRepository;

@Service
public class ProductoService {
	
	@Autowired
	ProductoRepository pr;
	
	public ArrayList<Producto> obtenerProductos() {
		ArrayList<Producto> catalogo = new ArrayList<Producto>();
		catalogo = (ArrayList<Producto>)pr.findAll();
		return catalogo;
	}
	
	public Producto buscarProducto(int id){
		Producto productoDevolver = null;
		Optional<Producto> opcional = pr.findById(id);
		productoDevolver = opcional.get();
		
		return productoDevolver;
	}
	
	public void crearProducto(Producto producto) {
		pr.save(producto);
	}
	
	public void eliminarProducto(Producto producto) {
		pr.delete(producto);
	}
	
	public void editarProducto(Producto producto) {
		pr.save(producto);
	}
	
	public ArrayList<Producto> buscarCampo(String campoBuscar){
		ArrayList<Producto> productos = new ArrayList<Producto>();
		productos = pr.buscarCampo(campoBuscar);
		return productos;
	}
	
	public ArrayList<Producto> buscarPorCategoria(Categoria categoria){
		ArrayList<Producto> productos = new ArrayList<Producto>();
		productos = pr.buscarCategoria(categoria);
		return productos;
	}
}
