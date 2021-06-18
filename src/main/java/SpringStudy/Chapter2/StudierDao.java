package SpringStudy.Chapter2;

import org.springframework.context.annotation.Configuration;

import java.sql.*;

@Configuration
public class StudierDao {
    private Connection c;


    public StudierDao studierDao(){
        return new StudierDao();
    }


    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/study","sa" , "");
        return c;
    }

    public Studier findByName(String name) throws SQLException, ClassNotFoundException {
        Connection c  = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        ps = c.prepareStatement(
                    "SELECT * FROM studier WHERE name = ?");
        ps.setString(1, name);
        rs = ps.executeQuery();
        Studier studier = new Studier();
        if (rs.next()) {
            studier.setAge(Integer.parseInt(rs.getString("age")));
            studier.setName(rs.getString("name"));
            studier.setIdx(Integer.parseInt(rs.getString("idx")));
        }
        rs.close();
        ps.close();
        c.close();
       return studier;
    }

    public void add(Studier studier) throws ClassNotFoundException, SQLException {

        Connection c = getConnection();
        PreparedStatement ps = c.prepareStatement(
                "INSERT INTO studier(name, age) VALUES(?,?)");
        ps.setString(1, studier.getName());
        ps.setString(2, String.valueOf(studier.getAge()));
        ps.executeUpdate();
        ps.close();
        c.close();
    }

    public int getCount() throws SQLException, ClassNotFoundException {
        Connection c = getConnection();
        PreparedStatement ps = c.prepareStatement("SELECT count(*) from studier");
        ResultSet rs =  ps.executeQuery();
        int count = 0;
        if (rs.next()) count = rs.getInt(1);
        rs.close();
        ps.close();
        c.close();
        return count;
    }

    public int deleteAll() throws SQLException, ClassNotFoundException{
        Connection c = getConnection();
        PreparedStatement ps = c.prepareStatement("DELETE from studier");
        int row = ps.executeUpdate();
        ps.close();
        c.close();
        return row;
    }

    public int sendMsg(String msg,String sender,String receiver) throws SQLException, ClassNotFoundException {
        Studier senderStu = findByName(sender);
        Studier receiverStu = findByName(receiver);
        Connection c = getConnection();
        PreparedStatement ps = c.prepareStatement("INSERT INTO msg(contents,sender,receiver) VALUES(?,?,?)");
        ps.setString(1,msg);
        ps.setString(2,String.valueOf(senderStu.getIdx()));
        ps.setString(3,String.valueOf(receiverStu.getIdx()));
        int row = ps.executeUpdate();
        ps.close();
        c.close();
        return row;
    }

    public int studierUpdate(Studier studier) throws SQLException, ClassNotFoundException {
        Connection c = getConnection();
        PreparedStatement ps = c.prepareStatement("UPDATE studier SET name = ? , age = ? where idx = ?");

        ps.setString(1,studier.getName());
        ps.setString(2,String.valueOf(studier.getAge()));
        ps.setString(3,String.valueOf(studier.getIdx()));

        int row = ps.executeUpdate();

        ps.close();
        c.close();

        return row;
    }

    public Studier readMsg(String name) throws SQLException, ClassNotFoundException {
        Connection c = getConnection();
        Studier studier = findByName(name);
        PreparedStatement ps = c.prepareStatement("SELECT contents FROM MSG where receiver = ?");
        ps.setInt(1,studier.getIdx());
        ResultSet rs = ps.executeQuery();

        if (rs.next()){
            studier.setMsg(rs.getString(1));
            ps = c.prepareStatement("UPDATE msg SET read=0 WHERE idx = ?");
            ps.setInt(1,studier.getIdx());
        }

        rs.close();
        ps.close();
        c.close();

        return studier;
    }
}
