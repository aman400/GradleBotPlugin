object Config {


    object Versions {
        const val retrofitVersion = "2.5.0"
        const val okHttpVersion = "3.12.1"
        const val androidGradleVersion = "3.3.1"
        const val jgitVersion = "5.2.0.201812061821-r"
        const val junitVersion = "4.12"
    }

    object Deps {
        const val androidGradle = "com.android.tools.build:gradle:${Versions.androidGradleVersion}"
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofitVersion}"
        const val retrofitGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofitVersion}"
        const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.okHttpVersion}"
        const val okHttpLogging = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttpVersion}"
        const val jgit = "org.eclipse.jgit:org.eclipse.jgit:${Versions.jgitVersion}"
    }

    object TestDeps {
        const val junit = "junit:junit:${Versions.junitVersion}"
    }
}