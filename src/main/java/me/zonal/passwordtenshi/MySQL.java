package me.zonal.passwordtenshi;

import java.sql.*;

public class MySQL {

	//db credentials
	String dbhost;
	int dbport;
	String dbname;
	String dbuser;
	String dbpass;
	//sql connections
	Connection con;
	ResultSet rs;
	Statement st;

	final String loginip;

	public MySQL (final String dbhost, final int dbport, final String dbname, final String dbuser, final String dbpass) {
		this.dbhost = dbhost;
		this.dbport = dbport;
		this.dbname = dbname;
		this.dbuser = dbuser;
		this.dbpass = dbpass;
		this.loginip = "jdbc:mysql://"+dbhost+":"+String.valueOf(dbport)+"/"+dbname + "?useSSL=false";
	}

	public boolean check() {
		boolean result = false;
		final StringBuilder tablereq = new StringBuilder("CREATE TABLE IF NOT EXISTS users (");
							tablereq.append("`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,");
							tablereq.append("`uuid` VARCHAR(128) NOT NULL,");
							tablereq.append("`password` VARCHAR(512) NOT NULL)");

		try {
			Class.forName("com.mysql.jdbc.Driver"); 
			con = DriverManager.getConnection("jdbc:mysql://"+dbhost+":"+String.valueOf(dbport), dbuser, dbpass); 
			rs = con.getMetaData().getCatalogs();
			while (rs.next()) {
				String catalogs = rs.getString(1);
				if (dbname.equals(catalogs)) {
					result = true;
				}
			}
			con.close();
			con = DriverManager.getConnection(loginip, dbuser, dbpass);
			st = con.createStatement();
			st.executeUpdate(tablereq.toString());
		} catch(Exception ex){
			ex.printStackTrace();
			result = false;
		}
		return result;
	}

	public boolean addpass(String playeruuid, String password){
		boolean result = false;
		final String insertquery = "INSERT INTO users (uuid, password) VALUES ('"+playeruuid+"', '"+password+"');";

		try {
			Class.forName("com.mysql.jdbc.Driver"); 
			con = DriverManager.getConnection(loginip, dbuser, dbpass); 
			st = con.createStatement();
			int response = st.executeUpdate(insertquery);
			if (response > 0){
				result = true;
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		closeSQL();
		return result;
	}

	public boolean deletepass(String playeruuid){
		boolean result = false;
		final String delquery = "DELETE FROM users WHERE uuid = "+"'"+playeruuid+"'";

		try {
			Class.forName("com.mysql.jdbc.Driver"); 
			con = DriverManager.getConnection(loginip, dbuser, dbpass); 
			st = con.createStatement();
			int response = st.executeUpdate(delquery);
			if (response > 0){
				result = true;
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		closeSQL();
		return result;
	}

	public String getpass(String playeruuid){
		String retrievedpass = null;
		final String getquery = "SELECT password FROM users WHERE uuid = "+"'"+playeruuid+"'";

		try {
			Class.forName("com.mysql.jdbc.Driver"); 
			con = DriverManager.getConnection(loginip, dbuser, dbpass);
			st = con.createStatement();
			rs = st.executeQuery(getquery);
			// if the user has not made a password yet
			if (!rs.next()) {
				return null;
			}
			retrievedpass = rs.getString("password");
		} catch(Exception ex){
			ex.printStackTrace();
		}
		closeSQL();
		return retrievedpass;
	}

	private void closeSQL(){
		try {
			if( con != null){
				try{
					con.close();
				}
				catch(SQLException ex){
					ex.printStackTrace();
				}
			}
			if( st != null){
				try{
					st.close();
				}
				catch(SQLException ex){
					ex.printStackTrace();
				}
			}
			if( rs != null){
				try{
					rs.close();
				}
				catch(SQLException ex){
					ex.printStackTrace();
				}
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
} 