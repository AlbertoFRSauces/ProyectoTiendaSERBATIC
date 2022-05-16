package curso.java.tienda.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", length = 11)
	private int id;
	@ManyToOne
	Categoria categoria;
	@Column(name = "nombre", length = 50)
	private String nombre;
	@Column(name = "descripcion", length = 250)
	private String descripcion;
	@Column(name = "precio")
	private double precio;
	@Column(name = "cantidad", length = 11)
	private int cantidad;
	
	public Producto(Categoria categoria, String nombre, String descripcion, double precio, int cantidad) {
		this.categoria = categoria;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.precio = precio;
		this.cantidad = cantidad;
	}
	
	
}
