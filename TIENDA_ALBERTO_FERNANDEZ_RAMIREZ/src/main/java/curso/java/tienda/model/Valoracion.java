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
@Table(name="valoraciones")
public class Valoracion {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id",  length = 11)
	private int id;
	@ManyToOne
	Producto producto;
	@ManyToOne
	Usuario usuario;
	@Column(name = "valoracion")
	private int valoracion;
	@Column(name = "comentario")
	private String comentario;
	
	public Valoracion(Producto producto, Usuario usuario, int valoracion, String comentario) {
		this.producto = producto;
		this.usuario = usuario;
		this.valoracion = valoracion;
		this.comentario = comentario;
	}
}
