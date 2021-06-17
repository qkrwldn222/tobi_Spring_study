package SpringStudy.Chapter1.factory;

import SpringStudy.Chapter1.maker.ConnectionMaker;
import SpringStudy.Chapter1.maker.DConnectionMaker;
import SpringStudy.Chapter1.dao.MessegeDao;
import SpringStudy.Chapter1.UserDao;
import SpringStudy.Chapter1.dao.AccountDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoFactory {

    @Bean
    public UserDao userDao(){
        ConnectionMaker cm = new DConnectionMaker();
        return new UserDao(cm);
    }

    public AccountDao accountDao(){
//        ConnectionMaker cm = new DConnectionMaker();
//        return new AccountDao(cm);
        return new AccountDao(connectionMaker());
    }
    public MessegeDao messegeDao(){
//        ConnectionMaker cm = new DConnectionMaker();
//        return new MessegeDao(cm);
        return  new MessegeDao(connectionMaker());
    }

    @Bean(name = "connectionMaker")
    public ConnectionMaker connectionMaker(){
        return new DConnectionMaker();
    }

}
