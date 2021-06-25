
# 초난감 DAO

JDBC를 사용한 초난감 DAO를 사용하여 DB를 조회해보자
```
import lombok.Data;  
  
@Data  // Lombok 어노테이션
public class User {  
	private String id;  
	private String name;  
	private String password;  
}
```
사용자 정보를 저장할 User 클래스

```
create table users(
	id varchar(10) primary key,
	name varchar(20) not null,
	password varchar(10) not null
)
```
조회에 사용할 DB Table을 생성 #DB는 종류는 무관

```
public class UserDao {  
  
  
  
    public void add(User user) throws ClassNotFoundException, SQLException {  
        Class.forName("org.h2.Driver");  
  Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/sa");  
  
  
  PreparedStatement ps = c.prepareStatement(  
                "INSERT INTO users(id, name, password) VALUES(?,?,?)");  
  ps.setString(1, user.getId());  
  ps.setString(2, user.getName());  
  ps.setString(3, user.getPassword());  
  
  
  ps.executeUpdate();  
  
  
  ps.close();  
  c.close();  
  }  
  
  
    public User get(String id) throws ClassNotFoundException, SQLException {  
  
        Class.forName("org.h2.Driver");  
  Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/sa");  
  
  
  PreparedStatement ps = c.prepareStatement(  
                "SELECT * FROM users WHERE id = ?");  
  ps.setString(1, id);  
  
  
  ResultSet rs = ps.executeQuery();  
  rs.next();  
  User user = new User();  
  user.setId(rs.getString("id"));  
  user.setName(rs.getString("name"));  
  user.setPassword(rs.getString("password"));  
  
  
  rs.close();  
  ps.close();  
  c.close();  
 return user;  
  }  
}
```
DB에 저장과 조회하는 두가지의 메소드만 구현한 UserDao 클래스

이제 UserDao를 사용하여 테스트를 진행해보자

```
public class Main {  
    public static void main(String[] args) throws ClassNotFoundException, SQLException {  
        // xml 전  
  UserDao dao =  new UserDao();  
  
  User user = new User();  
  user.setId("jiwoo12");  
  user.setName("박지우");  
  user.setPassword("1234");  
  
  dao.add(user);  
  
  System.out.println(user.getId() + " 등록 성공");  
  
  User user2 = dao.get(user.getId());  
  System.out.println(user2.getName());  
  System.out.println(user2.getPassword());  
  
  System.out.println(user2.getId() + "조회 성공");  
  }  
}
```
```
jiwoo12 등록 성공
박지우
1234
jiwoo12 조회 성공
```
이렇게 사용자 정보를 등록 조회하는 초간단 DAO와 테스트까지 완료했다.
하지만 위의 클래스 코드는 여러 문제가 있다.

스프링은 그 문제들을 해결해 과정에서 좋은 결론을 내릴 수 있도록 고민하고 제안한 방법에 대한 힌트를 제공한다.

# DAO의 분리

사용자의 비니지스 프로세스와 그에 따른 요구사항은 끊임없이 바뀌고 발전한다.
기술과 운영되는 환경 역시 바뀐다.
따라서 개발자는 초기 설계에 가장 염두에 둬야 할 사항은 미래의 변화를 대비하는 것이다.

프로그래밍 기초 개념중 관심사의 분리라는게 있다.
이를 객체지향에 적용해 보면 관심이 같은 것끼리는 하나의 객체, 친한 객체는 모이게하고
관심이 다른 것은 가능한 따로 떨어져 서로 영향을 주지 않도록 분리하는 것이다.

## DAO의 관심사
> DB와 연결을 위한 커넥션을 어떻게 가져올까?
> 사용자 등록을 위해 DB에 보낼 SQL문장을 담은 statement 정보를 담고 실행까지의 과정
> 작업이 끝나면 사용한 리소스를 닫아줘서 공유 리소스를 시스템에 돌려주는 것

이를 초난감 DAO에 적용해 보자!.

가장 먼저 커넥션을 가져오는 중복된 코드를 분리하는 것.

```
// 수정 전
public void add(User user) throws ClassNotFoundException, SQLException {  
	Class.forName("org.h2.Driver");  
	Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/sa");  
}  
  
  
public User get(String id) throws ClassNotFoundException, SQLException {  
  
	Class.forName("org.h2.Driver");  
	Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/sa");  

  }
```

```
// 수정 후
private Connection getConnection() throws ClassNotFoundException, SQLException{  
    Class.forName("org.h2.Driver");  
    Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/sa");  
  
 return c;  
}
```
관심 종류의 코드를 분리 했기 때문에 한 가지 관심에 대한 병경은 해당 관심 사항만 수정하면 된다.
이런 작업을 리팩토링이라하고 메소드를 중복된 코드로 뽑아내는 것을 리팩토링의 메소드 추출기법이라 한다.

### DB 케녁선 만들기의 독립

위에서 만들 DAO가 인기를 끌어 다양한 회사에 제공한다고 가정하자,
각 회사들은 원하는요구 사항이 다르고 보안이 필요할 수도 있고, 
고객에 바이너리된 파일만 제공하고 싶은 경우도 있다.
이런 경우 소스코드를 제공하지 않고  고객 스스로 원하는 DB 커넥션 생성 방식을 적용해가면서 DAO를 사용할 수 있을까?

#### 상속을 통한 확장
기존 코드를 한단계 더 분리하면 된다.
UserDao에서 메소드 구현 코드를 제거하고 getConnection()을 추상 메소드로 변경하자.
```
public abstract class UserDao{
	private abstract Connection getConnection() throws ClassNotFoundException, SQLException;
	public  User get(String id) throws ClassNotFoundException, SQLException{...};
	public  User add(User user) throws ClassNotFoundException, SQLException{...};
	
}

public class NUserDao extends UserDao{
	public Connection getConnection() throws ClassNotFoundException, SQLException{
	// N사 생성코드
}

public class DUserDao extends UserDao{
	public Connection getConnection() throws ClassNotFoundException, SQLException{
	// D사 생성코드
}
```

수정된 코드를 살펴보면 DAO의 핵심 기능 DB 연결과 조회의 두가지 관심 사항을 클래스 레벨로 분리한 것이다.
만약 새로운 DB가 연결 방식이 추가될 경우 UserDao를 상속을 통해 확장만 하면 되는 것이다.
서브 클래스에서 이런 메소드를 필요에 맞게 구현하는 방법을 디자인 패턴에서 템플릿 메소드 패턴이라 한다.
이 패턴은 Spring에서도 애용되는 패턴,
getConnection() 메소드의 Connection 객체를 가져오는 메소드이다. 이런 메소드를 서브 클래스에서  결정하게 하는 방법이 팩토리 메소드 패턴이라고 부른다.
이러한 디자인 패턴을 모르더라고 중요한 건 상속구조를 통해 성격이 다른 관심사항을 분리하고 서로에게 영향을 덜 주도록 설계한 것이다.


## DAO의 확장
앞서 상속이라는 방법을 사용하여 두 가지의 관심을 분리했다. 하지만 상속이라는 방식은 여러 단점이 있다.

#### 클래스의 분리
처음은 독립된 메소드, 다음 상하위 클래스를 분리했다.
이번은 상하위 관계없이 독립적인 클래스를 사용하여 관심사를 분리해 보겠습니다.

```
//DB 커넥션 생성 기능을 독립시킨 SimpleConnectionMaker
public class SimpleConnectionMaker {  
    public Connection makeNewConnection() throws ClassNotFoundException, SQLException {  
            Class.forName("org.h2.Driver");  
  Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/sa");  
 return c;  
  }  
}
```
내부 설계를 변경하여 좀 더 나은 코드로 개선했다.
하지만 UserDao의 코드가 SimpleConnectionMaker라는 특정 클래스의 종속되어 있기 때문에
상속을 사용했을 때 처럼 DB커넥션 부분을 변경할 방법이 없다.


#### 인터페이스의 도입

```
public interface ConnectionMaker {  
    public Connection makeConnection() throws ClassNotFoundException, SQLException;  
  
}

public class DConnectionMaker implements ConnectionMaker{  
  
    @Override  
  public Connection makeConnection() throws ClassNotFoundException, SQLException {  
  
        // D사의 커녁션 생성  
  }  
}
```
중간 단계에서 인터페이스 클래스를 도입했다.
DAO클래스에서 makeConnetion메소드를 사용해 DB 커넥션을 사용하면 DAO의  코드를 고칠 일이 사라진다.
하지만 어떤 클래스의 오브젝트를 사용할지를 결정하는 생성자의 코드가 제거되는 문제가 발생한다.
(Connection Maker의 고유 이름이 남아 구현 클래스를 알아야 사용가능)


#### 관계설정 책임의 분리
```
public class UserDao {  
  
    private ConnectionMaker cm;  
  
 public UserDao(ConnectionMaker cm){  
        this.cm = cm;  
  }
}
public class DaoTest{
	public static void main(String[] args) {  
	  ConnectionMaker cm = new DConnectionMaker();  
	  UserDao dao = new UserDao(cm);  
	}
}
 
```
UserDao의 생성자를 통해 ConnectionMaker를 UserDAO와 ConnetcionMaker간의 연관 관계를 클라이언트에서 정의하게 끔 수정한 코드이다.
새로운 책임을 맡게된 DaoTest 클래스의 main 메소드에서 DConnectionMaker라는 구체클래스가 등장하게 된다.

앞서 사용했던 상속을 통한 확장 방법보다 더 깔끔하고  유연한 방법으로 DAO와 Connection 클래스를 분리하고 서로 영향없이 필요에따라 확장 가능하게 구조과 완성 되었다.

### 제어의 역전
IoC(제어의 역전)가 무엇인지 알아보기 전에 DAO코드를 좀 더 개선해보자.

#### 오브젝트 팩토리
기존의 Test를 담당하던 클래스는 원래 테스트만을 담당하는 클래스이다. 
UserDao와 ConnectionMaker를 분리하기 위해 다른 책임을 담당하고있다. 

##### 팩토리 사용
> 객체 생성 방법을 결정하기 위해 만들어지 클래스를 팩토리라 한다.
> 팩토리 역할을 맡는 DaoFactory 클래스를 추가한다.
> DaoTest의 담당하던 부분을 DaoFactory로 이동한다.
```
// Factory 클래스
public class DaoFactory {  
    public UserDao userDao(){  
        ConnectionMaker cm = new DConnectionMaker();  
		return new UserDao(cm);  
  }  
}

//main 메소드가 있는 테스트 클래스
public class DaoTest {  
    public static void main(String[] args) {  
	  UserDao dao = new DaoFactory().userDao();  
  }  
}
```
Factory를 사용하여 테스트와 Connection 담당을 분리했다.


 #### 오브젝트 팩토리의 활용
 DaoFactory에 AccountDao, MessageDao 등 추가되었다고 가정해보자.
```
public class DaoFactory {  
    public UserDao userDao(){  
        ConnectionMaker cm = new DConnectionMaker();  
		return new UserDao(cm);  
    }
    public AccountDao accountDao(){  
        ConnectionMaker cm = new DConnectionMaker();  
		return new AccountDao(cm);  
    }
    public MessegeDao messegeDao(){  
        ConnectionMaker cm = new DConnectionMaker();  
		return new MessegeDao(cm);  
    }  
}
```

 ConnetionMaker 구현 클래스 생성하는 코드의 중복이 발생한다.
 
```
public ConnectionMaker connectionMaker(){  
    return new DConnectionMaker();  
}
```
connectionMaker 메소드를 추가하여 중복을 제거할 수 있다.

 #### 제어권의 이전을 통한 제어관계 역전
 > 제어 역전이란 프로그램의 제어 흐름구조가 뒤바뀌는것을 의미
 > main() 메소드와 같이 프로그램이 시작되는 지점에서 사용되는 오브젝트를 결정하고, 
 > 결정한 오브젝트가 생성되고, 그 오브젝트의 메소드가 호출되는 방식이 반복된다.
 > 이러한 방식으로 프로그램 구조에서 흐름은 해당 오브젝트에서 제어하게 된다.
 > 하지만 제어의 역전은 오브젝트 자신이 사용할 오브젝트를 스스로 선택하지 않는다.
 > main() 메소드를 제외하면 모든 오브젝트는 제어 권한을 갖는 특별한 오브젝트에 의해 결정된다.


## 스프링의 IoC
스프링의 핵심을 담당하는 건 빈 팩토리와 애플리케이션 컨텍스트라고 불리는 두 가지이다.
이 두 가지를 DaoFactory에 적용해 보자.

### 오브젝트 팩토리를 이용한 스프링 IoC

DaoFactory를 스프링에서 사용해보자.
#### 애플리케이션 컨텍스트와 설정정보
> 스프링에서 스프링이 제어권을 가지고 직접 만들고 관계를 부여하는 오브젝트를 빈(Bean)이라고 부른다.
> 빈의 생성과 관계설정 같은 제어를 담당하는 Ioc 오브젝트를 빈 팩토리(Bean Factory)라고 부른다.
> 보통 빈 프로젝트보다 확장된 애플리케이션 컨텍스트(application context) 를 주로 사용한다. 
> 애플리케이션 컨텍스트는 별도의 정보를 참고해서 빈의 생성, 관계설정 등의 제어를 총괄한다.

#### DaoFactory를 사용하는 애플리케이션 컨텍스트
> 스프링 빈 팩토리를 위한 오브젝트를 인식하기 위해 @Configration 어노테이션을 팩토리 클래스에 추가하자.
> 오브젝트를 만들어주는 메소드에는 @Bean 어노테이션을 붙여준다.
> 애플리케이션 컨텍스트는 ApplicationContext 타입의 오브젝트다. 이 오브젝트를 구현한 클래스는  여러가지 이지만 DaoFactory 처럼 @Configration이 붙은 자바 코드 설정 정보로 사용하려면 AnnotationConfigApplicationContext를 이용하면 된다.
```
@Configuration
public class DaoFactory {  
    @Bean
    public UserDao userDao(){  
        ConnectionMaker cm = new DConnectionMaker();  
		return new UserDao(cm);  
    }
    @Bean
    public AccountDao accountDao(){  
        ConnectionMaker cm = new DConnectionMaker();  
		return new AccountDao(cm);  
    }
    @Bean
    public MessegeDao messegeDao(){  
        ConnectionMaker cm = new DConnectionMaker();  
		return new MessegeDao(cm);  
    }  
}

public class DaoTest {  
    public static void main(String[] args) {  
	  ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);  
	  UserDao dao = context.getBean("userDao",UserDao.class);  
}
```
> getBean() 메소드는 ApplicationContext가 관리하는 오브젝트를 요청하는 메소드
> getBean() 메소드의 파라미터 인 "userDao"는 ApplicationContext에 등록된 빈의 이름
> getBean() 의 다른 빈을 부를 경우 파마미터 값을 해당 이름으로 변경


### 애플리케이션 컨텍스트의 동작방식
- 오브젝트 팩토리에 대응되는 것이 스프링의 애플리케이션 컨텍스트
	- 스프링에서 애플리케이션 컨텍스트를 IoC 컨테이너 또는 스프링 컨테이너라고 부른다.
	- 빈 팩토리라고 부를 수도 있다.
- 애플리케이션 컨텍스트는 애플리케이션에서 IoC를 적용해서 관리할 모든 오브젝트에 대한 생성과 관계설정을 담당.
- ApplicationContext는 직접 오브젝트를 생성하고 관계를 맺어주는 코드는 없고, 생성정보와 연관관계 정보를 별도의 설정 정보를 통해 얻음.

장점
> 클라이언트는 구체적인 팩토리 클래스를 알 필요가 없다.
> 애플리케이션 컨텍스트는 종합 IoC 서비스를 제공해준다.
> 애플리케이션 컨텍스트는 빈을 검색하는 다양한 방법을 제공한다.

### 스프링 IoC의 용어정리

- 빈(Bean) : 스프링이 IoC 방식으로 관리하는 오브젝트, 스프링이 직접 생성과 제어를 담당하는 오브젝트만 빈이라고 부른다.
- 빈 팩토리(Bean Factory) : IoC를 담당하는 핵심 컨테이너, 빈을 등록하고, 생성하고 조회하고 그 외에 부가적인 Bean을 관리하는 기능을 담당.
- 애플리케이션 컨텍스트 (ApplicationContext) : 빈 팩토리를 확장한 IoC컨테이너. 기본적인 기능은 빈 팩토리와 동일하며 여기에 스프링이 제공하는 각종 부가 서비스를 추가적으로 제공
- 설정정보 / 메타정보(Configuration metadata) : 스프링의 설정정보란 애플리케이션 컨테스트 또는 빈 팩토리가 IoC를 적용하기 위해 사용하는 메타정보. 
- 컨테이너(Container) 또는 IoC 컨테이너 : IoC 방식으로 빈을 관리한다는 의미에서 애플리케이션 컨텍스트나 빈 팩토리를 컨테이너 또는 IoC 컨테이너라고도 한다. 후자는 주로 빈 팩토리 관점, 그냥 컨테이너 또는 스프링컨테이너라고 할때는 애플리케이션 컨테이너라고 보면 된다.
- 스프링 프레임워크 : 스프링을 제공하는 모든 기능을 통틀어 말할 때 주로 사용 


## 싱글톤 레지스트리와 오브젝트 스코프

```
// 직접 생성한 DaoFactory 코드
DaoFactory daoFactory = new DaoFactory();  
UserDao uDao1 = daoFactory.userDao();  
UserDao uDao2 = daoFactory.userDao();  
  
System.out.println(uDao1);  
System.out.println(uDao2);
// 스프링 컨테스트로 가져온 오브젝트 출력 코드
ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);  
UserDao dao1 = context.getBean("userDao",UserDao.class);  
UserDao dao2 = context.getBean("userDao",UserDao.class);  
  
System.out.println(dao1);  
System.out.println(dao2);

```
DaoFactory를 사용하여 직접 userDao를 생성하는 코드와 스프링 컨텍스트로부터 가져온 코드의 결과를 확인해 보면 

```
// 직접 생성
SpringStudy.Chapter1.UserDao@214c265e
SpringStudy.Chapter1.UserDao@448139f0
// 스프링 컨텍스트로부터 생성
SpringStudy.Chapter1.UserDao@672872e1
SpringStudy.Chapter1.UserDao@672872e1
```
직접 생성한 경우는 오브젝트가 다르고 스프링을 통한 생성은 같은 오브젝트라는 것을 확인할 수 있다.
## Why?

### 싱글톤 레지스트리로서의 애플리케이션 컨텍스트
애플리케이션 컨텍스트는 우리가 만들었던 오브젝트 팩토리와 비슷한 방식으로 동작하는 IoC 컨테이너다.
그러면서 동시에 애플리케이션 컨텍스트는 싱글톤을 저장하고 관리하는 싱글톤 레지스트리이기도 하다.
(디자인 패턴의 싱글톤 패턴과 비슷한 개념이지만 구현 방법은 다름)

#### 서버 애플리케이션과 싱글톤

자바 엔터프라이스 기술을  사용하는 환경은 주로 서버환경이고, 서버는 클라이언트 요청으로부터 부하가 발생하면 안된다. 클라이언트로 부터 다수의 요청이 온다고 했을 경우 요청을 담당하는 오브젝트를 새로 만들어 낸다면 서버가 감당하기 힘들다. 이러한 이유로 주로 싱글톤을 사용한다.

싱글톤의 한계
-  private 생성자를 갖고 있기 때문에 상속이 불가능
-  싱글톤은 테스트하기 힘듬
-  서버환경에서 싱글톤이 하나만 만들어지는 것을 보장하지 못함
-  싱글톤의 사용은 전역 상태를 만들 수 있기 때문에 바람직하지 못함

#### 싱글톤 레지스트리
> 자바의 기본적인 싱글톤 패턴의 구현 방식은 여러가지 단점이 있기 때문에 스프링은 직접 싱글톤 형태의 오브 젝트를 관리하는 싱글톤 레지스트리를 제공한다.
싱글톤 레지스트리는 평범한 자바 클래스라도 IoC 컨테이너를 사용해서 생성과 관계설정, 사용 등에 대한 제어권을 컨테이너에게 넘기면 손쉽게 싱글톤 방식으로 관리할 수 있다.

#### 싱글톤과 오브젝트의 상태
싱글톤은 멀티 스레드 환경이라면 여러 스레드가 동시에 접근해서 사용할 수 있다.
멀티 스레드 환경에서 서비스된다면 상태정보를 내부에서 갖고 있지 않은 무상태(stateless) 방식으로 만들어야 한다.

```
private ConnectionMaker cm;  
private Connection c;  
private User user;

public User get(String id) throws ClassNotFoundException, SQLException {
	this.c = cm.makeConnection();  
	PreparedStatement ps = c.prepareStatement(  
            "SELECT * FROM users WHERE id = ?");  
	ps.setString(1, id);  
	ResultSet rs = ps.executeQuery();  
	rs.next();  
	this.user = new User();  
	this.user.setId(rs.getString("id"));  
	this.user.setName(rs.getString("name"));  
	this.user.setPassword(rs.getString("password"));  
	return user;  
}
```
기존에 만들었던 UserDao에서 기존 로컬 변수로 선언하고 사용했던  Connection과 User를 클래스 인스턴스 필드로 선언했다. 따라서 싱글톤으로 만들어져서 멀티 스레드 환경에서 사용하게 될 경우 심각한 문제가 발생한다.

#### 스프링 빈의 스코프
>스프링이 적용되는 범위를 빈의 스코프라고 한다.
스프링 빈의 스코프는 기본적으로 싱글톤이다.
경우의 따라서는 싱글톤 외의 스코프를 가질 수 있다.
대표적으로 프로토 타입 스코프가 있다.
프로토타입은 요청할 때마다 새로운 오브젝트를 생성해 준다.
이외에 다양한 스코프가 존재한다.

## 의존관계 주입(DI)

### 런타임 의존관계 설정
#### 의존관계


 

 

