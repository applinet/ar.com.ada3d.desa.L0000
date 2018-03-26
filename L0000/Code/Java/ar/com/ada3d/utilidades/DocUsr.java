package ar.com.ada3d.utilidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import org.openntf.domino.*;
import org.openntf.domino.xsp.XspOpenLogUtil;

public class DocUsr implements Serializable{
	private static final long serialVersionUID = 1L;
	private final HashMap<String, String> _map;
	private ArrayList<String> edificiosNoAccessLista;
	private ArrayList<String> ultimaActividad;



	public DocUsr() {
		//System.out.println("FPR - Constr. DocUsr");
		this._map = new HashMap<String, String>();		
		updateDocUsr();
	}

	private void updateDocUsr() {
		try {
			Session session = JSFUtil.getSession();
			Database currentDB = JSFUtil.getCurrentDatabase();

			Document docUsuario = null;
			docUsuario = getUserDocument(session.getEffectiveUserName(),
					currentDB);
			if (docUsuario != null) {
				synchronized (this._map) {
					this._map.put("userName", docUsuario
							.getItemValueString("usr_UserName_des"));
					this._map
							.put(
									"nombreyApellido",
									docUsuario
											.getItemValueString("usr_Nombre_des")
											+ " "
											+ docUsuario
													.getItemValueString("usr_Apellido_des"));
					this._map.put("userMaskName", docUsuario
							.getItemValueString("usr_UserMaskName_des"));
					this._map.put("userSequential", docUsuario
							.getItemValueString("usr_UserSequential_des"));
					this._map.put("userStatus", docUsuario
							.getItemValueString("usr_Status_des"));
					this._map.put("userSeg", docUsuario
							.getItemValueString("usr_USRSEG_opt"));
					this._map.put("userDB", currentDB.getFileName().substring(
							currentDB.getFileName().length() - 8, 5));

					//Para convertir de Vector<Object> a un ArrayList<String> 
					ArrayList<String> tempEdificiosSinAcceso = new ArrayList<String>();
					for (Object object : docUsuario
							.getItemValue("usr_EdificiosSinAcceso_cod")) {
						tempEdificiosSinAcceso.add(object.toString());
					}
					this.edificiosNoAccessLista = tempEdificiosSinAcceso;
				}
				
			} else {
				synchronized (this._map) {
					this._map.put("userName", session.getEffectiveUserName());

					this._map.put("userDB", currentDB.getFileName().substring(
							currentDB.getFileName().length() - 8, 5));
				}

			}
		} catch (Exception e) {
			XspOpenLogUtil.logError(e);

		}
	}

	
	
	
	/*
	 * Devuelve el documento de usuario de currentDatabase (base de
	 * administracion)
	 */
	private Document getUserDocument(String userName, Database currentDB) {
		Document docUsuario;
		View viewMenuPorUsuario;
		String nombreVista = JSFUtil
				.getOpcionesClave("VIEW_USUARIOS_POR_ADMINISTRACION");

		if (nombreVista == "") {
			return null;
		}
		viewMenuPorUsuario = currentDB.getView(nombreVista);
		docUsuario = viewMenuPorUsuario.getFirstDocumentByKey(userName, true);
		if (docUsuario != null) {
			return docUsuario;
		}
		return null;

	}

	
	// ******** Getters and Setters ***************
	public String getUser() {
		String ret;
		synchronized (this._map) {
			ret = this._map.get("userName");
		}
		return ret;
	}

	@SuppressWarnings("unused")
	private String getNombre() {
		String ret;
		synchronized (this._map) {
			ret = this._map.get("nombreyApellido");
		}
		return ret;
	}

	public String getUserMask() {
		String ret;
		synchronized (this._map) {
			ret = this._map.get("userMaskName");
		}
		return ret;
	}

	public String getUserSec() {
		String ret;
		synchronized (this._map) {
			ret = this._map.get("userSequential");
		}
		return ret;
	}

	@SuppressWarnings("unused")
	private String getStatus() {
		String ret;
		synchronized (this._map) {
			ret = this._map.get("userStatus");
		}
		return ret;
	}

	@SuppressWarnings("unused")
	private boolean isUsrSeg() {
		boolean ret = false;
		synchronized (this._map) {
			ret = this._map.get("userSeg").equals("1");
		}
		return ret;
	}

	public String getUserDB() {
		String ret;
		synchronized (this._map) {
			ret = this._map.get("userDB");
		}
		return ret;
	}

	public ArrayList<String> getEdificiosNoAccessLista() {
		return edificiosNoAccessLista;
	}

	public void setEdificiosNoAccessLista(ArrayList<String> edificiosNoAccessLista) {
		this.edificiosNoAccessLista = edificiosNoAccessLista;
	}

	
	public ArrayList<String> getUltimaActividad() {
		return ultimaActividad;
	}

	public void setUltimaActividad(ArrayList<String> ultimaActividad) {
		this.ultimaActividad = ultimaActividad;
	}

	public void setUltimaActividad(String ultimaActividad) {
		if(this.ultimaActividad == null)
			this.ultimaActividad = new ArrayList<String>();
		this.ultimaActividad.add(0, ultimaActividad);
	}
}
