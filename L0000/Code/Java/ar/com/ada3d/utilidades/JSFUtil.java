package ar.com.ada3d.utilidades;

import java.util.Map;
import org.openntf.domino.*;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.http.HttpServletRequest;


import com.ibm.xsp.designer.context.XSPContext;
import com.ibm.xsp.page.compiled.ExpressionEvaluatorImpl;

@SuppressWarnings("unchecked")
public class JSFUtil {

	public JSFUtil() {

	}

	public static XSPContext getContext() {
		return XSPContext.getXSPContext(FacesContext.getCurrentInstance());
	}

	public static String getServerUrl() {
		/***********************************************************************************
		 * Name : getServerurl Decription : returns the path (incl the
		 * domainname)
		 ***********************************************************************************/
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Object request = facesContext.getExternalContext().getRequest();
		if (request instanceof HttpServletRequest) {
			String server = ((HttpServletRequest) request).getHeader("Host");
			String protocolFull = ((HttpServletRequest) request).getProtocol(); // returns
			// protocol/version
			// (ex:
			// HTTP/1.1);
			String protocolOnly = protocolFull.split("/")[0]; // get just the
			// protocol
			// (lowercase it
			// in next
			// step...);
			String port = "";
			switch (((HttpServletRequest) request).getServerPort()) {
			case 80:
				port = "";
				break;
			case 443:
				port = "";
				protocolOnly = "https";
				break;
			default:
				port = ":" + ((HttpServletRequest) request).getServerPort();
				;
			}

			return protocolOnly.toLowerCase() + "://" + server + port;

		}

		return "";
	}

	public static Map getApplicationScope() {
		return (Map) resolveVariable("applicationScope");
	}

	public static Database getCurrentDatabase() {
		return (Database) getSession().getCurrentDatabase();
	}

	public static Map getRequestScope() {
		return (Map) resolveVariable("requestScope");
	}

	public static Session getSession() {
		return (Session) org.openntf.domino.utils.Factory.getSession(org.openntf.domino.utils.Factory.SessionType.CURRENT);
	}

	public static Session getSessionAsSigner() {
		return (Session) resolveVariable("sessionAsSigner");
	}

	public static Map getSessionScope() {
		return (Map) resolveVariable("sessionScope");
	}

	public static Map getViewScope() {
		return (Map) resolveVariable("viewScope");
	}

	public static Name getCurrentUser() {
		Session session = getSession();
			return session.createName(session.getEffectiveUserName());
	}

	public static Object resolveVariable(String variable) {
		return FacesContext.getCurrentInstance().getApplication()
				.getVariableResolver().resolveVariable(
						FacesContext.getCurrentInstance(), variable);
	}

	
	public static Database getDbCfg()  {
		View currentDbProfileView = null;
		Document docUbicTablas = null;
		Database dbTablas = null;

		currentDbProfileView = getCurrentDatabase().getView("v.Sys.Cfg");
		docUbicTablas = currentDbProfileView.getFirstDocumentByKey("Configuracion", true);
		String server = docUbicTablas.getItemValueString("conf_server");
		String path = docUbicTablas.getItemValueString("conf_path");
		dbTablas = getSession().getDatabase(server, path);
		return dbTablas;
	}

	public static String getOpcionesClave(String clave) {
		String result;
		View vOpciones = getDbCfg().getView("v.Sys.Opciones.Clave");

		ViewEntryCollection entryCol = vOpciones.getAllEntriesByKey(clave);
		if (entryCol.getCount() > 0) {
			ViewEntry entryResult = entryCol.getFirstEntry();
			result = entryResult.getDocument().getItemValueString(
					"opt_Codigo_des");
			return result;
		}
		return "";

	}
	
	public static Document getDocConexiones_y_Tablas(String clave){
		View vOpciones = getDbCfg().getView("v.Sys.DataSource");
		return vOpciones.getFirstDocumentByKey(clave, true);
	}

	public static String getFieldValueEvaluateSsjsInJava(
			FacesContext facesContext, String valueExpr) {

		ExpressionEvaluatorImpl evaluator = new ExpressionEvaluatorImpl(
				facesContext);
		ValueBinding vb = evaluator.createValueBinding(facesContext
				.getViewRoot(), valueExpr, null, null);
		String vreslt = (String) vb.getValue(facesContext);
		return vreslt;
	}

	public static Object getObjFieldValueEvaluateSsjsInJava(
			FacesContext facesContext, String campo) {

		String valueExpr = "#{javascript: getComponent('" + campo
				+ "').getValue();}";
		ExpressionEvaluatorImpl evaluator = new ExpressionEvaluatorImpl(
				facesContext);
		ValueBinding vb = evaluator.createValueBinding(facesContext
				.getViewRoot(), valueExpr, null, null);
		Object vreslt = vb.getValue(facesContext);
		return vreslt;
	}
	
	//Devuelve la biblioteca de la administracion (ej: L8669B)
	public static String getBiblioteca(String strTipo){
		String strFileName = getCurrentDatabase().getFileName(); 
		return strFileName.substring(strFileName.length() - 9, 5) + strTipo;
		
	}

	//Devuelve un documento falso
	public static Document getDocDummy(){
		return getCurrentDatabase().createDocument();
	}

}
