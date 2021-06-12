package SpringStudy.Chapter1.test;

import SpringStudy.Chapter1.UserDao;
import SpringStudy.Chapter1.dao.CountingDaoFactory;
import SpringStudy.Chapter1.maker.CountingConnectionMaker;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class UserDaoConnectionCountingTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CountingDaoFactory.class);
        UserDao dao = context.getBean("userDao",UserDao.class);

        CountingConnectionMaker ccm = context.getBean("connectionMaker",CountingConnectionMaker.class);
        System.out.println("Connection counter : " + ccm.getCounter());
    }
}
