package SpringStudy.Chapter1;


// JDBC 등록 조회

import SpringStudy.Chapter1.factory.DaoFactory;
import SpringStudy.Chapter1.maker.ConnectionMaker;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.*;

public class UserDao {

    private ConnectionMaker cm;
    private Connection c;
    private User user;

    public void setConnectionMaker(ConnectionMaker connectionMaker){
        this.cm = connectionMaker;
    }

    public UserDao(ConnectionMaker cm){
        this.cm = cm;
    }

    public UserDao(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        this.cm = context.getBean("connectionMaker",ConnectionMaker.class);
    }
    public UserDao userDao(){
        UserDao userDao = new UserDao();
        userDao.setConnectionMaker(cm);
        return userDao;
    }


    private Connection getConnection() throws ClassNotFoundException, SQLException{
        Class.forName("org.h2.Driver");
        Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/sa");

        return c;
    }


    public void add(User user) throws ClassNotFoundException, SQLException {
//        Class.forName("org.h2.Driver");
//        Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/sa");
        Connection c = getConnection();

        PreparedStatement ps = c.prepareStatement(
                "INSERT INTO users(id, name, password) VALUES(?,?,?)");
        ps.setString(1, user.getId());
        ps.setString(2,  user.getName());
        ps.setString(3, user.getPassword());


        ps.executeUpdate();


        ps.close();
        c.close();
    }


    public User get(String id) throws ClassNotFoundException, SQLException {

//        Class.forName("org.h2.Driver");
//        Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/sa");

//        Connection c = getConnection();
//        PreparedStatement ps = c.prepareStatement(
//                "SELECT * FROM users WHERE id = ?");
//        ps.setString(1, id);
//
//
//        ResultSet rs = ps.executeQuery();
//        rs.next();
//        User user = new User();
//        user.setId(rs.getString("id"));
//        user.setName(rs.getString("name"));
//        user.setPassword(rs.getString("password"));
//
//
//        rs.close();
//        ps.close();
//        c.close();
//        return user;

        //싱글 톤
        this.c = cm.makeConnection();
        PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM users WHERE id = ?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        rs.next();
        this.user = new User();
        this.user.setId(rs.getString("id"));
        this.user.setName(rs.getString("name"));
        this.user.setPassword(rs.getString("password"));
        return user;
    }

}
