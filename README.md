Custom Android Build Automation Plugin
===========================================


A Gradle plugin for build automation for android build system.


### Description

This plugin will pull the latest code from your git remote repository. Checkout to specified ```bot.config.branch```, Then
generate the APK and copy the generated APK to your specified directory in ```bot.config.destinationPath```.

### Configuration

```groovy
bot {
    // Git repo
    credentials {
        sshFilePath = SSH_FILE_PATH
        passphrase = SSH_PASSPHRASE
    }
    config {
        // Git branch to be checked out to before build generation
        branch = GIT_BRANCH
        
        // Destination file path where the app will be copied to             
        destinationPath = GENERATED_APP_BUILD_PATH
        
        // Build type eg. debug/release
        buildType = ANDROID_BUILD_TYPE
        
        // Android product flavour
        flavour =  ANDROID_PRODUCT_FLAVOUR // (Optional)
        
        // This prefix will be appended to app name before copying to destination directory
        filePrefix = GENERATED_APP_NAME_PREFIX // (Optional) 
        
        // Set git remote repo
        remote = REMOTE_REPO // Default is 'origin'
    }
}
```
