import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.NodeJSBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.SSHUpload
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.buildSteps.sshExec
import jetbrains.buildServer.configs.kotlin.buildSteps.sshUpload
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

    features {
        feature {
            id = "PROJECT_EXT_4"
            type = "sonar-qube"
            param("useToken", "true")
            param("name", "SonarServer")
            param("id", "9efd98dd-ab58-4702-a30a-f19a35036558")
            param("url", "http://172.233.131.14:9000/")
            param("token", "scrambled:c3FhXzQzMzY1NzBiMzZmZjk5NWM0MDgzMmNmYmU4NTk1NzFjMDUxZGZjODQ=")
        }
    }
}

object Build : BuildType({
    name = "Build"

    params {
        password("env.USER", "credentialsJSON:998ca956-78f1-4610-895d-0493026d2628")
        password("env.HOST", "credentialsJSON:0271e1c7-8494-4e4d-a67c-17df4cf92c69")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        step {
            name = "test-scan"
            id = "test_scan"
            type = "sonar-plugin"
            param("sonarServer", "9efd98dd-ab58-4702-a30a-f19a35036558")
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
