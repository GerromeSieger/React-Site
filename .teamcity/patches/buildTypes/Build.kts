package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.BuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.DockerCommandStep
import jetbrains.buildServer.configs.kotlin.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Build'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Build")) {
    params {
        add {
            password("env.DOCKERHUB_PASSWORD", "credentialsJSON:430d66d3-15bd-4188-925d-7370ca43bc32")
        }
        add {
            password("env.DOCKER_IMAGE", "credentialsJSON:8fa3c955-aa1a-4966-a273-3f82d8495a8c")
        }
        add {
            password("env.DOCKERHUB_USERNAME", "credentialsJSON:cdfa9bbb-47fa-4738-bae7-13d4a650c3f4")
        }
    }

    expectSteps {
        step {
            name = "test-scan"
            id = "test_scan"
            type = "sonar-plugin"
            param("sonarServer", "9efd98dd-ab58-4702-a30a-f19a35036558")
        }
    }
    steps {
        update<BuildStep>(0) {
            clearConditions()
            param("sonarProjectSources", "")
            param("teamcity.build.workingDir", "./")
            param("sonarProjectName", "React")
            param("teamcity.tool.sonarquberunner", "%teamcity.tool.sonar-qube-scanner.4.2.0.1873-scanner%")
            param("sonarProjectVersion", "")
            param("sonarProjectKey", "React")
        }
        insert(1) {
            dockerCommand {
                name = "docker_login"
                id = "docker_login"
                commandType = other {
                    subCommand = "login"
                    commandArgs = "-u %env.DOCKERHUB_USERNAME% -p %env.DOCKERHUB_PASSWORD%"
                }
            }
        }
        insert(2) {
            dockerCommand {
                name = "build"
                id = "build"
                commandType = build {
                    source = file {
                        path = "."
                    }
                    platform = DockerCommandStep.ImagePlatform.Linux
                    namesAndTags = "%env.DOCKER_IMAGE%"
                    commandArgs = "--pull"
                }
            }
        }
    }
}
