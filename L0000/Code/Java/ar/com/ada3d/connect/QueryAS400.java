package ar.com.ada3d.connect;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import org.openntf.domino.Document;

import ar.com.ada3d.utilidades.CfgDataSource;
import ar.com.ada3d.utilidades.CfgTablas;
import ar.com.ada3d.utilidades.JSFUtil;

import lotus.domino.NotesException;

public class QueryAS400 implements Serializable {

	private static final long serialVersionUID = 1L;
	private CfgDataSource configDs;

	public QueryAS400() {
		// No Args
	}

	/*
	 * Para iniciar la conexion con el AS400, toma los datos del documento de
	 * configuracion BaseConexionAS400
	 * 
	 * @return: verdadero si obtiene los datos, sino falso
	 * 
	 * @usedIn: getSelectAS, updateAS
	 */
	public Boolean initConexion() {
		//System.out.println("FPR - Init Conexion Base AS400");
		Document docDS = JSFUtil.getDocConexiones_y_Tablas("BaseConexionAS400");
		if (docDS == null)
			return false;
		configDs = new CfgDataSource(docDS);
		return true;
	}

	/*
	 * Realiza un select al AS400 utilizando un documento de configuración que
	 * envio el nombre por parámetros. Si le envío un doc en parametros utiliza
	 * ese, sino el de parametros. Tener en cuenta que si envio un doc, de
	 * cargar la biblioteca tal como la genera docDummy
	 * 
	 * @return: Arreglo con Strings
	 */
	public ArrayList<String> getSelectAS(String param_clave,
			Document param_doc, boolean param_booDescColumnas)
			throws NotesException {
		if (!initConexion())
			return null;
		Document docTabla = JSFUtil.getDocConexiones_y_Tablas(param_clave);
		if (docTabla == null){
			System.out.println("*** ERROR -> FALTA DOCUMENTO DE CONFIGURACION: " + param_clave);
			return null;
		}
		CfgTablas configTabla = new CfgTablas(docTabla);

		// INI - Siempre hay que hacer esto para que complete la biblioteca en
		// el sql
		if (param_doc == null) {
			// Invento un doc y le paso la biblioteca de la base que estoy
			Document docDummy = JSFUtil.getDocDummy();
			docDummy.appendItemValue("biblioteca", JSFUtil.getBiblioteca("B"));
			configTabla.setStrsSQL(docDummy);
		} else {
			// Al doc que tengo le agrego la biblioteca
			param_doc.appendItemValue("biblioteca", JSFUtil.getBiblioteca("B"));
			configTabla.setStrsSQL(param_doc);
		}
		// FIN - Siempre hay que hacer esto para que complete la biblioteca en
		// el sql

		Connection connection = null;

		ArrayList<String> returnArrlist = new ArrayList<String>(); // variable
		// que
		// devuelvo
		Vector<String> arrcampos = configTabla.getResultado();// Desde documento
		// de
		// configuracion
		if (configTabla.getMsgConsola().equals("1")){
			System.out.println("KEY: " + configTabla.getClave());
			System.out.println("SQL: " + configTabla.getStrsSQL());
		}
		String columna = "";
		try {
			DriverManager
					.registerDriver(new com.ibm.as400.access.AS400JDBCDriver());
			connection = DriverManager.getConnection(configDs.getUrlConexion(),
					configDs.getUserRead(), configDs.getPassRead());
			Statement stmt = connection.createStatement();

			
			ResultSet rs = stmt.executeQuery(configTabla.getStrsSQL());

			if (param_booDescColumnas) {
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				System.out
						.println("******************** COLUMNAS getSelectAS  *******************************************");
				for (int i = 1; i <= columnCount; ++i) {
					System.out.println("Columna:" + rsmd.getColumnLabel(i));
				}
			}
			
			

			int cont = 0;

			while (rs.next()) {
				cont++;
				String temp = "";
				for (String s : arrcampos) {
					columna = s.toString();
					if (s.contains("@text_")) {
						temp = temp + s.substring(6);
					} else {
						temp = temp + rs.getString(s).trim();
					}
				}
				if (configTabla.getMsgConsola().equals("1"))
					System.out.println("LINEA: " + temp);
				returnArrlist.add(temp);
			}
			close(rs);
		}

		catch (Exception e) {
			System.out.println("**ERROR** (col:" + columna + ") - "
					+ e.getMessage());
			return returnArrlist;
		}

		finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
			}
		}
		return returnArrlist;

	}

	/*
	 * Realiza un Update al AS400 utilizando un documento de configuración que
	 * envio el nombre por parámetros. Si le envío un doc en parametros utiliza
	 * ese, sino el de parametros. Tener en cuenta que si envio un doc, de
	 * cargar la biblioteca tal como la genera docDummy
	 * 
	 * @return: Verdadero si realizó el update, sino falso
	 */
	public boolean updateAS(String param_clave, Document param_doc) {
		Connection connection = null;
		
		if (!initConexion()) return false;
		Document docTabla = JSFUtil.getDocConexiones_y_Tablas(param_clave);
		if (docTabla == null)
			return false;
		CfgTablas configTabla = new CfgTablas(docTabla);

		// INI - Siempre hay que hacer esto para que complete la biblioteca en
		// el sql
		if (param_doc == null) {
			// Invento un doc y le paso la biblioteca de la base que estoy
			Document docDummy = JSFUtil.getDocDummy();
			docDummy.appendItemValue("biblioteca", JSFUtil.getBiblioteca("B"));
			configTabla.setStrsSQL(docDummy);
		} else {
			// Al doc que tengo le agrego la biblioteca
			param_doc.appendItemValue("biblioteca", JSFUtil.getBiblioteca("B"));
			configTabla.setStrsSQL(param_doc);
		}
		// FIN - Siempre hay que hacer esto para que complete la biblioteca en
		// el sql

		
		try {
			DriverManager
					.registerDriver(new com.ibm.as400.access.AS400JDBCDriver());
			connection = DriverManager.getConnection(configDs.getUrlConexion(),
					configDs.getUserWrite(), configDs.getPassWrite());
			Statement stmt = connection.createStatement();
			
			if (configTabla.getMsgConsola().equals("1"))
				System.out.println(configTabla.getStrsSQL());
			
			int resultStrSql = stmt.executeUpdate(configTabla.getStrsSQL());
			if (resultStrSql > 0){
				close(stmt);
				return true;
			}
		
		} catch (SQLException e) {
			System.out.println("**ERROR UPDATE ** (param_clave:" + param_clave
					+ ") - " + e.getMessage());
			return false;
		}
		
		finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
			}
		}
		return false;

	}

	
	public static void close(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close(Statement st) {
		try {
			if (st != null) {
				st.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
