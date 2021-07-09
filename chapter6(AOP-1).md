# AOP

### 트랜잭션 코드의 분리
기존 UserService는 트랜잭션 결계설정과 비지니스 로직이 합쳐져 있는 구조.
트랜잭션 결계설정은 비지니스 로직과 무관하고 단지 비지니스 로직 사이 트랜잭션의 시작과 끝만 지정해주는 역할

![토비의 스프링 6장(1) - AOP 트랜잭션 코드의 분리](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT8ycXVyM75RGPA4tIVLoselJKV5l8dexMOTQ&usqp=CAU)

위 와 같이 UserService를 인터페이스 클래스로 두고 비지니스 로직을 UserServiceImple, 트랜잭션을 UserServiceTx로 분리하여 구현

```
public  class  UserServiceTx  implements  UserService { 
	UserService userService; 
	PlatformTransactionManager transactionManager;
	
	public  void  setUserService(UserService userService) { userService를 DI  
		this.userService = userService; 
	} 
	
	public  void  setTransactionManager(PlatformTransactionManager 
														transactionManager) { 
		this.transactionManager = transactionManager; 
	} 
	
	public  void  add(User user) { 
		this.userService.add(user); // 비즈니스 로직
	} 
	public  void  upgradeLevels() { 
		TransactionStatus status = this.transactionManager .getTransaction(new 						
			DefaultTransactionDefinition()); 
			try { 
				userService.upgradeLevels(); // 비즈니스 로직을 가진 오브젝트에
				this.transactionManager.commit(status); 
			} catch (RuntimeException e) { 	
				this.transactionManager.rollback(status); 
				throw e; 
			} 
		} 
	}  
```
UserServiceTX 클래스는 UserService 인터페이스를 구현했으니 클라이언트에 대해 UserService 타입 오브젝트의 하나로서 행세할 수 있음.
DI를 통해 UserService의 구현 오브젝트 UserServiceImpl을 주입 받는다.

트랙잰셕 경계설정 코드 분리의 장점
- UserServiceImple 코드의 비지니스 로직 코드를 작성할 때 트랜잭션을 신경쓰지 않아도 된다.
- 비지니스 로직에 대한 테스트를 손쉽게 만들 수 있다.

(트랜잭션이 있는 오브젝트를 먼저 실행되도록 만들어야함)

### 고립된 테스트

![토비의 스프링 6장(2) - 고립된 단위 테스트](data:image/jpeg;base64,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)

위의 그림과 같이 UserService 클래스는 3가지의 의존 관계를 가지고 있어, 테스트가 진행되는 동안 같이 실행 되어야 하는 문제가 있다ㅣ.
그래서 테스트의 대상이 환경이나, 외부 서버, 다른 클래스의 코드에 종속되지 않게 고립 시킬 필요가 있다.

### Mockito 프레임워크
Mockito와 같은 프레임워크의 특징은 목 클래스를 일일이 준비해둘 필요가 없다.
[mockito 사용법(mockito usage) :: JDM's Blog](https://jdm.kr/blog/222)

## 프록시
```
public class UserServiceTx implements UserService {

    private UserService userService;
    private PlatformTransactionManager transactionManager;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public void upgradeLevels() {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());  //부가기능
        try {
            userService.upgradeLevels(); // 위임 
            transactionManager.commit(transaction); // 부가기능
        } catch (RuntimeException e) {
            transactionManager.rollback(transaction);
            throw e;
        }
    }

    @Override
    public void add(User user) {
        userService.add(user);
    }
}
```
위의 코드를 살펴보면 userService 타깃 오브젝트를 사용하여 비지니스 로직을 위임하고 upgradeLevels()메소드의 트랜잭션 부분을 부가기능으로 분류, 프록시의 역할은 위임과 부가작업 두가지고 구분됨. 이 처럼 프록시는 핵심 기능 인터페이스 클래스를 만들고 각 기능별로 다르게 구현된 클래스를 위임하여 사용

**데코레이터 패턴**

데코레이터 패턴은 타겟에 부가적인 기능을 런타임 시 다이나믹하게 부여해주기 위해 프록시를 사용하는 패턴을 말한다. UserServiceTx도 UserSerivce라는 인터페이스를 통해 다음 오브젝트로 위임하도록 되어 있지 UserServiceImpl이라는 특정 클래스로 위임하도록 되 어 있지 않다. 필요하다면 언제든지 트랜잭션 외에도 다른 기능을 부여해주는 데코레이터 를 만들어서 UserServiceTx와 UserServiceImpl 사이에 추가해줄 수도 있다.

**프록시 패턴**

일반적으로 사용하는 프록시라는 용어와 프록시 패턴은 다른 의미로 사용된다. 위에서 언급한 프록시와 다르게 프록시 패턴은 프록시를 사용하는 방법 중에서 타겟에 대한 접근 방법을 제어하려는 목적을 가진 경우를 가리킨다. JPA에서 사용하는 프록시도 프록시 패턴을 사용한 것이며 Collections.unmodifiableCollection()과 같은 메소드도 프록시를 반환하여 실제 오브젝트에 대한 접근권한을 제어하는 방식이다. 이렇게 프록시 패턴은 타깃의 기능 자체에 관여하지 않으면서 접근하는 방법을 제어해주는 프록시를 이용하는 것이다. 아래의 그림과 같이 프록시도 결국 인터페이스 타입을 가지며 계속 다른 프록시를 호출할 수 있다. 하지만 실제 오브젝트를 제어만 하는 경우 직접적으로 타겟 오브젝트를 아는 경우도 많이 존재한다.

## 다이나믹 프록시
프록시는 기존 코드에 영향을 주지 않으면서 타깃의 기능을 확장하거나 접근 방법을 제어할 수 있는 유용한 방법, 하지만 매번 새로운 클래스를 정의하고 모든 메소드를 일일이 구현해야 하는 단점이 생김.

자바에는 java.lang.reflect 패키지 안에 프록시를 손쉽게 만들 수 있도록 지원하는 클래스가 있음.
이는 프록시 클래스를 일일이 정의하지 않고 몇 가지 API를 사용하여 프록시 처럼 동작하는 오브젝트를 다이나믹하게 생성하는 것.

### 리플렉션
[자바 Reflection이란?. 많은 입문용 자바 서적에서 잘 다루지 않는 Reflection이라는… | by Maeng Sol | msolo021015 | Medium](https://medium.com/msolo021015/%EC%9E%90%EB%B0%94-reflection%EC%9D%B4%EB%9E%80-ee71caf7eec5)

```
public class TransactionHandler implements InvocationHandler {
	private Object target; 
	private PlatformTransactionManager transactionManager;
	private String pattern; 

	public void setTarget(Object target) { 
		this.target = target;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager; 
	}

	public void setPattern(String pattern) { 
		this.pattern = pattern;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable { 
		 if (method.getName().startsWith(pattern)) {
			 return invokeInTransaction(method, args); 
		 } else {
			 return method.invoke(target, args); 
		 }
	}

	private Object invokeInTransaction(Method method, Object[] args) throws Throwable {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			Object ret = method.invoke(target, args);
			this.transactionManager.commit(status);
		  return ret;
		} catch (InvocationTargetException e) { 
			this.transactionManager.rollback(status); 
			throw e.getTargetException();
		} 
	}
}
```
DI 받은 이름 패턴으로 시작되는 이름을 가진 메소드를 확인하고, 일ㅊ치하는 이름을 가진 메소드라면 트랙잰셕을 적용하는 메소드 호출, 아니라면 부가기능을 적용하지 않은 오브젝트의 메소를 호출하여 결과를 리턴.

스프링에서는 사전에 프록시 오브젝트의 클래스 정보를 미리 알아내서 스프링의 빈 에 정의할 방법이 없다. 때문에 스프링에서 프록시를 사용할 때 FactoryBean을 사용, **팩토리 빈**이란 스프링을 대신해서 오브젝트의 생성로직을 담당하도록 만들어진 특별한 빈을 말한다.
```
@AllArgsConstructor
public class TxProxyFactoryBean implements FactoryBean<Object> {
    Object object;
    PlatformTransactionManager transactionManager;
    String pattern;
    Class<?> serviceInterface;

    @Override
    public Object getObject() throws Exception {
        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(object);
        txHandler.setTransactionManager(transactionManager);
        txHandler.setPattern(pattern);
        
        return Proxy.newProxyInstance(
            getClass().getClassLoader(), new Class[] { serviceInterface}, txHandler
        );
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
```
DI를 통해 다이나믹 프록시를 생성하는 정보를 주입받고, getObject() 메소드를 호출 하면 Proxy.newProxyInstance를 통해 TransactionHandler를 사용하는 다이나믹 프록시를 리턴한다.

---
**장점**
- 기존의 문제인 프록시를 적용할 대상이 구현하고 있는 인터페이스를 구현하는 프록시 클래스를 일일이 만들어 줘야하는 점과, 부가적인 기능이 여러 메소드에 반복적으로 나타나게 돼서 코드 중복이 발생하는 것을 해결해준다. DI를 통해 효율적인 프록시 생성을 위한 다이나믹 프록시도 생성할 수 있었다. 뿐만 아니라 프록시 자체를 사용하기 위해서도 DI가 필요하다.

**단점**
- 프록시를 통해 타겟에 부가기능을 제공하는 것은 메소드 단위로 일어나는 일이다. 하지만 한 번에 여러 개의 클래스에 공통적인 부가기능을 제공하는 일은 불가능하다. 즉 비슷한 프록시 팩토리 빈 설정이 중복되는 것은 막을 수 없다.
- 하나의 타깃에 여러 개의 부가기능을 적용할 때도 문제가 된다.여러 부가 프록시를 제공하고 싶은 경우 빈 설정이 너무나도 많아지는 단점이 존재한다.
- TransactionHandler 오브젝트가 프록시 팩토리 빈 개수만큼 만들어진다는 점도 문제가 된다. 동일한 코드임에도 불구하고 타깃 오브젝트가 달라지면 새로운 오브젝트를 만들어야 한다.
- 결국 새로운 부가기능을 적용하게 되면 기존에 적용한 모든 설정에 새로운 빈을 등록해야 하며 이는 설정파일로 관리할 수 있다는 장점은 있지만 너무 많은 중복이 발생한다.

### ProxyFactoryBean
스프링의 ProxyFactoryBean은 프록시를 생성해서 빈 오브젝트로 등록하게 해주는 팩토리 빈,
기존과 다르게 ProxyFactoryBean은 순수하게 프록시를 생성하는 작업만을 담당하고 프록시를 통해 제공해줄 부가기능은 별도의 빈에 둘 수 있다. ProxyFactoryBean이 생성하는 프록시에서는 사용할 부가기능은 MethodInterceptor인터페이스를 구현해서 만든다.

기존의 차이점은 MethodInterceptor를 사용할 때 타깃 오브젝트가 등장하지 않는다.  또, 기존은 pattern을 사용하여 부가기능을 적용을 결정했다면 MethodInterceptor는 그 안에서 구별이 불가능하다.(싱글톤)

스프링은 부가기능을 제공하는 오브젝트를 어드바이스, 메소드 선정 알고리즘을 담는 오브젝트를 포인트 컷이라고 부른다. 모두 프록시에 DI로 주입되어 사용한다.(싱글톤)
어드바이저는  포인트컷 + 어드바이스로 불린다.

![토비의 스프링 6장 (AOP) - 92Hz](https://jongmin92.github.io/images/post/2018-04-15/comparison.png)

```
@Test
void pointcutAdvisor() {
    ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
    proxyFactoryBean.setTarget(new HelloTarget());

    NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
    pointcut.setMappedName("sayH*");
    proxyFactoryBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

    Hello proxy = (Hello) proxyFactoryBean.getObject();
    assertThat(proxy.sayHello("abc")).isEqualTo("HELLOABC");
    assertThat(proxy.sayHi("abc")).isEqualTo("HIABC");
    assertThat(proxy.sayThankYou("abc")).isNotEqualTo("THANK YOUABC");
}
```
클라이언트로부터 요청이 들어오면 NameMatchMethodPointcut를 이용해 메소드를 분리하고 이를 proxyFactoryBean의 addAdvisor( 포인트컷,  어드바이스)메소드를 통해 등록

> 트랜잭션 어드바이스
```
public class TransactionAdvice implements MethodInterceptor {

    private PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            Object result = invocation.proceed();
            transactionManager.commit(transaction);
            return result;
        } catch (RuntimeException runtimeException) {
            transactionManager.rollback(transaction);
            throw runtimeException;
        }
    }
}
```
invocation.proceed()를 사용하여 타깃의 메소드를 실행
> XML 설정
```
<bean id="userService" class="org.springframework.aop.framework.ProxyFactoryBean">
    <property name="target" ref="userServiceImpl"/>
    <property name="interceptorNames">
        <list>
            <value>transactionAdvisor</value>
        </list>
    </property>
</bean>
<bean id="transactionAdvisor" class="org.springframework.aop.support.DefaultPointcutAdvisor">
    <property name="advice" ref="transactionAdvice"/>
    <property name="pointcut" ref="transactionPointcut"/>
</bean>
<bean id="transactionAdvice" class="springbook.service.TransactionAdvice">
    <property name="transactionManager" ref="transactionManager"/>
</bean>
<bean id="transactionPointcut" class="org.springframework.aop.support.NameMatchMethodPointcut">
    <property name="mappedName" value="upgrade*"/>
</bean>
```

![image](https://user-images.githubusercontent.com/56240505/122429076-74d08500-cfcd-11eb-9fc7-1e0d4d2f49d6.png)

