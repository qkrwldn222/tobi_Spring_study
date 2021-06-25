package SpringStudy.Chapter1;


// JDBC 등록 조회

import SpringStudy.Chapter1.dao.AddStatement;
import SpringStudy.Chapter1.dao.DeleteAllStatement;
import SpringStudy.Chapter1.dao.StatementStrategy;
import SpringStudy.Chapter1.factory.DaoFactory;
import SpringStudy.Chapter1.maker.ConnectionMaker;

import SpringStudy.Chapter1.test.JdbcContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.*;

public class UserDao {
    public void setJdbcContext(JdbcContext jdbcContext){
        this.jdbcContext = jdbcContext;
    }
    private ConnectionMaker cm;
    private Connection c;
    private User user;
    private JdbcContext jdbcContext;


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
            this.jdbcContext.workWithStatementStrategy(
                    new StatementStrategy(){
                        @Override
                        public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                            PreparedStatement ps = c.prepareStatement("INSERT INTO users(id, name, password) VALUES(?,?,?)");
                            ps.setString(1,user.getId());
                            ps.setString(2,user.getName());
                            ps.setString(3,user.getPassword());
                            return ps;
                        }
                    }
            );
    }

//        Connection c = getConnection();
//
//        PreparedStatement ps = c.prepareStatement(
//                "INSERT INTO users(id, name, password) VALUES(?,?,?)");
//        ps.setString(1, user.getId());
//        ps.setString(2,  user.getName());
//        ps.setString(3, user.getPassword());
//
//
//        ps.executeUpdate();
//
//
//        ps.close();
//        c.close();
//    }



    public User get(String id) throws ClassNotFoundException, SQLException {
        PreparedStatement ps = null;
        Connection c = null;
        ResultSet rs = null;

        try {
            c = cm.makeConnection();
            ps = c.prepareStatement(
                    "SELECT * FROM users WHERE id = ?");
            ps.setString(1, id);
            rs = ps.executeQuery();
            rs.next();
            this.user = new User();
            this.user.setId(rs.getString("id"));
            this.user.setName(rs.getString("name"));
            this.user.setPassword(rs.getString("password"));
        }catch (SQLException e){
            throw e;
        }finally {
            if (rs != null){
                try{
                    rs.close();
                }catch (SQLException e2){
                }
            }
            if (ps != null){
                try{
                    ps.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if (c != null){
                try {
                    c.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

        }


        return user;
    }
    public void deleteAll() throws ClassNotFoundException{
        Connection c= null;
        PreparedStatement ps = null;

        try {
            c = getConnection();
            StatementStrategy st = new DeleteAllStatement();
            ps = st.makePreparedStatement(c);
        }catch (SQLException e){
            e.getStackTrace();
        }finally {
            if (ps != null){
                try{
                    ps.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if (c != null){
                try {
                    c.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }
    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException, ClassNotFoundException {
        Connection c =null;
        PreparedStatement ps = null;

        try{
            c = getConnection();
            ps = stmt.makePreparedStatement(c);
            ps.executeQuery();
        }catch (SQLException e){
            throw e;
        }finally {

        }
    }
}
