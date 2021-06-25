
# 템플릿

### 예외처리 기능을 찾춘 DAO
기존 초난감 DAO는 예외처리 기능이 없는 문제가 있음.
DB 커넥션이라는 한정된 리소스를 공유해서 사용하는 서버에 반드시 예외처리가 있어야 함.
그렇지 않을 경우 시스템에 심각한 문제를 야기할 수 있음.

---
Try / Catch를 사용하자!
```
Connection c= null;  
PreparedStatement ps = null;  
  
try {  
    c = getConnection();  
  
}catch (SQLException e){  
    e.getStackTrace();  
}finally {  
    if (ps != null){  
        try{  
            ps.close();  
  } catch (SQLException throwables) {  
            throwables.printStackTrace();  
  }  
    }  
    if (c != null){  
        try {  
            c.close();  
  } catch (SQLException throwables) {  
            throwables.printStackTrace();  
  }  
    }  
}
```
문제는 Connection과 PreparedStatement를 가져올 때 예외가 발생하면 둘 중에 어떤것을 close()메소드를 호출할 지 달라지게 됨

---
예를 들어보자!
> DB 서버의 문제로 Connection을 가져오는 중에 문제가 발생하여 예외가 발생될 경우 c는 null상태이기 때문에 close() 메소드를 호출하면 NullPointException이 발생하게 된다!! 따라서 null일 경우를 분리해서 처리해야 한다.

---
ResultSert이 있는 조회기능의 예외처리
```
PreparedStatement ps = null;  
Connection c = null;  
ResultSet rs = null;  
  try {  
    c = cm.makeConnection();  
  ps = c.prepareStatement(  
            "SELECT * FROM users WHERE id = ?");  
  ps.setString(1, id);  
  rs = ps.executeQuery();  
  rs.next();  
}catch (SQLException e){  
    throw e;  
}finally {  
    if (rs != null){  
        try{  
            rs.close();  
  }catch (SQLException e2){  
        }  
    }  
    if (ps != null){  
        try{  
            ps.close();  
  } catch (SQLException throwables) {  
            throwables.printStackTrace();  
  }  
    }  
    if (c != null){  
        try {  
            c.close();  
  } catch (SQLException throwables) {  
            throwables.printStackTrace();  
  }  
    }
```
위와 마찬가지로 rs의 null상태를 상각하여 finally에 close()메소드의 예외처리를 추가해 주면되는데 이때 close()는 만들어지는 순서의 반대로 하는 것이 원칙

### JDBC try/catch/finally의 문제점.
모든 메소드의 try/catch의 이중으로 중첩되어 매우 복잡해지고 추가적인 메소드를 작성할 때 마다. try/catch를 추가해야 하며 하나라도 실수할 경우 후에 DB 커넥션풀이 없는 상황이 발생하게 된다.

> - 메소드 추출 : 분리시킨 메소드가 재활용이 되지않음.
>- 템플릿 메소드 패턴 : DAO로직마다 상속 클래스를 만들어야 하며 서브 클래스의 수가 많아 장점이 없음
>- 전략 패턴 : OCP 에 맞지 않음.

```
public class JdbcContext {  
    private DataSource dataSource;  
  
 public void setDataSource(DataSource dataSource){  
        this.dataSource = dataSource;  
  }  
    public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {  
        Connection c = null;  
  PreparedStatement ps = null;  
  
 try {  
            c = this.dataSource.getConnection();  
  ps = stmt.makePreparedStatement(c);  
  ps.executeUpdate();  
  } catch (SQLException e) {  
            throw e;  
  } finally {  
            if(ps != null) try { ps.close(); } catch(SQLException e) {}  
            if(c != null)  try { c.close(); } catch(SQLException e) {}  
        }  
    }  
  
    public void executeSql(final String query) throws SQLException {  
        workWithStatementStrategy(  
                new StatementStrategy() {  
                    @Override  
  public PreparedStatement makePreparedStatement(Connection c)  
                            throws SQLException {  
                        return c.prepareStatement(query);  
  }  
                }  
        );  
  }  
}

public class UserDao {  
    public void setJdbcContext(JdbcContext jdbcContext){  
        this.jdbcContext = jdbcContext;  
  }
public void add(User user) throws ClassNotFoundException, SQLException {  
  this.jdbcContext.workWithStatementStrategy(  
  new StatementStrategy(){  
    @Override  
	public PreparedStatement makePreparedStatement(Connection c) throws SQLException {  
	 PreparedStatement ps = c.prepareStatement("INSERT INTO users(id, name, password) VALUES(?,?,?)");  
	ps.setString(1,user.getId());  
	ps.setString(2,user.getName());  
	ps.setString(3,user.getPassword());  
	return ps;  } }  
 );  
}
```
전략 패턴의 구조
(DAO마다 StatementStrategy구현 -> jdbcContextWith... 메소드로 추출 -> Class로 분리)
JdbcContext 클래스를 추가하여 분리

### JdbcContext 클래스의 DI
> 의존관계 주입 개념을 충실히 따른다면 온전한 DI로 볼 수 없음.
> 인터페이스를 사용해 클래스를 자유롭게 변경하지  못하지만 DI구조로 만들어야 되는 이유
> 스프링 컨테이너의 싱글톤 레지스트리에서 관리되는 싱글톤 빈이 되어 여러 오브젝트에서 공유되는게 이상적
> 또, JdbcContext 클래스가 DataSource를 의존하고 있기 때문에 스프링빈에 등록돼야 함.

 ```
 <bean id ="jdbcContext" class="SpringStudy/Chapter1/JdbcContext">
  <property name ="dataSource" ref="dataSource"/>
  </bean>
  
 public void setDataSource(DataSource dataSource){
	 this.jdbcContext = new JdbcContext();
	 this.jdbcContext.setDataSouce(dataSource);
	 this.dataSource = dataSource;
}
 ```
JdbcContext를 Bean등록하는 방법과 DAO코드를 통해 수동적으로 등록하는 두가지 방법
>빈 등록방법은 오브젝트 사이의 실제 의존관계가 설정파일에 명확하게 드러나는 장점이 있고, DI의 근본적인 원칙에 부합하지 않는 구체적인 클래스와의 관계가 설정에 직접 노출되는 단점이 있다.
>DAO를 통한 수동적인 방법은 내부에서 만들어지기 때문에 그 관계가 외부에  들어나지 않는다는 장점이 있지만 DI작업을 위한 부가적인 코드가 필요한 단점이 있다.

### 템플릿과 콜백

지금까지 만든 코드는 전략 패턴에 익명 내부 클래스를 활용한 방식이고,
이러한 방식을 스프링은 템플릿 / 콜백 패턴이라고 한다.
템플릿은 고정된 작업의 흐름을 가진 코드를 재사용한다는 의미.
콜백은 템플릿 안에서 호출되는 것을 목적으로 만들어진 오브젝트를 말한다.

콜백 메소드의 흐름도
![템플릿(2) : 네이버 블로그](https://mblogthumb-phinf.pstatic.net/MjAxODA0MjVfMTgg/MDAxNTI0NjQ2NDY0NzE4.d6bCE8j-y_3IqKzos_tQAA2IXCeP1cb5WXL4Dn_F3YQg.To35FWHdPHebxUG_m33KGSTj7pdFQ0NtbYFWVfiwA9kg.PNG.sung487/3_8.png?type=w800)

```
public void deleteAll() throws SQLException{
	executeSql("delete from users");
}

private void executeSql(final String query) throws SQLExcetion{
	this.jdbcContext.workWithStatementStrategy(
		new StatementStrategy(){
			public PreparedStatement makePreparedStatement(Connection c)
				throws SQLExcetion {
				return c.prepareStatement(query);
				}
		}
	);
}
```
위의 코드는 템플릿에서 바뀌는 부분 query문을 밖으로 분리시켜 deleteAll()메소드를 만들고 바뀌지 않는 부분을 executeSql()메소드를 만들었다. 익명의 내부 클래스인 콜백 안에서 직접 사용할 수 있게 주의하면 deleteAll()메소드 처럼 query를 다르게한 메소드들만 만들어 executeSQL()을 호출하면 간단하게 재사용이 가능해 진다.

### 템플릿/ 콜백 응용
지금까지 살펴본 템플릿 / 콜백 패턴은 스프링에서만 제공하는 독점적인 기술은 아니지만,
스프링에는 다양한 자바 엔터프라이즈 기술에서 사용할 수 있도록 미리 만들어져 제공되는 템플릿/콜백 클래스와 API가 수십 가지가 존재한다.
스프링을 사용하는 개발자라면 필수로 템플릿/콜백 기능을 사용할 수 있어야하고, 동시에 필요한곳이 있으면 직접 만들어서 사용하거나 확장할 수 있어야 한다.
때문에 스프링의 기본이 되는 전략 패턴과 DI는 물론이고 템플릿 / 콜백 패턴에 익숙해져야 한다.
( 특정 메소드나 기능에서 전략 패턴과 DI, 익명의 내부클래스를 사용해 반복되는 부분을 줄이고 사용하기 편하게 만든다?)

## 스프링의 JdbcTemplate
스프링은 JDBC를 이용하는 DAO에서 사용할 수 있도록 준비된 다양한 템플릿과 콜백을 제공한다. JdbcContext와 유사하지만 훨씬 강력하고 편리한 기능을 제공한다.

```
private JdbcTemplate jdbcTemplate;  
  
public void setJdbcTemplate(DataSource dataSource) {  
    this.jdbcTemplate = new JdbcTemplate(dataSource);  
}  
  
public void deleteAll(){  
    this.jdbcTemplate.update(  
            new PreparedStatementCreator() {  
                @Override  
				  public PreparedStatement createPreparedStatement(Connection con) 																																										                          throws SQLException {  
                    return con.prepareStatement("delete from users");  
					}  
	        }  
    );
}
```
JdbcTemplate를 JdbcContext와 바꿔주고 내장된 콜백과 템플릿 메소드 update를 사용하면 기존과 유사한 deleteAll()메소드를 만들 수 있다.

```
// 콜백만 사용해서 만든 deleteAll()
public void deleteAll(){  
 this.jdbcTemplate.update("delete from users");  
}
```

```
this.jdbcTemplate.update("insert into users(id, name, password valuse(?,?,?)",
 user.getId(), user.getName(),user.getPassword());  
```
기존의 복잡했던 add메소드 역시 간단하게 바꿀 수 있음.

---
지금까지 가장 복잡했던 get() 메소드를 JdbcTemplate을 적용해보자.
```
public User get(String id){  
	    return this.jdbcTemplate.queryForObject("select * from users where id = ?", new Object[]{id},  
	 new RowMapper<User>() {  
                @Override  
				  public User mapRow(ResultSet rs, int rowNum) throws SQLException {  
                    User user = new User();  
					user.setId(rs.getString("id"));  
					user.setName(rs.getString("name"));  
					user.setPassword(rs.getString("password"));  
					return user; 
					}  
            });  
}
```
queryForObject()메소드와 RowMapper 콜백으로 보다 편리하게 get() 메소드를 만들 수 있다.
ResultSet은 첫 번째 row를 가르키고 있기 때문에 rs.next()를 사용하지 않아도 된다. 또한 조회 결과가 없는 예외 상황 EmptyResultDataAcccessException을 던지도록 만들어져 있다.



# 정리
* 작업의 흐름이 반복된다면 전략 패턴과 컨텍스트를 이용.
* 메소드 안에 익명 내부 클래스를 사용해서 전략 오브젝트를 구현하면 코드가 간결해지고 메소드 정보를 직접 사용할 수 있음.
* 단일 전략 메소드를 갖는 전략 패턴이면서 익명 내부 클래스를 사용해서 매번 젼략을 새로 만들어 사용하고, 컨텍스트 호출과 동시에 전략 DI를 수행하는 방식을 템플릿 / 콜백 패턴이라 함.
* 콜백의 코드에도 일정한 패턴이 반복된다면 콜백을 템플릿에 넣고 재활용 하는 것이 편리 (ex: jdbcTemplate.update("delete from users");
* 템플릿과 콜백의 타입이 다양하게 바뀐다면 제네릭스 사용
* 템플릿과 콜백 설계 시 주고 받는 정보에 관심을 둬야 함
* 템플릿/콜백은 스프링이 객체지향 설계와 프로그래밍에 얼마나 가치를 두고 있는지 보여주는 예시이며, 스프링이 제공하는 템플릿/콜백을 잘 사용해야 하는것은 물론 직접 템플릿/콜백을 만들어 활용할 수 있어야 함(★)

