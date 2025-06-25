pipeline {
    agent any

    environment {
        REGISTRY = "ricardo1782"
        COMPOSE_FILE = "docker-compose.yml"
        TAG = "latest"
    }

    stages {
        stage('Clonando o código fonte') {
            steps {
                git branch: 'main', url: 'https://github.com/ricbatera/consultdg-microservices.git'
            }
        }

        stage('Compilando utilitários e bibliotecas') {
            steps {
                script {
                    def services = [
                        'protocolo-service-util',
                        'database-mysql-service',
                        'api-gateway',
                        'eureka-naming-server'
                    ]
                    for (service in services) {
                        dir(service) {
                            sh 'mvn clean install -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Compilando microserviços') {
            steps {
                script {
                    def services = [
                        'api-boleto-service',
                        'aws-service',
                        'protocolo-service'
                    ]
                    for (service in services) {
                        dir(service) {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Criando imagens Docker e fazendo push para o Docker Hub') {
            steps {
                script {
                    def services = [
                        'api-gateway',
                        'eureka-naming-server',
                        'api-boleto-service',
                        'aws-service',
                        'protocolo-service'
                    ]

                    withCredentials([usernamePassword(credentialsId: 'dockerhub_credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh '''
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        '''

                        for (service in services) {
                            dir(service) {
                                def version = null
                                try {
                                    version = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
                                    if (!version || version.contains('project.version')) {
                                        echo "Versão inválida ou não encontrada para ${service}. Usando apenas 'latest'."
                                        version = null
                                    } else {
                                        echo "Versão detectada para ${service}: ${version}"
                                    }
                                } catch (e) {
                                    echo "Erro ao extrair a versão do ${service}: ${e.message}. Usando apenas 'latest'."
                                }

                                def imageBase = "${REGISTRY}/${service}"

                                if (version) {
                                    sh "docker build -t ${imageBase}:latest -t ${imageBase}:${version} ."
                                    sh "docker push ${imageBase}:latest"
                                    sh "docker push ${imageBase}:${version}"
                                } else {
                                    sh "docker build -t ${imageBase}:latest ."
                                    sh "docker push ${imageBase}:latest"
                                }
                            }
                        }

                        sh 'docker logout'
                    }
                }
            }
        }

        // stage('Remover containers e imagens antigas') {
        //     steps {
        //         script {
        //             def services = [
        //                 'api-gateway',
        //                 'eureka-naming-server',
        //                 'api-boletos-service',
        //                 'aws-service',
        //                 'protocolo-service'
        //             ]

        //             // Tira a stack do ar
        //             sh 'docker compose down || true'

        //             // Remove imagens locais com tag "latest"
        //             for (service in services) {
        //                 def image = "${REGISTRY}/${service}:latest"
        //                 sh "docker rmi -f ${image} || true"
        //             }
        //         }
        //     }
        // }

        // stage('Subindo nova stack com docker-compose') {
        //     steps {
        //         script {
        //             // Aqui você vai configurar seu docker-compose.yml com as versões desejadas
        //             sh 'docker compose up -d'
        //         }
        //     }
        // }
    }

    post {
        success {
            echo '✅ Stack atualizada com sucesso!'
        }
        failure {
            echo '❌ Ocorreu um erro na pipeline. Verifique os logs!'
        }
    }
}
