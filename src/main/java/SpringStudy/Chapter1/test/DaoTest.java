package SpringStudy.Chapter1.test;

import SpringStudy.Chapter1.UserDao;
import SpringStudy.Chapter1.factory.DaoFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DaoTest {
    public static void main(String[] args) {
//        ConnectionMaker cm = new DConnectionMaker();
//
//        UserDao dao = new UserDao(cm);

//        UserDao dao = new DaoFactory().userDao();
        DaoFactory daoFactory = new DaoFactory();
        UserDao uDao1 = daoFactory.userDao();
        UserDao uDao2 = daoFactory.userDao();

        System.out.println(uDao1);
        System.out.println(uDao2);

        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao dao1 = context.getBean("userDao",UserDao.class);
        UserDao dao2 = context.getBean("userDao",UserDao.class);

        System.out.println(dao1);
        System.out.println(dao2);
    }




}
