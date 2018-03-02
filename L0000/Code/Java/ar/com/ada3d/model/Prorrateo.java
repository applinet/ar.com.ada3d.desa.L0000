package ar.com.ada3d.model;

import java.io.Serializable;
import java.math.*;

public class Prorrateo implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public Prorrateo() {
		
	}

	private int prt_posicion;
	private int prt_posicionEnGrilla;
	private String prt_titulo;
	// gastos, cuota fija o presupuesto
	private String prt_tipo; 
	
	//define si la posición es utilizada o no (cero no se utiliza)
	private int prt_porcentaje;
	private BigDecimal prt_importe;

	//Getters and Setters
	
	public int getPrt_posicion() {
		return prt_posicion;
	}
	public void setPrt_posicion(int prt_posicion) {
		this.prt_posicion = prt_posicion;
	}
	public int getPrt_posicionEnGrilla() {
		return prt_posicionEnGrilla;
	}
	public void setPrt_posicionEnGrilla(int prt_posicionEnGrilla) {
		this.prt_posicionEnGrilla = prt_posicionEnGrilla;
	}
	public String getPrt_titulo() {
		return prt_titulo;
	}
	public void setPrt_titulo(String prt_titulo) {
		this.prt_titulo = prt_titulo;
	}
	public String getPrt_tipo() {
		return prt_tipo;
	}
	public void setPrt_tipo(String prt_tipo) {
		this.prt_tipo = prt_tipo;
	}
	public int getPrt_porcentaje() {
		return prt_porcentaje;
	}
	public void setPrt_porcentaje(int prt_porcentaje) {
		this.prt_porcentaje = prt_porcentaje;
	}
	public BigDecimal getPrt_importe() {
		return prt_importe;
	}
	public void setPrt_importe(BigDecimal prt_importe) {
		this.prt_importe = prt_importe;
	}
	
}
