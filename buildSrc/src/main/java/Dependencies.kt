object Config {
    private const val retrofitVersion = "2.5.0"
    private const val okHttpVersion = "3.12.0"
    private const val androidGradleVersion = "1.3.0"
    private const val jgitVersion = "5.1.3.201810200350-r"
    private const val junitVersion = "4.12"

    object Versions {
    }

    object Deps {
        const val androidGradle = "com.android.tools.build:gradle:$androidGradleVersion"
        const val retrofit = "com.squareup.retrofit2:retrofit:$retrofitVersion"
        const val retrofitGson = "com.squareup.retrofit2:converter-gson:$retrofitVersion"
        const val okHttp = "com.squareup.okhttp3:okhttp:$okHttpVersion"
        const val okHttpLogging = "com.squareup.okhttp3:logging-interceptor:$okHttpVersion"
        const val jgit = "org.eclipse.jgit:org.eclipse.jgit:$jgitVersion"
    }

    object TestDeps {
        const val junit = "junit:junit:$junitVersion"
    }
}