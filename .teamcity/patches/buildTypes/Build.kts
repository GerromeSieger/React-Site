package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.DockerCommandStep
import jetbrains.buildServer.configs.kotlin.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.buildSteps.sshExec
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Build'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Build")) {
    params {
        remove {
            password("env.PROJECT_KEY", "credentialsJSON:20ff450f-4846-47e6-8d9b-c47cee639d9d")
        }
        expect {
            password("env.SONAR_HOST_URL", "credentialsJSON:9f2d9368-e5bc-4dcf-abf6-fb69a1e90532")
        }
        update {
            password("env.SONAR_HOST_URL", "credentialsJSON:4d099940-9acf-420f-8f65-88d50d2dc95a")
        }
        remove {
            password("env.SONAR_TOKEN", "credentialsJSON:1b043825-95ef-4c59-b7da-1cb9aa272e56")
        }
        add {
            password("SONAR_HOST_URL", "credentialsJSON:4d099940-9acf-420f-8f65-88d50d2dc95a", display = ParameterDisplay.HIDDEN, readOnly = true)
        }
        add {
            password("PROJECT_KEY", "credentialsJSON:20ff450f-4846-47e6-8d9b-c47cee639d9d", display = ParameterDisplay.HIDDEN, readOnly = true)
        }
        add {
            password("SONAR_TOKEN", "credentialsJSON:617e93e3-e92c-4c35-94bd-b123bd30952e", display = ParameterDisplay.HIDDEN, readOnly = true)
        }
    }

    expectSteps {
        dockerCommand {
            name = "test"
            commandType = other {
                subCommand = "run"
                commandArgs = """
                    --rm 
                    -e SONAR_HOST_URL=%SONAR_HOST_URL%
                    -e SONAR_LOGIN=sqa_6886e865b8e42c330891b056a3351385b8f85133
                    -v "%teamcity.build.checkoutDir%:/usr/src" 
                    sonarsource/sonar-scanner-cli:latest 
                    -Dsonar.projectKey=%PROJECT_KEY%
                """.trimIndent()
            }
        }
        dockerCommand {
            name = "Login to DockerHub"
            commandType = other {
                subCommand = "run"
                commandArgs = """
                    --rm 
                    -e DOCKER_USERNAME="%env.DOCKERHUB_USERNAME%" 
                    -e DOCKER_PASSWORD="%env.DOCKERHUB_PASSWORD%" 
                    docker:dind 
                    sh -c "echo ${'$'}DOCKER_PASSWORD | docker login -u ${'$'}DOCKER_USERNAME --password-stdin"
                """.trimIndent()
            }
        }
        dockerCommand {
            name = "Build Docker Image"
            commandType = build {
                source = file {
                    path = "Dockerfile"
                }
                contextDir = "."
                namesAndTags = "%env.DOCKER_IMAGE%"
            }
        }
        dockerCommand {
            name = "Push Docker Image"
            commandType = push {
                namesAndTags = "%env.DOCKER_IMAGE%"
            }
        }
        sshExec {
            name = "Deploy to EC2 Instance"
            commands = "docker run -p 5000:80 -d --name myapp %env.DOCKER_IMAGE%"
            targetUrl = "%env.HOST%"
            authMethod = uploadedKey {
                username = "%env.USER%"
                key = "id_rsa"
            }
        }
    }
    steps {
        update<DockerCommandStep>(0) {
            clearConditions()
            commandType = other {
                subCommand = "run"
                workingDir = ""
                commandArgs = """
                    --rm 
                    -e SONAR_HOST_URL=%SONAR_HOST_URL%
                    -e SONAR_LOGIN="sqa_6886e865b8e42c330891b056a3351385b8f85133"
                    -v "%teamcity.build.checkoutDir%:/usr/src" 
                    sonarsource/sonar-scanner-cli:latest 
                    -Dsonar.projectKey=%PROJECT_KEY%
                """.trimIndent()
            }
        }
    }
}
