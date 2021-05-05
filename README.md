<h1 align="center">✨ Kotlin Spring Boot Tutorial ✨</h1>

<h2> 스프링 부트를 왜 시작하게 되었는가? </h2>

- 다양한 이유가 있었지만 우선 내가 진행하는 프로젝트들에서 Backend 개발자로 직접 참여하고 싶다는 마음이 강했고
- 그러지 못하더라도 서버 코드는 어떻게 작성하는 지 알고 싶었다.
- 작년에 [Spring Framework](https://github.com/SoptSpringStudy/SOPTSpring_HyunWoo) 스터디를 진행했었으나 API는 설계하지 않고 프레임워크 자체를 깊게 공부하려고 하던터라 직접 RESTful API를 설계해보고 싶었다.
- 또한 코틀린이라는 아름답고 우아한 언어로 안드로이드만 하기에는 너무 아깝다는 생각이 들어 사용범위를 확장하고자 한다.
- 회사를 다니면서 안드로이드만 공부하게 될까봐 쉬는 기간에는 그냥 하루 날잡고 안드로이드 말고 다른 테크를 공부하고 재밌으면 파보려고 한다. ~~(근데 코틀린 활용하는건 웬만하면 재밌어)~~

<h2> What did you do? </h2>

- IntelliJ에서 Spring Boot(ver 2.4.5) 프로젝트를 init한다.
- 서버 코드를 구성하는 간단한 아키텍처를 보고
    
    - GET
    - POST
    - PATCH
    - DELETE API 코드를 어떻게 작성하는 지 학습한다
    
- JUnit5와 Mock Object(Mockk)을 활용하여 테스트 코드를 작성하고 확장 가능하고 유지보수가 가능한 코드를 만드는 연습을 해본다.

<h2> Very Very Simple(VVS 💎) Code </h2>

**Memory** - **DataSource** - **Service** - **Controller**

로 구성되어있고 Controller에서 요청을 Mapping 해주면 이에 연결된 로직을 Service, DataSource에 걸쳐서 실행을 한다.

<h3> Controller </h3>

```kotlin
// Spring Boot에서 Controller라는 의미
@RestController
// end point가 "/api/banks"일 때의 request를 mapping 해주는 controller이다
@RequestMapping("/api/banks")
// DI를 활용하여 bankService가 주입이 된다
// RestController - View가 필요없는 API만 지원하는 서비스에서만 사용
class BankController(private val bankService: BankService) {
  // Exception이 터질때 해당 Exception을 handling 해주는 기능
  @ExceptionHandler(NoSuchElementException::class)
  fun handleNotFound(e: NoSuchElementException): ResponseEntity<String> =
    ResponseEntity(e.message, HttpStatus.NOT_FOUND)

  @ExceptionHandler(IllegalArgumentException::class)
  fun handleNotFound(e: IllegalArgumentException): ResponseEntity<String> =
    ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

  // REST GET /api/banks 
  @GetMapping
  fun getBanks() = bankService.getBanks()

  // REST GET /api/banks/{accountNumber}
  @GetMapping("/{accountNumber}")
  fun getBank(@PathVariable accountNumber: String): Bank = bankService.getBank(accountNumber)

  // REST POST /api/banks
  @PostMapping
  // Response의 status가 200이 아닌 isCreated로 반환
  @ResponseStatus(HttpStatus.CREATED)
  fun registerBank(@RequestBody bank: Bank): Bank = bankService.addBank(bank)

  // REST PUT /api/banks
  @PatchMapping
  // 이 API는 RequestBody를 요구한다
  fun updateBank(@RequestBody bank: Bank) = bankService.updateBank(bank)

  // REST PUT /api/banks/{accountNumber}
  // 이 API는 isNoContent를 반환
  @DeleteMapping("/{accountNumber}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  fun deleteBank(@PathVariable accountNumber: String) = bankService.deleteBank(accountNumber)
}
```

<h3> Service </h3>

- @Service 어노테이션을 붙이면 Spring Boot에서 해당 클래스는
- Business Logic을 수행하는 Class임을 알게 시켜줌

    - 이번 튜토리얼에서는 이 부분의 책임이 너무 미약하다.
    - 다음부터는 비즈니스 로직을 잘 작성하기 위한 분리/책임 부여 등을 더 철지히 해야겠다.

<h3> DataSource </h3>

- Data를 fetch(retrieve)하기 위한 Class

<h3> Test Code </h3>

```kotlin
<BankServiceTest>

internal class BankServiceTest {
  // Mock 객체를 생성
  private val dataSource: BankDataSource = mockk()
  private val bankService = BankService(dataSource)
  @Test
  fun `should call its DataSource to retrieve banks`() {
    // Mock DataSource의 역할을 정의
    every { dataSource.retrieveBanks() } returns emptyList()

    // when
    val banks = bankService.getBanks()

    // then
    // mockk의 기능
    // bankService.getBanks() 을 호출하면서 최소한 1번 이상의 dataSource.retrieveBanks()가 호출되었다.
    verify(exactly = 1) { dataSource.retrieveBanks() }
    assertThat(banks).isEmpty()
  }
}
```

```kotlin
<Controller Test>

// 전체 Application Context를 Initialize할 수 있음
// 부분적으로 객체를 초기화시키는 등의 전략으로 Spring Boot Test를 해줘야 됨
// @SpringBootTest:Application Bean만 init
// @AutoConfigureMockMvc: MockMvc bean init
@SpringBootTest
@AutoConfigureMockMvc
// 객체 자동 주입(DI)
internal class BankControllerTest @Autowired constructor(
  // Request mapping 요청을 mocking하는 객체
  val mockMvc: MockMvc,
  // Object<->Json mapper
  val objectMapper: ObjectMapper
) {

  // 각 API에 대한 검증을 하기 위한 inner class
  // 외부 클래스의 Resource(MockMVC, ObjectMapper)를 활용하기 위한 전략
  @Nested
  @DisplayName("GET /api/banks")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  inner class GetBanks {
    @Test
    fun `should return all banks`() {
      // when
      // Mock Request 요청
      mockMvc.get("/api/banks")
        // Logging
        .andDo { print() }
        //then
        .andExpect {
          // AssertThat과 같은 부분
          status { isOk() }
          content { contentType(MediaType.APPLICATION_JSON) }
          jsonPath("$[0].accountNumber") { value("1101110") }
        }
    }
  }

  @Nested
  @DisplayName("GET /api/banks/{accountNumber}")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  inner class GetBank {
    @Test
    fun `should return the bank with the given account number`() {
      // given
      val accountNumber = 1101110
      // when
      mockMvc.get("/api/banks/$accountNumber")
        .andDo { print() }
        // then
        .andExpect {
          status { isOk() }
          content { contentType(MediaType.APPLICATION_JSON) }
          jsonPath("$.trust") { value("1.2") }
        }
    }

    @Test
    fun `should return NOT FOUND if the account number does not exist`() {
      // given
      val accountNumber = "0"
      // when
      mockMvc.get("/api/banks/$accountNumber")
        .andDo { print() }
        // then
        .andExpect {
          status { isNotFound() }
        }
    }
  }

  @Nested
  @DisplayName("POST /api/banks")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  inner class PostBankControllerTest {
    @Test
    fun `should add the new bank`() {
      // given
      val newBank = Bank("acc123", 12.24, 0)

      // when
      val performPost = mockMvc.post("/api/banks") {
        contentType = MediaType.APPLICATION_JSON
        content = objectMapper.writeValueAsString(newBank)
      }

      performPost
        .andDo { print() }
        // then
        .andExpect {
          status { isCreated() }
          content { contentType(MediaType.APPLICATION_JSON) }
          jsonPath("$.accountNumber") { value("acc123") }
          jsonPath("$.trust") { value("12.24") }
          jsonPath("$.transactionFee") { value("0") }
        }
    }

    @Test
    fun `should return BAD REQUEST if bank with given account number already exists`() {
      // given
      val invalidBank = Bank("1101110", 1.2, 3)

      // when
      val performPost = mockMvc.post("/api/banks") {
        contentType = MediaType.APPLICATION_JSON
        content = objectMapper.writeValueAsString(invalidBank)
      }

      performPost.andDo { print() }
        // then
        .andExpect { status { isBadRequest() } }
    }
  }

  @Nested
  @DisplayName("PATCH /api/banks")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  inner class PutControllerTest {
    @Test
    fun `should update an existing bank`() {
      // given
      val updatedBank = Bank("1101110", 1.2, 4)

      // when
      val performPut = mockMvc.patch("/api/banks") {
        contentType = MediaType.APPLICATION_JSON
        content = objectMapper.writeValueAsString(updatedBank)
      }

      // then
      performPut.andDo { print() }
        .andExpect {
          status { isOk() }
          content {
            contentType(MediaType.APPLICATION_JSON)
            json(objectMapper.writeValueAsString(updatedBank))
          }
        }

      mockMvc.get("/api/banks/${updatedBank.accountNumber}")
        .andExpect { content { json(objectMapper.writeValueAsString(updatedBank)) } }
    }

    @Test
    fun `should return BAD REQUEST if no bank with given account number`() {
      // given
      val invalidBank = Bank("1101115", 1.2, 3)

      // when
      val performPatch = mockMvc.patch("/api/banks") {
        contentType = MediaType.APPLICATION_JSON
        content = objectMapper.writeValueAsString(invalidBank)
      }

      // then
      performPatch.andDo { print() }
        .andExpect { status { isNotFound() } }
    }
  }

  @Nested
  @DisplayName("DELETE /api/banks/{accountNumber}")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  inner class DeleteBankTest {
    @Test
    fun `should delete the bank with the given account number`() {
      // given
      val accountNumber = "1101110"
      // when
      mockMvc.delete("/api/banks/${accountNumber}")
        // then
        .andDo { print() }
        .andExpect { status { isNoContent() } }

      mockMvc.get("/api/banks/${accountNumber}")
        .andExpect { status { isNotFound() } }
    }

    @Test
    fun `should return BAD REQUEST if no bank with given account number`() {
      // given
      val accountNumber = 1101115
      // when
      mockMvc.delete("/api/banks/${accountNumber}")
        .andDo { print() }
        // then
        .andExpect { status { isNotFound() } }
    }
  }
}

```

