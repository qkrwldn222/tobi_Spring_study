# 서비스 추상화
> Service 클래스를 만들어 비지니스 로직을 추가함.
> 정기적으로 사용자의 활동내역을 참고해서 레벨을 조정해주는 기능
> 사용자의 레벨은 BASIC, SILVER, GOLD 세가지 중 하나이다.
> 사용자가 처음 가입하면 BASIC 레벨, 가입 후 50회 이상 SILVER 레벨이 된다.
SILVER 레벨이면서 30번 이상 추천을 받으면 GOLD 레벨이 된다.
사용자 레벨의 변경작업은 일정한 주기를 가지고 일괄적으로 진행됨, 변경 작업 전에는 조건을 충족하더라도 레벨의 변경이 일어나지 않음

## 트랜잭션 서비스 추상화

모든 사용자에 대해 업그레이드 작업을 진행하다가 중간에 예외가 발생해서 작업이 중단된다면 롤백이 되지않고 일부만 업데이트가 진행된 상태로 저장된다. 그 이유는 바로 트랜잭션! 메소드가 하나의 트랜잭션에서 동작하지 않았기 때문.

-   트랜잭션 롤백 : 하나의 트랜잭션 안에서 여러개의 SQL문을 실행할 때, 모든 SQL문을 실행하기 전에 문제가 발생할 경우에는 앞에서 처리한 SQL 작업도 취소시켜야 한다. 이런 취소 작업을 트랜잭션 롤백이라 한다.
-   트랜잭션 커밋 : 여러 개의 SQL을 하나의 트랜잭션으로 처리하는 경우에 모든 SQL 수행 작업이 다 성공적으로 마무리됐다고 DB에 알려줘서 작업을 확정시켜야 한다.
- 트랜잭션 경계 설정 : 트랜잭션이 시작하는 곳과 끝나는 곳을 지정하는 것
```
public  void  upgradeLevels()  throws  Exception  {
  (1)  DB  Connection  생성
  (2)  트랜잭션  시작  
  try  {  
	  (3)  DAO  메소드  호출  
	  (4)  트랜잭션  커밋  
} catch(Exception  e)  {  
	(5)  트랜잭션  롤백  
	throw  e;  
} finally  {
 (6)  DB  Connection  종료  
 }  
}
```
비지니스 로직으로 Connection을 가져왔지만 JDBC API를 직접 사용하는 초기방식으로 돌아가야 함.

```
DataSource dataSource;  
  
public void setDataSource(DataSource dataSource) {  
    this.dataSource = dataSource;  
}  
  
public void upgradeLevels() throws Exception {  
    //   
 TransactionSynchronizationManager.initSynchronization();  
  // DB 커넥션을 생성하고 트랜잭션을 시작한다.   
// 이후의 DAO 작업은 모두 여기서 시작한 트랜잭션 안에서 진행된다.  
  Connection c = DataSourceUtils.getConnection(dataSource);  
  c.setAutoCommit(false);  
  
 try {  
        List<User> users = userDao.getAll();  
 for (User user : users) {  
            if (canUpgradeLevel(user)) {  
                upgradeUser(user);  
  }  
        }  
        c.commit();  
  } catch (Exception e) {  
        // 예외가 발생하면 롤백한다.  
  c.rollback();  
 throw e;  
  } finally {  
        DataSourceUtils.releaseConnection(c, dataSource);  
  // 스프링 유틸리티 메소드를 이용해 DB 커넥션을 안전하게 닫는다.  
  TransactionSynchronizationManager.unbindResource(this.dataSource);  
  TransactionSynchronizationManager.clearSynchronization();  
  }  
}
```
스프링은 JdbcTemplate과 더불어 이런 트랜잭션 동기화 기능을 지원하는 간단한 유틸리티 메소드 TransactionSynchronizationManager를 사용할 수 있다. 해당 메소드를 이용하여 비지니스 로직에서 사용하는 DAO DB Connection을 동기화 시킬 수 있다.
JdbcTemplate는 트랜잭션 동기화 저장소에서 미리 트랜잭션이 등록된 DB를 찾고 없으면 트랜잭션을 생성하는 방식으로 진행된다.
문제는 JDBC에 종속적인 Connection을 이용한 트랜잭션 코드가 비지니스 로직에 등장하면서부터 UserService는 UserDaoJdbc에 간접적으로 의존하는 코드가 돼버렸다는 점이다.

### 스프링의 트랜잭션 추상화

```
@Autowired
PlatformTransactionManager transactionManager  
           
public void upgradeLevels() throws Exception {  
  TransactionStatus status =  
            this.transactionManager.getTransaction(new DefaultTransactionDefinition());  
 try {  
        List<User> users = userDao.getAll();  
 for (User user : users) {  
            if (canUpgradeLevel(user)) {  
                upgradeUser(user);  
  }  
        }  
        transactionManager.commit(status);  
  } catch (Exception e) {  
        transactionManager.rollback(status);  
 throw e;  
  }  
}
```
스프링이 제공하는 트랜잭션 경계설정을 위한 추상 인터페이스는PlatformTransactionManagerJDBC , 로컬 트랜잭션을 이용한다면 PlatformTransactionManager를 구현한 DataSourceTransactionManager를 사용하면 된다.
이로써 비지니스 로직은 트랜잭션을 PlatformTransactionManager에 의존하여 느슨한 결합?(약한 결합)이 되어 DAO와 Service간의 영향을 주지 않고 자유롭게 확장이 가능 ( 단일 책임의 원칙을 지키게 된다)
#### 단일 책임 원칙
-   단일 책임 원칙은 하나의 모듈은 한 가지 책임을 가져야 한다는 의미다. 하나의 모듈이 바뀌는 이유는 한 가지여야 한다고 설명할 수도 있다. 변경의 이유가 2가지 이상이라면 단일 책임의 원칙을 지키지 못한 것이다.
#### 단일 책임 원칙의 장점
-   단일 책임 원칙을 잘 지키고 있다면, 어떤 변경이 필요할 때 수정 대상이 명확해진다.
-   적절하게 책임과 관심이 다른 코드를 분리하고, 서로 영향을 주지 않도록 다양한 추상화 기법을 도입하고, 애플리케이션 로직과 기술/환경을 분리하는 등의 작업은 갈수록 복잡해지는 엔터프라이즈 애플리케이션에는 반드시 필요하다. 이를 위한 핵심적인 도구가 바로 DI이다.
-   스프링의 의존관계 주입 기술인 DI는 모든 스프링 기술의 기반이 되는 핵심 엔진이자 원리이며, 스프링이 지지하고 지원하는, 좋은 설계와 코드를 만드는 모든 과정에서 사용되는 가장 중요한 도구다.
