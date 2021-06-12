package SpringStudy.Chapter1.dao;

import SpringStudy.Chapter1.maker.ConnectionMaker;

public class MessegeDao {
    ConnectionMaker cm;
    public MessegeDao(ConnectionMaker cm){
        this.cm = cm;
    }
}
