package curso.java.tienda.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.hibernate.engine.config.spi.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import curso.java.tienda.model.Categoria;
import curso.java.tienda.model.Producto;
import curso.java.tienda.model.Valoracion;
import curso.java.tienda.service.CategoriaService;
import curso.java.tienda.service.ConfiguracionService;
import curso.java.tienda.service.ProductoService;
import curso.java.tienda.service.ValoracionService;

/**
 * ProductoController.java
 * 
 * Representa la clase controlador de producto.
 * 
 * @author Alberto Fernandez
 * @version 1.0
 */
@Controller
@RequestMapping("")
public class ProductoController {
	
	/**
	 * Representa un objeto del servicio de producto.
	 */
	@Autowired
	ProductoService ps;
	
	/**
	 * Representa un objeto del servicio de configuracion.
	 */
	@Autowired
	ConfiguracionService cs;
	
	/**
	 * Representa un objeto del servicio de categoria.
	 */
	@Autowired
	CategoriaService cats;
	
	/**
	 * Representa un objeto del servicio valoracion.
	 */
	@Autowired
	ValoracionService vs;
	
	/**
	 * Metodo que permite mostrar el catalogo de todos los productos disponibles en el index.
	 * Inicializa en la sesion el carrito del usuario vacio.
	 * 
	 * @param model Objeto modelo
	 * @param miSesion Objeto que contiene la sesion
	 * @return devuelve el index
	 */
	@GetMapping("")
	public String inicio(Model model, HttpSession miSesion) {
		ArrayList<Producto> catalogo = ps.obtenerProductos();
		String nombreTienda;
		model.addAttribute("catalogo", catalogo);
		ArrayList<Categoria> categorias = new ArrayList<Categoria>();
		categorias = cats.getCategorias();
		model.addAttribute("categorias", categorias);
		nombreTienda = cs.getValor("nombre");
		Object carritoSesion = miSesion.getAttribute("carrito"); 
		ArrayList<Producto> carrito;
		
		if(carritoSesion == null) {
			carrito = new ArrayList<Producto>();
		}else {
			carrito = (ArrayList<Producto>) carritoSesion;
		}
		
		ArrayList<Valoracion> valoraciones = vs.getValoraciones();
		model.addAttribute("valoraciones", valoraciones);
		
		miSesion.setAttribute("nombreTienda", nombreTienda);
		miSesion.setAttribute("carrito", carrito);
		return "index";
	}
	
	/**
	 * Metodo que permite agregar un producto al carrito del usuario.
	 * Almaceno el precio del producto en la sesion para mostrar el total del carro.
	 * 
	 * @param miSesion Objeto que contiene la sesion
	 * @param id El id del producto seleccionado
	 * @return devuelve el index
	 */
	@GetMapping("/meterProducto")
	public String meterProducto(HttpSession miSesion, @RequestParam(name="id", required=false) int id) {
		ArrayList<Producto> carrito = (ArrayList<Producto>) miSesion.getAttribute("carrito");
		Producto producto;
		producto = ps.buscarProducto(id);
		//Producto insertarProducto = new Producto(producto.getId(), producto.getCategoria(), producto.getNombre(), producto.getDescripcion(), producto.getPrecio(), cantidad);
		carrito.add(producto);
		miSesion.setAttribute("carrito", carrito);
		
		
		
		
		double totalCarrito = 0.0;
		for(Producto productos : carrito) {
			double precio = productos.getPrecio();
			totalCarrito += precio;
		}
		miSesion.setAttribute("totalCarrito", Math.round(totalCarrito*100.00)/100.00);
		
		return "redirect:/";
	}
	
	/**
	 * Metodo que permite buscar un producto mediante un campo de busqueda.
	 * Mostrara los productos que coincidan con dicha busqueda.
	 * 
	 * 
	 * @param model Objeto que contiene el modelo
	 * @param miSesion Objeto que contiene la sesion
	 * @param campoBuscar el texto introducido por el usuario en el campo buscar
	 * @return devuelve el index
	 */
	@GetMapping("/buscarProducto")
	public String buscarProducto(Model model, HttpSession miSesion, @RequestParam(name="buscar", required=false) String campoBuscar) {
		if(campoBuscar != null) {
			ArrayList<Producto> productosBuscados = new ArrayList<Producto>();
			productosBuscados = ps.buscarCampo(campoBuscar);
			miSesion.setAttribute("productosBuscados", productosBuscados);
			model.addAttribute("catalogo", productosBuscados);
			
			ArrayList<Valoracion> valoraciones = vs.getValoraciones();
			model.addAttribute("valoraciones", valoraciones);
			return "index";
		}
		
		return "redirect:/";
	}
	
	@GetMapping("/filtrarProducto")
	public String filtrarProducto(Model model, HttpSession miSesion, @RequestParam(name="seleccionado", required=false) String opcionSeleccionada) {
		if(opcionSeleccionada != null) {
			ArrayList<Producto> productosBuscados = new ArrayList<Producto>();
			Categoria categoria = cats.getCategoriaNombre(opcionSeleccionada);
			productosBuscados = ps.buscarPorCategoria(categoria);
			miSesion.setAttribute("productosBuscados", productosBuscados);
			model.addAttribute("catalogo", productosBuscados);
			
			ArrayList<Categoria> categorias = new ArrayList<Categoria>();
			categorias = cats.getCategorias();
			model.addAttribute("categorias", categorias);
			
			ArrayList<Valoracion> valoraciones = vs.getValoraciones();
			model.addAttribute("valoraciones", valoraciones);
			return "index";
		}
		
		return "redirect:/";
	}
	
	/**
	 * Metodo que permite ver el detalle de un producto seleccionado en el index.
	 * 
	 * @param id el id del producto selecionado
	 * @param miSesion Objeto que contiene la sesion
	 * @return devuelve el detalle del producto
	 */
	@GetMapping("/anonimo/detalleProducto/{id}")
	public String detalleProducto(@PathVariable("id") Integer id, HttpSession miSesion, Model model) {
		Producto detalleDeProducto = ps.buscarProducto(id);
		miSesion.setAttribute("detalleProducto", detalleDeProducto);
		
		ArrayList<Valoracion> valoraciones = vs.getValoraciones();
		ArrayList<Valoracion> comentariosProducto = new ArrayList<Valoracion>();

		model.addAttribute("valoraciones", valoraciones);
		
		double contadorValoraciones = 0;
		double valoracionTotal = 0;
		double valoracionProducto = 0;		
		
		for(Valoracion valoracion: valoraciones) {
			if(valoracion.getProducto().getId() == id) {
				contadorValoraciones ++;
				valoracionTotal += valoracion.getValoracion();
			}
		}
		
		valoracionProducto = valoracionTotal/contadorValoraciones;
				
		for(Valoracion valoracion: valoraciones) {
			if(valoracion.getProducto().getId() == id) {
				comentariosProducto.add(valoracion);
			}
		}
		
		model.addAttribute("valoracionProducto", Math.round(valoracionProducto*100.0)/100.0);
		model.addAttribute("comentariosProducto", comentariosProducto);
		return "anonimo/detalleProducto";
	}
}
