import jetbrains.buildServer.configs.kotlin.*
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

    artifactRules = "build/**"
    publishArtifacts = PublishMode.SUCCESSFUL

    vcs {
        root(ReactSite_HttpsGithubComGerromesiegerReactSiteRefsHeadsMain)
    }

    steps {
        nodeJS {
            name = "build"
            id = "nodejs_runner"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            shellScript = """
                npm install
                npm run build
            """.trimIndent()
        }
        script {
            name = "test"
            id = "simpleRunner"
            scriptContent = "./test.sh"
            dockerImage = "ubuntu:22.04"
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerPull = true
        }
        sshUpload {
            name = "deploy_copy"
            id = "deploy_copy"
            transportProtocol = SSHUpload.TransportProtocol.SCP
            sourcePath = "build => ."
            targetUrl = "45.33.17.134:/root/"
            authMethod = uploadedKey {
                username = "root"
                key = "id_rsa"
            }
        }
        sshExec {
            name = "deploy_run"
            id = "deploy_run"
            commands = "cp -r build/* /var/www/html && systemctl restart nginx"
            targetUrl = "45.33.17.134"
            authMethod = uploadedKey {
                username = "root"
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
