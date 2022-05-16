package curso.java.tienda.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import curso.java.tienda.model.Categoria;
import curso.java.tienda.model.Configuracion;
import curso.java.tienda.model.Descuento;
import curso.java.tienda.model.Estado;
import curso.java.tienda.model.LineaPedido;
import curso.java.tienda.model.Pedido;
import curso.java.tienda.model.Producto;
import curso.java.tienda.model.Proveedor;
import curso.java.tienda.model.Roles;
import curso.java.tienda.model.Usuario;
import curso.java.tienda.service.CategoriaService;
import curso.java.tienda.service.ConfiguracionService;
import curso.java.tienda.service.DescuentoService;
import curso.java.tienda.service.LineaPedidoService;
import curso.java.tienda.service.PedidoService;
import curso.java.tienda.service.ProductoService;
import curso.java.tienda.service.ProveedorService;
import curso.java.tienda.service.UsuarioService;
import curso.java.tienda.util.Encriptacion;
import curso.java.tienda.util.PDFFactura;

@Controller
@RequestMapping("/admin")
public class AdministradorController {
	
	@Autowired
	UsuarioService us;
	
	@Autowired
	ProductoService ps;
	
	@Autowired
	PedidoService pes;
	
	@Autowired
	LineaPedidoService lps;
	
	@Autowired
	CategoriaService cs;
	
	@Autowired
	ConfiguracionService confs;
	
	@Autowired
	ProveedorService pros;
	
	@Autowired
	DescuentoService ds;
	
	@GetMapping("")
	public String cargaAdministracion(HttpSession miSesion){
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			String nombreAdministrador = miSesion.getAttribute("usuarioAdmin").toString();
			Usuario usuarioAdministrador = us.buscarUsuario(nombreAdministrador);
			miSesion.setAttribute("datosAdministrador", usuarioAdministrador);
			
			return "administrador/administracionPerfil";
		}
		
		return "redirect:/admin/login";
	}
	
	@GetMapping("/login")
	public String cargaLogin(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "administrador/login";
	}
	
	@PostMapping("/login")
	public String loginAdmin(@ModelAttribute Usuario usuario, HttpSession miSesion) {
		if(usuario.getNombre() != null && usuario.getPassword() != null) {
			for(Usuario usuarioBuscado : us.getUsuarios()) {
				if(usuarioBuscado.getNombre().equals(usuario.getNombre()) && Encriptacion.validarPassword(usuario.getPassword(), usuarioBuscado.getPassword()) == true && (usuarioBuscado.getRol().equals(Roles.administrador) || usuarioBuscado.getRol().equals(Roles.empleado))) {
						miSesion.setAttribute("usuarioAdmin", usuario.getNombre());
						if(usuarioBuscado.getRol().equals(Roles.empleado)) {
							miSesion.setAttribute("rolUsuario", Roles.empleado.toString());
						}
						if(usuarioBuscado.getRol().equals(Roles.administrador)) {
							miSesion.setAttribute("rolUsuario", Roles.administrador.toString());
						}
						return "redirect:/admin";
				}
			}
		}
		
		return "redirect:/admin/login";
	}
	
	@GetMapping("/cerrarSesion")
	public String cerrarSesion(HttpSession miSesion) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			miSesion.setAttribute("usuarioAdmin", null);
			miSesion.setAttribute("rolUsuario", null);
			return "redirect:/admin/login";
		}
		
		return "redirect:/admin";
	}
	
	@GetMapping("/configuracion")
	public String cargaConfiguracionTienda(HttpSession miSesion) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			ArrayList<Configuracion> datosTienda = confs.getValores();
			miSesion.setAttribute("datosTienda", datosTienda);
		
			return "administrador/administracionConfiguracion";
		}
		
		return "redirect:/admin/login";
	}
	
	@GetMapping("/configuracion/verConfiguracion")
	public String editarConfiguracion(Model model) {
		ArrayList<Configuracion> configuracion = confs.getValores();
		model.addAttribute("configuracionesAdmin", configuracion);
		return "administrador/verConfiguracion";
	}
	
	@GetMapping("/configuracion/editarConfiguracion/{id}")
	public String editarConfiguracion(Model model, @PathVariable(name="id", required=false) int id) {
		ArrayList<Configuracion> configuraciones = new ArrayList<Configuracion>();
		configuraciones = confs.getValores();
		model.addAttribute("configuraciones", configuraciones);
		Configuracion configuracion = confs.buscarConfiguracion(id);
		model.addAttribute("configuracion", configuracion);
		return "administrador/editarConfiguracion";
	}
	
	
	@PostMapping("/configuracion/editarConfiguracionExistente")
	public String editarConfiguracionExistente(HttpSession miSesion, @ModelAttribute Configuracion configuracion) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			if(configuracion != null) {
				confs.editarConfiguracion(configuracion);
				return "redirect:/admin/configuracion";
			}
		}
		
		return "administrador/editarConfiguracion";
	}
	
	@GetMapping("/productos")
	public String cargaProductos(Model model, HttpSession miSesion) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			ArrayList<Producto> productos = new ArrayList<Producto>();
			productos = ps.obtenerProductos();
			miSesion.setAttribute("productosAdmin", productos);
			return "administrador/administracionProductos";
		}
	
		return "redirect:/admin/login";
	}
	
	@GetMapping("/productos/nuevoProducto")
	public String botonNuevoProducto(Model model) {
		model.addAttribute("producto", new Producto());
		ArrayList<Categoria> categorias = new ArrayList<Categoria>();
		categorias = cs.getCategorias();
		model.addAttribute("categorias", categorias);
		return "administrador/nuevoProducto";
	}
	
	@PostMapping("/productos/crearNuevoProducto")
	public String nuevoProducto(Model model, @ModelAttribute Producto producto, HttpSession miSesion) {
		if(producto.getNombre() != null && producto.getDescripcion() != null && producto.getCantidad() != 0 && producto.getPrecio() != 0) {
			Categoria categoriaNuevoProducto = cs.getCategoria(producto.getCategoria().getId());
			Producto crearProducto = new Producto(categoriaNuevoProducto, producto.getNombre(), producto.getDescripcion(), producto.getPrecio(), producto.getCantidad());
			ps.crearProducto(crearProducto);
			return "redirect:/admin/productos";
		}
		
		return "redirect:/admin/productos/crearNuevoProducto";
	}
	
	@GetMapping("/productos/editarProducto/{id}")
	public String editarProducto(Model model, @PathVariable(name="id", required=false) int id) {
		ArrayList<Categoria> categorias = new ArrayList<Categoria>();
		categorias = cs.getCategorias();
		model.addAttribute("categorias", categorias);
		Producto producto = ps.buscarProducto(id);
		model.addAttribute("producto", producto);
		return "administrador/editarProducto";
	}
	
	@PostMapping("/productos/editarProductoExistente")
	public String editarProductoExistente(HttpSession miSesion, @ModelAttribute Producto producto) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			if(producto != null) {
				ps.editarProducto(producto);
				return "redirect:/admin/productos";
			}
		}
		
		return "administrador/editarProducto";
	}
	
	@GetMapping("/productos/eliminarProducto/{id}")
	public String eliminarProducto(HttpSession miSesion, @PathVariable(name="id", required=false) int id) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			Producto producto = ps.buscarProducto(id);
			ps.eliminarProducto(producto);
			return "redirect:/admin/productos";
		}
		
		return "administrador/administracionProductos";
	}
	
	@GetMapping("/categorias")
	public String cargaCategorias(Model model, HttpSession miSesion) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			ArrayList<Categoria> categorias = new ArrayList<Categoria>();
			categorias = cs.getCategorias();
			miSesion.setAttribute("categoriasAdmin", categorias);
			return "administrador/administracionCategorias";
		}
	
		return "redirect:/admin/login";
	}
	
	@GetMapping("/categorias/nuevaCategoria")
	public String botonNuevaCategoria(Model model) {
		model.addAttribute("categoria", new Categoria());
		return "administrador/nuevoCategoria";
	}
	
	@PostMapping("/categorias/crearNuevaCategoria")
	public String nuevaCategoria(Model model, @ModelAttribute Categoria categoria, HttpSession miSesion) {
		if(categoria.getNombre() != null && categoria.getDescripcion() != null) {
			cs.crearCategoria(categoria);
			return "redirect:/admin/categorias";
		}
		
		return "redirect:/admin/categorias/crearNuevaCategoria";
	}
	
	@GetMapping("/descuentos")
	public String cargaDescuentos(Model model, HttpSession miSesion) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			ArrayList<Descuento> descuentos = new ArrayList<Descuento>();
			descuentos = ds.getDescuentos();
			miSesion.setAttribute("descuentosAdmin", descuentos);
			return "administrador/administracionDescuentos";
		}
	
		return "redirect:/admin/login";
	}
	
	@GetMapping("/descuentos/nuevoDescuento")
	public String botonNuevoDescuento(Model model) {
		model.addAttribute("descuento", new Descuento());
		return "administrador/nuevoDescuento";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	@PostMapping("/descuentos/crearNuevoDescuento")
	public String nuevoDescuento(@ModelAttribute Descuento descuento, HttpSession miSesion) {
		if(descuento.getCodigo() != null && descuento.getDescuento() != 0 && descuento.getFecha_inicio() != null && descuento.getFecha_fin() != null) {
			
			//Timestamp fecha_inicio = new Timestamp(descuento.getFecha_inicio().getTime());
			//Timestamp fecha_fin = new Timestamp(descuento.getFecha_fin().getTime());
			
			//SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
			
			//String fecha_inicio_formateada = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(fecha_inicio);
			//String fecha_fin_formateada = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(fecha_fin);
			//Long datetime_inicio = Long.parseLong(fecha_inicio_formateada);
			//Long datetime_fin = Long.parseLong(fecha_fin_formateada);
			//Timestamp fechaInicio = new Timestamp(datetime_inicio);
			//Timestamp fechaFin = new Timestamp(datetime_fin);
			//Timestamp fechaInicio = Timestamp.valueOf(fecha_inicio_formateada);
			//Timestamp fechaFin = Timestamp.valueOf(fecha_fin_formateada);
			
			Descuento nuevoDescuento = new Descuento(descuento.getCodigo(), descuento.getDescuento(), descuento.getFecha_inicio(), descuento.getFecha_fin());
			ds.nuevoDescuento(nuevoDescuento);
			return "redirect:/admin/descuentos";
		}
		
		return "redirect:/admin/descuentos/crearNuevoDescuento";
	}
	
	@GetMapping("/descuentos/editarDescuento/{id}")
	public String editarDescuento(Model model, @PathVariable(name="id", required=false) int id) {
		Descuento descuento = ds.getDescuento(id);
		model.addAttribute("descuento", descuento);
		return "administrador/editarDescuento";
	}
	
	@PostMapping("/descuentos/editarDescuentoExistente")
	public String editarDescuentoExistente(HttpSession miSesion, @ModelAttribute Descuento descuento) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			if(descuento != null) {
				ds.editarDescuento(descuento);
				return "redirect:/admin/descuentos";
			}
		}
		
		return "administrador/editarDescuento";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@GetMapping("/descuentos/eliminarDescuento/{id}")
	public String eliminarDescuento(HttpSession miSesion, @PathVariable(name="id", required=false) int id) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			Descuento descuento = ds.getDescuento(id);
			ds.borrarDescuento(descuento);
			return "redirect:/admin/descuentos";
		}
		
		return "administrador/administracionDescuentos";
	}
	
	@GetMapping("/proveedores")
	public String cargaProveedores(Model model, HttpSession miSesion) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			ArrayList<Proveedor> proveedores = new ArrayList<Proveedor>();
			proveedores = pros.getProveedores();
			miSesion.setAttribute("proveedoresAdmin", proveedores);
			return "administrador/administracionProveedores";
		}
	
		return "redirect:/admin/login";
	}
	
	@GetMapping("/proveedores/nuevoProveedor")
	public String botonNuevoProveedor(Model model) {
		model.addAttribute("proveedor", new Proveedor());
		return "administrador/nuevoProveedor";
	}
	
	@PostMapping("/proveedores/crearNuevoProveedor")
	public String nuevoProveedor(Model model, @ModelAttribute Proveedor proveedor, HttpSession miSesion) {
		if(proveedor.getCif() != null && proveedor.getDireccion() != null && proveedor.getEmail() != null && proveedor.getLocalidad() != null && proveedor.getNombre() != null &&  proveedor.getProvincia() != null && proveedor.getTelefono() != null) {
			pros.crearProveedor(proveedor);
			return "redirect:/admin/proveedores";
		}
		
		return "redirect:/admin/provedores/crearNuevoProveedor";
	}
	
	@GetMapping("/proveedores/editarProveedor/{id}")
	public String editarProveedor(Model model, @PathVariable(name="id", required=false) int id) {
		Proveedor proveedor = pros.getProveedor(id);
		model.addAttribute("proveedor", proveedor);
		return "administrador/editarProveedor";
	}
	
	@PostMapping("/proveedores/editarProveedorExistente")
	public String editarProveedorExistente(HttpSession miSesion, @ModelAttribute Proveedor proveedor) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			if(proveedor != null) {
				pros.editarProveedor(proveedor);
				return "redirect:/admin/proveedores";
			}
		}
		
		return "administrador/editarProveedor";
	}
	
	@GetMapping("/proveedores/eliminarProveedor/{id}")
	public String eliminarProveedor(HttpSession miSesion, @PathVariable(name="id", required=false) int id) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			Proveedor proveedor = pros.getProveedor(id);
			pros.borrarProveedor(proveedor);
			return "redirect:/admin/proveedores";
		}
		
		return "administrador/administracionProveedores";
	}
	
	@GetMapping("/clientes")
	public String cargaClientes(Model model, HttpSession miSesion) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			ArrayList<Usuario> clientes = new ArrayList<Usuario>();
			Roles rol = Roles.cliente;
			clientes = us.getUsuariosRol(rol);
			miSesion.setAttribute("clientesAdmin", clientes);
			return "administrador/administracionClientes";
		}
		
		return "redirect:/admin/login";
	}
	
	@GetMapping("/clientes/nuevoCliente")
	public String botonNuevoCliente(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "administrador/nuevoCliente";
	}
	
	@PostMapping("/clientes/crearNuevoCliente")
	public String nuevoCliente(@Valid @ModelAttribute Usuario usuario, BindingResult br, Model model, HttpSession miSesion) {
		if(br.hasErrors()) {
			return "administrador/nuevoCliente";
		}else {
			if(usuario.getNombre() != null && usuario.getPassword()  != null && usuario.getNombreUsuario() != null && usuario.getApellido() != null && usuario.getEmail() != null && usuario.getDireccion() != null && usuario.getLocalidad() != null && usuario.getProvincia() != null && usuario.getPais() != null && usuario.getTelefono() != null && usuario.getRol() != null) {
				String passwordEncriptada = Encriptacion.encriptarPassword(usuario.getPassword());
				Usuario usuarioNuevo = new Usuario(usuario.getNombre(), usuario.getEmail(), passwordEncriptada, usuario.getNombreUsuario(), usuario.getApellido(),  usuario.getDireccion(), usuario.getProvincia(), usuario.getLocalidad(), usuario.getPais(), usuario.getTelefono(), usuario.getRol());
				us.crearUsuario(usuarioNuevo);
				return "redirect:/admin/clientes";
			}
			
			return "redirect:/admin/clientes/crearNuevoCliente";
		}
	}
	
	@GetMapping("/clientes/editarCliente/{id}")
	public String editarCliente(Model model, @PathVariable(name="id", required=false) int id) {
		Usuario usuario = us.getUsuario(id);
		model.addAttribute("usuario", usuario);
		return "administrador/editarCliente";
	}
	
	@PostMapping("/clientes/editarClienteExistente")
	public String editarClienteExistente(@Valid @ModelAttribute Usuario usuario, BindingResult br, HttpSession miSesion) {
		if(br.hasErrors()) {
			return "administrador/editarCliente";
		}else {
			if(miSesion.getAttribute("usuarioAdmin") != null) {
				if(usuario != null) {
					us.editarUsuario(usuario);
					return "redirect:/admin/clientes";
				}
			}
			
			return "administrador/editarCliente";
		}
	}
	
	@GetMapping("/clientes/eliminarCliente/{id}")
	public String eliminarCliente(HttpSession miSesion, @PathVariable(name="id", required=false) int id) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			Usuario empleado = us.getUsuario(id);
			us.eliminarUsuario(empleado);
			return "redirect:/admin/clientes";
		}
		
		return "administrador/administracionClientes";
	}
	
	@GetMapping("/empleados")
	public String cargaEmpleados(Model model, HttpSession miSesion) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			ArrayList<Usuario> empleados = new ArrayList<Usuario>();
			Roles rol = Roles.empleado;
			empleados = us.getUsuariosRol(rol);
			miSesion.setAttribute("empleadosAdmin", empleados);
			return "administrador/administracionEmpleados";
		}
		
		return "redirect:/admin/login";
	}
	
	@GetMapping("/empleados/nuevoEmpleado")
	public String botonNuevoEmpleado(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "administrador/nuevoEmpleado";
	}
	
	@PostMapping("/empleados/crearNuevoEmpleado")
	public String nuevoEmpleado(@Valid @ModelAttribute Usuario usuario, BindingResult br, Model model, HttpSession miSesion) {
		if(br.hasErrors()) {
			return "administrador/nuevoEmpleado";
		}else {
			if(usuario.getNombre() != null && usuario.getNombre() != null && usuario.getNombreUsuario() != null && usuario.getApellido() != null && usuario.getEmail() != null && usuario.getDireccion() != null && usuario.getLocalidad() != null && usuario.getProvincia() != null && usuario.getPais() != null && usuario.getTelefono() != null && usuario.getRol() != null) {
				String passwordEncriptada = Encriptacion.encriptarPassword(usuario.getPassword());
				Usuario empleadoNuevo = new Usuario(usuario.getNombre(), usuario.getEmail(), passwordEncriptada, usuario.getNombreUsuario(), usuario.getApellido(),  usuario.getDireccion(), usuario.getProvincia(), usuario.getLocalidad(), usuario.getPais(), usuario.getTelefono(), usuario.getRol());
				us.crearUsuario(empleadoNuevo);
				return "redirect:/admin/empleados";
			}
		
			return "redirect:/admin/empleados/crearNuevoEmpleado";
		}
	}
	
	@GetMapping("/empleados/editarEmpleado/{id}")
	public String editarEmpleado(Model model, @PathVariable(name="id", required=false) int id) {
		Usuario usuario = us.getUsuario(id);
		model.addAttribute("usuario", usuario);
		return "administrador/editarEmpleado";
	}
	
	@PostMapping("/empleados/editarEmpleadoExistente")
	public String editarEmpleadoExistente(@Valid @ModelAttribute Usuario usuario, BindingResult br, HttpSession miSesion) {
		if(br.hasErrors()) {
			return "administrador/editarEmpleado";
		}else {
			if(miSesion.getAttribute("usuarioAdmin") != null) {
				if(usuario != null) {
					us.editarUsuario(usuario);
					return "redirect:/admin/empleados";
				}
			}
			
			return "administrador/editarEmpleado";
		}
	}
	
	@GetMapping("/empleados/eliminarEmpleado/{id}")
	public String eliminarEmpleado(HttpSession miSesion, @PathVariable(name="id", required=false) int id) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			Usuario empleado = us.getUsuario(id);
			us.eliminarUsuario(empleado);
			return "redirect:/admin/empleados";
		}
		
		return "administrador/administracionEmpleados";
	}
	
	@GetMapping("/administradores")
	public String cargaAdministradores(Model model, HttpSession miSesion) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			ArrayList<Usuario> administradores = new ArrayList<Usuario>();
			Roles rol = Roles.administrador;
			administradores = us.getUsuariosRol(rol);
			miSesion.setAttribute("administradoresAdmin", administradores);
			return "administrador/administracionAdministradores";
		}
		
		return "redirect:/admin/login";
	}
	
	@GetMapping("/administradores/nuevoAdministrador")
	public String botonNuevoAdministrador(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "administrador/nuevoAdministrador";
	}
	
	@PostMapping("/administradores/crearNuevoAdministrador")
	public String nuevoAdministrador(@Valid @ModelAttribute Usuario usuario, BindingResult br, Model model, HttpSession miSesion) {
		if(br.hasErrors()) {
			return "administrador/nuevoAdministrador";
		}else {
			if(usuario.getNombre() != null && usuario.getNombre() != null && usuario.getNombreUsuario() != null && usuario.getApellido() != null && usuario.getEmail() != null && usuario.getDireccion() != null && usuario.getLocalidad() != null && usuario.getProvincia() != null && usuario.getPais() != null && usuario.getTelefono() != null && usuario.getRol() != null) {
				String passwordEncriptada = Encriptacion.encriptarPassword(usuario.getPassword());
				Usuario administradorNuevo = new Usuario(usuario.getNombre(), usuario.getEmail(), passwordEncriptada, usuario.getNombreUsuario(), usuario.getApellido(),  usuario.getDireccion(), usuario.getProvincia(), usuario.getLocalidad(), usuario.getPais(), usuario.getTelefono(), usuario.getRol());
				us.crearUsuario(administradorNuevo);
				return "redirect:/admin/administradores";
			}
		
			return "redirect:/admin/administradores/crearNuevoAdministrador";
		}
	}
	
	@GetMapping("/administradores/editarAdministrador/{id}")
	public String editarAdministrador(Model model, @PathVariable(name="id", required=false) int id) {
		Usuario usuario = us.getUsuario(id);
		model.addAttribute("usuario", usuario);
		return "administrador/editarAdministrador";
	}
	
	@PostMapping("/administradores/editarAdministradorExistente")
	public String editarAdministradorExistente(@Valid @ModelAttribute Usuario usuario, BindingResult br, HttpSession miSesion) {
		if(br.hasErrors()) {
			return "administrador/editarAdministrador";
		}else {
			if(miSesion.getAttribute("usuarioAdmin") != null) {
				if(usuario != null) {
					us.editarUsuario(usuario);
					return "redirect:/admin/administradores";
				}
			}
			
			return "administrador/editarAdministrador";
		}
	}
	
	@GetMapping("/administradores/eliminarAdministrador/{id}")
	public String eliminarAdministrador(HttpSession miSesion, @PathVariable(name="id", required=false) int id) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			Usuario administrador = us.getUsuario(id);
			us.eliminarUsuario(administrador);
			return "redirect:/admin/administradores";
		}
		
		return "administrador/administracionAdministradores";
	}
	
	@GetMapping("/procesarPedidos")
	public String cargaPedidos(Model model, HttpSession miSesion) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			ArrayList<Pedido> pedidos = new ArrayList<Pedido>();
			pedidos = pes.getPedidos();
			miSesion.setAttribute("pedidosAdmin", pedidos);
			return "administrador/administracionProcesarPedidos";
		}
		
		return "redirect:/admin/login";
	}
	
	@GetMapping("/procesarPedidos/enviarPedido/{id}")
	public String enviarPedido(RedirectAttributes ra, HttpSession miSesion, @PathVariable(name="id") int id) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			Pedido pedidoSelecionado = pes.obtenerPedidoUsuario(id);
			if(pedidoSelecionado.getEstado_pedido() != Estado.PC && pedidoSelecionado.getEstado_pedido() != Estado.C && pedidoSelecionado.getEstado_pedido() != Estado.E) {
				Estado nuevoEstado = Estado.E;
				pes.cambiarEstado(nuevoEstado, id);
				
				Usuario usuario = us.buscarUsuario(miSesion.getAttribute("usuarioAdmin").toString());
				Pedido pedido = pes.obtenerPedidoUsuario(id);
				ArrayList<LineaPedido> productosPedido = new ArrayList<LineaPedido>();
				productosPedido = lps.verLineasPedido(pedido);
				PDFFactura.crearFactura(usuario, pedido, productosPedido);
				return "redirect:/admin/procesarPedidos";
			}else {
				String numPedido = pedidoSelecionado.getNum_factura();
				if(pedidoSelecionado.getEstado_pedido() == Estado.PC) {
					ra.addFlashAttribute("errorEnviarPedidoAdmin", "No se puede enviar el pedido " + numPedido + " por que esta pendiente de cancelacion.");
				}
				if(pedidoSelecionado.getEstado_pedido() == Estado.C) {
					ra.addFlashAttribute("errorEnviarPedidoAdmin", "No se puede enviar el pedido " + numPedido + " por que ha sido cancelado.");
				}
				if(pedidoSelecionado.getEstado_pedido() == Estado.E) {
					ra.addFlashAttribute("errorEnviarPedidoAdmin", "No se puede enviar el pedido " + numPedido + " por que ya ha sido enviado.");
				}
				return "redirect:/admin/procesarPedidos";
			}
		}
		return "redirect:/admin";
	}
	
	@GetMapping("/procesarPedidos/cancelarPedido/{id}")
	public String cancelarPedido(RedirectAttributes ra, HttpSession miSesion, @PathVariable(name="id") int id) {
		if(miSesion.getAttribute("usuarioAdmin") != null) {
			Pedido pedidoSelecionado = pes.obtenerPedidoUsuario(id);
			if(pedidoSelecionado.getEstado_pedido() != Estado.E && pedidoSelecionado.getEstado_pedido() != Estado.C) {
				Estado nuevoEstado = Estado.C;
				pes.cambiarEstado(nuevoEstado, id);
				return "redirect:/admin/procesarPedidos";
			}else {
				String numPedido = pedidoSelecionado.getNum_factura();
				if(pedidoSelecionado.getEstado_pedido() == Estado.E) {
					ra.addFlashAttribute("errorCancelarPedidoAdmin", "No se puede cancelar el pedido " + numPedido + " por que ya ha sido enviado.");
				}
				if(pedidoSelecionado.getEstado_pedido() == Estado.C) {
					ra.addFlashAttribute("errorCancelarPedidoAdmin", "No se puede cancelar el pedido " + numPedido + " por que ya ha sido cancelado.");
				}
				return "redirect:/admin/procesarPedidos";
			}
		}
		return "redirect:/admin";
	}
}
