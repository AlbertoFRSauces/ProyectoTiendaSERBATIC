package curso.java.tienda.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import curso.java.tienda.model.Configuracion;
import curso.java.tienda.repository.ConfiguracionRepository;

@Service
public class ConfiguracionService{
	
	@Autowired
	ConfiguracionRepository cr;
	
	public String getValor(String clave) {
		Configuracion configuracion;
		configuracion = cr.findByClave(clave); 
		String valor = configuracion.getValor();
		
		return valor;
	}
	
	public Configuracion buscarConfiguracion(int id) {
		Configuracion configuracion;
		configuracion = cr.getById(id);
		return configuracion;
	}
	
	public ArrayList<Configuracion> getValores(){
		ArrayList<Configuracion> datosTienda = new ArrayList<Configuracion>();
		datosTienda = (ArrayList<Configuracion>)cr.findAll();
		return datosTienda;
	}
	
	public void editarConfiguracion(Configuracion configuracion) {
		cr.save(configuracion);
	}
	
	public Configuracion getConfiguracion(String clave) {
		Configuracion configuracion;
		configuracion = cr.findByClave(clave);
		return configuracion;
	}
}
