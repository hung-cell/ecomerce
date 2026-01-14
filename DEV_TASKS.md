# 📋 NHIỆM VỤ CHO DEVELOPER

## 🎯 MỤC TIÊU
Đọc hiểu toàn bộ kiến trúc và thành phần của base project E-Commerce để có thể phát triển các tính năng mới một cách nhất quán và đúng chuẩn.

---

## ✅ TASK 1: HIỂU KIẾN TRÚC TỔNG QUAN

### Yêu cầu:
- [ ] Đọc và hiểu cấu trúc thư mục của project
- [ ] Vẽ sơ đồ kiến trúc tổng quan (Layer Architecture)
- [ ] Giải thích vai trò của từng package

### Câu hỏi cần trả lời:

1. **Cấu trúc package được tổ chức như thế nào?**
   - Package `common` chứa những gì?
   - Package `config` chứa những gì?
   - Package `module` chứa những gì?

2. **Kiến trúc áp dụng là gì?**
   - Module-based architecture là gì?
   - Tại sao lại tổ chức theo module?
   - Ưu điểm của cách tổ chức này?

3. **Luồng xử lý request từ Client đến Database:**
   ```
   Client → ??? → ??? → ??? → ??? → Database
   ```
   Điền vào các ??? và giải thích vai trò từng tầng.

### Deliverable:
- File Markdown hoặc diagram mô tả kiến trúc
- Giải thích chi tiết vai trò từng layer

---

## ✅ TASK 2: PHÂN TÍCH COMMON PACKAGE

### Yêu cầu:
Đọc và phân tích từng package con trong `common/`

### 2.1. Package `common.constant`

**Files cần đọc:**
- `AppConstants.java`
- `ErrorCode.java`

**Câu hỏi:**
1. `AppConstants` chứa những loại constant nào?
2. Tại sao cần tách riêng constants ra file riêng?
3. `ErrorCode` enum có những thông tin gì?
4. Giải thích cấu trúc của một ErrorCode (status, code, message)
5. Khi nào nên thêm ErrorCode mới?

### 2.2. Package `common.entity`

**Files cần đọc:**
- `BaseEntity.java`

**Câu hỏi:**
1. `@MappedSuperclass` có ý nghĩa gì?
2. BaseEntity chứa những field nào?
3. JPA Auditing là gì? Các annotation @CreatedDate, @LastModifiedDate hoạt động như thế nào?
4. Tại sao cần có field `deleted`?
5. Khi tạo entity mới, cần extend BaseEntity không? Tại sao?

### 2.3. Package `common.exception`

**Files cần đọc:**
- `AppException.java`
- `ResourceNotFoundException.java`
- `BadRequestException.java`
- `GlobalExceptionHandler.java`

**Câu hỏi:**
1. Vẽ sơ đồ phân cấp của các Exception class
2. `AppException` có vai trò gì? Tại sao các exception khác extend nó?
3. `GlobalExceptionHandler` hoạt động như thế nào?
4. `@RestControllerAdvice` có tác dụng gì?
5. Khi nào exception được bắt và xử lý ở GlobalExceptionHandler?
6. Luồng xử lý exception từ Service → Controller → GlobalExceptionHandler
7. Khi nào nên tạo custom exception mới?

### 2.4. Package `common.response`

**Files cần đọc:**
- `ApiResponse.java`
- `ErrorResponse.java`
- `PageResponse.java`

**Câu hỏi:**
1. Tại sao cần wrapper response bằng `ApiResponse`?
2. Cấu trúc của `ApiResponse` gồm những field nào?
3. Sự khác biệt giữa `ApiResponse.success()` và `ApiResponse.error()`
4. `ErrorResponse` được dùng ở đâu?
5. `PageResponse` dùng cho mục đích gì? Cấu trúc của nó?
6. `@JsonInclude(JsonInclude.Include.NON_NULL)` có ý nghĩa gì?

### 2.5. Package `common.util`

**Files cần đọc:**
- `DateTimeUtil.java`
- `StringUtil.java`
- `ValidationUtil.java`

**Câu hỏi:**
1. Tại sao các util class có constructor private?
2. Liệt kê các method trong mỗi util class và công dụng
3. Khi nào nên thêm method vào util class?
4. Tại sao dùng static method cho util class?

### Deliverable:
- Document phân tích chi tiết từng package
- Trả lời tất cả các câu hỏi
- Ví dụ cách sử dụng từng component

---

## ✅ TASK 3: PHÂN TÍCH CONFIG PACKAGE

### Yêu cầu:
Đọc và hiểu từng configuration class

### 3.1. `SecurityConfig.java`

**Câu hỏi:**
1. Spring Security hoạt động như thế nào trong project này?
2. `PUBLIC_URLS` là gì? Tại sao cần define chúng?
3. `SessionCreationPolicy.STATELESS` có ý nghĩa gì?
4. `PasswordEncoder` (BCrypt) hoạt động ra sao?
5. JWT filter sẽ được thêm vào đâu? (xem TODO trong code)
6. Luồng authentication sẽ như thế nào khi implement JWT?

### 3.2. `CorsConfig.java`

**Câu hỏi:**
1. CORS là gì? Tại sao cần config CORS?
2. `allowedOrigins`, `allowedMethods`, `allowedHeaders` là gì?
3. `allowCredentials` có tác dụng gì?
4. Làm sao để thêm origin mới?

### 3.3. `JpaAuditingConfig.java`

**Câu hỏi:**
1. JPA Auditing là gì?
2. `AuditorAware` có vai trò gì?
3. Làm sao để lấy user hiện tại đang thao tác?
4. Khi nào field `createdBy` và `updatedBy` được tự động fill?

### 3.4. `ModelMapperConfig.java`

**Câu hỏi:**
1. ModelMapper dùng để làm gì?
2. Tại sao cần map giữa Entity và DTO?
3. `MatchingStrategies.STRICT` có ý nghĩa gì?
4. Cách sử dụng ModelMapper trong Service?

### 3.5. `WebConfig.java`

**Câu hỏi:**
1. Config resource handler để làm gì?
2. `/uploads/**` được map đến đâu?
3. Khi nào cần add resource handler mới?

### Deliverable:
- Document giải thích chi tiết từng config
- Trả lời tất cả câu hỏi
- Vẽ sơ đồ tương tác giữa các config

---

## ✅ TASK 4: PHÂN TÍCH MODULE USER (CRUD EXAMPLE)

### Yêu cầu:
Hiểu rõ luồng CRUD hoàn chỉnh qua module User

### 4.1. Phân tích Layer

**Files cần đọc theo thứ tự:**
1. `User.java` (Entity)
2. `UserRequest.java` & `UserResponse.java` (DTO)
3. `UserRepository.java` (Repository)
4. `UserService.java` (Service)
5. `UserController.java` (Controller)

### 4.2. Câu hỏi phân tích

**Entity Layer:**
1. User entity có những field nào?
2. Relationship annotations (`@OneToMany`, `@ManyToOne`) sẽ thêm ở đâu?
3. `@Enumerated(EnumType.STRING)` có tác dụng gì?
4. Tại sao dùng `@Builder` pattern?

**DTO Layer:**
1. Tại sao cần tách Entity và DTO?
2. UserRequest có những validation annotation nào?
3. Ý nghĩa của từng validation annotation
4. Khi nào validation được trigger?
5. UserResponse khác Entity ở điểm nào?

**Repository Layer:**
1. `JpaRepository` cung cấp những method nào sẵn?
2. Custom query method `findByUsername` hoạt động ra sao?
3. `existsByUsername` vs `findByUsername` - khi nào dùng cái nào?
4. Khi nào cần dùng `@Query` annotation?

**Service Layer:**
1. Annotation `@Service`, `@RequiredArgsConstructor`, `@Slf4j` có tác dụng gì?
2. `@Transactional` hoạt động như thế nào?
3. Sự khác biệt giữa `@Transactional(readOnly = true)` và `@Transactional`
4. Phân tích method `createUser()`:
   - Các bước xử lý
   - Validation logic
   - Password encoding
   - Mapping entity to DTO
5. Tại sao cần check `existsByUsername` trước khi create?
6. Method `updateUser()` xử lý update như thế nào?
7. Khi nào nên throw exception?

**Controller Layer:**
1. `@RestController` vs `@Controller` khác nhau gì?
2. `@RequestMapping` define base path như thế nào?
3. Các HTTP method annotation: `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
4. `@PathVariable` vs `@RequestParam` - khác nhau và khi nào dùng?
5. `@Valid` có tác dụng gì?
6. `ResponseEntity` là gì? Tại sao cần dùng nó?
7. HTTP Status codes: 200, 201, 400, 404, 500 - khi nào trả về status nào?

### 4.3. Luồng xử lý hoàn chỉnh

**Vẽ sequence diagram cho các luồng:**

1. **Create User (Success case):**
   ```
   Client → Controller → Service → Repository → Database
   ```
   Chi tiết từng bước xử lý

2. **Create User (Error case - Username exists):**
   ```
   Client → Controller → Service (throw BadRequestException) 
         → GlobalExceptionHandler → Client (400 Bad Request)
   ```

3. **Get User by ID (Success):**
   ```
   Client → ??? → ??? → ??? → ???
   ```

4. **Get User by ID (Not Found):**
   ```
   Client → ??? → ??? (throw ResourceNotFoundException) → ???
   ```

5. **Update User:**
   - Vẽ luồng chi tiết
   - Xử lý validation
   - Check duplicate username/email

6. **Get All Users (Pagination):**
   - Pageable là gì?
   - Sort hoạt động ra sao?
   - PageResponse được build như thế nào?

### Deliverable:
- Document phân tích chi tiết từng layer
- Sequence diagram cho từng use case
- Trả lời tất cả câu hỏi
- Code ví dụ về cách tương tác với API (curl hoặc Postman)

---

## ✅ TASK 5: TEST API THỰC TÉ

### Yêu cầu:
Chạy application và test tất cả API endpoints

### 5.1. Setup Database và chạy

**Khởi động MySQL:**
```bash
docker-compose up -d
```

**Chạy application:**
```bash
mvn clean install
mvn spring-boot:run
```

### 5.2. Test từng endpoint

**Sử dụng cURL, Postman, hoặc REST Client**

1. **POST /api/v1/users - Create User**
   ```bash
   curl -X POST http://localhost:8080/api/v1/users \
     -H "Content-Type: application/json" \
     -d '{
       "username": "john_doe",
       "email": "john@example.com",
       "password": "Password@123",
       "firstName": "John",
       "lastName": "Doe",
       "phone": "+84901234567"
     }'
   ```
   - Test với data hợp lệ
   - Test với data thiếu field required
   - Test với email không hợp lệ
   - Test với password yếu
   - Test duplicate username
   - Test duplicate email

2. **GET /api/v1/users - Get All Users**
   ```bash
   curl "http://localhost:8080/api/v1/users?page=0&size=10&sortBy=id&sortDir=asc"
   ```
   - Test pagination (page, size)
   - Test sorting (sortBy, sortDir)
   - Verify response structure

3. **GET /api/v1/users/{id} - Get User by ID**
   ```bash
   curl http://localhost:8080/api/v1/users/1
   ```
   - Test với ID tồn tại
   - Test với ID không tồn tại

4. **GET /api/v1/users/username/{username} - Get User by Username**
   - Test với username tồn tại
   - Test với username không tồn tại

5. **PUT /api/v1/users/{id} - Update User**
   ```bash
   curl -X PUT http://localhost:8080/api/v1/users/1 \
     -H "Content-Type: application/json" \
     -d '{
       "username": "john_doe_updated",
       "email": "john.updated@example.com",
       "password": "NewPassword@123",
       "firstName": "John",
       "lastName": "Doe Updated"
     }'
   ```
   - Update tất cả fields
   - Update một số fields
   - Test validation
   - Test duplicate username/email

6. **DELETE /api/v1/users/{id} - Delete User**
   ```bash
   curl -X DELETE http://localhost:8080/api/v1/users/1
   ```
   - Delete user tồn tại
   - Delete user không tồn tại

### 5.3. Quan sát

- [ ] Xem response format cho success case
- [ ] Xem response format cho error case
- [ ] Check database (phpMyAdmin: http://localhost:8081 hoặc H2 Console: http://localhost:8080/h2-console)
- [ ] Xem logs trong console
- [ ] Verify JPA Auditing fields (createdAt, updatedAt, createdBy, updatedBy)
- [ ] Xem data trong MySQL qua phpMyAdmin

### Deliverable:
- Document kết quả test chi tiết
- Screenshot hoặc JSON response mẫu
- List các lỗi gặp phải và cách fix

---

## ✅ TASK 6: HIỂU APPLICATION CONFIGURATION

### Yêu cầu:
Phân tích các file configuration

### 6.1. `application.yml`

**Câu hỏi:**
1. Spring profiles là gì?
2. Profile active mặc định là gì?
3. JPA configuration:
   - `ddl-auto: update` có nghĩa gì?
   - `show-sql: true` dùng để làm gì?
4. Datasource configuration cho PostgreSQL
5. File upload configuration (multipart)
6. Mail configuration
7. JWT configuration trong `app.jwt`
8. CORS configuration trong `app.cors`
9. Logging configuration

### 6.2. `application-dev.yml`

**Câu hỏi:**
1. Profile dev khác gì với profile mặc định?
2. H2 in-memory database là gì?
3. H2 Console được enable ở đâu?
4. `ddl-auto: create-drop` vs `update` khác gì?

### 6.3. `application-prod.yml`

**Câu hỏi:**
1. Profile prod khác dev ở những điểm nào?
2. Tại sao `ddl-auto: validate`?
3. Environment variables được dùng như thế nào?
4. Logging level khác dev ra sao?

### 6.4. `.env.example`

**Câu hỏi:**
1. File .env dùng để làm gì?
2. Các environment variable nào cần thiết?
3. Làm sao để load .env vào application?

### Deliverable:
- Document giải thích chi tiết configuration
- So sánh dev vs prod profile
- Hướng dẫn setup environment variables

---

## ✅ TASK 7: PHÂN TÍCH DEPENDENCIES (pom.xml)

### Yêu cầu:
Hiểu vai trò của từng dependency

### Câu hỏi:

**Spring Boot Starters:**
1. `spring-boot-starter-web` - Dùng để làm gì?
2. `spring-boot-starter-data-jpa` - Vai trò?
3. `spring-boot-starter-validation` - Khi nào dùng?
4. `spring-boot-starter-security` - Chức năng?
5. `spring-boot-starter-cache` - Cache gì?
6. `spring-boot-starter-mail` - Email service?

**Database:**
1. PostgreSQL - Production database
2. H2 - Khi nào dùng?

**JWT:**
1. jjwt-api, jjwt-impl, jjwt-jackson - Vai trò từng thư viện?

**Others:**
1. modelmapper - Object mapping
3. aws-java-sdk-s3 - File storage
4. commons-lang3 & commons-io - Utilities
5. lombok - Code generation

**Dev Tools:**
1. spring-boot-devtools - Hot reload

**Testing:**
1. spring-boot-starter-test - Test framework
2. spring-security-test - Security testing

### Deliverable:
- Bảng phân loại dependencies theo chức năng
- Giải thích chi tiết vai trò từng dependency
- Gợi ý dependencies cần thêm cho tương lai

---

## ✅ TASK 8: TẠO MODULE MỚI (HANDS-ON)

### Yêu cầu:
Áp dụng kiến thức đã học để tạo module Product

### 8.1. Thiết kế

**Product Entity fields:**
- id, name, description, price, stock, categoryId
- images (List of image URLs)
- status (ACTIVE, INACTIVE, OUT_OF_STOCK)
- Extend BaseEntity

### 8.2. Implementation checklist

- [ ] Tạo `Product` entity
- [ ] Tạo `ProductRequest` DTO (với validation)
- [ ] Tạo `ProductResponse` DTO
- [ ] Tạo `ProductRepository` (với custom queries)
- [ ] Tạo `ProductService` (CRUD methods)
- [ ] Tạo `ProductController` (REST endpoints)
- [ ] Test tất cả endpoints

### 8.3. Yêu cầu nâng cao

- [ ] Implement search Product by name
- [ ] Filter by price range
- [ ] Filter by status
- [ ] Sort by price, name, createdAt
- [ ] Pagination

### Deliverable:
- Source code đầy đủ cho Product module
- cURL commands hoặc Postman collection
- Test results

---

## ✅ TASK 9: XỬ LÝ EDGE CASES

### Yêu cầu:
Phân tích và xử lý các trường hợp đặc biệt

### Câu hỏi:

1. **Validation:**
   - Điều gì xảy ra khi gửi request với field null?
   - Validation message có customize được không?
   - Làm sao để validate custom business logic?

2. **Exception Handling:**
   - Khi nào dùng BadRequestException vs ResourceNotFoundException?
   - Làm sao để thêm custom exception mới?
   - HTTP status code nào phù hợp cho từng loại lỗi?

3. **Transaction:**
   - @Transactional hoạt động như thế nào?
   - Khi nào transaction được rollback?
   - Nested transaction xử lý ra sao?

4. **Security:**
   - API nào cần authentication?
   - Làm sao để allow anonymous access?
   - Role-based authorization sẽ implement như thế nào?

5. **Performance:**
   - N+1 query problem là gì? Làm sao tránh?
   - Khi nào nên dùng cache?
   - Pagination quan trọng như thế nào?

### Deliverable:
- Document phân tích chi tiết
- Code examples cho edge cases
- Best practices và anti-patterns

---

## ✅ TASK 10: BEST PRACTICES & CODING STANDARDS

### Yêu cầu:
Nắm vững các quy chuẩn code trong project

### 10.1. Naming Conventions

- Package names: lowercase
- Class names: PascalCase
- Method names: camelCase
- Constants: UPPER_SNAKE_CASE
- Variables: camelCase

### 10.2. Code Organization

- Một class một file
- Group related methods
- Private methods ở cuối class
- Constants ở đầu class

### 10.3. Documentation

- Javadoc cho public methods
- Meaningful variable names
- Comment cho logic phức tạp

### 10.4. Error Handling

- Không bắt exception chung (catch Exception)
- Throw specific exceptions
- Log error với level phù hợp

### 10.5. Testing

- Unit test cho Service layer
- Integration test cho Controller
- Test coverage minimum 80%

### Deliverable:
- Checklist coding standards
- Code review guidelines
- Common mistakes cần tránh

---

## 📊 TIÊU CHÍ ĐÁNH GIÁ

### Mức độ hoàn thành:

**Level 1 - Basic (Tasks 1-4):**
- Hiểu kiến trúc tổng quan
- Hiểu common components
- Hiểu luồng CRUD cơ bản

**Level 2 - Intermediate (Tasks 5-7):**
- Test API thành công
- Hiểu configuration
- Hiểu dependencies

**Level 3 - Advanced (Tasks 8-10):**
- Tự tạo module mới
- Xử lý edge cases
- Áp dụng best practices

### Output mong đợi:

1. **Technical Document (Markdown)**
   - Trả lời TẤT CẢ câu hỏi
   - Diagrams và flowcharts
   - Code examples

2. **Product Module (Source Code)**
   - Hoàn chỉnh và hoạt động
   - Follow coding standards
   - Có API documentation

3. **Test Results**
   - Screenshots
   - Test cases
   - Bug reports (nếu có)

---

## 🎯 THỜI GIAN ƯỚC TÍNH

- **Tasks 1-4**: 2-3 ngày
- **Tasks 5-7**: 1-2 ngày  
- **Tasks 8-10**: 2-3 ngày

**Tổng**: 5-8 ngày (tùy experience level)

---

## 📝 LƯU Ý

1. Đọc code theo thứ tự từ dưới lên (Database → Service → Controller)
2. Chạy application và debug để hiểu luồng
3. Thử break code để xem error handling
4. Đặt câu hỏi khi không hiểu
5. Document càng chi tiết càng tốt

---

## 🚀 SAU KHI HOÀN THÀNH

Bạn sẽ có thể:
- ✅ Hiểu rõ kiến trúc Spring Boot
- ✅ Tự tin tạo module mới
- ✅ Apply best practices
- ✅ Debug và fix issues
- ✅ Review code của team members
- ✅ Mentor junior developers

**Good luck! 💪**

