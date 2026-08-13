package helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider {
	static Connection con;
	
	public static Connection getCon() {
		try {
			if(con==null || con.isClosed()) {
				try {
					Class.forName("oracle.jdbc.OracleDriver");
					con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe","c##osms","oracle123");
//					con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe","c##osms_test","oracle123");

					System.out.println("DB Connected!");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return con;
	}
}
