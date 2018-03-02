package ar.com.ada3d.utilidades;

/*201330517 - FPR - Para lockear un documento
 * ------UTILIZA---------------------------------------------------------------------------------
 * <managed-bean-name>DocLock</managed-bean-name> 
 * con una beanScope de Aplicacion 
 * ------COMO FUNCIONA---------------------------------------------------------------------------
 * Agregar un lockeo: DocLock.addLock(ID,Info) --> ID = Key del documento, valor = UserName(txt)
 * Quitar un lockeo: DocLock.removeLock(ID) --> ID = Key del documento
 * Preguntar estado: DocLock.isLocked(ID) --> return true/false
 * Preguntar quien lo tiene: DocLock.getLock(ID) --> return valor(txt)
 * ------EJEMPLO---------------------------------------------------------------------------------
 * Agregar Lockeo a un documento que tomo el Id de un parametro de la url
 * var ID=context.getUrlParameter("documentId");
 * var Info=@Name("[CN]",@UserName());
 * DocLock.addLock(Key,valor)
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.*;

import org.openntf.domino.Session;


public class DocLock implements Serializable {
	private static final long serialVersionUID = 2L;
	private HashMap<String, String> _map;

	// ---------------------------------------------------------

	public DocLock() {
		this._map = new HashMap<String, String>();
	}

	// ---------------------------------------------------------
	public boolean isLocked(String Key) {
		boolean ret = false;
		synchronized (this._map) {
			ret = this._map.containsKey(Key);
		}
		return ret;
	}

	public void addLock(String Key, String valor) {
		if(!isLocked(Key)){
			synchronized (this._map) {
				this._map.put(Key, valor);
			}
		}
	}

	public String getLock(String Key) {
		String ret;
		ret = this._map.get(Key);
		return ret;
	}

	public void removeLock(String Key) {
		Session session = JSFUtil.getSession();
		if(this._map.get(Key).equals(session.getEffectiveUserName())){
			synchronized (this._map) {
				this._map.remove(Key);
			}
		}
	}

	/**
	 * Elimina todos los lockeos de un usuario
	 * */
	public void removeAllMyLocks() {
		Session session = JSFUtil.getSession();
		this._map.values().removeAll(Collections.singleton(session.getEffectiveUserName()));
	}
	
	public void setMap(HashMap<String, String> map) {
		synchronized (this._map) {
			this._map = map;
		}
	}

	// ---------------------------------------------------------

	public HashMap<String, String> getMap() {
		return _map;
	}

}
