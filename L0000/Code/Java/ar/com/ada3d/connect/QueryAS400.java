package ar.com.ada3d.connect;

import java.io.Serializable;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openntf.domino.Document;
import ar.com.ada3d.model.Porcentual;
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
						if(rs.getString(s) == null){
							temp = temp + "";
						}else{
							temp = temp + rs.getString(s).trim();
						}
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

	
	/**
	 * Realiza un Update al AS400 de Honorariosutilizando un documento de configuración que
	 * envio el nombre por parámetros. Si le envío un doc en parametros utiliza
	 * ese, sino el de parametros. Tener en cuenta que si envio un doc, de
	 * cargar la biblioteca tal como la genera docDummy
	 * @param clave que busca en la base de configuracion
	 * @param documento dummy que evalua con el doc de configuracion
	 * @param lista de porcentuales
	 * @return: Verdadero si realizó el update, sino falso
	 */
	public boolean updateBatchAS(String param_clave, Document param_doc,  List<Porcentual> prm_listaHonorariosEdificiosTrabajo) {
		Connection connection = null;
		PreparedStatement pstmt_1 = null;
		PreparedStatement pstmt_2 = null;
		PreparedStatement pstmt_3 = null;
		PreparedStatement pstmt_4 = null;
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

		if (configTabla.getMsgConsola().equals("1"))
			System.out.println(configTabla.getStrsSQL());
		
		try {
			DriverManager
					.registerDriver(new com.ibm.as400.access.AS400JDBCDriver());
			connection = DriverManager.getConnection(configDs.getUrlConexion(),
					configDs.getUserWrite(), configDs.getPassWrite());
			
			ArrayList<String> tempControlPorcentual = new ArrayList<String>();
			pstmt_1 = connection.prepareStatement(configTabla.getStrsSQL() + "E391=? WHERE EDIF=?");
			pstmt_2 = connection.prepareStatement(configTabla.getStrsSQL() + "E392=? WHERE EDIF=?");
			pstmt_3 = connection.prepareStatement(configTabla.getStrsSQL() + "E441=? WHERE EDIF=?");
			pstmt_4 = connection.prepareStatement(configTabla.getStrsSQL() + "E442=? WHERE EDIF=?");
			
			for (Porcentual myPorcentual : prm_listaHonorariosEdificiosTrabajo){
				switch (myPorcentual.getPorc_posicion()){
				case 1:
					pstmt_1.setString(1, ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(myPorcentual.getPorc_importeHonorariosMasivo(), 2));
					pstmt_1.setString(2, myPorcentual.getPorc_edf_codigo());
					pstmt_1.addBatch();
					if(!tempControlPorcentual.contains("1"))
						tempControlPorcentual.add("1");
					break; 
				case 2:
					pstmt_2.setString(1, ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(myPorcentual.getPorc_importeHonorariosMasivo(), 2));
					pstmt_2.setString(2, myPorcentual.getPorc_edf_codigo());
					pstmt_2.addBatch();
					if(!tempControlPorcentual.contains("2"))
						tempControlPorcentual.add("2");
					break; 
				case 3:
					pstmt_3.setString(1, ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(myPorcentual.getPorc_importeHonorariosMasivo(), 2));
					pstmt_3.setString(2, myPorcentual.getPorc_edf_codigo());
					pstmt_3.addBatch();
					if(!tempControlPorcentual.contains("3"))
						tempControlPorcentual.add("3");
					break; 
				case 4:
					pstmt_4.setString(1, ar.com.ada3d.utilidades.Conversores.bigDecimalToAS400(myPorcentual.getPorc_importeHonorariosMasivo(), 2));
					pstmt_4.setString(2, myPorcentual.getPorc_edf_codigo());
					pstmt_4.addBatch();
					if(!tempControlPorcentual.contains("4"))
						tempControlPorcentual.add("4");
					break; 
				}
			}
			
			int[] numUpdates = null;
			if(tempControlPorcentual.contains("1")){
				numUpdates = pstmt_1.executeBatch();
				for (int i=0; i < numUpdates.length; i++) {
					if (numUpdates[i] == Statement.SUCCESS_NO_INFO)
						System.out.println("Execution_1 " + i +": unknown number of rows updated");
				}
			}
			if(tempControlPorcentual.contains("2")){
				numUpdates = pstmt_2.executeBatch();
				for (int i=0; i < numUpdates.length; i++) {
					if (numUpdates[i] == Statement.SUCCESS_NO_INFO)
						System.out.println("Execution_2 " + i +": unknown number of rows updated");
				}
			}
			if(tempControlPorcentual.contains("3")){
				numUpdates = pstmt_3.executeBatch();
				for (int i=0; i < numUpdates.length; i++) {
					if (numUpdates[i] == Statement.SUCCESS_NO_INFO)
						System.out.println("Execution_3 " + i +": unknown number of rows updated");
				}
			}
			if(tempControlPorcentual.contains("4")){
				numUpdates = pstmt_4.executeBatch();
				for (int i=0; i < numUpdates.length; i++) {
					if (numUpdates[i] == Statement.SUCCESS_NO_INFO)
						System.out.println("Execution_4 " + i +": unknown number of rows updated");
				}
			}		
			connection.commit();
			
			return true;
			
			
		} catch (SQLException e) {
			System.out.println("**ERROR UPDATE ** (param_clave:" + param_clave
					+ ") - " + e.getMessage());
			return false;
		}
		
		finally {
			try {
				if (pstmt_1 != null)
					pstmt_1.close();
				if (pstmt_2 != null)
					pstmt_2.close();
				if (pstmt_3 != null)
					pstmt_3.close();
				if (pstmt_4 != null)
					pstmt_4.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
			}
		}
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
