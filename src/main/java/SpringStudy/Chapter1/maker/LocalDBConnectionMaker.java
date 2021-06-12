package SpringStudy.Chapter1.maker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class LocalDBConnectionMaker implements ConnectionMaker {

	@Override
	public Connection makeConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.h2.Driver");
		Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/sa");
		return c;
	}
}
