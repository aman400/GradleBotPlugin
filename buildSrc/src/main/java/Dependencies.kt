object Config {
    object Versions {
        const val androidGradleVersion = "3.6.0-alpha10"
        const val jgitVersion = "5.4.3.201909031940-r"
        const val junitVersion = "4.12"
    }

    object Deps {
        const val androidGradle = "com.android.tools.build:gradle:${Versions.androidGradleVersion}"
        const val jgit = "org.eclipse.jgit:org.eclipse.jgit:${Versions.jgitVersion}"
    }

    object TestDeps {
        const val junit = "junit:junit:${Versions.junitVersion}"
    }
}