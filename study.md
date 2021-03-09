## MockMvc
- 웹어플리케이션을 서버에 배포하지 않고도 spring mvc 동작을 재현할 수 있는 클래스
- TestDispatcherServlet
- 가상의 클라이언트로 어플리케이션에 요청 날리는 역할

## SpringBootTest vs WebMvcTest
- @WebMvcTest for slice test
    - Controller layer를 테스트하고 모의 객체 사용하기 때문에 나머지 필요한 bean은 직접 세팅.
    - 가볍고 빠르다
    - Web 레이어 관련 빈들만(filter, session 등) 등록하므로 service는 등록되지 않음 -> @MockBean으로 만들어줘야함
- @SpringBootTest for Integration Test
    - 전체 응용 프로그램 컨텍스트 시작. 전체 응용프로그램 로드하여 모든 bean주입하므로 느림
    - MockMvc 빈으로 등록 안시키므로 @AutoConfigureMockMvc 필요
    - 장점) 실제와 같은 환경으로 실행되므로 외부 Rest API, DB 테스트 가능. 실제 response 확인 가능
    - 단점) 로직변경에 민감. 테스트 DB 구성하기 어려움. 유지보수 어려움. unit 테스트보다 자세히 테스트 불가능


SpringBootTest는 전체 컨텍스트를 빈으로 등록하기 때문에 속도가 느림. 하지만 외부 API까지 전체 테스트한다는 의미에서 사용하면 좋지
원하는 클래스만 넣어줄 수도 있고
근데 SpringBootTest에는 웹서버를 어떻게 설정할지 가능한데, 기본 Mock 모킹한 서블릿, None, RANDOM_PORT 내장 톰캣으로
Controller Test하기 위해서는 테스트위한 클라이언트가 필요한데 mockMvc
웹서버가 모킹한 Mock 서버이면 mockMvc로 테스트하고 
실제 서버가 올라가는 RandomPort 인 경우는 testRestTemplate 이용하여 테스트하면 되는데
근데 webMvcTest는 controller에 필요한 빈만 등록해. web 레이어만 mock 하기 떄문에 나머지 service, repository 레이어들은 mock해서
stubbing 해줘야함


```
// 모든 bean 생성
@SpringBootTest 

// 원하는 bean만 생성
@SpringBootTest(classes = {CoffeeService.class})

// 서블릿 웹서버 환경 제공되지 않음
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONR)
// ServletWebServerApplicationContect 실행
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) -> default. 내장서버X, 가짜 서블릿 환경
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) -> 내장 톰캣 실행
```

```
// MockMvc 이용하여 mock 환경에서 컨트롤러 테스트 ( webEnvironment=mock ) -> webEnvironment가 none인 경우 mockMvc 주입안됨
@SpringBootTest
@AutoContifureMockMvc
public class ContorllerTest {
    @AUtowired
    MockMvc mockMvc;
}

// 내장톰켓 구동되면 MockMvc로는 테스트 불가 
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTest {
    @Autowired
    TestRestTemplate testRestTemplate;
}
```    
- SpringBootTest 설명이 좋아요 https://brunch.co.kr/@springboot/207
- test slice를 위한 annotation https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-test-auto-configuration.html#test-auto-configuration-slices

사례1) 데이터가 민감했던 프로젝트 
- Integration Test + WebMvcTest(or unit) : 테스트 DB 유지가 어려움. 로직은 계속 변경되는데??
사례2) 리팩토링 프로젝트 
- Integration Test : 기능 추가 없이 리팩토링만 목적으로 했음. 외부 API 호출이 많음 (캡쳐링해서 response stubbing)
사례3) 로직이 복잡하지 않고 배포 전 API 호출 회귀 테스트 목적
- Postman으로 테스트 

## Spring Framework Test 코드 작성하기
- applicationContext 설정을 다 잡아줘야함 

### Container
- BeanFactory : lazy-loading
- ApplicationContext : beanFactory 상속받음. inital-loading 

- ServletContext : Resolver 등등 객체 관리
- RootApplicationContext : 비즈니스

### field injection vs 생성자 injection
- field 인젝션 사용할 경우 spring runner로 돌리지 않으면 테스트 불가능하기 떄문에 unit 테스트를 위해 생성자 injection 사용

## Question
- Integration 테스트 작성한 사례가 있나