pipeline {
    environment {
        APP_NAME = 'synergy_cooperative'
        DOCKER_REGISTRY_URL = "${DOCKER_REGISTRY_URL}"
        BUILD_NO = "${BUILD_NUMBER}"
        HOST = "${SYNERGY_POSTGRES_URL}"
        PORT = '25060'
        DATABASE = 'synergy'
        FILEPATH = 'src/main/resources/application.yml'
        USERNAME = 'synergy'
        PASSWORD = "${SYNERGY_POSTGRES_PASS}"
        TARGET_SERVER_IP = "${SYNERGY_SERVER_URL}"
        TARGET_SERVER_PASS = "${SYNERGY_SERVER_PASS}"
        SECRET_KEY = "${SECRET_KEY}"
        DOCKER_USERNAME = "${DOCKER_USERNAME}"
        DOCKER_PASSWORD = "${DOCKER_PASSWORD}"
    }
    agent any
    tools {
        maven 'Maven'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Change Datasource URL') {
            steps {
                script {
                    def newUrl = "jdbc:postgresql://${HOST}:${PORT}/${DATABASE}"
                    def newUserName = "username: ${USERNAME}"
                    def newPassword = "password: ${PASSWORD}"
                    def secretKey = "secret: ${SECRET_KEY}"

                    def configFile = readFile("$FILEPATH")
                    configFile = configFile.replaceAll('jdbc:postgresql://localhost:5432/synergy_cooperative', newUrl)
                    configFile = configFile.replaceAll('username: postgres', newUserName)
                    configFile = configFile.replaceAll('password: root', newPassword)
                    configFile = configFile.replaceAll('secret: secret-key', secretKey)

                    writeFile(file: "$FILEPATH", text: configFile)
                }
            }
        }
        stage('Maven Build') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Docker Build and Push') {
            steps {
                script {
                    sh 'docker build -t $APP_NAME:1.0 .'
                    sh 'docker tag $APP_NAME:1.0 $DOCKER_REGISTRY_URL:$BUILD_NO'
                    sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD'
                    sh 'docker push $DOCKER_REGISTRY_URL:$BUILD_NO'
                }
            }
        }

        stage('Cleanup Remote Docker Containers') {
            steps {
                script {
                    // Connect to the target server and clean up containers
                    sh "sshpass -p $SYNERGY_POSTGRES_PASS ssh root@$TARGET_SERVER_IP 'docker ps -q --filter \"label=$APP_NAME\" | xargs docker stop'"
                    sh "sshpass -p $SYNERGY_POSTGRES_PASS ssh root@$TARGET_SERVER_IP 'docker ps -q --filter \"label=$APP_NAME\" -a | xargs docker rm'"
                }
            }
        }

        stage('Run Docker Container on Remote Server') {
            steps {
                script {
                    sh "sshpass -p $SYNERGY_SERVER_PASS ssh root@$TARGET_SERVER_IP 'docker pull $DOCKER_REGISTRY_URL/$APP_NAME:$BUILD_NO'"
                    sh "sshpass -p $SYNERGY_SERVER_PASS ssh root@$TARGET_SERVER_IP 'docker run -d --name $APP_NAME -p 8000:8000 $DOCKER_REGISTRY_URL/$APP_NAME:$BUILD_NO'"
                }
            }
        }
    }
    triggers {
        githubPush()
    }
}