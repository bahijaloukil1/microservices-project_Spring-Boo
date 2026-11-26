pipeline {
  agent any
  environment {
    REGISTRY = '100.93.99.105:8082'
    IMAGE    = "${REGISTRY}/eureka-server"
    TAG      = "${env.BUILD_NUMBER}"
    APP_DIR  = 'eureka-server/eureka-server'
  }
  stages {
    stage('Checkout') {
      steps { checkout scm }
    }
    stage('Test') {
      steps { dir("${APP_DIR}") { sh 'mvn clean test' } }
      post { always { junit allowEmptyResults: true, testResults: "${APP_DIR}/target/surefire-reports/*.xml" } }
    }
    stage('SonarQube Analysis') {
      steps {
        dir("${APP_DIR}") {
          withSonarQubeEnv('sonar') {
            sh 'mvn sonar:sonar -Dsonar.projectKey=eureka-server'
          }
        }
      }
    }
    stage('Build JAR') {
      steps { dir("${APP_DIR}") { sh 'mvn clean package -DskipTests' } }
    }
    stage('Build Image') {
      steps { dir("${APP_DIR}") { sh 'docker build -t $IMAGE:$TAG -t $IMAGE:latest .' } }
    }
    stage('Push to Nexus') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'nexus-creds',
                         usernameVariable: 'U', passwordVariable: 'P')]) {
          sh '''
            echo $P | docker login $REGISTRY -u $U --password-stdin
            docker push $IMAGE:$TAG
            docker push $IMAGE:latest
          '''
        }
      }
    }
    stage('Deploy to K8s') {
      steps {
        withKubeConfig([credentialsId: 'kubeconfig']) {
          sh 'kubectl apply -f k8s/'
          sh 'kubectl rollout restart deployment/eureka-server'
        }
      }
    }
  }
}
