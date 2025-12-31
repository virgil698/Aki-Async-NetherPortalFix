rootProject.name = "NetherPortalFix"
pluginManagement {
    repositories {
        maven("https://repo.leavesmc.org/releases") {
            name = "leavesmc-releases"
        }
        maven("https://repo.leavesmc.org/snapshots") {
            name = "leavesmc-snapshots"
        }
        gradlePluginPortal()
    }
}