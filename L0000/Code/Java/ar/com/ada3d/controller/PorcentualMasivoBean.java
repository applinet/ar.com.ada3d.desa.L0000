package ar.com.ada3d.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lotus.domino.NotesException;

import ar.com.ada3d.connect.QueryAS400;
import ar.com.ada3d.model.Porcentual;
import ar.com.ada3d.utilidades.DocLock;
import ar.com.ada3d.utilidades.DocUsr;
import ar.com.ada3d.utilidades.JSFUtil;

public class PorcentualMasivoBean implements Serializable {
	private static final long serialVersionUID = 1L;

	public PorcentualMasivoBean() {
		AddPorcentualAs400();
	}

	public List<Porcentual> listaHonorariosEdificiosTrabajo;
	public boolean isMasivoActualizado = false;
	private String tipoRedondeo;
	
	
	//Agregar tomando del AS400
	private void AddPorcentualAs400() {
		DocUsr docUsuario = (DocUsr) JSFUtil.resolveVariable("DocUsr");
		ArrayList<String> tempEdificiosSinAcceso = docUsuario.getEdificiosNoAccessLista();
		
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
			
			//Solo voy a cargar los que tengo autorizados
			if (tempEdificiosSinAcceso.isEmpty()){
				//Agrego el grupo de porcentuales
				listaHonorariosEdificiosTrabajo.addAll((List<Porcentual>) actualizoUnEdificioPorcentualAs400( myPorcentual, strLinea));
			}else if(!tempEdificiosSinAcceso.contains(strLinea.split("\\|")[0].trim())){
				//Agrego el grupo de porcentuales
				listaHonorariosEdificiosTrabajo.addAll((List<Porcentual>) actualizoUnEdificioPorcentualAs400( myPorcentual, strLinea));
			}
			
			
		}
	}
	
	
	/**
	 * Actualizo solo el porcentual recibido
	 * @usedIn: Es una clase privada solo se usa aca
	 * @Param objeto Porcentual por actualizar
	 * @Param strLinea leida del AS, solo con datos si es un nuevo el objeto. (blanco si es una actualización)
	 * @return lista de objetos Porcentuales
	 */
	private List<Porcentual> actualizoUnEdificioPorcentualAs400(Porcentual myPorcentual, String strLinea) {
		List<Porcentual> listaPorcentualesEdificio = new ArrayList<Porcentual>();
		BigDecimal tempImporteHonorarios; //Para definir antes si es cero no va
		int posicionPorcentaje = 1;
		int posicionHonorarios = 5;
		for(int i=1; i<5; i++){ //Son 4 porcentuales por ahora
			tempImporteHonorarios = new BigDecimal(ar.com.ada3d.utilidades.Conversores.stringToStringDecimal(strLinea.split("\\|")[posicionHonorarios].trim(), Locale.UK, 2));
			
			if(tempImporteHonorarios.compareTo(BigDecimal.ZERO) != 0 && !strLinea.split("\\|")[posicionPorcentaje].trim().equals("0")){ //				
				myPorcentual = new Porcentual();
				myPorcentual.setPorc_edf_codigo(strLinea.split("\\|")[0].trim());
				myPorcentual.setPorc_posicion(i);
				myPorcentual.setPorc_titulo("Honorarios % " + i);
				myPorcentual.setPorc_porcentaje(Integer.parseInt(strLinea.split("\\|")[posicionPorcentaje].trim()));
				myPorcentual.setPorc_importeHonorarios(tempImporteHonorarios);
				myPorcentual.setPorc_importeHonorariosMasivo(tempImporteHonorarios);
				posicionPorcentaje = posicionPorcentaje + 1;
				posicionHonorarios = posicionHonorarios + 1;
				listaPorcentualesEdificio.add(myPorcentual);
			}
		}
		
		return listaPorcentualesEdificio;
	}
	
	public ArrayList<String> modificoHonorariosMasivos(Object prm_valor){		
		ArrayList<String> listAcumulaErrores = new ArrayList<String>();
		DocLock lock = (DocLock) JSFUtil.resolveVariable("DocLock");
		String strUsuario = JSFUtil.getSession().getEffectiveUserName();
		
		if(prm_valor == null){
			listAcumulaErrores.add("porcentajePorAplicar~Debe ingresar un valor.");	
		}else if(prm_valor instanceof Double || prm_valor instanceof Long){
			
			BigDecimal valor = new BigDecimal(prm_valor.toString());
			if(valor.compareTo(new BigDecimal(9999)) == 1){ //Hasta 9999
				listAcumulaErrores.add("porcentajePorAplicar~El % de aumento no puede superar el 9999 %" );
				return listAcumulaErrores;
			}
			if(valor.compareTo(new BigDecimal(-99)) == -1){ //Hasta -99
				listAcumulaErrores.add("porcentajePorAplicar~El % de disminución no puede ser menor que 99 %" );
				return listAcumulaErrores;
			}
			//valor = valor.setScale(1, RoundingMode.HALF_EVEN);//redondeo si puso mas de 1 decimal
			BigDecimal tempCalculo;
			int enteroTipoRedondeo = Integer.parseInt(tipoRedondeo);
			
			for (Porcentual myPorcentual : listaHonorariosEdificiosTrabajo){
				if( (lock.isLocked("edf_" + myPorcentual.getPorc_edf_codigo()) && lock.getLock("edf_" + myPorcentual.getPorc_edf_codigo()).equals(strUsuario) ) || !lock.isLocked("edf_" + myPorcentual.getPorc_edf_codigo())  ){
					tempCalculo = myPorcentual.getPorc_importeHonorarios().multiply(valor).divide(new BigDecimal(100));
					tempCalculo = tempCalculo.setScale(enteroTipoRedondeo, RoundingMode.HALF_EVEN);//ajusto a 2 decimales
					
					myPorcentual.setPorc_importeHonorariosMasivo(myPorcentual.getPorc_importeHonorarios().add(tempCalculo));
					isMasivoActualizado = true;
				}else{
					listAcumulaErrores.add("porcentajePorAplicar~El edificio " + myPorcentual.getPorc_edf_codigo() + " no se pudo actualizar ya que está siendo modificado por: " + lock.getLock("edf_" + myPorcentual.getPorc_edf_codigo()).substring(4) );
				}
			}
		}
		return(listAcumulaErrores);
	}
	
	public ArrayList<String> saveMasivoHonorarios(){
		ArrayList<String> listAcumulaErrores = new ArrayList<String>();
		String strSQL;
		if(isMasivoActualizado){
			
			QueryAS400 query = new QueryAS400();
			
			if (query.updateBatchAS("updateEdificiosPH_E01", null)) {
				System.out.println("update ok");
			}else{
				throw new java.lang.Error("No se pudo actualizar la tabla PH_E01.");
			}
/*			
			DocLock lock = (DocLock) JSFUtil.resolveVariable("DocLock");
			lock.removeLock("edf_" + prm_edificio.getEdf_codigo());
			
			
			
			DocLock lock = (DocLock) JSFUtil.resolveVariable("DocLock");
			for (Porcentual myPorcentual : listaHonorariosEdificiosTrabajo){
				
			}
	*/		
			listAcumulaErrores.add("porcentajePorAplicar~Gabando.");
		}
		return listAcumulaErrores;
	}
	
	//*** Getters & Setters *****

	public List<Porcentual> getListaHonorariosEdificiosTrabajo() {
		return listaHonorariosEdificiosTrabajo;
	}

	public void setListaHonorariosEdificiosTrabajo(
			List<Porcentual> listaHonorariosEdificiosTrabajo) {
		this.listaHonorariosEdificiosTrabajo = listaHonorariosEdificiosTrabajo;
	}
	
	public boolean getIsMasivoActualizado() {
		return isMasivoActualizado;
	}


	public String getTipoRedondeo() {
		return tipoRedondeo;
	}


	public void setTipoRedondeo(String tipoRedondeo) {
		this.tipoRedondeo = tipoRedondeo;
	}
	
}
