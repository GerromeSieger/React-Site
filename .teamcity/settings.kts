import jetbrains.buildServer.configs.kotlin.*
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

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})
