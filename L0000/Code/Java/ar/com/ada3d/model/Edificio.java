package ar.com.ada3d.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

public class Edificio implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public Edificio() {
		// Empty constructor
	}

	private String edf_codigo;
	private String edf_codigoNumerico;
	private String edf_codigoVisual;
	private String edf_direccion;
	private String edf_codigoPostal;
	private String edf_localidad;
	private String edf_provincia;
	private String edf_dependiente;
	private String edf_estadoProceso;
	private Date edf_fechaUltimaLiquidacion;
	private Integer edf_frecuenciaLiquidacion;
	private Date edf_fechaProximaLiquidacion;
	private String edf_cuit;
	private boolean edf_isReadMode;
	private List<Prorrateo> listaProrrateos;
	// si es cuota fija define el mes de prorrateo (1 o 31)
	private String edf_cuotaFijaDia;
	private List<SelectItem> edf_cuotaFijaDiaOpcionesCombo;
	private String edf_imprimeTitulosEnLiquidacion;
	private BigDecimal edf_importeInteresPunitorioDeudores;
	private BigDecimal edf_importeRecargoSegundoVencimiento;
	private Date edf_fechaPrimerVencimientoRecibos;
	private Date edf_fechaSegundoVencimientoRecibos;
	private String edf_modalidadInteresesPunitorios;
	private List<Porcentual> listaPorcentuales;
	private BigDecimal edf_importeFranqueo;
	private BigDecimal edf_importeMultaDeudores;
	private String edf_lockedBy;
	private BigDecimal edf_importeMasivoE12;
	private BigDecimal edf_importeMasivoE08A;
	private Date edf_fechaMasivoVTOEX1;
	private Date edf_fechaMasivoVTOEX2;
	
	//Getters and Setters
	
	public String getEdf_codigo() {
		return edf_codigo;
	}
	public void setEdf_codigo(String edf_codigo) {
		this.edf_codigo = edf_codigo;
	}
	public String getEdf_codigoNumerico() {
		return edf_codigoNumerico;
	}
	public void setEdf_codigoNumerico(String edf_codigoNumerico) {
		this.edf_codigoNumerico = edf_codigoNumerico;
	}
	public String getEdf_codigoVisual() {
		return edf_codigoVisual;
	}
	public void setEdf_codigoVisual(String edf_codigoVisual) {
		this.edf_codigoVisual = edf_codigoVisual;
	}
	public String getEdf_direccion() {
		return edf_direccion;
	}
	public void setEdf_direccion(String edf_direccion) {
		this.edf_direccion = edf_direccion;
	}
	public String getEdf_codigoPostal() {
		return edf_codigoPostal;
	}
	public void setEdf_codigoPostal(String edf_codigoPostal) {
		this.edf_codigoPostal = edf_codigoPostal;
	}
	public String getEdf_localidad() {
		return edf_localidad;
	}
	public void setEdf_localidad(String edf_localidad) {
		this.edf_localidad = edf_localidad;
	}
	public String getEdf_provincia() {
		return edf_provincia;
	}
	public void setEdf_provincia(String edf_provincia) {
		this.edf_provincia = edf_provincia;
	}
	
	public void setEdf_dependiente(String edf_dependiente) {
		this.edf_dependiente = edf_dependiente;
	}
	public String getEdf_dependiente() {
		return edf_dependiente;
	}
	public String getEdf_estadoProceso() {
		return edf_estadoProceso;
	}
	public void setEdf_estadoProceso(String edf_estadoProceso) {
		this.edf_estadoProceso = edf_estadoProceso;
	}
	public Date getEdf_fechaUltimaLiquidacion() {
		return edf_fechaUltimaLiquidacion;
	}
	public void setEdf_fechaUltimaLiquidacion(Date edf_fechaUltimaLiquidacion) {
		this.edf_fechaUltimaLiquidacion = edf_fechaUltimaLiquidacion;
	}
	public Integer getEdf_frecuenciaLiquidacion() {
		return edf_frecuenciaLiquidacion;
	}
	public void setEdf_frecuenciaLiquidacion(Integer edf_frecuenciaLiquidacion) {
		this.edf_frecuenciaLiquidacion = edf_frecuenciaLiquidacion;
	}
	public Date getEdf_fechaProximaLiquidacion() {
		return edf_fechaProximaLiquidacion;
	}
	public void setEdf_fechaProximaLiquidacion(Date edf_fechaProximaLiquidacion) {
		this.edf_fechaProximaLiquidacion = edf_fechaProximaLiquidacion;
	}
	public String getEdf_cuit() {
		return edf_cuit;
	}
	public void setEdf_cuit(String edf_cuit) {
		this.edf_cuit = edf_cuit;
	}
	public boolean isEdf_isReadMode() {
		return edf_isReadMode;
	}
	public void setEdf_isReadMode(boolean edf_isReadMode) {
		this.edf_isReadMode = edf_isReadMode;
	}
	public List<Prorrateo> getListaProrrateos() {
		return listaProrrateos;
	}
	public void setListaProrrateos(List<Prorrateo> listaProrrateos) {
		this.listaProrrateos = listaProrrateos;
	}
	public String getEdf_cuotaFijaDia() {
		return edf_cuotaFijaDia;
	}
	public void setEdf_cuotaFijaDia(String edf_cuotaFijaDia) {
		this.edf_cuotaFijaDia = edf_cuotaFijaDia;
	}
	public List<SelectItem> getEdf_cuotaFijaDiaOpcionesCombo() {
		return edf_cuotaFijaDiaOpcionesCombo;
	}
	public void setEdf_cuotaFijaDiaOpcionesCombo(
			List<SelectItem> edf_cuotaFijaDiaOpcionesCombo) {
		this.edf_cuotaFijaDiaOpcionesCombo = edf_cuotaFijaDiaOpcionesCombo;
	}
	
	public String getEdf_imprimeTitulosEnLiquidacion() {
		return edf_imprimeTitulosEnLiquidacion;
	}
	public void setEdf_imprimeTitulosEnLiquidacion(
			String edf_imprimeTitulosEnLiquidacion) {
		this.edf_imprimeTitulosEnLiquidacion = edf_imprimeTitulosEnLiquidacion;
	}
	public BigDecimal getEdf_importeInteresPunitorioDeudores() {
		return edf_importeInteresPunitorioDeudores;
	}
	public void setEdf_importeInteresPunitorioDeudores(
			BigDecimal edf_importeInteresPunitorioDeudores) {
		this.edf_importeInteresPunitorioDeudores = edf_importeInteresPunitorioDeudores;
	}
	public BigDecimal getEdf_importeRecargoSegundoVencimiento() {
		return edf_importeRecargoSegundoVencimiento;
	}
	public void setEdf_importeRecargoSegundoVencimiento(
			BigDecimal edf_importeRecargoSegundoVencimiento) {
		this.edf_importeRecargoSegundoVencimiento = edf_importeRecargoSegundoVencimiento;
	}
	public Date getEdf_fechaPrimerVencimientoRecibos() {
		return edf_fechaPrimerVencimientoRecibos;
	}
	public void setEdf_fechaPrimerVencimientoRecibos(
			Date edf_fechaPrimerVencimientoRecibos) {
		this.edf_fechaPrimerVencimientoRecibos = edf_fechaPrimerVencimientoRecibos;
	}
	public Date getEdf_fechaSegundoVencimientoRecibos() {
		return edf_fechaSegundoVencimientoRecibos;
	}
	public void setEdf_fechaSegundoVencimientoRecibos(
			Date edf_fechaSegundoVencimientoRecibos) {
		this.edf_fechaSegundoVencimientoRecibos = edf_fechaSegundoVencimientoRecibos;
	}
	public String getEdf_modalidadInteresesPunitorios() {
		return edf_modalidadInteresesPunitorios;
	}
	public void setEdf_modalidadInteresesPunitorios(
			String edf_modalidadInteresesPunitorios) {
		this.edf_modalidadInteresesPunitorios = edf_modalidadInteresesPunitorios;
	}
	public List<Porcentual> getListaPorcentuales() {
		return listaPorcentuales;
	}
	public void setListaPorcentuales(List<Porcentual> listaPorcentuales) {
		this.listaPorcentuales = listaPorcentuales;
	}
	public BigDecimal getEdf_importeFranqueo() {
		return edf_importeFranqueo;
	}
	public void setEdf_importeFranqueo(BigDecimal edf_importeFranqueo) {
		this.edf_importeFranqueo = edf_importeFranqueo;
	}
	public BigDecimal getEdf_importeMultaDeudores() {
		return edf_importeMultaDeudores;
	}
	public void setEdf_importeMultaDeudores(BigDecimal edf_importeMultaDeudores) {
		this.edf_importeMultaDeudores = edf_importeMultaDeudores;
	}
	public String getEdf_lockedBy() {
		return edf_lockedBy;
	}
	public void setEdf_lockedBy(String edf_lockedBy) {
		this.edf_lockedBy = edf_lockedBy;
	}
	public BigDecimal getEdf_importeMasivoE12() {
		return edf_importeMasivoE12;
	}
	public void setEdf_importeMasivoE12(BigDecimal edf_importeMasivoE12) {
		this.edf_importeMasivoE12 = edf_importeMasivoE12;
	}
	public BigDecimal getEdf_importeMasivoE08A() {
		return edf_importeMasivoE08A;
	}
	public void setEdf_importeMasivoE08A(BigDecimal edf_importeMasivoE08A) {
		this.edf_importeMasivoE08A = edf_importeMasivoE08A;
	}
	public Date getEdf_fechaMasivoVTOEX1() {
		return edf_fechaMasivoVTOEX1;
	}
	public void setEdf_fechaMasivoVTOEX1(Date edf_fechaMasivoVTOEX1) {
		this.edf_fechaMasivoVTOEX1 = edf_fechaMasivoVTOEX1;
	}
	public Date getEdf_fechaMasivoVTOEX2() {
		return edf_fechaMasivoVTOEX2;
	}
	public void setEdf_fechaMasivoVTOEX2(Date edf_fechaMasivoVTOEX2) {
		this.edf_fechaMasivoVTOEX2 = edf_fechaMasivoVTOEX2;
	}
	
}
