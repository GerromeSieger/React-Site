import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.SSHUpload
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.buildSteps.sshExec
import jetbrains.buildServer.configs.kotlin.buildSteps.sshUpload
import jetbrains.buildServer.configs.kotlin.projectFeatures.buildReportTab
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

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
    description = "Contains all other projects"

    features {
        buildReportTab {
            id = "PROJECT_EXT_1"
            title = "Code Coverage"
            startPage = "coverage.zip!index.html"
        }
    }

    cleanup {
        baseRule {
            all(days = 365)
            history(days = 90)
            preventDependencyCleanup = false
        }
    }

    subProject(ReactSite)
}


object ReactSite : Project({
    name = "React Site"

    vcsRoot(ReactSite_HttpsGithubComGerromesiegerReactSiteRefsHeadsMain)

    buildType(ReactSite_Build)
})

object ReactSite_Build : BuildType({
    name = "React"

    params {
        password("env.HOST", "credentialsJSON:01d99e4c-8d5b-4a5f-9839-20bb51349186")
        password("env.PROJECT_KEY", "credentialsJSON:20ff450f-4846-47e6-8d9b-c47cee639d9d")
        password("env.SONAR_TOKEN", "credentialsJSON:1b043825-95ef-4c59-b7da-1cb9aa272e56")
        password("env.DOCKERHUB_PASSWORD", "credentialsJSON:9cd69afe-e02b-43ca-9717-9e73c7302b0d")
        password("env.DOCKER_IMAGE", "credentialsJSON:c8b36424-1973-45c3-84e5-58a31d380474")
        password("env.USER", "credentialsJSON:b322c631-af42-4ed2-a1a7-6dd7bb684134")
        password("env.DOCKERHUB_USERNAME", "credentialsJSON:407f140b-cb3b-4009-9d5f-cdd1275efafd")
        password("env.SONAR_HOST_URL", "credentialsJSON:9f2d9368-e5bc-4dcf-abf6-fb69a1e90532")
    }

    vcs {
        root(ReactSite_HttpsGithubComGerromesiegerReactSiteRefsHeadsMain)
    }

    steps {
        // SonarQube Analysis
        sonarQube {
            name = "Test"
            projectName = "%env.PROJECT_KEY%"
            projectKey = "%env.PROJECT_KEY%"
            serverUrl = "%env.SONAR_HOST_URL%"
            token = "%env.SONAR_TOKEN%"
        }

        // Docker Login
        script {
            name = "Login to DockerHub"
            scriptContent = "docker login -u %env.DOCKERHUB_USERNAME% -p %env.DOCKERHUB_PASSWORD%"
        }

        // Docker Build and Push
        dockerCommand {
            name = "Build"
            commandType = build {
                source = file {
                    path = "Dockerfile"
                }
            }
            commandArgs = "--pull --push -t %env.DOCKER_IMAGE%"
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

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})

object ReactSite_HttpsGithubComGerromesiegerReactSiteRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/gerromesieger/react-site#refs/heads/main"
    url = "https://github.com/gerromesieger/react-site"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "GerromeSieger"
        password = "credentialsJSON:990c2977-39d9-46b8-b6d4-7e0a1934d725"
    }
})
