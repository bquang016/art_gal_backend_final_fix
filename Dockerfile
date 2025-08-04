# --- GIAI ĐOẠN 1: BUILD ỨNG DỤNG ---
# Sử dụng image chính thức của Maven và OpenJDK 21 để build.
FROM maven:3.9-eclipse-temurin-21 AS build

# Thiết lập thư mục làm việc bên trong image.
WORKDIR /app

# Sao chép các file cấu hình Maven và tải thư viện trước.
COPY pom.xml .
COPY .mvn .mvn
RUN mvn dependency:go-offline

# Sao chép toàn bộ mã nguồn của dự án vào image.
COPY src ./src

# Chạy lệnh Maven để build ứng dụng thành file .jar.
RUN mvn package -DskipTests

# --- GIAI ĐOẠN 2: TẠO IMAGE CHẠY ỨNG DỤNG ---
# Sử dụng một image Java 21 nhẹ hơn.
FROM openjdk:21-jdk-slim

# Thiết lập thư mục làm việc.
WORKDIR /app

# ✅ SỬA LỖI: Chỉ định chính xác tên file .jar cần sao chép
COPY --from=build /app/target/art_gal-0.0.1-SNAPSHOT.jar app.jar

# Mở cổng 8086 của container.
EXPOSE 8086

# Lệnh sẽ được thực thi khi container được khởi động
ENTRYPOINT ["java", "-jar", "app.jar"]