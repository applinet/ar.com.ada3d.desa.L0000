package ar.com.ada3d.utilidades;

import java.util.HashMap;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

public class DocUsr {
	private static final long serialVersionUID = 1L;
	private final HashMap<String, String> _map;
	private Vector usrSelected;

	public DocUsr() {
		System.out.println("Constructor DocUsr");
		this._map = new HashMap<String, String>();
		updateDocUsr();
	}

	private void updateDocUsr() {
		Session session = JSFUtil.getSession();
		Database currentDB = JSFUtil.getCurrentDatabase();

		Document docUsuario = null;
		try {
			docUsuario = getUserDocument(session.getEffectiveUserName(),
					currentDB);
			if (docUsuario != null) {
				synchronized (this._map) {
					this._map.put("userName", docUsuario
							.getItemValueString("usr_UserName_des"));					
					this._map.put("nombreyApellido", docUsuario
							.getItemValueString("usr_Nombre_des") + " " + docUsuario
							.getItemValueString("usr_Apellido_des"));
					this._map.put("userMaskName", docUsuario
							.getItemValueString("usr_UserMaskName_des"));
					this._map.put("userSequential", docUsuario
							.getItemValueString("usr_UserSequential_nro"));
					this._map.put("userStatus", docUsuario
							.getItemValueString("usr_Status_des"));
					this._map.put("userSeg", docUsuario
							.getItemValueString("usr_USRSEG_opt"));
					this._map.put("userDB", currentDB.getFileName().substring(
							currentDB.getFileName().length() - 8, 5));
				}
				setUsrSelected(docUsuario.getItemValue("usr_MenuSelected_cod"));
				/* getItemValue definirlo como Vector=import java.util.Vector */
				docUsuario.recycle();
				currentDB.recycle();
			}else{
				synchronized (this._map) {
					this._map.put("userName", session.getEffectiveUserName());
					this._map.put("userDB", currentDB.getFileName().substring(
							currentDB.getFileName().length() - 8, 5));
				}
				
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}

	}

	public String getUser() {
		String ret;
		synchronized (this._map) {
			ret = this._map.get("userName");
		}
		return ret;
	}
	
	public String getNombre() {
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

	public String getStatus() {
		String ret;
		synchronized (this._map) {
			ret = this._map.get("userStatus");
		}
		return ret;
	}

	public boolean isUsrSeg() {
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

	/*
	 * Devuelve el documento de usuario de currentDatabase (base de
	 * administracion)
	 */
	private Document getUserDocument(String userName, Database currentDB) {
		Document docUsuario;
		View viewMenuPorUsuario;
		try {
			String nombreVista = JSFUtil
					.getOpcionesClave("VIEW_USUARIOS_POR_ADMINISTRACION");

			if (nombreVista == "") {
				return null;
			}
			viewMenuPorUsuario = currentDB.getView(nombreVista);
			docUsuario = viewMenuPorUsuario.getDocumentByKey(userName, true);
			if (docUsuario != null) {
				viewMenuPorUsuario.recycle();
				return docUsuario;
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return null;

	}

	// Getters y Setteres
	public void setUsrSelected(Vector usrSelected) {
		this.usrSelected = usrSelected;
	}

	public Vector getUsrSelected() {
		return usrSelected;
	}

}
