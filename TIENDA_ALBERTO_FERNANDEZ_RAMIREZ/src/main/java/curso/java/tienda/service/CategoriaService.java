package curso.java.tienda.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import curso.java.tienda.model.Categoria;
import curso.java.tienda.repository.CategoriaRepository;

@Service
public class CategoriaService {
	@Autowired
	CategoriaRepository cr;
	
	public ArrayList<Categoria> getCategorias(){
		ArrayList<Categoria> listaCategorias = new ArrayList<Categoria>();
		listaCategorias =  (ArrayList<Categoria>)cr.findAll();
		return listaCategorias;
	}
	
	public Categoria getCategoria(int id) {
		Categoria categoria;
		categoria = cr.getById(id);
		return categoria;
	}
	
	public Categoria getCategoriaNombre(String nombre) {
		Categoria categoria;
		categoria = cr.getByNombre(nombre);
		return categoria;
	}
	
	public void crearCategoria(Categoria categoria) {
		cr.save(categoria);
	}
}
