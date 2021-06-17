package SpringStudy.Chapter2;



import org.springframework.context.annotation.Configuration;

import java.sql.*;

@Configuration
public class StudierDao {
    private Connection c;

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/sa");
        return c;
    }

    public Studier findByAge(int age) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = this.c.prepareStatement(
                    "SELECT * FROM studier WHERE age = ?");
            ps.setString(1, String.valueOf(age));
            rs = ps.executeQuery();
            rs.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        Studier studier = new Studier();
        studier.setAge(Integer.parseInt(rs.getString("age")));
        studier.setName(rs.getString("name"));
        studier.setAdress(rs.getString("adress"));
        studier.setAdvantages(rs.getString("advantage"));
        studier.setId(rs.getString("id"));
        rs.close();
        ps.close();

       return studier;

    }
    public void add(Studier studier) throws ClassNotFoundException, SQLException {
        Connection c = getConnection();
        PreparedStatement ps = c.prepareStatement(
                "INSERT INTO users(id, name, age,adress,advantage) VALUES(?,?,?,?,?)");
        ps.setString(1, studier.getId());
        ps.setString(2, studier.getName());
        ps.setString(2, String.valueOf(studier.getAge()));
        ps.setString(4, studier.getAdress());
        ps.setString(5, studier.getAdvantages());
        ps.executeUpdate();
        ps.close();
        c.close();
    }


}
