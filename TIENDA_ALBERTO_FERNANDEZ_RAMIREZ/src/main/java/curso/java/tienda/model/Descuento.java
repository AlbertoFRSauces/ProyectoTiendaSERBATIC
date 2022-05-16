package curso.java.tienda.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="descuentos")
public class Descuento {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", length = 11)
	private int id;
	@Column(name="codigo")
	private String codigo;
	@Column(name="descuento")
	private float descuento;
	@Column(name="fecha_inicio")
	private Timestamp fecha_inicio;
	@Column(name="fecha_fin")
	private Timestamp fecha_fin;
	
	public Descuento(String codigo, float descuento, Timestamp fecha_inicio, Timestamp fecha_fin) {
		this.codigo = codigo;
		this.descuento = descuento;
		this.fecha_inicio = fecha_inicio;
		this.fecha_fin = fecha_fin;
	}

	
}
