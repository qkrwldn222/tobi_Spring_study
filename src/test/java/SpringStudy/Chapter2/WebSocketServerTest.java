package SpringStudy.Chapter2;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.annotation.DirtiesContext;


import java.sql.SQLException;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WebSocketServerTest {


    private static StudierDao studierDao = new StudierDao();
    @Autowired
    WebSocketServer webSocketServer;
    @Autowired
    WebSocketClient webSocketClient;

//    @Autowired
//    private StudierDao studierDao;

    @BeforeAll
    @Test
    public static void startTest() throws SQLException, ClassNotFoundException {
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StudierDao.class);
//        studierDao = context.getBean("studierDao",StudierDao.class);
        Studier jiwoo = new Studier();
        jiwoo.setName("지우");
        jiwoo.setAge(28);
        Studier suhan = new Studier();
        suhan.setName("수한");
        suhan.setAge(29);
        studierDao.add(jiwoo);
        studierDao.add(suhan);
        assertThat(studierDao.getCount()).isEqualTo(2);
    }
    @Test
    @Order(1)
    public void stuUpdate() throws SQLException, ClassNotFoundException {
        Studier jiwoo = studierDao.findByName("지우");
        assertThat(jiwoo.getIdx()).isNotNull();

        jiwoo.setName("지후");
        jiwoo.setAge(jiwoo.getAge()+1);
        int row = studierDao.studierUpdate(jiwoo);
        assertThat(row).isEqualTo(1);
    }

    @Test
    @Order(2)
    public void sendMsg() throws SQLException, ClassNotFoundException {
        int row = studierDao.sendMsg("안녕","수한","지후");
        assertThat(row).isEqualTo(1);
    }

    @Test
    @Order(3)
    public void readMsg() throws SQLException, ClassNotFoundException {
        Studier jiwoo = studierDao.readMsg("지후");
        assertThat(jiwoo.getMsg()).isNotNull();
    }

    @Disabled
    @Test
    public void serverStart(){
        assertThat(webSocketServer.returnTest()).isEqualTo(1);
    }

    @Test
    @Disabled
    public void clientStart(){

        webSocketServer.start();

    }

    @AfterAll
    @Test
    @DirtiesContext
    public static void shutDown() throws SQLException, ClassNotFoundException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StudierDao.class);
        studierDao = context.getBean("studierDao",StudierDao.class);
        assertThat(studierDao.deleteAll()).isEqualTo(2);

    }
}