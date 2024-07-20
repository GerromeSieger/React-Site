import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.triggers.vcs

version = "2024.03"

project {

    buildType(Build)

    params {
        password("env.USER", "credentialsJSON:12db73fa-8d7c-46b9-b2c6-eaa926b3eab5")
        password("env.HOST", "credentialsJSON:e960e252-27b0-42d3-9b47-4fe2bef751e9")
    }
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(DslContext.settingsRoot)
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

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})
