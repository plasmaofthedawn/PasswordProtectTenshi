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

	public MySQL (final String dbhost2) {
		this.dbhost = dbhost2;
	}
	public void setdbport(final int dbport2) {
		dbport = dbport2;
	}
	public void setdbname(final String dbname2) {
		dbname = dbname2;
	}
	public void setdbuser(final String dbuser2) {
		dbuser = dbuser2;
	}
	public void setdbpass(final String dbpass2) {
		dbpass = dbpass2;
	}

	public boolean check() {
		boolean result = false;
		final String loginip = "jdbc:mysql://"+dbhost+":"+String.valueOf(dbport);
		final StringBuilder tablereq = new StringBuilder("CREATE TABLE IF NOT EXISTS users (");
							tablereq.append("`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,");
							tablereq.append("`uuid` VARCHAR(128) NOT NULL,");
							tablereq.append("`password` VARCHAR(512) NOT NULL)");
		try {
			Class.forName("com.mysql.jdbc.Driver"); 
			con = DriverManager.getConnection(loginip, dbuser, dbpass); 

			if(con != null) {
				rs = con.getMetaData().getCatalogs();
				while (rs.next()) {
					String catalogs = rs.getString(1);
					if (dbname.equals(catalogs)) {
						result = true;
					}
				}

				con.close();
				con = DriverManager.getConnection(loginip + "/" + dbname, dbuser, dbpass);
				st = con.createStatement();
				st.executeUpdate(tablereq.toString());

			}

		} catch(Exception ex){
			ex.printStackTrace();
			result = false;
		} 
		closeSQL();
		return result;
	}

	public boolean addpass(String playeruuid, String password){
		boolean result = false;
		final String loginip = "jdbc:mysql://"+dbhost+":"+String.valueOf(dbport)+"/"+dbname;
		final String insertquery = "INSERT INTO users (uuid, password) VALUES ("+playeruuid+","+password+")";
		
		try {
			Class.forName("com.mysql.jdbc.Driver"); 
			con = DriverManager.getConnection(loginip, dbuser, dbpass); 
			if(con != null){
				st = con.createStatement();
				st.executeUpdate(insertquery);
				result = true;
			}
			else{
				result = false;
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		closeSQL();
		return result;
	}

	public boolean deletepass(String playeruuid){
		boolean result = false;
		final String loginip = "jdbc:mysql://"+dbhost+":"+String.valueOf(dbport)+"/"+dbname;
		final String delquery = "DELETE FROM users WHERE uuid = "+"'"+playeruuid+"'";

		try {
			Class.forName("com.mysql.jdbc.Driver"); 
			con = DriverManager.getConnection(loginip, dbuser, dbpass); 
			if(con != null){
				st = con.createStatement();
				st.executeUpdate(delquery);
				result = true;
			}
			else{
				result = false;
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		closeSQL();
		return result;
	}

	public String getpass(String playeruuid){
		String retrievedpass = null;
		final String loginip = "jdbc:mysql://"+dbhost+":"+String.valueOf(dbport)+"/"+dbname;
		final String getquery = "SELECT password FROM users WHERE uuid = "+"'"+playeruuid+"'";

		try {
			Class.forName("com.mysql.jdbc.Driver"); 
			con = DriverManager.getConnection(loginip, dbuser, dbpass);
			st = con.createStatement();
			rs = st.executeQuery(getquery);
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