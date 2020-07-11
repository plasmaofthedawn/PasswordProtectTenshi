package me.zonal.passwordtenshi;

import java.sql.*; 

public class MySQL {

    String dbhost;
    int dbport;
    String dbname;
    String dbuser;
    String dbpass;

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
        Connection con = null;
        ResultSet rs = null;
        Statement st = null;
        final StringBuilder tablereq = new StringBuilder("CREATE TABLE IF NOT EXISTS users (");
                            tablereq.append("`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,");
                            tablereq.append("`uuid` VARCHAR(128) NOT NULL,");
                            tablereq.append("`password` VARCHAR(512) NOT NULL)");
        
        try {
            final String loginip = "jdbc:mysql://"+dbhost+":"+String.valueOf(dbport);
            Class.forName("com.mysql.jdbc.Driver"); 
            con = DriverManager.getConnection(loginip, dbuser, dbpass); 

            if(con != null){
                rs = con.getMetaData().getCatalogs();

                while(rs.next()){
                    String catalogs = rs.getString(1);
                    if(dbname.equals(catalogs)){
                        result = true;
                    }
                }
            } else{
                result = false;
            }
            
            con.close();
            con = DriverManager.getConnection(loginip+"/"+dbname, dbuser, dbpass); 
            st = con.createStatement();
            st.executeUpdate(tablereq.toString());

        } catch(Exception ex){
			ex.printStackTrace();
		} finally {
			if( rs != null){
				try{
				    rs.close();
				}
				catch(SQLException ex){
					ex.printStackTrace();
				}
			}
			if( con != null){
				try{
				    con.close();
				}
				catch(SQLException ex){
					ex.printStackTrace();
				}
            }
        }
        return result;
    }

    // public boolean add(){
    //     boolean result = false;
    //     Connection con = null;
    //     try {
    //         final String loginip = "jdbc:mysql://"+dbhost+":"+String.valueOf(dbport)+"/"+dbname;
    //         Class.forName("com.mysql.jdbc.Driver"); 
    //         con = DriverManager.getConnection(loginip, dbuser, dbpass); 

    //         if(con != null){
    //             stmt = (Statement) con.createStatement();
                
    //         }
    //         else{
    //             result = false;
    //         }
    //     } catch(Exception ex){
	// 		ex.printStackTrace();
	// 	} finally {
	// 		if( rs != null){
	// 			try{
	// 			    rs.close();
	// 			}
	// 			catch(SQLException ex){
	// 				ex.printStackTrace();
	// 			}
	// 		}
	// 		if( con != null){
	// 			try{
	// 			    con.close();
	// 			}
	// 			catch(SQLException ex){
	// 				ex.printStackTrace();
	// 			}
    //         }
    //     }
    //     return result;
    // }
} 