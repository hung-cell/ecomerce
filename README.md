# E-Commerce Application

Project E-Commerce được xây dựng với Spring Boot 4.0.1 và Java 25.

## 🚀 Yêu cầu

- Java 21 hoặc cao hơn
- Maven 3.6+
- Docker Desktop (để chạy MySQL server)
- MySQL 8.0+ (hoặc dùng Docker) hoặc H2 (development)

## 📦 Cài đặt và chạy

### 1. Clone repository

```bash
git clone <repository-url>
cd ecomerce
```

### 2. Khởi động MySQL Server (bằng Docker)

**Xem hướng dẫn chi tiết trong [DOCKER_SETUP.md](DOCKER_SETUP.md)**

```bash
# Chạy MySQL server
docker-compose up -d

# Kiểm tra status
docker-compose ps
```

MySQL sẽ chạy tại `localhost:3306` với:
- Database: `ecommerce_db`
- Username: `root`
- Password: `root`

**phpMyAdmin:** http://localhost:8081 (để quản lý database)

### 3. Cấu hình môi trường (Optional)

**Tạo file `.env` nếu muốn custom:**
```bash
copy .env.example .env
```

**Cập nhật thông tin trong `.env`:**
```env
DATABASE_URL=jdbc:mysql://localhost:3306/ecommerce_db
DATABASE_USERNAME=root
DATABASE_PASSWORD=root
JWT_SECRET=your-secret-key-here
CORS_ORIGINS=http://localhost:3000
```

### 4. Build project

```bash
mvn clean install
```

### 5. Chạy application

**Development mode (H2 in-memory database):**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Với MySQL (Docker):**
```bash
# Đảm bảo MySQL đã chạy: docker-compose ps
mvn spring-boot:run
```

**Production mode:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

**Hoặc chạy từ JAR file:**
```bash
mvn clean package -DskipTests
java -jar target/ecomerce-0.0.1-SNAPSHOT.jar
```

## 🔗 URLs

| Service | URL | Mô tả |
|---------|-----|-------|
| Application | http://localhost:8080 | API endpoint chính |
| phpMyAdmin | http://localhost:8081 | MySQL Database Management |
| H2 Console | http://localhost:8080/h2-console | Database console (dev mode) |

## 🗄️ Database Configuration

### MySQL (Docker) - Recommended

**1. Khởi động MySQL:**
```bash
docker-compose up -d
```

**2. Kết nối:**
```yaml
Host: localhost
Port: 3306
Database: ecommerce_db
Username: root
Password: root
```

**3. Quản lý qua phpMyAdmin:**
- URL: http://localhost:8081
- Server: mysql
- Username: root
- Password: root

**Chi tiết:** Xem [DOCKER_SETUP.md](DOCKER_SETUP.md)

### H2 (Development - In-Memory)
```yaml
URL: jdbc:h2:mem:ecommerce_db
Username: sa
Password: (để trống)
```

### MySQL (Manual Installation)

**1. Cài đặt MySQL 8.0+**

**2. Tạo database:**
```sql
CREATE DATABASE ecommerce_db;
CREATE USER 'ecommerce_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON ecommerce_db.* TO 'ecommerce_user'@'localhost';
FLUSH PRIVILEGES;
```

**3. Cập nhật `application.yml`:**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ecommerce_db
    username: ecommerce_user
    password: your_password
```

## 📝 Maven Commands

```bash
# Clean project
mvn clean

# Compile
mvn compile

# Run tests
mvn test

# Package (tạo JAR file)
mvn package

# Skip tests
mvn clean install -DskipTests

# Run với profile cụ thể
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🔧 Thay đổi cấu hình

### Đổi port server

**Cách 1: Sửa trong `application.yml`**
```yaml
server:
  port: 8081
```

**Cách 2: Chạy với parameter**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Thay đổi database connection

Sửa trong `application-prod.yml` hoặc set environment variables:
```bash
set DATABASE_URL=jdbc:postgresql://localhost:5432/your_db
set DATABASE_USERNAME=your_username
set DATABASE_PASSWORD=your_password
```

## 🧪 Testing

```bash
# Chạy tất cả tests
mvn test

# Skip tests
mvn clean install -DskipTests
```

## 📚 API Documentation

### Test API endpoints:

**Example - Create User:**
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "Password@123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**Example - Get All Users:**
```bash
curl http://localhost:8080/api/v1/users?page=0&size=10
```

## 🐛 Troubleshooting

### Port đã được sử dụng

**Windows:**
```bash
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

Hoặc đổi port trong `application.yml`

### Database connection error

**Với Docker MySQL:**
1. Kiểm tra MySQL container đang chạy: `docker-compose ps`
2. Xem logs: `docker-compose logs mysql`
3. Restart container: `docker-compose restart mysql`

**Với MySQL manual:**
1. Kiểm tra MySQL service đang chạy
2. Verify username/password trong `application.yml`
3. Đảm bảo database `ecommerce_db` đã được tạo

### Docker issues

```bash
# Xem logs
docker-compose logs

# Restart services
docker-compose restart

# Stop và start lại
docker-compose down
docker-compose up -d
```

### Build fails

```bash
# Clean và rebuild
mvn clean install -U

# Skip tests nếu test fail
mvn clean install -DskipTests
```

## 📖 Tài liệu bổ sung

### Cho Developer
**[DEV_TASKS.md](DEV_TASKS.md)** - Hiểu chi tiết về:
- Kiến trúc project
- Các thành phần và luồng xử lý
- Hướng dẫn phát triển tính năng mới
- Best practices

### Docker & MySQL Setup
**[DOCKER_SETUP.md](DOCKER_SETUP.md)** - Hướng dẫn:
- Cài đặt và cấu hình Docker
- Quản lý MySQL container
- Backup/Restore database
- Troubleshooting Docker issues

## 🤝 Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

Apache License 2.0

## 📧 Contact

- Email: support@ecommerce.com
- Project: [https://github.com/yourusername/ecomerce](https://github.com/yourusername/ecomerce)
