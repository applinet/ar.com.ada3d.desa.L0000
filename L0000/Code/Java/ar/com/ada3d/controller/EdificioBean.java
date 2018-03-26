package ar.com.ada3d.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.math.*;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.io.Serializable;
import javax.faces.model.SelectItem;

import org.openntf.domino.Document;
import org.openntf.domino.Session;


import ar.com.ada3d.model.Edificio;
import ar.com.ada3d.model.Porcentual;
import ar.com.ada3d.model.Prorrateo;
import ar.com.ada3d.utilidades.*;
import lotus.domino.NotesException;
import ar.com.ada3d.connect.QueryAS400;
import ar.com.ada3d.utilidades.DocUsr;
import java.math.BigDecimal;
import java.util.*;

/*
 * Cuando hablo de edificios son todos los de la administracion.
 * Cuando hablo de MyEdificios son los edificios que puede ver el usuario.
 * Cuando hablo de MyEdificiosTrabajo son para trabajar el usuario es decir que no estan bloqueados
 * Tomo la scope de usuario para los edificios autorizados por usuario
 * */
public class EdificioBean implements Serializable {

	public EdificioBean() {
		//System.out.println("FPR - Constr. Edificios y llamada AS400");
		AddEdificiosAs400();
	}

	private Edificio edificio;
	private static final long serialVersionUID = 1L;
	HashMap<String, Edificio> hmEdificios = new HashMap<String, Edificio>();
	private static List<Edificio> listaEdificios;
	private static List<Edificio> listaEdificiosTrabajo;
	private boolean isMasivoActualizado = false;
	@SuppressWarnings("unused")
	private int cantidadTotalEdificiosTrabajo;

	
	/**
	 * Esto devuelve para cada usuario el ComboBox de Edificios autorizados para
	 * trabajar
	 * @return: etiqueta y valor para xp:comboBox
	 * @usedIn: Combo principal en ccLayoutBootstrap, está asociado a una
	 * sessionScope(edificioSelected)
	 */
	public List<SelectItem> getComboboxMyEdificios() {
		DocUsr docUsuario = (DocUsr) JSFUtil.resolveVariable("DocUsr");
		List<SelectItem> options = new ArrayList<SelectItem>();
		for (Edificio miEdificio : listaEdificios) {
			
			if (!docUsuario.getEdificiosNoAccessLista().contains(
					miEdificio.getEdf_codigo())) { // Solo edificios autorizados
				SelectItem option = new SelectItem();
				
				option
						.setLabel(miEdificio.getEdf_codigoVisual().equals("") ? miEdificio
								.getEdf_codigo()
								: miEdificio.getEdf_codigoVisual() + " "
										+ miEdificio.getEdf_direccion() + " "
										+ ar.com.ada3d.utilidades.Conversores.DateToString(miEdificio.getEdf_fechaUltimaLiquidacion(), "dd/MM/yyyy" ));
				option.setValue(miEdificio.getEdf_codigo());
				options.add(option);
			}
		}
		return options;
	}

	/**
	 * Esto devuelve para cada usuario el ComboBox de Edificios autorizados para
	 * trabajar y en estadoProceso=1
	 * @return etiqueta y valor para xp:comboBox
	 * @usedIn layout tiene una propiedad para mostrar o no en cada XPage
	 */
	public static List<SelectItem> getComboboxMyEdificiosTrabajo() {
		DocUsr docUsuario = (DocUsr) JSFUtil.resolveVariable("DocUsr");
		List<SelectItem> options = new ArrayList<SelectItem>();
		for (Edificio miEdificio : listaEdificios) {
			if (miEdificio.getEdf_estadoProceso().equals("1")) { // solo
																	// estado=1
				if (!docUsuario.getEdificiosNoAccessLista().contains(
						miEdificio.getEdf_codigo())) { // Solo edificios
														// autorizados
					SelectItem option = new SelectItem();
					option.setLabel(miEdificio.getEdf_direccion());
					option.setValue(miEdificio.getEdf_codigo());
					options.add(option);
				}
			}
		}
		return options;
	}

	/**
	 * Devuelve un string con la lista de edificios
	 *  @param verdadero devuelve solo los que trabaja el usuario, sino todos
	 *  @return un string con la lista de edificios
	 *  @usedin frmConfigUsr para traer la lista de edificios autorizados para el usuario
	 *  @usedin typeAhead de edificios
	 * */
		
	public ArrayList<String> getArraySrtringListaEdificios(boolean prm_isDeTrabajo) {
		ArrayList<String> result = new ArrayList<String>();
		List<Edificio> lista = prm_isDeTrabajo ? listaEdificiosTrabajo : listaEdificios;
		for (Edificio miEdificio : lista) {
			result.add(miEdificio.getEdf_direccion() + "|"
					+ miEdificio.getEdf_codigo());
		}
		return result;
	}

	/**
	 * Agrego edificios de a uno al hashMap
	 * @param objeto Edificio
	 */
	private void AddEdificioMap(Edificio prmEdificio) {
		hmEdificios.put(prmEdificio.getEdf_codigo(), prmEdificio);
	}

	/**
	 * Obtengo un edificio por el codigo SASA
	 * @param texto con el codigo de edificio
	 * @return un objeto Edificio o nulo
	 */
	public Edificio getEdificioMap(String prmCodigoEdificio) {
		if (hmEdificios.containsKey(prmCodigoEdificio)) {
			return hmEdificios.get(prmCodigoEdificio);
		}
		return null;
	}

	/**
	 * Agrego Edificios consultando As400, cada linea separa el dato por un pipe
	 */
	private void AddEdificiosAs400() {
		DocUsr docUsuario = (DocUsr) JSFUtil.resolveVariable("DocUsr");
		ArrayList<String> tempEdificiosSinAcceso = docUsuario.getEdificiosNoAccessLista();		
		if (!(listaEdificios == null)){
			listaEdificios.clear();
			listaEdificiosTrabajo.clear();
			hmEdificios.clear();
			
		}	
		QueryAS400 query = new ar.com.ada3d.connect.QueryAS400();
		ArrayList<String> nl = null;
		Edificio myEdificio = null;
		
		try {
			nl = query.getSelectAS("controllerEdificios", null, false);
		} catch (NotesException e) {
			e.printStackTrace();
		}
		for (String strLinea : nl) {
			if (listaEdificios == null) {
				listaEdificios = new ArrayList<Edificio>();
			}
			if (listaEdificiosTrabajo == null) {
				listaEdificiosTrabajo = new ArrayList<Edificio>();
			}
			myEdificio = actualizoUnEdificioAs400( myEdificio, strLinea);
			
			listaEdificios.add(myEdificio);
			if (tempEdificiosSinAcceso.isEmpty()){
				listaEdificiosTrabajo.add(myEdificio);				
			}else if(!tempEdificiosSinAcceso.contains(strLinea.split("\\|")[0].trim())){
				listaEdificiosTrabajo.add(myEdificio);
			}
			AddEdificioMap(myEdificio); // Lo agrego al mapa por código
		}
	}
	
	
	private List<SelectItem> opcionesDiaCuotaFija(Calendar cal){
		List<SelectItem> options = new ArrayList<SelectItem>();
	    SelectItem optionMesLiquedacion = new SelectItem();
	    optionMesLiquedacion.setLabel(ar.com.ada3d.utilidades.Conversores.DateToString(cal.getTime(), "dd/MM/yyyy"));
	    optionMesLiquedacion.setValue("N");
	    options.add(optionMesLiquedacion);
	    
	    cal.add(Calendar.DATE, 1);
	    SelectItem optionMesSiguiente = new SelectItem();
	    optionMesSiguiente.setLabel(ar.com.ada3d.utilidades.Conversores.DateToString(cal.getTime(), "dd/MM/yyyy"));
	    optionMesSiguiente.setValue("B");
	    options.add(optionMesSiguiente);
	    return options;

	}
	
	/**
	 * Al cargar un edificio en la misma consulta al AS400 tambien cargo los datos de prorrateo
	 * @param strLinea de AS400 que estoy procesando
	 * @param tipo de prorrateo del edificio
	 * @return la lista de prorrateos
	 */
	private List<Prorrateo> cargaProrrateoEdificio(String strLinea, String strTipo){
		Prorrateo myProrrateo;
		List<Prorrateo> listaProrrateosEdificio = new ArrayList<Prorrateo>();
		int posicionPorcentaje = 5;
		int posicionCuotaFija = 9;
		int tempPorcentaje = 0;
		int tempPosicionEnGrilla = 0;
		String strValorCuotaFija;
		
		for(int i=1; i<5; i++){ //Son 4 porcentuales por ahora
			//variables que recorren 4 valores a prorratear
			posicionPorcentaje = posicionPorcentaje + 1;
			posicionCuotaFija = posicionCuotaFija + 1;
			
			//Define si se crea o no el objeto Prorrateo
			tempPorcentaje = Integer.parseInt(strLinea.split("\\|")[posicionPorcentaje].trim()); 
			if(tempPorcentaje != 0){
				myProrrateo = new Prorrateo();
				myProrrateo.setPrt_posicion(i);
				myProrrateo.setPrt_posicionEnGrilla(tempPosicionEnGrilla);
				tempPosicionEnGrilla = tempPosicionEnGrilla + 1;
				myProrrateo.setPrt_titulo("Porcentual # " + i);
				myProrrateo.setPrt_porcentaje(tempPorcentaje);
				
				//valor que va a tener la cuota fija o el presupuesto
				strValorCuotaFija = strLinea.split("\\|")[posicionCuotaFija].trim();

				//Defino el tipo individual de prorrateo, pero si el valor es 0 es gasto
				
				if (strValorCuotaFija.equals("0")){
					myProrrateo.setPrt_tipo("G");
				}else if (strTipo.equals("P")){
					myProrrateo.setPrt_tipo("P");
					myProrrateo.setPrt_importe(new BigDecimal(ar.com.ada3d.utilidades.Conversores.stringToStringDecimal(strValorCuotaFija, Locale.UK, 2)));
				}else if(strTipo.equals("C")){
					myProrrateo.setPrt_tipo("C");
					myProrrateo.setPrt_importe(new BigDecimal(ar.com.ada3d.utilidades.Conversores.stringToStringDecimal(strValorCuotaFija, Locale.UK, 2)));
				}else{
					myProrrateo.setPrt_tipo("G");
				}
				listaProrrateosEdificio.add(myProrrateo);
			}
		}
		return listaProrrateosEdificio;
	}
	
	/**
	 * Al cargar un edificio en la misma consulta al AS400 tambien cargo los datos de porcentuales
	 * @param strLinea de AS400 que estoy procesando
	 * @return lista con porcentuales
	 */
	
	private List<Porcentual> cargaPorcentualEdificio(String strLinea){
		Porcentual myPorcentual;
		List<Porcentual> listaPorcentualesEdificio = new ArrayList<Porcentual>();
		int posicionPorcentaje = 6;
		int posicionHonorarios = 22;
		int posicionEnGrilla = 0;
		for(int i=1; i<5; i++){ //Son 4 porcentuales por ahora
			myPorcentual = new Porcentual();
			myPorcentual.setPorc_posicion(i);
			myPorcentual.setPorc_titulo("Honorarios % " + i);
			myPorcentual.setPorc_porcentaje(Integer.parseInt(strLinea.split("\\|")[posicionPorcentaje].trim()));
			myPorcentual.setPorc_importeHonorarios(new BigDecimal(ar.com.ada3d.utilidades.Conversores.stringToStringDecimal(strLinea.split("\\|")[posicionHonorarios].trim(), Locale.UK, 2)));
			posicionPorcentaje = posicionPorcentaje + 1;
			posicionHonorarios = posicionHonorarios + 1;
			posicionEnGrilla = posicionEnGrilla + 1;
			listaPorcentualesEdificio.add(myPorcentual);
		}
		return listaPorcentualesEdificio;
		
	}
	
	/**
	 * Prorrateo: Oculta o visualiza otros campos al cambiar la opción
	 * @param El edificio que estoy modificando
	 * @param La posición que estoy modificando en la XPage (es un repeat)
	 * @usedIn: en el combo de valores a prorratear
	 */
	public String onChangeProrrateos(Edificio prm_edificio, Integer prm_idxRptProrrateoTipo){
		BigDecimal tempSumatoriaImportes = new BigDecimal(0);
		ArrayList<String> tempArrayCuotaFijaDia = new ArrayList<String>();
		String tempCuotaFijaDia = "";
		for(Prorrateo myProrrateo : prm_edificio.getListaProrrateos()){
			tempArrayCuotaFijaDia.add(myProrrateo.getPrt_tipo());
			if (prm_idxRptProrrateoTipo.equals(myProrrateo.getPrt_posicionEnGrilla())){
				//Solo modifico la linea del repeat que ha cambiado
				if (myProrrateo.getPrt_tipo().equals("G")){
					myProrrateo.setPrt_importe(null);
					
				}else{//CUOTA FIJA o Presupuesto
					if(myProrrateo.getPrt_importe() == null){
						myProrrateo.setPrt_importe(new BigDecimal(1));						
					}
					if (myProrrateo.getPrt_tipo().equals("C")){	//CF cargo el combo
						prm_edificio.setEdf_cuotaFijaDiaOpcionesCombo(opcionesDiaCuotaFija(ar.com.ada3d.utilidades.Conversores.dateToCalendar(prm_edificio.getEdf_fechaProximaLiquidacion())));
						tempCuotaFijaDia = "B";
					}else{
						tempCuotaFijaDia = "P";
					}
				}
			}
			
			//Hago una sumatoria si da cero tengo que enviar un *
			if(myProrrateo.getPrt_tipo().equals("G")){
				tempSumatoriaImportes = tempSumatoriaImportes.add(new BigDecimal(0));
			}else{
				tempSumatoriaImportes = tempSumatoriaImportes.add(myProrrateo.getPrt_importe());
			}
		}
		
		//Al final chequeo todo antes de confirmar la cuotaFijaDia
		if(tempSumatoriaImportes.equals(new BigDecimal(0))){
			//Son todos gastos
			prm_edificio.setEdf_cuotaFijaDia("*");
		}else{
			if(tempCuotaFijaDia.equals("P")){//Chequeo si cambio realmente a P
				if(tempArrayCuotaFijaDia.contains("C") || tempArrayCuotaFijaDia.contains("B")){
					return "prt_tipo~No puede seleccionar PRESUPUESTO si existe una CUOTA FIJA.";
				}else{
					prm_edificio.setEdf_cuotaFijaDia("P");
				}
			}else if(tempCuotaFijaDia.equals("C") || tempCuotaFijaDia.equals("B")){//Chequeo si cambio realmente a C
				if(tempArrayCuotaFijaDia.contains("P")){
					return "prt_tipo~No puede seleccionar CUOTA FIJA si existe un PRESUPUESTO .";
				}else{
					prm_edificio.setEdf_cuotaFijaDia(tempCuotaFijaDia);
				}
			}else if(tempCuotaFijaDia.equals("*")){//Chequeo si cambio realmente a *
				if(tempArrayCuotaFijaDia.contains("P") || tempArrayCuotaFijaDia.contains("C") || tempArrayCuotaFijaDia.contains("B")){
					prm_edificio.setEdf_cuotaFijaDia(tempCuotaFijaDia);
				}
			}
			
		}
		return "";
	}
	
	/**
	 * Modifica los siguientes datos:
	 * -Fecha próxima liquidacion
	 * -Mes de prorrateo y recibos (solo si es cuota fija)
	 * -Fecha 1er vto en recibos
	 * -Fecha 2do vto en recibos
	 * @param El edificio que estoy modificando
	 * @usedIn Slider de frecuencia
	 */
	@SuppressWarnings("static-access")
	public void onClickFrecuencia(Edificio prm_edificio){			
		Calendar cal = Calendar.getInstance();
		cal.setTime(prm_edificio.getEdf_fechaUltimaLiquidacion());
		cal.add(Calendar.MONTH, prm_edificio.getEdf_frecuenciaLiquidacion());
		cal.set(cal.DATE, cal.getActualMaximum(cal.DAY_OF_MONTH));
		prm_edificio.setEdf_fechaProximaLiquidacion(cal.getTime());
		if(prm_edificio.getEdf_cuotaFijaDia().equals("N") || prm_edificio.getEdf_cuotaFijaDia().equals("B")){
			prm_edificio.setEdf_cuotaFijaDiaOpcionesCombo(opcionesDiaCuotaFija(ar.com.ada3d.utilidades.Conversores.dateToCalendar(prm_edificio.getEdf_fechaProximaLiquidacion())));
		}
		
		
		/*
		cal.setTime(sessionScope.edfObj.edf_fechaPrimerVencimientoRecibos)
		cal.add(java.util.Calendar.MONTH, frecuencia);
		sessionScope.edfObj.edf_fechaPrimerVencimientoRecibos = cal.getTime();
		*/
		
	}

	/**
	 * Actualizo solo el edificio recibido
	 * @usedIn: Es una clase privada solo se usa aca
	 * @Param objeto Edificio por actualizar
	 * @Param strLinea leida del AS, solo con datos si es un nuevo el objeto. (blanco si es una actualización)
	 * @return objeto Edificio
	 */
	@SuppressWarnings("static-access")
	private Edificio actualizoUnEdificioAs400(Edificio myEdificio, String strLinea) {
		DocLock lockeos = (DocLock) JSFUtil.resolveVariable("DocLock");
		if(!strLinea.equals("")){
			myEdificio = new Edificio();
			myEdificio.setEdf_codigo(strLinea.split("\\|")[0].trim());
		}else{
			//Es una actualizacion
			Document docDummy = JSFUtil.getDocDummy();
			docDummy.appendItemValue("Codigo", myEdificio.getEdf_codigo());
			QueryAS400 query = new ar.com.ada3d.connect.QueryAS400();
			ArrayList<String> nl = null;
			try {
				nl = query.getSelectAS("controllerUnEdificio", docDummy, false);
			} catch (NotesException e) {
				e.printStackTrace();
			}
			
			strLinea = nl.get(0);
				
		}
		//Agrego el usuario si el edificio está lockeado
		if(lockeos.isLocked("edf_" + myEdificio.getEdf_codigo()))
			myEdificio.setEdf_lockedBy(lockeos.getLock("edf_" + myEdificio.getEdf_codigo()));

		myEdificio.setEdf_codigoVisual(strLinea.split("\\|")[1].trim());
		String tempDireccionLocalidad = strLinea.split("\\|")[2].trim();
		if(!tempDireccionLocalidad.contains("-")){
			throw new java.lang.Error("La direccion del edificio " + myEdificio.getEdf_codigo() + " no tiene un guión.");
			//throw new java.lang.RuntimeException("La direccion del edificio " + myEdificio.getEdf_codigo() + " no tiene un guión.");
		}
		myEdificio.setEdf_direccion(strLinea.split("\\|")[2].trim().split("-")[0]);
		myEdificio.setEdf_localidad(strLinea.split("\\|")[2].trim().split("-")[1]);
		myEdificio.setEdf_estadoProceso(strLinea.split("\\|")[3].trim());
		myEdificio.setEdf_fechaUltimaLiquidacion(ar.com.ada3d.utilidades.Conversores.StringToDate("ddMMyy", strLinea.split("\\|")[4].trim()));
		myEdificio.setEdf_frecuenciaLiquidacion  (Integer.parseInt(strLinea.split("\\|")[5].trim()));
								
		Calendar cal = Calendar.getInstance();
		cal.setTime(myEdificio.getEdf_fechaUltimaLiquidacion());
		cal.add(Calendar.MONTH, myEdificio.getEdf_frecuenciaLiquidacion());
		cal.set(cal.DATE, cal.getActualMaximum(cal.DAY_OF_MONTH));
		myEdificio.setEdf_fechaProximaLiquidacion(cal.getTime());

		
		//Defino el tipo de prorrateo			
		String strTipo = strLinea.split("\\|")[14].trim();
		String strTempTipo;
		if(strTipo.equals("") || strTipo.equals("N")){
		    strTempTipo = "C";
		}else if (strTipo.equals("P")){//Presupuesto
			strTempTipo = "P";	
		}else{
			strTempTipo = "G";	
		}	
		myEdificio.setListaProrrateos(cargaProrrateoEdificio(strLinea, strTempTipo));
		//Creo el combo de fecha prorrateo si es cuota fija
		if(strTipo.equals("") || strTipo.equals("N")){ //blanco puede ser por gastos
			for(Prorrateo myProrrateo : myEdificio.getListaProrrateos()){
				if(myProrrateo.getPrt_tipo().equals("C")){
					
					List<SelectItem> options = new ArrayList<SelectItem>();
				    SelectItem optionMesLiquedacion = new SelectItem();
				    optionMesLiquedacion.setLabel(ar.com.ada3d.utilidades.Conversores.DateToString(cal.getTime(), "dd/MM/yyyy"));
				    optionMesLiquedacion.setValue("N");
				    options.add(optionMesLiquedacion);

				    if(strTipo.equals("")){//CF y fecha prorrateo le agrego 1 dia para que sea el dia 1 del mes siguiente
				    	cal.add(Calendar.DATE, 1);
						myEdificio.setEdf_cuotaFijaDia("B");
				    }else{//CF y fecha prorrateo ídem liquidación
				    	myEdificio.setEdf_cuotaFijaDia("N");
				    	cal.add(Calendar.DATE, 1);
				    }
				    
				    SelectItem optionMesSiguiente = new SelectItem();
				    optionMesSiguiente.setLabel(ar.com.ada3d.utilidades.Conversores.DateToString(cal.getTime(), "dd/MM/yyyy"));
				    optionMesSiguiente.setValue("B");
				    options.add(optionMesSiguiente);
				    myEdificio.setEdf_cuotaFijaDiaOpcionesCombo(options);
					break;
				}
			}
		}
		
		myEdificio.setEdf_interesPunitorioDeudores( new BigDecimal(ar.com.ada3d.utilidades.Conversores.stringToStringDecimal(strLinea.split("\\|")[15].trim(), Locale.US, 1)));
		myEdificio.setEdf_interesRecargoSegundoVencimiento( new BigDecimal(ar.com.ada3d.utilidades.Conversores.stringToStringDecimal(strLinea.split("\\|")[16].trim(), Locale.UK, 1)));

		if(!strLinea.split("\\|")[17].trim().equals("0")) //la fecha si es nula viene un cero
			myEdificio.setEdf_fechaPrimerVencimientoRecibos(ar.com.ada3d.utilidades.Conversores.StringToDate("ddMMyy", strLinea.split("\\|")[17].trim()));
		if(!strLinea.split("\\|")[18].trim().equals("0")) //la fecha si es nula viene un cero
			myEdificio.setEdf_fechaSegundoVencimientoRecibos(ar.com.ada3d.utilidades.Conversores.StringToDate("ddMMyy", strLinea.split("\\|")[18].trim()));
		
		myEdificio.setEdf_modalidadInteresesPunitorios(strLinea.split("\\|")[19].trim());
		myEdificio.setEdf_cuit(strLinea.split("\\|")[20].trim());
		myEdificio.setEdf_imprimeTitulosEnLiquidacion(strLinea.split("\\|")[21].trim().equals("1") ? "1":"0");
		
		//Voy a cargar todos los porcentuales, los utilizados y los no utilizados
		myEdificio.setListaPorcentuales(cargaPorcentualEdificio(strLinea));
		
		//TODO: falta saber que campo tomar del AS400
		myEdificio.setEdf_importeFranqueo( new BigDecimal(ar.com.ada3d.utilidades.Conversores.stringToStringDecimal(strLinea.split("\\|")[15].trim(), Locale.US, 2)));
		myEdificio.setEdf_importeMultaDeudores( new BigDecimal(ar.com.ada3d.utilidades.Conversores.stringToStringDecimal(strLinea.split("\\|")[15].trim(), Locale.US, 2)));
		
		myEdificio.setEdf_isReadMode(true);
		return myEdificio;
	}


	/**
	 * Funcion en el boton editar
	 * Actualizo solo el edificio que voy a modificar y lockea el edificio
	 * @usedIn: Boton edit edificio
	 */
	public void editEdificio(Edificio prm_edificio) {
		actualizoUnEdificioAs400(prm_edificio, "");
		Session session = JSFUtil.getSession();
		DocLock lock = (DocLock) JSFUtil.resolveVariable("DocLock");
		if (lock.isLocked("edf_" + prm_edificio.getEdf_codigo())){
			if (!lock.getLock("edf_" + prm_edificio.getEdf_codigo()).equals(session.getEffectiveUserName()))
				return;
		}
		lock.addLock("edf_" + prm_edificio.getEdf_codigo(), session.getEffectiveUserName());
		prm_edificio.setEdf_isReadMode(false);
		prm_edificio.setEdf_lockedBy(session.getEffectiveUserName());
		
	}
	
	/**
	 * Update de tablas AS400 con los datos del edificio
	 */
	public void saveEdificio(Edificio prm_edificio) {
		Document docDummy = JSFUtil.getDocDummy();
		docDummy.appendItemValue("Codigo", prm_edificio.getEdf_codigo());
		docDummy.appendItemValue("DIRECC", prm_edificio.getEdf_direccion() + "-" + prm_edificio.getEdf_localidad());
		docDummy.appendItemValue("CodigoVisual", prm_edificio.getEdf_codigoVisual());
		docDummy.appendItemValue("frecuencia", prm_edificio.getEdf_frecuenciaLiquidacion());
		docDummy.appendItemValue("imprimirTitulos", prm_edificio.getEdf_imprimeTitulosEnLiquidacion());
		docDummy.appendItemValue("CTFJ1", "0");
		docDummy.appendItemValue("CTFJ2", "0");
		docDummy.appendItemValue("CTFJ3", "0");
		docDummy.appendItemValue("CTFJ4", "0");
		docDummy.appendItemValue("E12", ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(prm_edificio.getEdf_interesPunitorioDeudores(),1));
		docDummy.appendItemValue("E08A", ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(prm_edificio.getEdf_interesRecargoSegundoVencimiento(),1));
		docDummy.appendItemValue("VTOEX1", ar.com.ada3d.utilidades.Conversores.DateToString(prm_edificio.getEdf_fechaPrimerVencimientoRecibos(), "ddMMyy"));
		if(prm_edificio.getEdf_fechaSegundoVencimientoRecibos() == null){
			docDummy.appendItemValue("VTOEX2", "0");
		}else{
			docDummy.appendItemValue("VTOEX2", ar.com.ada3d.utilidades.Conversores.DateToString(prm_edificio.getEdf_fechaSegundoVencimientoRecibos(), "ddMMyy"));
		}
		
		
		//Recorro los prorrateos para cargar Cuota Fija o Presupuesto
		for(Prorrateo myProrrateo: prm_edificio.getListaProrrateos()){
			if (myProrrateo.getPrt_importe() != null){
				docDummy.replaceItemValue("CTFJ" + myProrrateo.getPrt_posicion(), ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(myProrrateo.getPrt_importe(), 2));
			}
		}
		//Estado blanco de AS400 yo lo tengo como B. Se reemplaza
		if(prm_edificio.getEdf_cuotaFijaDia() == null){
			docDummy.appendItemValue("ESTADO2", "");
		}else{	
			docDummy.appendItemValue("ESTADO2", prm_edificio.getEdf_cuotaFijaDia().equals("B")  ? "" : prm_edificio.getEdf_cuotaFijaDia());
			
		}
		docDummy.appendItemValue("E13A", prm_edificio.getEdf_modalidadInteresesPunitorios());
		
		for (Porcentual myPorcentual : prm_edificio.getListaPorcentuales()){
			switch (myPorcentual.getPorc_posicion()){
			case 1:
				docDummy.replaceItemValue("E391", ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(myPorcentual.getPorc_importeHonorarios(), 2));
				break; 
			case 2:
				docDummy.replaceItemValue("E392", ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(myPorcentual.getPorc_importeHonorarios(), 2));
				break; 
			case 3:
				docDummy.replaceItemValue("E441", ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(myPorcentual.getPorc_importeHonorarios(), 2));
				break; 
			case 4:
				docDummy.replaceItemValue("E442", ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(myPorcentual.getPorc_importeHonorarios(), 2));
				break; 
			}
		}
		
		
		QueryAS400 query = new QueryAS400();
		
		if (query.updateAS("updateEdificiosPH_E01", docDummy)) {
			if (!query.updateAS("updateEdificiosValoresCTFJ", docDummy)) {
				if (!query.updateAS("updateEdificiosValoresCTFJ_insert", docDummy))						
					throw new java.lang.Error("No se pudo actualizar la tabla PH_CTFJ.");
			}
			
			if (!query.updateAS("updateEdificiosDIFED", docDummy)) {
				throw new java.lang.Error("No se pudo actualizar la tabla PH_DIFED.");
			}
		}else{
			throw new java.lang.Error("No se pudo actualizar la tabla PH_E01.");
		}
		DocLock lock = (DocLock) JSFUtil.resolveVariable("DocLock");
		lock.removeLock("edf_" + prm_edificio.getEdf_codigo());
	}
	

	/**
	 * Chequea los datos del edificio recibido por parámetros antes de guardarlos
	 * @usedIn: Boton save edificio
	 * @return: un texto con: idComponente con error ~ Mensaje a Mostrar en pantalla
	 */
	public ArrayList<String> strValidacionEdificio(Edificio prm_edificio){
		/*FacesMessage message = new FacesMessage("No es válido");
			// Throw exception so that it prevents document from being saved
			throw new ValidatorException(message);
		 */
		ArrayList<String> listAcumulaErrores = new ArrayList<String>();
		String strTemp = "";
		
		/*INI - Codigo Visual(reemplazo)
		 * - la consulta debe ser por SQL  
		 * - Puede ser = al codigo del edificio actual
		 * - No puede ser = a un codigo visual existente
		 * - No puede ser = a un codigo sasa que no sea el que estoy 
		*/
		QueryAS400 query = new QueryAS400();
		try {
			Document docDummy = JSFUtil.getDocDummy();
			docDummy.appendItemValue("CodigoVisual", prm_edificio.getEdf_codigoVisual());
			ArrayList<String> nl = query.getSelectAS("edificiosValidacionCodigoReemplazo", docDummy, false);
			for (String strLinea : nl) {
				if(!strLinea.split("\\|")[0].trim().equals(prm_edificio.getEdf_codigo())){
					listAcumulaErrores.add("edf_codigoVisual~Código de reemplazo duplicado." + " El código " + prm_edificio.getEdf_codigoVisual() + " está siendo utilizado en otro edificio.");
				}
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}
		//FIN - Codigo Visual(reemplazo)

		//La direccion + localidad max. 27 long.
		strTemp = prm_edificio.getEdf_direccion() + "-" + prm_edificio.getEdf_localidad();
		if(strTemp.length() > 27){
			listAcumulaErrores.add("edf_direccion~El domicilio con la localidad, no deben superar los 26 caracteres.");
			listAcumulaErrores.add("edf_localidad~Los siguientes caracteres exceden el largo permitido: " + strTemp.substring(0, 27) + " --> " + strTemp.substring(27));
		}
		
		//Prorrateos
		ArrayList<String> tempArrayCuotaFijaDia = new ArrayList<String>();
		for(Prorrateo myProrrateo : prm_edificio.getListaProrrateos()){
			tempArrayCuotaFijaDia.add(myProrrateo.getPrt_tipo());
		}
		if(prm_edificio.getEdf_cuotaFijaDia() != null){
			String tempCuotaFijaDia = prm_edificio.getEdf_cuotaFijaDia();
			if(tempCuotaFijaDia.equals("P")){//Chequeo si cambio realmente a P
				if(tempArrayCuotaFijaDia.contains("C") || tempArrayCuotaFijaDia.contains("B")){
					listAcumulaErrores.add("prt_tipo~No puede seleccionar PRESUPUESTO si existe una CUOTA FIJA.");
				}
			}else if(tempCuotaFijaDia.equals("C") || tempCuotaFijaDia.equals("B")){//Chequeo si cambio realmente a C
				if(tempArrayCuotaFijaDia.contains("P")){
					listAcumulaErrores.add("prt_tipo~No puede seleccionar CUOTA FIJA si existe un PRESUPUESTO .");
				}
			}			
		}
		
		//El título de cada valor a prorratear max. 11 long.
		
		//valido fecha 1er vto, si devuelve un string es que existe un error
		String tempAcumulaErrores = strAcumulaErroresFechaPrimer(prm_edificio);
		if (!tempAcumulaErrores.equals("")){
			listAcumulaErrores.add(tempAcumulaErrores);
		}

		//valido fecha 1er vto, si devuelve un string es que existe un error
		tempAcumulaErrores = strAcumulaErroresFechaSegundo(prm_edificio);
		if (!tempAcumulaErrores.equals("")){
			listAcumulaErrores.add(tempAcumulaErrores);
		}

		return listAcumulaErrores;
		
	}

	
	/**
	 * Validacion fecha 1° vto, tambien aplica a masivos
	 * @return: un texto con: idComponente con error ~ Mensaje a Mostrar en pantalla
	 */
	private String strAcumulaErroresFechaPrimer(Edificio prm_edificio){
		String strAcumulaErrores = "";
		if (prm_edificio.getEdf_interesPunitorioDeudores() == null)
			prm_edificio.setEdf_interesPunitorioDeudores(new BigDecimal(0));
		//Defino fechas de ayuda
		Calendar calProxLiquidacionMasUno = Calendar.getInstance();
		calProxLiquidacionMasUno.setTime(prm_edificio.getEdf_fechaProximaLiquidacion());
		calProxLiquidacionMasUno.add(Calendar.DATE, 1);
		Calendar calPrimerVto = Calendar.getInstance();
		
		//ACA
		//La fecha 1er vto no debe nunca estar null y la comparo con prox liquidacion
		if(prm_edificio.getEdf_fechaPrimerVencimientoRecibos() != null){
			calPrimerVto.setTime(prm_edificio.getEdf_fechaPrimerVencimientoRecibos());
			if (calPrimerVto.before(calProxLiquidacionMasUno)) {
				strAcumulaErrores = "edf_fechaPrimerVencimientoRecibos~" + prm_edificio.getEdf_codigo() + ". La fecha de primer vto. no puede ser menor a " + ar.com.ada3d.utilidades.Conversores.DateToString(calProxLiquidacionMasUno.getTime(), "dd/MM/yyyy" );
			}else if(prm_edificio.getEdf_fechaSegundoVencimientoRecibos() != null){
				Calendar calSegundoVto = Calendar.getInstance();
				calSegundoVto.setTime(prm_edificio.getEdf_fechaSegundoVencimientoRecibos());
				if(calPrimerVto.after(calSegundoVto)){
					strAcumulaErrores = "edf_fechaPrimerVencimientoRecibos~" + prm_edificio.getEdf_codigo() + ". La fecha de segundo vto. no permite modificar la fecha de primer vto.";
				}
			}
		}else{
			strAcumulaErrores = "edf_fechaPrimerVencimientoRecibos~La fecha de primer vto. es un campo obligatorio.";
		}
		
		
		
		
		
		
		return strAcumulaErrores;
	}
	
	
	/**
	 * Validacion fecha 2° vto, tambien aplica a masivos
	 * @return: un texto con: idComponente con error ~ Mensaje a Mostrar en pantalla
	 */
	private String strAcumulaErroresFechaSegundo(Edificio prm_edificio){
		String strAcumulaErrores = "";
		if (prm_edificio.getEdf_interesRecargoSegundoVencimiento() == null)
			prm_edificio.setEdf_interesRecargoSegundoVencimiento(new BigDecimal(0));
		//Si la fecha de segundo vto viene debe ser mayor que 1er vto
		if(prm_edificio.getEdf_fechaSegundoVencimientoRecibos() != null){
			if(prm_edificio.getEdf_fechaPrimerVencimientoRecibos() != null){
				Calendar calPrimerVto = Calendar.getInstance();
				calPrimerVto.setTime(prm_edificio.getEdf_fechaPrimerVencimientoRecibos());
				Calendar calSegundoVto = Calendar.getInstance();
				calSegundoVto.setTime(prm_edificio.getEdf_fechaSegundoVencimientoRecibos());
				calSegundoVto.set(Calendar.HOUR_OF_DAY, 0);
				calSegundoVto.set(Calendar.MINUTE, 0);
				calSegundoVto.set(Calendar.SECOND, 0);
				calSegundoVto.set(Calendar.MILLISECOND, 0);
				
				
				if (calSegundoVto.before(calPrimerVto) || calSegundoVto.equals(calPrimerVto)){ 
					strAcumulaErrores = "edf_fechaSegundoVencimientoRecibos~" + prm_edificio.getEdf_codigo() + ". La fecha de 2° vto. debe ser mayor al 1° vto ";
				}else if (calPrimerVto.after(calSegundoVto) || calPrimerVto.equals(calSegundoVto)){ 
					strAcumulaErrores = "edf_fechaSegundoVencimientoRecibos~" + prm_edificio.getEdf_codigo() + ". La fecha de 1° vto. no puede ser mayor al 2° vto ";
				}
			}
			if(prm_edificio.getEdf_interesRecargoSegundoVencimiento().compareTo(BigDecimal.ZERO) == 0)//El interes es cero y tengo fecha 2| vto
				prm_edificio.setEdf_fechaSegundoVencimientoRecibos(null);	
			
			
		}else{ //No hay fecha 2do vto, no debe existir recargo 2do
			if(prm_edificio.getEdf_interesRecargoSegundoVencimiento().compareTo(BigDecimal.ZERO) > 0){
				strAcumulaErrores = "edf_fechaSegundoVencimientoRecibos~" + prm_edificio.getEdf_codigo() + ". Si agrega un porcentaje en 2° vto. debe agregar tambien una fecha de 2° vto ";
			}
		}
		return strAcumulaErrores;
	}
	
	
	/**
	 * Validación cuando se modifica de forma masiva los edificios. 
	 * Los edificios modificados van a ser loqueados
	 * Fechas de Vto e Intereses
	 * @usedIn: Boton btnAplicarMasivo en el formulario de Modificacion Automática
	 * @return: un texto con: idComponente con error ~ Mensaje a Mostrar en pantalla
	 */
	public ArrayList<String> strValidacionMasivoEdificios(String prm_campo, Object prm_valor){
		ArrayList<String> listAcumulaErrores = new ArrayList<String>();
		DocLock lock = (DocLock) JSFUtil.resolveVariable("DocLock");
		String strUsuario = JSFUtil.getSession().getEffectiveUserName();
		if(prm_valor instanceof String){
			
			if(prm_valor.equals("") || prm_valor.equals("0"))
				listAcumulaErrores.add(prm_campo + "~Debe ingresar un valor.");
			
		}else if(prm_valor instanceof Double || prm_valor instanceof Long){
			
			//Validacion de ambos campos de Intereses
			
			BigDecimal valor = new BigDecimal(prm_valor.toString());
			if(valor.compareTo(new BigDecimal(9999)) == 1){
				listAcumulaErrores.add(prm_campo + "~El % de interés no puede superar el 9999 %" );
				return listAcumulaErrores;
			}
			valor = valor.setScale(1, RoundingMode.HALF_EVEN);//redondeo si puso mas de 1 decimal
			for(Edificio myEdificio : listaEdificiosTrabajo){
				//Actualizo el edificio
				//myEdificio = actualizoUnEdificioAs400(myEdificio, "");
				//Valido que no este lockeado
				
				if( (lock.isLocked("edf_" + myEdificio.getEdf_codigo()) && lock.getLock("edf_" + myEdificio.getEdf_codigo()).equals(strUsuario) ) || !lock.isLocked("edf_" + myEdificio.getEdf_codigo())  ){
					if (prm_campo.equals("interesPunitorioDeudoresMasivo")){
						myEdificio.setEdf_interesPunitorioDeudores(valor);
						myEdificio.setEdf_importeMasivoE12(valor);
						lockearEdificio(myEdificio, JSFUtil.getSession().getEffectiveUserName());//lock de los que estoy modificando
						isMasivoActualizado = true;
					}
					BigDecimal tempValor = myEdificio.getEdf_interesRecargoSegundoVencimiento();
					if (prm_campo.equals("recargoSegundoVencimientoMasivo")){
						myEdificio.setEdf_interesRecargoSegundoVencimiento(valor);
						if (strAcumulaErroresFechaSegundo(myEdificio).equals("")){
							myEdificio.setEdf_importeMasivoE08A(valor);
							lockearEdificio(myEdificio, JSFUtil.getSession().getEffectiveUserName());//lock de los que estoy modificando
							isMasivoActualizado = true;
							if(valor.compareTo(BigDecimal.ZERO) == 0) //elimino la fecha 2° vto
								myEdificio.setEdf_fechaSegundoVencimientoRecibos(null);
							
						}else{
							listAcumulaErrores.add(strAcumulaErroresFechaSegundo(myEdificio));
							myEdificio.setEdf_interesRecargoSegundoVencimiento(tempValor);
						}
					}
						
				}else{
					listAcumulaErrores.add(prm_campo + "~El edificio " + myEdificio.getEdf_codigo() + " no se pudo actualizar ya que está siendo modificado por: " + lock.getLock("edf_" + myEdificio.getEdf_codigo()).substring(4) );
				}
			}
		}else if(prm_valor instanceof Date){
			
			//Para las fechas voy a presentar un unico mensaje ya que existen más validaciones
			String tempFecha = ar.com.ada3d.utilidades.Conversores.DateToString((Date) prm_valor, "dd/MM/yyyy");
			ArrayList<String> arrAcumulaErrorCodigoEdificio = new ArrayList<String>();
			int countEdificiosModificados = 0;
			for(Edificio myEdificio : listaEdificiosTrabajo){

				//Valido que no este lockeado
				if( (lock.isLocked("edf_" + myEdificio.getEdf_codigo()) && lock.getLock("edf_" + myEdificio.getEdf_codigo()).equals(strUsuario) ) || !lock.isLocked("edf_" + myEdificio.getEdf_codigo())  ){

					//Voy a validar las fechas ingresadas en cada edificio
					//Defino fechas de ayuda
					Calendar calProxLiquidacionMasUno = Calendar.getInstance();
					calProxLiquidacionMasUno.setTime(myEdificio.getEdf_fechaProximaLiquidacion());
					calProxLiquidacionMasUno.add(Calendar.DATE, 1);
					
					//La fecha 1er vto no debe nunca estar null y la comparo con prox liquidacion
					if(myEdificio.getEdf_fechaPrimerVencimientoRecibos() != null && prm_campo.equals("fechaPrimerVtoMasivo")){
						Calendar calNew = Calendar.getInstance();
						calNew.setTime((Date) prm_valor);
						calNew.set(Calendar.HOUR_OF_DAY, 0);
						calNew.set(Calendar.MINUTE, 0);
						calNew.set(Calendar.SECOND, 0);
						calNew.set(Calendar.MILLISECOND, 0);
						
						if (calNew.before(calProxLiquidacionMasUno)) {
							arrAcumulaErrorCodigoEdificio.add(myEdificio.getEdf_codigo());
						}else if(myEdificio.getEdf_fechaSegundoVencimientoRecibos() != null){
							Calendar calSegundoVto = Calendar.getInstance();
							calSegundoVto.setTime(myEdificio.getEdf_fechaSegundoVencimientoRecibos());
							if(calNew.after(calSegundoVto)){
								arrAcumulaErrorCodigoEdificio.add(myEdificio.getEdf_codigo());
							}
						}
						
					}	
						
					
					
					
					
					
				
					
					//1° Vto
					if(prm_campo.equals("fechaPrimerVtoMasivo")){
						Date oldFecha1erVto = myEdificio.getEdf_fechaPrimerVencimientoRecibos();
						myEdificio.setEdf_fechaPrimerVencimientoRecibos((Date) prm_valor);
						//valido fecha 1er vto, si devuelve un string es que existe un error
						String tempAcumulaErrores = strAcumulaErroresFechaPrimer(myEdificio);
						if (tempAcumulaErrores.equals("")){
							myEdificio.setEdf_fechaMasivoVTOEX1((Date) prm_valor);
							lockearEdificio(myEdificio, JSFUtil.getSession().getEffectiveUserName());//lock de los que estoy modificando
							countEdificiosModificados = countEdificiosModificados + 1;
							isMasivoActualizado = true;
						}else{
							myEdificio.setEdf_fechaPrimerVencimientoRecibos(oldFecha1erVto);
							arrAcumulaErrorCodigoEdificio.add(myEdificio.getEdf_codigo());
						}
						
						/*
						if (calNew.before(calProxLiquidacionMasUno) || calNew.equals(calSegundoVto) || (calNew.after(calSegundoVto) && myEdificio.getEdf_interesRecargoSegundoVencimiento().compareTo(BigDecimal.ZERO) > 0)) {
							arrAcumulaErrorCodigoEdificio.add(myEdificio.getEdf_codigo());
							//listAcumulaErrores.add(prm_campo + "~Edificio " + myEdificio.getEdf_codigo() + " la fecha de primer vto. no puede ser menor a " + ar.com.ada3d.utilidades.Conversores.DateToString(calMin.getTime(), "dd/MM/yyyy" ));
						}else{
							myEdificio.setEdf_fechaPrimerVencimientoRecibos((Date) prm_valor);
							myEdificio.setEdf_fechaMasivoVTOEX1((Date) prm_valor);
							lockearEdificio(myEdificio, JSFUtil.getSession().getEffectiveUserName());//lock de los que estoy modificando
							countEdificiosModificados = countEdificiosModificados + 1;
							isMasivoActualizado = true;
						}*/
					}
					
					//2° Vto
					if(myEdificio.getEdf_interesRecargoSegundoVencimiento().compareTo(BigDecimal.ZERO) > 0 && prm_campo.equals("fechaSegundoVtoMasivo")){
						calProxLiquidacionMasUno.setTime(myEdificio.getEdf_fechaPrimerVencimientoRecibos());
						Calendar calNew = Calendar.getInstance();
						calNew.setTime((Date) prm_valor);
						calNew.set(Calendar.HOUR_OF_DAY, 0);
						calNew.set(Calendar.MINUTE, 0);
						calNew.set(Calendar.SECOND, 0);
						calNew.set(Calendar.MILLISECOND, 0);
						
						if (calNew.before(calProxLiquidacionMasUno) || calNew.equals(calProxLiquidacionMasUno)) {
							arrAcumulaErrorCodigoEdificio.add(myEdificio.getEdf_codigo());
							//listAcumulaErrores.add(prm_campo + "~Edificio " + myEdificio.getEdf_codigo() + " la fecha de 2° vto. debe ser mayor al 1° vto ");
						}else{
							myEdificio.setEdf_fechaSegundoVencimientoRecibos((Date) prm_valor);
							myEdificio.setEdf_fechaMasivoVTOEX2((Date) prm_valor);
							lockearEdificio(myEdificio, JSFUtil.getSession().getEffectiveUserName());//lock de los que estoy modificando
							countEdificiosModificados = countEdificiosModificados + 1;
							isMasivoActualizado = true;
						}
					}else if(myEdificio.getEdf_interesRecargoSegundoVencimiento().compareTo(BigDecimal.ZERO) == 0 && prm_campo.equals("fechaSegundoVtoMasivo")){
						listAcumulaErrores.add(prm_campo + "~El % de interés del edificio " + myEdificio.getEdf_codigo() + " es cero. No se modificó la fecha indicada(" + tempFecha + ")." );
					}
					
				}else{
					listAcumulaErrores.add(prm_campo + "~El edificio " + myEdificio.getEdf_codigo() + " no se pudo actualizar ya que está siendo modificado por: " + lock.getLock("edf_" + myEdificio.getEdf_codigo()).substring(4) );
				}
			}
			if(!arrAcumulaErrorCodigoEdificio.isEmpty()){
				if (countEdificiosModificados > 0){
					listAcumulaErrores.add(prm_campo + "~Los edificios " + arrAcumulaErrorCodigoEdificio.toString().replace("[","").replace("]","") + " no han sido modificados con la fecha " + tempFecha + " , favor de revisar. Se actualizaron " + countEdificiosModificados + " edificios.");
				}else{
					listAcumulaErrores.add(prm_campo + "~No se ha modificado ningún edificio con la fecha " + tempFecha + " , favor de revisar. Recuerde que la fecha de 1er Vto. debe ser menor que la de 2do Vto.");
				}
			}
		}else{
			listAcumulaErrores.add(prm_campo + "~No es un número.");
		}
		
		
		return listAcumulaErrores;
	}
	
	
	
	/**
	 * Validacion con varios campos completos en pantalla
	 * Se duplica la validacion ya que si lo hago por edificio tarda una eternidad
	 * @param los campos que se completaron en pantalla, sino el lugar viene null
	 * @return un array de texto con: idComponente con error ~ Mensaje a Mostrar en pantalla
	 */
	public ArrayList<String> strValidacionAplicarTodoMasivo(ArrayList<Object> prm_campos){
		ArrayList<String> listAcumulaErrores = new ArrayList<String>(); // return
		
		//Chequeo que venga algun dato, si vienen todos nulos error
		boolean booContinuar = false;
		for (Object tempObj : prm_campos){
			if(tempObj != null)
				booContinuar = true;
		}
		if(!booContinuar){
			listAcumulaErrores.add("interesPunitorioDeudoresMasivo~No se ha ingresado ningún dato por aplicar");
			return listAcumulaErrores;
		}
		
		
		DocLock lock = (DocLock) JSFUtil.resolveVariable("DocLock");
		String strUsuario = JSFUtil.getSession().getEffectiveUserName();
		
		//Inicializo variables temporales para los 4 Campos 
		BigDecimal tempNewInteresPunitorio = null;
		Date tempNewFecha1erVto = null;
		BigDecimal tempNewRecargoSegundoVencimiento = null;
		Date tempNewFecha2doVto = null;
		
		//Inicializo variables temporales que guardo el valor original
		Date oldFecha1erVto = null;
		BigDecimal oldRecargoSegundoVencimiento = null;
		Date oldFecha2doVto = null;
		
		for(int i=0; i<4; i++){
			Object obj = prm_campos.get(i);
			if (obj != null){
				switch (i) {
				case 0:
					tempNewInteresPunitorio = new BigDecimal(obj.toString());
					break;
				case 1:
					tempNewFecha1erVto = (Date) obj;
					break;
				case 2:
					tempNewRecargoSegundoVencimiento = new BigDecimal(obj.toString());
					break;
				case 3:
					tempNewFecha2doVto = (Date) obj;
					break;
				}
			}
		}
				
		
		for(Edificio myEdificio : listaEdificiosTrabajo){
			//Valido que no esté lockeado
			if( (lock.isLocked("edf_" + myEdificio.getEdf_codigo()) && lock.getLock("edf_" + myEdificio.getEdf_codigo()).equals(strUsuario) ) || !lock.isLocked("edf_" + myEdificio.getEdf_codigo())  ){
				
				//Intereses punitorios
				if(tempNewInteresPunitorio != null){
					myEdificio.setEdf_interesPunitorioDeudores(tempNewInteresPunitorio);
					myEdificio.setEdf_importeMasivoE12(tempNewInteresPunitorio);
					lockearEdificio(myEdificio, JSFUtil.getSession().getEffectiveUserName());//lock de los que estoy modificando
					isMasivoActualizado = true;
				}
				
				//Fecha 1er vto
				if(tempNewFecha1erVto != null){
					oldFecha1erVto = myEdificio.getEdf_fechaPrimerVencimientoRecibos();
					myEdificio.setEdf_fechaPrimerVencimientoRecibos(tempNewFecha1erVto);
					
					//valido fecha 1er vto, si devuelve un string es que existe un error
					String tempAcumulaErrores = strAcumulaErroresFechaPrimer(myEdificio);
					if (tempAcumulaErrores.equals("")){
						myEdificio.setEdf_fechaMasivoVTOEX1(tempNewFecha1erVto);
						lockearEdificio(myEdificio, JSFUtil.getSession().getEffectiveUserName());//lock de los que estoy modificando
						isMasivoActualizado = true;
					}else{
						myEdificio.setEdf_fechaPrimerVencimientoRecibos(oldFecha1erVto);
						listAcumulaErrores.add(tempAcumulaErrores);
					}
				}
				
				//Recargo 2do Vto
				if(tempNewRecargoSegundoVencimiento != null){
					oldRecargoSegundoVencimiento = myEdificio.getEdf_interesRecargoSegundoVencimiento();
					myEdificio.setEdf_interesRecargoSegundoVencimiento(tempNewRecargoSegundoVencimiento);
					
					if(tempNewFecha2doVto != null){ // Tengo que anticipar la fecha porque van juntos
						oldFecha2doVto = myEdificio.getEdf_fechaSegundoVencimientoRecibos();
						myEdificio.setEdf_fechaSegundoVencimientoRecibos(tempNewFecha2doVto);
					}
					
					//valido fecha 2do vto, ya que tambien valida el recargo, devuelve un string es que existe un error
					String tempAcumulaErrores = strAcumulaErroresFechaSegundo(myEdificio);
					if (tempAcumulaErrores.equals("")){
						myEdificio.setEdf_importeMasivoE08A(tempNewRecargoSegundoVencimiento);
						myEdificio.setEdf_fechaMasivoVTOEX2(tempNewFecha2doVto);
						lockearEdificio(myEdificio, JSFUtil.getSession().getEffectiveUserName());//lock de los que estoy modificando
						isMasivoActualizado = true;
					}else{
						myEdificio.setEdf_interesRecargoSegundoVencimiento(oldRecargoSegundoVencimiento);
						myEdificio.setEdf_fechaSegundoVencimientoRecibos(oldFecha2doVto);
						listAcumulaErrores.add(tempAcumulaErrores);
					}
					
				}
				
				//Fecha 2do vto
				if(tempNewFecha2doVto != null){
					oldFecha2doVto = myEdificio.getEdf_fechaSegundoVencimientoRecibos();
					myEdificio.setEdf_fechaSegundoVencimientoRecibos(tempNewFecha2doVto);

					//valido fecha 2do vto, si devuelve un string es que existe un error
					String tempAcumulaErrores = strAcumulaErroresFechaSegundo(myEdificio);
					if (tempAcumulaErrores.equals("")){
						myEdificio.setEdf_fechaMasivoVTOEX2(tempNewFecha2doVto);
						lockearEdificio(myEdificio, JSFUtil.getSession().getEffectiveUserName());//lock de los que estoy modificando
						isMasivoActualizado = true;
					}else{
						myEdificio.setEdf_fechaSegundoVencimientoRecibos(oldFecha2doVto);
						listAcumulaErrores.add(tempAcumulaErrores);
					}
				}
			}else{
				listAcumulaErrores.add("~El edificio " + myEdificio.getEdf_codigo() + " no se pudo actualizar ya que está siendo modificado por: " + lock.getLock("edf_" + myEdificio.getEdf_codigo()).substring(4) );	
			}
		}
		return listAcumulaErrores;
	}
	
	
	/**
	 * Se pasa a null la fecha y el interés de 2° vto a cero. 
	 * @usedIn: Boton btnEliminarRecargoSegundoVencimientoMasivo en el formulario de Modificacion Automática
	 * @return: un texto con: idComponente con error ~ Mensaje a Mostrar en pantalla
	 */
	public ArrayList<String> eliminarInteresFechasSegundoVto(String prm_campo){
		ArrayList<String> listAcumulaErrores = new ArrayList<String>();
		DocLock lock = (DocLock) JSFUtil.resolveVariable("DocLock");
		String strUsuario = JSFUtil.getSession().getEffectiveUserName();
		for(Edificio myEdificio : listaEdificiosTrabajo){
			if( (lock.isLocked("edf_" + myEdificio.getEdf_codigo()) && lock.getLock("edf_" + myEdificio.getEdf_codigo()).equals(strUsuario) ) || !lock.isLocked("edf_" + myEdificio.getEdf_codigo())  ){
				myEdificio.setEdf_fechaSegundoVencimientoRecibos(null);
				myEdificio.setEdf_interesRecargoSegundoVencimiento(new BigDecimal(0));
				myEdificio.setEdf_importeMasivoE08A(new BigDecimal(0));
				isMasivoActualizado = true;
			}else{
				listAcumulaErrores.add(prm_campo + "~El edificio " + myEdificio.getEdf_codigo() + " no se pudo actualizar ya que está siendo modificado por: " + lock.getLock("edf_" + myEdificio.getEdf_codigo()).substring(4) );
				
			}
		}
		return listAcumulaErrores;
		
	}
	
	
	
	
	/**
	 * Escribe en AS400 los datos modificados masivamente
	 * Ya fueron validados y estan lockeados, al final se liberan
	 * @usedIn Boton btnConfirmarMasivo en el formulario de Modificacion Automática
	 * @return Si no actualizaron nada presento el mensaje
	 */
	public ArrayList<String> saveMasivoEdificios() {
		ArrayList<String> listAcumulaErrores = new ArrayList<String>();
		if(isMasivoActualizado){
			DocLock lock = (DocLock) JSFUtil.resolveVariable("DocLock");
			ArrayList<String> listSQL = new ArrayList<String>();
			
			//Voy a utilizar variables temporales para al final hacer como máximo 4 updates
			ArrayList<String> listaEdificiosE12 = new ArrayList<String>();
			String tempE12 = "";
			ArrayList<String> listaEdificiosE08A = new ArrayList<String>();
			String tempE08A = "";
			ArrayList<String> listaEdificiosVTOEX1 = new ArrayList<String>();
			String tempVTOEX1 = "";
			ArrayList<String> listaEdificiosVTOEX2 = new ArrayList<String>();
			String tempVTOEX2 = "";
			
			for(Edificio myEdificio : listaEdificiosTrabajo){
				//ini E12
				if(myEdificio.getEdf_importeMasivoE12() != null){
						if(tempE12.equals("")){
							tempE12 = ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(myEdificio.getEdf_importeMasivoE12(),1);
							listaEdificiosE12.add(myEdificio.getEdf_codigo());
						}
						else if (tempE12.equals(ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(myEdificio.getEdf_importeMasivoE12(),1))){
							listaEdificiosE12.add(myEdificio.getEdf_codigo());						
						}else{
							listSQL.add("PH_E01~SET E12 = " + tempE12 + " WHERE EDIF IN (" + listaEdificiosE12.toString().replace("[","").replace("]","") + ")");
							tempE12 = ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(myEdificio.getEdf_importeMasivoE12(),1);
							listaEdificiosE12.clear();//reinicio valores
							listaEdificiosE12.add(myEdificio.getEdf_codigo());
						}
						myEdificio.setEdf_importeMasivoE12(null);//reinicio valores
				}
				//fin E12
	
				//ini E08A
				if(myEdificio.getEdf_importeMasivoE08A() != null){
						if(tempE08A.equals("")){
							tempE08A = ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(myEdificio.getEdf_importeMasivoE08A(),1);
							listaEdificiosE08A.add(myEdificio.getEdf_codigo());
						}
						else if (tempE08A.equals(ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(myEdificio.getEdf_importeMasivoE08A(),1))){
							listaEdificiosE08A.add(myEdificio.getEdf_codigo());						
						}else{
							listSQL.add("PH_E01~SET E08A = " + tempE08A + " WHERE EDIF IN (" + listaEdificiosE08A.toString().replace("[","").replace("]","") + ")");
							tempE08A = ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(myEdificio.getEdf_importeMasivoE08A(),1);
							listaEdificiosE08A.clear();//reinicio valores
							listaEdificiosE08A.add(myEdificio.getEdf_codigo());
						}
						myEdificio.setEdf_importeMasivoE08A(null);//reinicio valores
				}
				//fin E08A
				
				//ini VTOEX1
				if(myEdificio.getEdf_fechaMasivoVTOEX1() != null){
						if(tempVTOEX1.equals("")){
							tempVTOEX1 = ar.com.ada3d.utilidades.Conversores.DateToString(myEdificio.getEdf_fechaPrimerVencimientoRecibos(), "ddMMyy");
							listaEdificiosVTOEX1.add(myEdificio.getEdf_codigo());
						}
						else if (tempVTOEX1.equals(ar.com.ada3d.utilidades.Conversores.DateToString(myEdificio.getEdf_fechaPrimerVencimientoRecibos(), "ddMMyy"))){
							listaEdificiosVTOEX1.add(myEdificio.getEdf_codigo());						
						}else{
							listSQL.add("PH_DIFED~SET VTOEX1 = " + tempVTOEX1 + " WHERE EDIF IN (" + listaEdificiosVTOEX1.toString().replace("[","").replace("]","") + ")");
							tempVTOEX1 = ar.com.ada3d.utilidades.Conversores.DateToString(myEdificio.getEdf_fechaPrimerVencimientoRecibos(), "ddMMyy");
							listaEdificiosVTOEX1.clear();//reinicio valores
							listaEdificiosVTOEX1.add(myEdificio.getEdf_codigo());
						}
						myEdificio.setEdf_fechaMasivoVTOEX1(null);//reinicio valores
				}
				//fin VTOEX1
				
				//ini VTOEX2
				if(myEdificio.getEdf_fechaMasivoVTOEX2() != null){
					if(tempVTOEX2.equals("")){
						tempVTOEX2 = ar.com.ada3d.utilidades.Conversores.DateToString(myEdificio.getEdf_fechaSegundoVencimientoRecibos(), "ddMMyy");
						listaEdificiosVTOEX2.add(myEdificio.getEdf_codigo());
					}
					else if (tempVTOEX2.equals(ar.com.ada3d.utilidades.Conversores.DateToString(myEdificio.getEdf_fechaSegundoVencimientoRecibos(), "ddMMyy"))){
						listaEdificiosVTOEX2.add(myEdificio.getEdf_codigo());						
					}else{
						listSQL.add("PH_DIFED~SET VTOEX2 = " + tempVTOEX2 + " WHERE EDIF IN (" + listaEdificiosVTOEX2.toString().replace("[","").replace("]","") + ")");
						tempVTOEX2 = ar.com.ada3d.utilidades.Conversores.DateToString(myEdificio.getEdf_fechaSegundoVencimientoRecibos(), "ddMMyy");
						listaEdificiosVTOEX2.clear();//reinicio valores
						listaEdificiosVTOEX2.add(myEdificio.getEdf_codigo());
					}
					myEdificio.setEdf_fechaMasivoVTOEX2(null);//reinicio valores
				}
				//fin VTOEX2
			}// FIN recorrer edificios

			if(!listaEdificiosE12.isEmpty()){
				listSQL.add("PH_E01~SET E12 = " + tempE12 + " WHERE EDIF IN (" + listaEdificiosE12.toString().replace("[","").replace("]","") + ")");
			}
			if(!listaEdificiosE08A.isEmpty()){
				listSQL.add("PH_E01~SET E08A = " + tempE08A + " WHERE EDIF IN (" + listaEdificiosE08A.toString().replace("[","").replace("]","") + ")");
			}
			if(!listaEdificiosVTOEX1.isEmpty()){
				listSQL.add("PH_DIFED~SET VTOEX1 = " + tempVTOEX1 + " WHERE EDIF IN (" + listaEdificiosVTOEX1.toString().replace("[","").replace("]","") + ")");
			}
			if(!listaEdificiosVTOEX2.isEmpty()){
				listSQL.add("PH_DIFED~SET VTOEX2 = " + tempVTOEX2 + " WHERE EDIF IN (" + listaEdificiosVTOEX2.toString().replace("[","").replace("]","") + ")");
			}
			
			//Al final remuevo los lockeos
			ArrayList<String> temp1EdificioMerge = ar.com.ada3d.utilidades.Conversores.mergeTwoArrayRemoveDuplicate(listaEdificiosE12, listaEdificiosE08A);
			ArrayList<String> temp2EdificioMerge = ar.com.ada3d.utilidades.Conversores.mergeTwoArrayRemoveDuplicate(listaEdificiosVTOEX1, listaEdificiosVTOEX2);
			ArrayList<String> edificioMerge = ar.com.ada3d.utilidades.Conversores.mergeTwoArrayRemoveDuplicate(temp1EdificioMerge, temp2EdificioMerge);
			for (String temp : edificioMerge) {
				lock.removeLock("edf_" + temp);
				getEdificioMap(temp).setEdf_lockedBy("");
			}
			
			//Hago los Updates al AS400
			QueryAS400 query = new QueryAS400();
			for(String temp : listSQL){
				Document docDummy = JSFUtil.getDocDummy();
				String tempLista = temp.split("\\~")[1];
				docDummy.appendItemValue("tabla", temp.split("\\~")[0]);
				docDummy.appendItemValue("LISTA_EDIF", tempLista.replace(", ","', '").replace("(","('").replace(")","')"));
				if (query.updateAS("updateMasivoEdificios", docDummy)) {
					
				}else{
					throw new java.lang.Error("No se pudo actualizar masivamente la tabla " + temp.split("\\~")[0]);
				}
			}
			isMasivoActualizado = false;//reinicio flag de actualizacion
		}else{
			listAcumulaErrores.add("widgetContainer1" + "~No se ha encontrado ningún cambio. Recuerde presionar 'Aplicar' y una vez que los vea en pantalla podrá confirmarlos.");
		}
		return listAcumulaErrores;
		
	}

	
	/**
	 * Lockeo de edificio en el objeto Edificio y en el bean DocLock
	 * @param el obleto edificio
	 * @param el nombre de usuario
	 * */
	private void lockearEdificio(Edificio prm_Edificio, String prm_userName){
		DocLock lockeos = (DocLock) JSFUtil.resolveVariable("DocLock");
		//Agrego el usuario si el edificio está lockeado
		if(!lockeos.isLocked("edf_" + prm_Edificio.getEdf_codigo())){
			lockeos.addLock("edf_" + prm_Edificio.getEdf_codigo(), prm_userName);
			prm_Edificio.setEdf_lockedBy(lockeos.getLock("edf_" + prm_Edificio.getEdf_codigo()));
		}
	}
	
	
	
	//*** Getters & Setters *****
	
	public List<Edificio> getListaEdificios() {
		return listaEdificios;
	}

	public void setListaEdificios(ArrayList<Edificio> edificios) {
		EdificioBean.listaEdificios = edificios;
	}
	
	public List<Edificio> getListaEdificiosTrabajo() {
		return listaEdificiosTrabajo;
	}
	
	public void setListaEdificiosTrabajo(ArrayList<Edificio> edificios) {
		EdificioBean.listaEdificiosTrabajo = edificios;
	}

	public Edificio getEdificio() {
		return edificio;
	}

	public void setEdificio(Edificio edificio) {
		this.edificio = edificio;
	}

	public int getCantidadTotalEdificiosTrabajo() {
		return listaEdificiosTrabajo.size();
	}

	public boolean isMasivoActualizado() {
		return isMasivoActualizado;
	}

}
