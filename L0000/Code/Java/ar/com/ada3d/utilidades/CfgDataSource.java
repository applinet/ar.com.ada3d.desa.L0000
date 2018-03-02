package ar.com.ada3d.utilidades;

import java.io.Serializable;

import org.openntf.domino.Document;

public class CfgDataSource implements Serializable {

	private static final long serialVersionUID = 1L;

	public CfgDataSource() {

	}

	public CfgDataSource(String urlConexion, String dataSource, int puerto, String userRead, String userWrite,
			String passRead, String passWrite) {
		super();
		this.urlConexion = urlConexion;
		this.dataSource = dataSource;
		this.puerto = puerto;
		this.userRead = userRead;
		this.userWrite = userWrite;
		this.passRead = passRead;
		this.passWrite = passWrite;
	}

			
	public CfgDataSource(Document docDataSource){
		this.urlConexion = docDataSource.getItemValueString("ds_JdbcUrl_des");
		this.dataSource = docDataSource.getItemValueString("ds_DataSource_des");
		this.puerto = docDataSource.getItemValueInteger("ds_Puerto_nro");
		this.userRead = docDataSource.getItemValueString("ds_LectUser_des");
		this.userWrite = docDataSource.getItemValueString("ds_EscrUser_des");
		this.passRead = docDataSource.getItemValueString("ds_LectPass_des");
		this.passWrite = docDataSource.getItemValueString("ds_EscrPass_des");
		
		
	}
	
	private String urlConexion = "";
	private String dataSource = "";
	private int puerto = 0;
	private String userRead = "";
	private String userWrite = "";
	private String passRead = "";
	private String passWrite = "";

	public String getUrlConexion() {
		return urlConexion;
	}
	
	

	public void setUrlConexion(String urlConexion) {
		this.urlConexion = urlConexion;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public int getPuerto() {
		return puerto;
	}

	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}

	public String getUserRead() {
		return userRead;
	}

	public void setUserRead(String userRead) {
		this.userRead = userRead;
	}

	public String getUserWrite() {
		return userWrite;
	}

	public void setUserWrite(String userWrite) {
		this.userWrite = userWrite;
	}

	public String getPassRead() {
		return passRead;
	}

	public void setPassRead(String passRead) {
		this.passRead = passRead;
	}

	public String getPassWrite() {
		return passWrite;
	}

	public void setPassWrite(String passWrite) {
		this.passWrite = passWrite;
	}
}
