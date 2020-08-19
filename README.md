<div style="width:100%">
<div style="width:100%">
	<div style="width:50%; display:inline-block">
		<p align="center">
		<img style="text-align:center" width="180" height="180" alt="" src="https://raw.githubusercontent.com/cometchat-pro/ios-swift-chat-app/master/Screenshots/CometChat%20Logo.png">	
		</p>	
	</div>	
</div>
</br>
</br>
</div>

CometChat Pro enables you to add voice, video & text chat for your website & app.

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen)](#)


# Quick Start

This guide demonstrates how to add chat to an Android application using CometChat Pro. Before you begin, we strongly recommend you read the <a href="https://prodocs.cometchat.com/docs/concepts" target="_blank">Key Concepts</a> guide.

## Get your Application Keys

<a href="https://app.cometchat.io" target="_blank">Signup for CometChat</a> and then:

1. Create a new app: Enter a name & hit the **+** button
2. Head over to the **API Keys** section and note the **API Key** and **App ID** (for Auth Only key)

## Add the CometChat Dependency

### CometChat as a module
1. Download the latest aar file.
2. Navigate to your project in Android Studio.
3. Click on **File** and select **New** -> **Module**
4. Now select Import.JAR/AAR package from the available options.
5. Specify the path to the .aar file dowloaded earlier.
6. Now open the app level build.gradle file and under the dependencies section add the below line:
```groovy
	implementation project(path: ':pro-android-chat-sdk-$version')
```
where $version is the version of the aar downloaded.

Now that the CometChat module is successfully added to your project, you need to follow the below steps to add CometChat Pro dependency bundle to your project.
1. Open the project level build.gradle file and in the `repositories` section under `allprojects` add the below line:
``` groovy
allprojects {
repositories {
    maven {
      url "https://dl.bintray.com/cometchat/pro"
    }
  }
}
```
2. Now open the app level build.gradle file and add the below line under the `dependencies` section:
``` groovy
	implementation 'com.cometchat:cometchat-pro-android-dependencies:2.1.0'
```

### CometChat via gradle
If you do not wish to add the CometChat Pro dependency as a module, you can directly add the CometChat Pro SDK to your project using gradle. You can refer to the below link for instructions on how to do so:

https://prodocs.cometchat.com/docs/android-quick-start#section-add-the-cometchat-dependency

Please refer to our [Developer Documentation](https://prodocs.cometchat.com/docs/android-quick-start) for more information on how to setup the CometChat Pro SDK and implement various features using the same.
