package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.SSHUpload
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.buildSteps.sshExec
import jetbrains.buildServer.configs.kotlin.buildSteps.sshUpload
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Build'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Build")) {
    expectSteps {
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
            targetUrl = "%env.HOST%:/root/"
            authMethod = uploadedKey {
                username = "%env.USER%"
                key = "id_rsa"
            }
        }
        sshExec {
            name = "deploy_run"
            id = "deploy_run"
            commands = "cp -r build/* /var/www/html && systemctl restart nginx"
            targetUrl = "%env.HOST%"
            authMethod = uploadedKey {
                username = "%env.USER%"
                key = "id_rsa"
            }
        }
    }
    steps {
        update<ScriptBuildStep>(1) {
            clearConditions()
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Any
        }
    }
}
