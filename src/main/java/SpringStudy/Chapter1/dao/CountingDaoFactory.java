package SpringStudy.Chapter1.dao;

import SpringStudy.Chapter1.maker.ConnectionMaker;
import SpringStudy.Chapter1.maker.CountingConnectionMaker;
import SpringStudy.Chapter1.maker.DConnectionMaker;
import SpringStudy.Chapter1.UserDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CountingDaoFactory {
    @Bean
    public UserDao userDao(){
        return new UserDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker(){
        return new CountingConnectionMaker(realConnectionMaker());
    }

    @Bean
    public ConnectionMaker realConnectionMaker(){
        return new DConnectionMaker();
    }
}