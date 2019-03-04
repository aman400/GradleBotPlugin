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
  id "com.github.aman400.gradlebot" version "0.2"
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
    classpath "gradle.plugin.com.github.aman400.gradlebot:gradlebot:0.2"
  }
}

apply plugin: "com.github.aman400.gradlebot"
```


### Configuration

Add `gradlebot.properties` file in the root folder of your project with following configuration:

```properties
#Set credentials for fetching latest data from git repo
git.ssh.path=YOUR_SSH_FILE_PATH
git.ssh.passphrase=YOUR_SSH_PASSPHRASE
git.username=GIT_USERNAME
git.password=GIT_PASSWORD

#Default branch
git.branch.default=YOUR_DEFAULT_BRANCH

#(Optional) Default is 'origin'
git.remote=REPO_ORIGIN
```

`git.ssh.path` is the path for ssh file.

`git.ssh.passphrase` is the passphrase for ssh.

`git.username` is the git remote repository username.

`git.password` is the git remote repository password.

`git.branch.default` is the default branch that is checked out when performing `resetBranch` task.

`git.remote` is to set the git remote. Default is `origin`.


Either set `git.ssh.path` and `git.ssh.passphrase` when using ssh as transfer protocol
or set `git.username` and `git.password` when using https as transfer protocol


***In your `build.gradle` file add following config:***

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
            // Git branch to be checked out to before build generation
            branch = GIT_BRANCH
        }
    }
}
```

`bot.config.git.branch` config is to specify the branch to checkout to.

`bot.config.destinationPath` config is to specify the destination path to copy generate APK to.

`bot.config.buildType` config is to specify any android build you want to build.

`bot.config.flavour` config is to specify android build flavour you want to build.

`bot.config.filePrefix` config is to prepend prefix to generated APK.

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

**resetBranch** Task resets the current branch to `git.branch.default` and deletes the current local branch
```bash
./gradlew resetBranch
```


**When working with Github:**

Github Settings allow to create OAuth token. These can also be used to authenticate HTTPS connections. Use OAuth token as username and "" as password. 

**When working with Gitlab:**

GitLab Profile Settings allow to create Personal Access Tokens. These can also be used to authenticate HTTPS connections. 
Access tokens can have an expiration date or can be revoked manually at any time.