import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.NodeJSBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
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
    name = "Build"

    params {
        password("env.HOST", "credentialsJSON:0271e1c7-8494-4e4d-a67c-17df4cf92c69")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        nodeJS {
            name = "build"
            id = "nodejs_runner"
            shellScript = """
                npm install
                npm run build
            """.trimIndent()
            dockerImage = "node:18-alpine"
            dockerImagePlatform = NodeJSBuildStep.ImagePlatform.Any
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
