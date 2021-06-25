package SpringStudy.Chapter1.factory;

import SpringStudy.Chapter1.maker.ConnectionMaker;
import SpringStudy.Chapter1.maker.DConnectionMaker;
import SpringStudy.Chapter1.dao.MessegeDao;
import SpringStudy.Chapter1.UserDao;
import SpringStudy.Chapter1.dao.AccountDao;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {


    @Bean
    public BasicDataSource source() {
        BasicDataSource source = new BasicDataSource();
        source.setDriverClassName("org.h2.Driver");
        source.setUrl("jdbc:h2:tcp://localhost/~/study");
        source.setUsername("sa");
        return source;
    }


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
