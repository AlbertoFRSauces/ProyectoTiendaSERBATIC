package curso.java.tienda.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import curso.java.tienda.model.Producto;
import curso.java.tienda.model.Usuario;
import curso.java.tienda.model.Valoracion;
import curso.java.tienda.repository.ValoracionRepository;

@Service
public class ValoracionService {
	@Autowired
	ValoracionRepository vr;
	
	public ArrayList<Valoracion> getValoraciones(){
		ArrayList<Valoracion> valoraciones = new ArrayList<Valoracion>();
		valoraciones = (ArrayList<Valoracion>)vr.findAll();
		return valoraciones;
	}
	
	public void nuevaValoracion(Valoracion valoracion) {
		vr.save(valoracion);
	}
	
	public void editarValoracion(Valoracion valoracion) {
		vr.save(valoracion);
	}
	
	public void eliminarValoracion(Valoracion valoracion) {
		vr.delete(valoracion);
	}
	
}
