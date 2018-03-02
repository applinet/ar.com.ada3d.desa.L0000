package ar.com.ada3d.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lotus.domino.NotesException;

import ar.com.ada3d.connect.QueryAS400;
import ar.com.ada3d.model.Porcentual;

public class PorcentualMasivoBean implements Serializable {
	private static final long serialVersionUID = 1L;

	public PorcentualMasivoBean() {
		AddPorcentualAs400();
	}

	public List<Porcentual> listaHonorariosEdificiosTrabajo;
	
	
	//
	private void AddPorcentualAs400() {
		if (!(listaHonorariosEdificiosTrabajo == null)){
			listaHonorariosEdificiosTrabajo.clear();			
		}	
		QueryAS400 query = new ar.com.ada3d.connect.QueryAS400();
		ArrayList<String> nl = null;
		Porcentual myPorcentual = null;
		
		try {
			nl = query.getSelectAS("controllerPorcentualMasivo", null, false);
		} catch (NotesException e) {
			e.printStackTrace();
		}
		for (String strLinea : nl) {
			if (listaHonorariosEdificiosTrabajo == null) {
				listaHonorariosEdificiosTrabajo = new ArrayList<Porcentual>();
			}
			
					
			listaHonorariosEdificiosTrabajo.addAll((List<Porcentual>) actualizoUnEdificioPorcentualAs400( myPorcentual, strLinea));
			
			
		}
		/*
		 * for(Porcentual myPorcentual2 : listaHonorariosEdificiosTrabajo){
				if(!docUsuario.getEdificiosNoAccessLista().contains(myPorcentual2.getPorc_edf_codigo())){
					listaHonorariosEdificiosTrabajo.remove(myPorcentual2);
				}
			}
		 * */
	}
	
	
	private List<Porcentual> actualizoUnEdificioPorcentualAs400(Porcentual myPorcentual, String strLinea) {
		List<Porcentual> listaPorcentualesEdificio = new ArrayList<Porcentual>();
		
		int posicionPorcentaje = 1;
		int posicionHonorarios = 5;
		for(int i=1; i<5; i++){ //Son 4 porcentuales por ahora
			myPorcentual = new Porcentual();
			myPorcentual.setPorc_edf_codigo(strLinea.split("\\|")[0].trim());
			myPorcentual.setPorc_posicion(i);
			myPorcentual.setPorc_titulo("Honorarios % " + i);
			myPorcentual.setPorc_porcentaje(Integer.parseInt(strLinea.split("\\|")[posicionPorcentaje].trim()));
			myPorcentual.setPorc_importeHonorarios(new BigDecimal(ar.com.ada3d.utilidades.Conversores.stringToStringDecimal(strLinea.split("\\|")[posicionHonorarios].trim(), Locale.UK, 2)));
			posicionPorcentaje = posicionPorcentaje + 1;
			posicionHonorarios = posicionHonorarios + 1;
			listaPorcentualesEdificio.add(myPorcentual);
		}
		
		return listaPorcentualesEdificio;
	}
	
	//*** Getters & Setters *****

	public List<Porcentual> getListaHonorariosEdificiosTrabajo() {
		return listaHonorariosEdificiosTrabajo;
	}

	public void setListaHonorariosEdificiosTrabajo(
			List<Porcentual> listaHonorariosEdificiosTrabajo) {
		this.listaHonorariosEdificiosTrabajo = listaHonorariosEdificiosTrabajo;
	}
	


	
	
	
}
