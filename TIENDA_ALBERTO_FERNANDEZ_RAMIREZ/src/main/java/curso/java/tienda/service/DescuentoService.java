package curso.java.tienda.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import curso.java.tienda.model.Descuento;
import curso.java.tienda.repository.DescuentoRepository;

@Service
public class DescuentoService {
	@Autowired
	DescuentoRepository dr;
	
	public ArrayList<Descuento> getDescuentos() {
		ArrayList<Descuento> descuentos = new ArrayList<Descuento>();
		descuentos = (ArrayList<Descuento>)dr.findAll();
		
		return descuentos;
	}
	
	public Descuento getDescuento(int id) {
		Descuento descuento = dr.getById(id);
		
		return descuento;
	}
	
	public void nuevoDescuento(Descuento descuento) {
		dr.save(descuento);
	}
	
	public void editarDescuento(Descuento descuento){
		dr.save(descuento);
	}
	
	public void borrarDescuento(Descuento descuento) {
		dr.delete(descuento);
	}
}
