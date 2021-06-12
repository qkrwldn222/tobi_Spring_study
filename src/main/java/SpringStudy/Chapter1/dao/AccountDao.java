package SpringStudy.Chapter1.dao;

import SpringStudy.Chapter1.maker.ConnectionMaker;

public class AccountDao {
    ConnectionMaker cm;
    public AccountDao(ConnectionMaker cm){
        this.cm = cm;
    }
}
