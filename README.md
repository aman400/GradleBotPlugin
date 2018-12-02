Custom Android Build Automation Plugin
===========================================


A Gradle plugin for build automation for android build system.


### Description

This plugin will pull the latest code from your git remote repository. Checkout to specified ```bot.config.branch```, Then
generate the APK and copy the generated APK to your specified directory in ```bot.config.destinationPath```.


### Installation

For Gradle version 2.1 and later:
```groovy
plugins {
  id "com.github.aman400.gradlebot" version "0.1-beta04"
}
```

For Gradle version lower than 2.1:
```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.com.github.aman400.gradlebot:gradlebot:0.1-beta04"
  }
}

apply plugin: "com.github.aman400.gradlebot"
```


### Configuration

```groovy
    bot {
        config {
            // Destination file path where the app will be copied to             
            destinationPath = GENERATED_APP_BUILD_PATH // (Optional)
            
            // Build type eg. debug/release
            buildType = ANDROID_BUILD_TYPE // (Optional)
            
            // Android product flavour
            flavour =  ANDROID_PRODUCT_FLAVOUR // (Optional)
           
            // This prefix will be appended to app name before copying to destination directory 
            filePrefix = GENERATED_APP_NAME_PREFIX // (Optional) 
                       
            git {
                // for fetching latest data from git repo
                // (Optional)
                credentials {
                    sshFilePath = SSH_FILE_PATH
                    passphrase = SSH_PASS
                    username = USERNAME
                    password = PASSWORD
                }
                
                // Git branch to be checked out to before build generation
                branch = GIT_BRANCH
                
                // Set git remote repo
                remote = REMOTE_REPO // (Optional) Default is 'origin'
            }
        }
    }
```

`bot.config.git.credentials` block is to specify the authentication for github repo. Either you can specify username and password authentication or you can use SSH(specify `bot.config.git.credentials.passphrase` if any).

`bot.config.git.branch` config is to specify the branch to checkout to.

`bot.config.destinationPath` config is to specify the destination path to copy generate APK to.

`bot.config.buildType` config is to specify any android build you want to build.

`bot.config.flavour` config is to specify android build flavour you want to build.

`bot.config.filePrefix` config is to prepend prefix to generated APK.

`bot.config.git.remote` config is to set the git remote. Default is `origin`.

## Tasks and usage

**pullCode** Task fetches the latest code from the github Repo and checkout to the specified `bot.config.git.branch`.
`bot.config.git.credentials` are mandatory for fetching the latest code from branch.
```bash
./gradlew pullCode
```

**cleanOutput** Task cleans up android `build/outputs` directory for root android project and sub-projects.
```bash
./gradlew cleanOutput
```

**fetchRemoteBranches** Task fetches and print all available remote branches.
```bash
./gradlew fetchRemoteBranches
```

**assembleWithArgs** Task depends upon `cleanOutput` and `assemble` tasks. It generates APK according to specified config and move generated APK to given destination.
```bash
./gradlew assembleWithArgs
```