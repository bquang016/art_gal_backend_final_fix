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
                echo 'Bắt đầu build ứng dụng với Maven...'
                // Chạy lệnh Maven để build ra file .jar, bỏ qua các unit test
                sh 'mvn -B package --file pom.xml -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Bắt đầu build Docker image...'
                script {
                    // Sử dụng biến môi trường đã định nghĩa để đặt tên cho image
                    sh "docker build -t ${DOCKER_IMAGE_NAME}:latest ."
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                echo 'Đang đẩy image lên Docker Hub...'
                script {
                    // Sử dụng username và password từ Jenkins Credentials để đăng nhập
                    sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                    // Đẩy image với tag 'latest' lên Docker Hub
                    sh "docker push ${DOCKER_IMAGE_NAME}:latest"
                }
            }
        }
    }

    // Các hành động sẽ được thực hiện sau khi pipeline kết thúc, dù thành công hay thất bại
    post {
        always {
            echo 'Hoàn tất, đang đăng xuất khỏi Docker Hub.'
            sh 'docker logout'
        }
    }
}
