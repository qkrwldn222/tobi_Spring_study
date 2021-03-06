package SpringStudy.Chapter1.maker;

import java.sql.Connection;
import java.sql.SQLException;

public class CountingConnectionMaker implements ConnectionMaker {
    private int counter = 0;
    private ConnectionMaker realConnectionMaker;

    public CountingConnectionMaker(ConnectionMaker realConnectionMaker) {
        this.realConnectionMaker = realConnectionMaker;
    }

    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        this.counter++;

        return realConnectionMaker.makeConnection();
    }

    public int getCounter() {
        return this.counter;
    }
}
