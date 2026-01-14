# 🐳 Docker MySQL Setup Guide

Hướng dẫn cài đặt và chạy MySQL server bằng Docker cho project E-Commerce.

## 📋 Yêu cầu

- Docker Desktop đã cài đặt
- Docker Compose (đi kèm với Docker Desktop)

### Cài đặt Docker Desktop

**Windows:**
1. Download từ: https://www.docker.com/products/docker-desktop
2. Cài đặt và khởi động Docker Desktop
3. Đợi Docker khởi động hoàn tất (icon Docker ở system tray)

**Verify Docker đã cài:**
```bash
docker --version
docker-compose --version
```

---

## 🚀 Khởi động MySQL Server

### Bước 1: Chạy Docker Compose

Từ thư mục root của project:

```bash
docker-compose up -d
```

**Giải thích:**
- `up`: Khởi động services
- `-d`: Chạy ở chế độ detached (background)

### Bước 2: Kiểm tra services đang chạy

```bash
docker-compose ps
```

Bạn sẽ thấy:
```
NAME                    STATUS         PORTS
ecommerce-mysql         Up (healthy)   0.0.0.0:3306->3306/tcp
ecommerce-phpmyadmin    Up             0.0.0.0:8081->80/tcp
```

### Bước 3: Xem logs (nếu cần)

```bash
# Xem logs của MySQL
docker-compose logs mysql

# Xem logs real-time
docker-compose logs -f mysql
```

---

## 🔧 Thông tin kết nối Database

### MySQL Connection Info

| Thông tin | Giá trị |
|-----------|---------|
| **Host** | localhost |
| **Port** | 3306 |
| **Database** | ecommerce_db |
| **Root User** | root |
| **Root Password** | root |
| **App User** | ecommerce_user |
| **App Password** | ecommerce_pass |

### Connection String

```
jdbc:mysql://localhost:3306/ecommerce_db
```

---

## 🌐 phpMyAdmin - Web Interface

Quản lý database qua giao diện web:

**URL:** http://localhost:8081

**Login:**
- Server: mysql
- Username: root
- Password: root

### Sử dụng phpMyAdmin:

1. Truy cập http://localhost:8081
2. Đăng nhập với thông tin trên
3. Chọn database `ecommerce_db`
4. Xem tables, data, chạy queries

---

## 🔗 Kết nối từ Application

### Option 1: Sử dụng root user (Development)

File `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ecommerce_db?createDatabaseIfNotExist=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### Option 2: Sử dụng app user (Recommended)

File `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ecommerce_db
    username: ecommerce_user
    password: ecommerce_pass
    driver-class-name: com.mysql.cj.jdbc.Driver
```

---

## 📝 Docker Commands Thường dùng

### Quản lý services

```bash
# Khởi động services
docker-compose up -d

# Dừng services (giữ data)
docker-compose stop

# Khởi động lại services
docker-compose restart

# Dừng và xóa containers (giữ data)
docker-compose down

# Dừng và xóa containers + volumes (XÓA DATA)
docker-compose down -v
```

### Xem logs

```bash
# Xem logs tất cả services
docker-compose logs

# Xem logs MySQL
docker-compose logs mysql

# Follow logs real-time
docker-compose logs -f

# Xem 50 dòng log cuối
docker-compose logs --tail=50
```

### Kiểm tra status

```bash
# Xem status services
docker-compose ps

# Xem resource usage
docker stats

# Xem thông tin chi tiết MySQL container
docker inspect ecommerce-mysql
```

---

## 🗄️ Quản lý Database

### Kết nối MySQL CLI

```bash
# Kết nối vào MySQL container
docker exec -it ecommerce-mysql mysql -uroot -proot

# Hoặc connect vào bash của container
docker exec -it ecommerce-mysql bash
mysql -uroot -proot
```

### MySQL Commands

```sql
-- Xem databases
SHOW DATABASES;

-- Chọn database
USE ecommerce_db;

-- Xem tables
SHOW TABLES;

-- Xem cấu trúc table
DESCRIBE users;

-- Query data
SELECT * FROM users;

-- Tạo user mới
CREATE USER 'newuser'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON ecommerce_db.* TO 'newuser'@'%';
FLUSH PRIVILEGES;
```

### Backup Database

```bash
# Backup database
docker exec ecommerce-mysql mysqldump -uroot -proot ecommerce_db > backup.sql

# Restore database
docker exec -i ecommerce-mysql mysql -uroot -proot ecommerce_db < backup.sql
```

---

## 🔄 Reset Database

### Xóa tất cả data và bắt đầu lại

```bash
# Dừng services và xóa volumes
docker-compose down -v

# Khởi động lại (database mới)
docker-compose up -d
```

### Chỉ xóa tables (giữ lại database)

```bash
docker exec -it ecommerce-mysql mysql -uroot -proot -e "DROP DATABASE ecommerce_db; CREATE DATABASE ecommerce_db;"
```

---

## 🐛 Troubleshooting

### 1. Port 3306 đã được sử dụng

**Lỗi:** `Bind for 0.0.0.0:3306 failed: port is already allocated`

**Giải pháp:**

**Cách 1: Dừng MySQL service đang chạy**
```bash
# Windows
net stop MySQL80

# Hoặc tìm và tắt service MySQL trong Services
```

**Cách 2: Đổi port trong docker-compose.yml**
```yaml
ports:
  - "3307:3306"  # Đổi từ 3306 thành 3307
```

Sau đó update application.yml:
```yaml
url: jdbc:mysql://localhost:3307/ecommerce_db
```

### 2. Container không khởi động

```bash
# Xem logs để biết lỗi
docker-compose logs mysql

# Xóa container và tạo lại
docker-compose down
docker-compose up -d
```

### 3. Không kết nối được từ application

**Kiểm tra:**
1. Docker container có đang chạy không?
   ```bash
   docker-compose ps
   ```

2. Ping MySQL port:
   ```bash
   telnet localhost 3306
   ```

3. Check logs MySQL:
   ```bash
   docker-compose logs mysql
   ```

4. Verify connection string trong application.yml

### 4. "Public Key Retrieval is not allowed"

**Lỗi khi connect:**
```
java.sql.SQLException: Public Key Retrieval is not allowed
```

**Giải pháp:** Thêm parameter vào URL:
```yaml
url: jdbc:mysql://localhost:3306/ecommerce_db?allowPublicKeyRetrieval=true&useSSL=false
```

### 5. Authentication plugin error

**Giải pháp:** Docker compose đã config `mysql_native_password`, restart container:
```bash
docker-compose restart mysql
```

---

## 📊 Monitoring

### Xem resource usage

```bash
# Real-time stats
docker stats ecommerce-mysql

# Disk usage
docker system df
```

### Xem MySQL logs

```bash
# Logs trong container
docker exec ecommerce-mysql tail -f /var/log/mysql/error.log
```

---

## 🔒 Security Notes

### Development vs Production

**⚠️ Cấu hình hiện tại chỉ dùng cho DEVELOPMENT!**

### Cho Production:

1. **Đổi passwords mạnh:**
```yaml
environment:
  MYSQL_ROOT_PASSWORD: strong_random_password_here
  MYSQL_PASSWORD: another_strong_password
```

2. **Tắt phpMyAdmin hoặc protect bằng authentication**

3. **Không expose port 3306 ra ngoài:**
```yaml
ports: []  # Bỏ port mapping nếu app cùng docker network
```

4. **Sử dụng secrets management**

---

## 🎯 Quick Reference

### Start/Stop Commands

```bash
# Start
docker-compose up -d

# Stop
docker-compose stop

# Restart
docker-compose restart

# Stop and remove
docker-compose down

# View logs
docker-compose logs -f mysql

# Access MySQL CLI
docker exec -it ecommerce-mysql mysql -uroot -proot

# Access phpMyAdmin
# Browser: http://localhost:8081
```

### Connection URLs

| Service | URL/Connection |
|---------|----------------|
| MySQL | `localhost:3306` |
| phpMyAdmin | http://localhost:8081 |
| Application | http://localhost:8080 |

---

## 📚 Tài liệu thêm

- [Docker Documentation](https://docs.docker.com/)
- [MySQL Docker Hub](https://hub.docker.com/_/mysql)
- [Docker Compose Reference](https://docs.docker.com/compose/compose-file/)
- [MySQL Documentation](https://dev.mysql.com/doc/)

---

## ✅ Checklist Setup

- [ ] Cài đặt Docker Desktop
- [ ] Clone project
- [ ] Chạy `docker-compose up -d`
- [ ] Verify MySQL running: `docker-compose ps`
- [ ] Truy cập phpMyAdmin: http://localhost:8081
- [ ] Update `application.yml` với MySQL config
- [ ] Chạy Spring Boot application
- [ ] Test kết nối database

---

**🎉 Done! MySQL server đã sẵn sàng cho development.**

