package ar.com.ada3d.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openntf.domino.Document;

import lotus.domino.NotesException;

import ar.com.ada3d.connect.QueryAS400;
import ar.com.ada3d.model.Porcentual;
import ar.com.ada3d.utilidades.DocLock;
import ar.com.ada3d.utilidades.DocUsr;
import ar.com.ada3d.utilidades.JSFUtil;

public class PorcentualMasivoBean implements Serializable {
	private static final long serialVersionUID = 1L;

	public PorcentualMasivoBean() {
		fillPorcentualAs400();
	}

	public List<Porcentual> listaHonorariosEdificiosTrabajo;
	public boolean isMasivoActualizado = false;
	private String tipoRedondeo;
	
	
	/**
	 * Completa la lista de porcentuales desde el AS400
	 * Carga los valores en: listaHonorariosEdificiosTrabajo
	 * */
	private void fillPorcentualAs400() {
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
		int posicionReal = 1;
		int posicionHonorarios = 5;
		for(int i=1; i<5; i++){ //Son 4 porcentuales por ahora
			tempImporteHonorarios = new BigDecimal(ar.com.ada3d.utilidades.Conversores.stringToStringDecimal(strLinea.split("\\|")[posicionHonorarios].trim(), Locale.UK, 2));
			
			if(tempImporteHonorarios.compareTo(BigDecimal.ZERO) != 0 && !strLinea.split("\\|")[posicionPorcentaje].trim().equals("0")){ //				
				myPorcentual = new Porcentual();
				myPorcentual.setPorc_edf_codigo(strLinea.split("\\|")[0].trim());
				myPorcentual.setPorc_posicion(posicionReal);
				myPorcentual.setPorc_titulo("Honorarios % " + posicionReal);
				myPorcentual.setPorc_porcentaje(Integer.parseInt(strLinea.split("\\|")[posicionPorcentaje].trim()));
				myPorcentual.setPorc_importeHonorarios(tempImporteHonorarios);
				myPorcentual.setPorc_importeHonorariosMasivo(tempImporteHonorarios);
				posicionPorcentaje = posicionPorcentaje + 1;
				posicionHonorarios = posicionHonorarios + 1;
				listaPorcentualesEdificio.add(myPorcentual);
			}
			posicionReal = posicionReal + 1;
		}
		
		return listaPorcentualesEdificio;
	}
	
	/**
	 * En la vista de honorarios masivos actualiza los valores temporalen en pantalla
	 * @param el % que tengo que aplicarle a cada honorario
	 * @return un array de texto con: idComponente con error ~ Mensaje a Mostrar en pantalla
	 * */
	public ArrayList<String> modificoHonorariosMasivos(Object prm_valor){		
		ArrayList<String> listAcumulaErrores = new ArrayList<String>();
		DocLock lock = (DocLock) JSFUtil.resolveVariable("DocLock");
		String strUsuario = JSFUtil.getSession().getEffectiveUserName();
		
		if(prm_valor == null){
			listAcumulaErrores.add("porcentajePorAplicar~Debe ingresar un valor.");	
		}else if(prm_valor instanceof Double || prm_valor instanceof Long){
			BigDecimal maxImporteHonorarios  = new BigDecimal("9999999.99"); //Es el maximo aceptado por AS400 (5 posiciones AS400)
			BigDecimal valor = new BigDecimal(prm_valor.toString());
			BigDecimal maxValor  = new BigDecimal("999.9");
			BigDecimal minValor  = new BigDecimal("-99.9");
			
			if(valor.compareTo(maxValor) == 1){ //Hasta 999
				listAcumulaErrores.add("porcentajePorAplicar~El % de aumento no puede superar el 999,9 %" );
				return listAcumulaErrores;
			}
			if(valor.compareTo(minValor) == -1){ //Hasta -99
				listAcumulaErrores.add("porcentajePorAplicar~El % de disminución no puede ser menor que 99,9 %" );
				return listAcumulaErrores;
			}
			//valor = valor.setScale(1, RoundingMode.HALF_EVEN);//redondeo si puso mas de 1 decimal, ya no puede
			BigDecimal tempCalculo;
			int enteroTipoRedondeo = Integer.parseInt(tipoRedondeo);
			
			for (Porcentual myPorcentual : listaHonorariosEdificiosTrabajo){
				if( (lock.isLocked("edf_" + myPorcentual.getPorc_edf_codigo()) && lock.getLock("edf_" + myPorcentual.getPorc_edf_codigo()).equals(strUsuario) ) || !lock.isLocked("edf_" + myPorcentual.getPorc_edf_codigo())  ){
					tempCalculo = myPorcentual.getPorc_importeHonorarios().multiply(valor).divide(new BigDecimal(100));
					tempCalculo = myPorcentual.getPorc_importeHonorarios().add(tempCalculo); //Hago la suma con todos los decimales
					tempCalculo = tempCalculo.setScale(enteroTipoRedondeo, RoundingMode.HALF_EVEN);//ajusto a los decimales solicitados
					if(tempCalculo.compareTo(maxImporteHonorarios) == 1){ //Maximo permitido por AS400
						listAcumulaErrores.add("porcentajePorAplicar~El valor máximo de honorarios es de 9.999.999,99. El edificio " + myPorcentual.getPorc_edf_codigo() + " como supera ese máximo, se visualiza con el máximo posible.");
						if(myPorcentual.getPorc_importeHonorarios().compareTo(maxImporteHonorarios) != 0){
							myPorcentual.setPorc_importeHonorariosMasivo(maxImporteHonorarios); 
						}
					}else{
						myPorcentual.setPorc_importeHonorariosMasivo(tempCalculo.setScale(2, RoundingMode.HALF_EVEN)); //le agrego los 2 decimales al guardar
						isMasivoActualizado = true;
					}
				}else{
					listAcumulaErrores.add("porcentajePorAplicar~El edificio " + myPorcentual.getPorc_edf_codigo() + " no se pudo actualizar ya que está siendo modificado por: " + lock.getLock("edf_" + myPorcentual.getPorc_edf_codigo()).substring(4) );
				}
			}
		}
		return(listAcumulaErrores);
	}
	
	/**
	 * En la vista de honorarios masivos guarda los valores temporalen en AS400
	 * @return un array de texto con: idComponente con error ~ Mensaje a Mostrar en pantalla
	 * */
	public ArrayList<String> saveMasivoHonorarios(){
		ArrayList<String> listAcumulaErrores = new ArrayList<String>();
		if(isMasivoActualizado){
			
			QueryAS400 query = new QueryAS400();
			Document docDummy = JSFUtil.getDocDummy();
			docDummy.appendItemValue("LISTA_EDIF", "SET");
			
			if (query.updateBatchAS("updateMasivoHonorariosEdificiosBatch", docDummy, listaHonorariosEdificiosTrabajo)) {
				System.out.println("saveMasivoHonorarios --> updateBatch ok");
			}else{
				listAcumulaErrores.add("No se pudo actualizar la tabla PH_E01.");
			}
/*			
			DocLock lock = (DocLock) JSFUtil.resolveVariable("DocLock");
			lock.removeLock("edf_" + prm_edificio.getEdf_codigo());
			
			
			
			DocLock lock = (DocLock) JSFUtil.resolveVariable("DocLock");
			for (Porcentual myPorcentual : listaHonorariosEdificiosTrabajo){
				
			}
	*/		
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
