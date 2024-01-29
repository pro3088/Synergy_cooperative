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
                    sh 'echo "$DOCKER_PASSWORD" | docker login -u $DOCKER_USERNAME --password-stdin'
                    sh 'docker push $DOCKER_REGISTRY_URL:$BUILD_NO'
                }
            }
        }

        stage('Stop and Cleanup Remote Docker Containers') {
            steps {
                script {
                    // Connect to the target server and clean up containers
                    def stopCmd = "sshpass -p $SYNERGY_POSTGRES_PASS ssh root@$TARGET_SERVER_IP 'docker ps -q --filter \"label=$APP_NAME\" | xargs docker stop'"
                    def removeCmd = "sshpass -p $SYNERGY_POSTGRES_PASS ssh root@$TARGET_SERVER_IP 'docker ps -q --filter \"label=$APP_NAME\" -a | xargs docker rm'"

                    // Execute stop command and check exit code
                    def stopExitCode = sh(script: stopCmd, returnStatus: true)

                    if (stopExitCode == 0) {
                        echo "Stopped containers on the target server"
                    } else {
                        echo "No containers found to stop on the target server"
                    }

                    // Execute remove command and check exit code
                    def removeExitCode = sh(script: removeCmd, returnStatus: true)

                    if (removeExitCode == 0) {
                        echo "Removed containers on the target server"
                    } else {
                        echo "No containers found to remove on the target server"
                    }
                }
            }
        }


        stage('Run Docker Container on Remote Server') {
            steps {
                script {
                    sh "sshpass -p $SYNERGY_SERVER_PASS ssh root@$TARGET_SERVER_IP 'docker pull $DOCKER_REGISTRY_URL:$BUILD_NO'"
                    sh "sshpass -p $SYNERGY_SERVER_PASS ssh root@$TARGET_SERVER_IP 'docker run -d --name $APP_NAME -p 8000:8000 $DOCKER_REGISTRY_URL:$BUILD_NO'"
                }
            }
        }
    }
    triggers {
        githubPush()
    }
}