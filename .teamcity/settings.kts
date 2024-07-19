import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2024.03"

project {

    buildType(Build)
}

object Build : BuildType({
    name = "React"

    params {
        param(env.HOST, credentialsJSON:01d99e4c-8d5b-4a5f-9839-20bb51349186)
        param(env.PROJECT_KEY, credentialsJSON:20ff450f-4846-47e6-8d9b-c47cee639d9d)
        param(env.SONAR_TOKEN, credentialsJSON:1b043825-95ef-4c59-b7da-1cb9aa272e56)
        param(env.DOCKERHUB_PASSWORD, credentialsJSON:9cd69afe-e02b-43ca-9717-9e73c7302b0d)
        param(env.DOCKER_IMAGE, credentialsJSON:c8b36424-1973-45c3-84e5-58a31d380474)
        param(env.USER, credentialsJSON:b322c631-af42-4ed2-a1a7-6dd7bb684134)
        param(env.DOCKERHUB_USERNAME, credentialsJSON:407f140b-cb3b-4009-9d5f-cdd1275efafd)
        param(env.SONAR_HOST_URL, credentialsJSON:9f2d9368-e5bc-4dcf-abf6-fb69a1e90532)
    }

    steps {
        // SonarQube Analysis
        dockerCommand {
            name = "test"
            commandType = other {
                subCommand = "run"
                commandArgs = """
                    --rm 
                    -e SONAR_HOST_URL=%env.SONAR_HOST_URL%
                    -e SONAR_LOGIN=%env.SONAR_TOKEN%
                    -v "%teamcity.build.checkoutDir%:/usr/src" 
                    sonarsource/sonar-scanner-cli:latest 
                    -Dsonar.projectKey=%env.PROJECT_KEY%
                """.trimIndent()
            }
        }

        // Docker Login
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

        // Deploy to EC2 Instance
        sshExec {
            name = "Deploy to EC2 Instance"
            commands = """
                docker run -p 5000:80 -d --name myapp %env.DOCKER_IMAGE%
            """.trimIndent()
            targetUrl = "%env.HOST%"
            authMethod = uploadedKey {
                username = "%env.USER%"
                key = "id_rsa"
            }
        }
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})
