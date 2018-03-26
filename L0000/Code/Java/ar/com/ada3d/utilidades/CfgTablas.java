package ar.com.ada3d.utilidades;

import java.io.Serializable;
import java.util.Vector;

import org.openntf.domino.*;
import org.openntf.domino.xsp.XspOpenLogUtil;

public class CfgTablas implements Serializable {

	private static final long serialVersionUID = 1L;

	public CfgTablas() {
		super();
		// Auto-generated constructor stub
	}

	private String clave = "";
	private String tabla = "";
	private String select = "";
	private String where = "";
	private Vector<String> resultado;
	private String msgConsola = "";
	private String strsSQL = "";

	public CfgTablas(String clave, String tabla, String select, String where, Vector<String> resultado,
			String msgConsola) {
		super();
		this.clave = clave;
		this.tabla = tabla;
		this.select = select;
		this.where = where;
		this.resultado = resultado;
		this.msgConsola = msgConsola;
	}

	@SuppressWarnings("unchecked")
	public CfgTablas(Document docTabla){
		this.clave = docTabla.getItemValueString("ds_Con_cod");
		this.tabla = docTabla.getItemValueString("ds_Tabla1_des");
		this.select = docTabla.getItemValueString("ds_select_des");
		this.where = docTabla.getItemValueString("ds_where_des");
		this.resultado = docTabla.getItemValue("ds_queObtengo_des", java.util.Vector.class);
		this.msgConsola = docTabla.getItemValueString("ds_MsgConsole_des");
	}
	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getTabla() {
		return tabla;
	}

	public void setTabla(String tabla_T) {
		this.tabla = tabla_T;
	}

	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public Vector<String> getResultado() {
		return resultado;
	}

	public void setResultado(Vector<String> resultado) {
		this.resultado = resultado;
	}

	public String getMsgConsola() {
		return msgConsola;
	}

	public void setMsgConsola(String msgConsola) {
		this.msgConsola = msgConsola;
	}

	public String getStrsSQL() {
		return strsSQL;
	}

	public void setStrsSQL(String strsSQL) {
		this.strsSQL = strsSQL;
	}

	public void setStrsSQL() {
		this.strsSQL = getSelect() + " FROM " + getTabla() + " " + getWhere();
	}

	@SuppressWarnings("unchecked")
	public void setStrWs(Document docTarget) {
		try {
			Session s = JSFUtil.getSession();
			Vector vecResult;
			vecResult = s.evaluate(this.getSelect(), docTarget);
			this.setSelect(vecResult.elementAt(0).toString());
		} catch (Exception e) {
			XspOpenLogUtil.logError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void setStrsSQL(Document docTarget) {
		Session s = JSFUtil.getSession();
		docTarget = docSinComillasSimples(docTarget);
		try {
			Vector vecResult;
			 //System.out.println("setStrsSQL_tabla=" + this.getTabla());
			vecResult = s.evaluate(this.getTabla(), docTarget);
			this.setTabla(vecResult.elementAt(0).toString());
			 //System.out.println("setStrsSQL_select=" + this.getSelect());
			vecResult = s.evaluate(this.getSelect(), docTarget);
			this.setSelect(vecResult.elementAt(0).toString());
			 //System.out.println("setStrsSQL_where=" + this.getWhere());
			if (this.getWhere().equals(""))
				this.setWhere("''");
			vecResult = s.evaluate(this.getWhere(), docTarget);
			this.setWhere(vecResult.elementAt(0).toString());
			// vecResult = s.evaluate(this.getResultado().elementAt(0),
			// docTarget);
			// this.setResultado(vecResult);
			this.strsSQL = getSelect() + " " + getTabla() + " " + getWhere();
			// docTarget.recycle();
		} catch (Exception e) {
			XspOpenLogUtil.logError(e);
		}

	}
	
	/** Del documento docTarget si viene con una comilla simples me rompe el sql
	 * Una manera de escapar eso es poner doble comillas simples que es lo que hace esta función
	 * @param  docTarget antes de hacer el evaluate
	 * @return el mismo docTarget con los campos escapados
	 * */
	private Document docSinComillasSimples(Document prm_docTarget){
		if (prm_docTarget != null){
			for (Item i : prm_docTarget.getItems()){
				if (i.getValueString().contains("'"))
					prm_docTarget.replaceItemValue(i.getName(), i.getValueString().replace("'", "''"));
			}
		}
		return prm_docTarget;
		
	}
}
