package SpringStudy.Chapter1.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import SpringStudy.Chapter1.User;


public class AddStatement implements StatementStrategy {
	User user;
	

	public AddStatement(User user) {
		this.user = user;
	}
	
	@Override
	public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
		PreparedStatement ps = c.prepareStatement(
				"INSERT INTO users (id, name, password) VALUES (?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		return ps;
	}

}
