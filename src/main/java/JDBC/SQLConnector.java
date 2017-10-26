package JDBC;

//import com.mysql.jdbc.Connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SQLConnector {
    private static void doSshTunnel(String strSshUser, String strSshPassword, String strSshHost, int nSshPort,
	    String strRemoteHost, int nLocalPort, int nRemotePort) throws JSchException {
	final JSch jsch = new JSch();
	Session session = jsch.getSession(strSshUser, strSshHost, 2222);
	session.setPassword(strSshPassword);

	final Properties config = new Properties();
	config.put("StrictHostKeyChecking", "no");
	session.setConfig(config);

	session.connect();
	session.setPortForwardingL(nLocalPort, strRemoteHost, nRemotePort);
    }

    public static void main(String[] args) {
	try {
	    String strSshUser = "ubuntu"; // SSH loging username
	    String strSshPassword = "497387e1f61ccbac2580ec54"; // SSH login
								// password
	    String strSshHost = "127.0.0.1"; // hostname or ip or SSH server
	    int nSshPort = 2222; // remote SSH host port number
	    String strRemoteHost = "127.0.0.1"; // hostname or ip of your
						// database server
	    int nLocalPort = 3306; // local port number use to bind SSH tunnel
	    int nRemotePort = 3306; // remote port number of your database
	    String strDbUser = "root"; // database loging username
	    String strDbPassword = "root"; // database login password
	    String[] db = { "USE db_test1;", "USE db_test3;", }; // Use
										  // DB
	    // Query to Execute
	    String[] queries = {
		    "SELECT * FROM tbl_2row WHERE id = (SELECT min(a.id) FROM tbl_2row a, tbl_2row b WHERE a.id > b.id);",
		    "SELECT name, SUM(revenue) AS total FROM tbl_revenue GROUP BY name ORDER BY total DESC LIMIT 1;" };

	    SQLConnector.doSshTunnel(strSshUser, strSshPassword, strSshHost, nSshPort, strRemoteHost, nLocalPort,
		    nRemotePort);

	    Class.forName("com.mysql.jdbc.Driver");
	    Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:" + nLocalPort, strDbUser,
		    strDbPassword);
	    // Create Statement Object
	    java.sql.Statement stmt = con.createStatement();
	    // Iterate DB Array
	    for (String i : db) {
		stmt.executeQuery(i);
		// Execute the SQL Query. Store results in ResultSet
		ResultSet rs = stmt.executeQuery(queries[Arrays.asList(db).indexOf(i)]);
		// // While Loop to iterate through all data and print results
		
		while (rs.next()) {
		    String fRow = rs.getString(1);
		    String sRow = rs.getString(2);
		    
		    System.out.println("------------");
		    System.out.println("| " + fRow + " | " + sRow + " |");
		    System.out.println("------------");
		}

	    }
	    // // closing DB Connection
	    con.close();

	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    System.exit(0);
	}
    }
}