pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "Github Repo"
include(":app")
include(":shared-test")

include(":core:common")
include(":core:designsystem")
include(":core:auth")
include(":core:domain")
include(":core:data")
include(":core:local")
include(":core:network")
include(":core:navigation")

include(":feature:login")
include(":feature:profile")
include(":feature:main")
include(":feature:detail")
include(":feature:home")
include(":feature:bottom")
