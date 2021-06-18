#  테스트

스프링의 핵심 중 하나가 테스트라고 말할 수 있고,  이를 사용하지 않으면 스프링의 절반을 포기하는 것과 마찬가지다.

### 웹을 통한 DAO테스트와 단위 테스트
> 웹을 통한 DAO 테스트의 경우 가장 흔히 쓰이는 방식이지만,
> DAO를 제외한 서비스 클래스, JSP, 컨트롤러 등 모든 계층을 다 만들어야 테스트가 가능
> 테스트가 실패하거나 오류가 발생했을 시 찾기 어려운 단점
> 이를 보완하는 것이 단위 테스트, 기본 DAO를 제외한 계층이 필요없음
> 테스트의 범위와 크기가 정해놓지 않고 하나의 관심사항에 집중해서 테스트를 진행

### 자동수행 테스트 코드
> UserDaoTest 코드처럼  UserDao를 테스트하기 위한 User 생성부터 DB연결 UserDao의 메소드 호출까지 이어지는 단계가 구현된 코드로 테스트 코드 실행만으로 UserDao를 테스트할 수 있는코드
어플리케이션을 구성하는 클래스안에 테스트코드를 포함시키는 것이 아닌 별도로 분리하는 것이 좋음

### UserDaoTest 특징과 문제점
> 특징
- main() 메소드를 사용
- 테스트 대상 UserDao의 오브젝트를 가져와 메소드를 호출
- 입력 값(User)을 직접 코드에서 만들어 사용
- 결과를 콘솔에 호출
- 각 단계의 작업이 에러 없이 끝나면 콘솔에 성공 메세지 출력

> 문제점
- 수동 확인 작업의 번거로움 : 콘솔에 값만 출력해주는 테스트로 올바르게 테스트가 완료되었는데 확인하는 작업이 필요
- 실행 작업의 번거로움 : 간단한 main() 메소드라 하더라도 DAO가 늘어나면 그 만큼 main() 메소드를 실행하는 수고가 필요하고, 이를 정리하는 작업또한 커지게 됨

### 테스트 검증의 자동화

수정 전 Test
```
System.out.println(user2.getName());
System.out.println(user2.getPassword());
System.out.println(user2.getId() + "조회 성공");
```
수정 후 Test
```
if(!user.getName().equals(user2.getName()){
	System.out.printls("테스트 실패 (name)");
}else if(!user.getPassword().equals(user2.getPassword()){
	System.out.printls("테스트 실패 (password)");
}else{
	System.out.println("조회 테스트 성공");
}
```
수정전 테스트 코드는 단순히 콘솔 출력에 그쳤지만 수정 후 테스트 코드는 검증단계를 거쳐 콘솔 출력의 결과 값으로 테스트 수행을 확인하는 단계를 생략 가능하게 해줌

자동화는 JUnit 프레임워크를 사용함

### JUnit 테스트로 전환
기존 main()메소드로 만들어 졌다는 것은 제어권을 직접 갖는다는 의미
JUnit은 프레임워크로 1장의 제어의 역전이 적용되어 main()메소드와 오브젝트를 만들어서 실행시키는 코드를 만들 필요가 없이 JUnit의 메소드에 public 선언과 @Test 어노테이션을 붙여 사용

```
@Test  
public void addAndGet() throws SQLException {  
   ApplicationContext context =
   new ClassPathXmlApplicationContext("applicationContest.xml");  
  }
  ```

### 검증 전환
테스트 결과 검증을 JUnit에서 제공하는 방법으로 전환하자.
```
Assertions.assertThat(user1.getId()).isEqualTo(user2.getId());
Assertions.assertThat(user1.getName()).isEqualTo(user2.getName());
Assertions.assertThat(user1.getPassword()).isEqualTo(user2.getPassword());


assertThat(user1.getId()).isEqualTo(user2.getId());  -> assertJ형식
assertThat(user1.getId(), is(user2.getId())); -> hamcrest 형식
```
[Unit Test에서 AssertThat을 사용하자 - 92Hz (jongmin92.github.io)](https://jongmin92.github.io/2020/03/31/Java/use-assertthat/)

### 테스트 결과의 일관성

UserDaoTest의 문제는 이전 테스트 때문에 DB에 등록된 중복 데이터가 있어 테스트가 제대로 되지 않을 수도 있다.
이를 해결하기 위해 deleteAll() 메소드를 추가하여 테스트 완료 후 DB의 모든 데이터를 삭제해주는 기능을 추가하고, getCount() 메소드를 만들어 삭제가 제대로 됬는데 남아있는 데이터를 조회하는 메소드를 추가한다. 하지만 deleteAll()과 getCounte() 메소드 역시 정상 적동되는지 확인 과정을 거쳐야 된다.

### 동일한 결과를 보장하는 테스트
단위 테스트는 항상 일관성 있는 결과를 보장해야 한다.
deleteAll(),getCount() 같은 메소드를 활용하여 외부 환경에 영향을 받지 말아야 하고 테스트를 실행하는 순서가 바뀌어도 동일한 결과가 보장되어야 한다.


### 포괄적인 테스트
개발자는 PC에서 테스트할 때는 예외적인 상황을 모두 피하고 정상적인 케이스만 테스트하는 경우가 많기 때문에 항상 테스트를 작성할 때 부정적인 케이스를 먼더 만드는 습관을 들이는게 좋다.

### TDD


[Test-Driven Development(TDD), Behaviour-Driven Development(BDD), Domain-Driven Development(DDD) - Naon's log](https://naon.me/posts/til54)
