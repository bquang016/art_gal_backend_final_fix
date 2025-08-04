// File: Jenkinsfile

pipeline {
    // Chỉ định pipeline sẽ chạy trên bất kỳ agent nào có sẵn
    agent any

    // Khai báo các biến môi trường sẽ được sử dụng trong pipeline
    environment {
        // Lấy thông tin đăng nhập từ Credentials đã lưu trong Jenkins
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials') 
        // Thay 'your-dockerhub-username' bằng tên tài khoản Docker Hub của bạn
        DOCKER_IMAGE_NAME = "quangtb7/art-gallery-backend" 
    }

    // Định nghĩa các giai đoạn (stages) của pipeline
    stages {
        stage('Checkout Code') {
            steps {
                echo 'Bắt đầu lấy mã nguồn từ GitHub...'
                // Lấy mã nguồn từ repository đã được cấu hình trong Jenkins Job
                checkout scm
            }
        }

        stage('Build Spring Boot App') {
            steps {
                echo 'Đang build ứng dụng với Maven...'
                // ✅ SỬA LỖI: Dùng 'bat' thay vì 'sh' cho Windows
                bat 'mvnw -B package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Đang build Docker image...'
                script {
                    // ✅ SỬA LỖI: Dùng 'bat' thay vì 'sh' cho Windows
                    bat "docker build -t ${DOCKER_IMAGE_NAME}:latest ."
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                echo 'Đang đẩy image lên Docker Hub...'
                script {
                    // ✅ SỬA LỖI: Dùng 'bat' thay vì 'sh' cho Windows
                    bat 'echo %DOCKERHUB_CREDENTIALS_PSW% | docker login -u %DOCKERHUB_CREDENTIALS_USR% --password-stdin'
                    bat "docker push ${DOCKER_IMAGE_NAME}:latest"
                }
            }
        }
    }

    // Các hành động sẽ được thực hiện sau khi pipeline kết thúcc.
    post {
        always {
            echo 'Hoàn tất, đang đăng xuất khỏi Docker Hub.'
            // ✅ SỬA LỖI: Dùng 'bat' thay vì 'sh' cho Windows
            bat 'docker logout'
        }
    }
}