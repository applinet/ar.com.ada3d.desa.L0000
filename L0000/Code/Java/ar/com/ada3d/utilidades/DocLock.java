package ar.com.ada3d.utilidades;

/*201330517 - FPR - Para lockear un documento
 * ------UTILIZA---------------------------------------------------------------------------------
 * <managed-bean-name>DocLock</managed-bean-name> 
 * con una beanScope de Aplicacion 
 * ------COMO FUNCIONA---------------------------------------------------------------------------
 * Agregar un lockeo: DocLock.addLock(ID,Info) --> ID = UNID del documento, Info = UserName(txt)
 * Quitar un lockeo: DocLock.removeLock(ID) --> ID = UNID del documento
 * Preguntar estado: DocLock.isLocked(ID) --> return true/false
 * Preguntar quien lo tiene: DocLock.getLock(ID) --> return Info(txt)
 * ------EJEMPLO---------------------------------------------------------------------------------
 * Agregar Lockeo a un documento que tomo el Id de un parametro de la url
 * var ID=context.getUrlParameter("documentId");
 * var Info=@Name("[CN]",@UserName());
 * DocLock.addLock(ID,Info)
 */

import java.io.Serializable;
import java.util.HashMap;

import com.ibm.xsp.designer.context.XSPContext;

public class DocLock implements Serializable {
	private static final long serialVersionUID = 2L;
	private HashMap<String, String> _map;

	// ---------------------------------------------------------

	public DocLock() {
		this._map = new HashMap<String, String>();
	}

	// ---------------------------------------------------------
	public boolean isLocked(String UNID) {
		boolean ret = false;
		synchronized (this._map) {
			ret = this._map.containsKey(UNID);
		}
		return ret;
	}

	public void setCurrentUserName(String UNID, String Key) {
		XSPContext ctx = JSFUtil.getContext();
		if (ctx != null) {
			com.ibm.designer.runtime.directory.DirectoryUser user = ctx
					.getUser();
			if (user.isAnonymous()) {
				ctx.redirectToPage(ctx.getUrl() + "?OpenXpage&Login");
			} else {
				synchronized (this._map) {
					this._map.put(UNID, Key);
				}
			}// end if user is anonymous
		}// end if ctx is not null
	}// end SetCurrentUserName

	public void addLock(String UNID, String Key) {
		synchronized (this._map) {
			this._map.put(UNID, Key);
		}
	}

	public String getLock(String UNID) {
		String ret;
		ret = this._map.get(UNID);
		return ret;
	}

	public void removeLock(String UNID) {
		synchronized (this._map) {
			this._map.remove(UNID);
		}
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
