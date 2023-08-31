<div style="width:100%">
<div style="width:100%">
	<div style="width:50%; display:inline-block">
		<p align="center">
		<img style="text-align:center" width="180" height="180" alt="" src="https://avatars2.githubusercontent.com/u/45484907?s=200&v=4">	
		</p>	
	</div>	
</div>
</br>
</br>
</div>

# CometChat Android Chat SDK

CometChat enables you to add voice, video & text chat for your website & app.
This guide demonstrates how to add chat to an Android application using CometChat.


[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen)](#)
<a href=" "> <img src="https://img.shields.io/badge/Version-4.0.0_beta1-important" /></a>
![GitHub repo size](https://img.shields.io/github/repo-size/cometchat-pro/android-chat-sdk)
![GitHub contributors](https://img.shields.io/github/contributors/cometchat-pro/android-chat-sdk)
![GitHub stars](https://img.shields.io/github/stars/cometchat-pro/android-chat-sdk?style=social)
![Twitter Follow](https://img.shields.io/twitter/follow/cometchat?style=social)
<hr/>


## Prerequisites :star:
Before you begin, ensure you have met the following requirements:<br/>
 ✅ &nbsp; You have `Android Studio` installed in your machine.<br/>
 ✅ &nbsp; You have a `Android Device or Emulator` with Android Version 6.0 or above.<br/>
 ✅ &nbsp; You have read [CometChat Key Concepts](https://prodocs.cometchat.com/docs/concepts).<br/>

<hr/>

## Installing CometChat Android SDK
## Setup :wrench:

To setup Android SDK, you  need to first register on CometChat Dashboard. [Click here to sign up](https://app.cometchat.com/login).

### i. Get your Application Keys :key:

<a href="https://app.cometchat.io" target="_blank">Signup for CometChat</a> and then:

1. Create a new app: Click **Add App** option available  →  Enter App Name & other information  → Create App
2. At the Top in **QuickStart** section you will find **Auth Key** & **App ID** or else you can head over to the **API & Auth Keys** section and note the **Auth Key** and **App ID**
<img align="center" src="https://files.readme.io/4b771c5-qs_copy.jpg"/>

<hr/>

### ii. Add the CometChat Dependency
<ul>
<li>
<b>CometChat as a module</b><br/>
1. Download the latest aar file from .<br/>
2. Navigate to your project in Android Studio.<br/>
3. Click on **File** and select **New** -> **Module**<br/>
4. Now select Import.JAR/AAR package from the available options.<br/>
5. Specify the path to the .aar file dowloaded earlier.<br/>
6. Now open the app level build.gradle file and under the dependencies section add the below line:<br/>

```groovy
	implementation project(path: ':chat-sdk-android-$version')
```
where $version is the version of the aar downloaded.

Now that the CometChat module is successfully added to your project, you need to follow the below steps to add CometChat dependency bundle to your project.

1. Open the project level build.gradle file and in the `repositories` section under `allprojects` add the below line:

``` groovy
allprojects {
repositories {
    maven {
      url "https://dl.cloudsmith.io/public/cometchat/cometchat/maven/"
    }
  }
}
```
2. Now open the app level build.gradle file and add the below line under the `dependencies` section:

``` groovy
dependencies {
  implementation 'com.cometchat:chat-sdk-android-dependencies:4.0.0'
}
```
</li><li>
<b>CometChat via gradle</b>
If you do not wish to add the CometChat dependency as a module, you can directly add the CometChat SDK to your project using gradle.
Open the app level build.gradle file and <br/>

1. Add the below line in the dependencies section.

```groovy
dependencies {
  implementation 'com.cometchat:chat-sdk-android:4.0.0'
}
```
2. Add the below lines android section

```groovy
android {
  compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }
}
```
## Please Note:
**v2.4+ onwards, Voice & Video Calling functionality has been moved to a separate library. Please add the following dependency to your app level `build.gradle` file in case you plan on using the Voice & Video Calling feature.**

```groovy
dependencies {
  implementation 'com.cometchat:calls-sdk-android:{calling_module_latest_version}'
}
```
To know the latest version of the Calling dependency, please check the [Calling documentation](https://prodocs.cometchat.com/docs/android-calling)

 You can refer to the below link for instructions on how to do so:<br/>
[📝 Add CometChat Dependency](https://prodocs.cometchat.com/docs/android-quick-start#section-add-the-cometchat-dependency)
</li>
</ul>
<hr/>

## Configure CometChat SDK

### i. Initialize CometChat 🌟
The init() method initializes the settings required for CometChat. We suggest calling the init() method on app startup, preferably in the onCreate() method of the Application class.

```java
String appID = "APP_ID"; // Replace with your App ID
String region = "REGION"; // Replace with your App Region ("eu" or "us")

AppSettings appSettings=new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(region).build();

CometChat.init(this, appID,appSettings, new CometChat.CallbackListener<String>() {
  @Override
  public void onSuccess(String successMessage) {
    Log.d(TAG, "Initialization completed successfully");
  }
  @Override
  public void onError(CometChatException e) {
    Log.d(TAG, "Initialization failed with exception: " + e.getMessage());
  }
});
```

| :information_source: &nbsp; <b> Note - Make sure to replace `region` and `appID` with your credentials.</b> |
|------------------------------------------------------------------------------------------------------------|

### ii. Create User 👤
Once initialisation is successful, you will need to create a user. You need to user createUser() method to create user on the fly.
```java
String authKey = "AUTH_KEY"; // Replace with your App Auth Key
User user = new User();
user.setUid("user1"); // Replace with the UID for the user to be created
user.setName("Kevin"); // Replace with the name of the user

CometChat.createUser(user, authKey, new CometChat.CallbackListener<User>() {
  @Override
    public void onSuccess(User user) {
    Log.d("createUser", user.toString());
  }

  @Override
    public void onError(CometChatException e) {
    Log.e("createUser", e.getMessage());
  }
});
```

| :information_source: &nbsp; <b>Note -  Make sure that UID and name are specified as these are mandatory fields to create a user.</b> |
|------------------------------------------------------------------------------------------------------------|

<hr/>

### iii. Login User 👤
Once you have created the user successfully, you will need to log the user into CometChat using the login() method.
```java
String UID = "user1"; // Replace with the UID of the user to login
String authKey = "AUTH_KEY"; // Replace with your App Auth Key

 if (CometChat.getLoggedInUser() == null) {
     CometChat.login(UID, authKey, new CometChat.CallbackListener<User>() {

      @Override
      public void onSuccess(User user) {
        Log.d(TAG, "Login Successful : " + user.toString());
  }

   @Override
    public void onError(CometChatException e) {
        Log.d(TAG, "Login failed with exception: " + e.getMessage());
   }
 });
 } else {
   // User already logged in
 }
```

| :information_source: &nbsp; <b>Note - The login() method needs to be called only once. Also replace AUTH_KEY with your App Auth Key.</b> |
|------------------------------------------------------------------------------------------------------------|

<hr/>

📝 Please refer to our [Developer Documentation](https://prodocs.cometchat.com/docs/android-quick-start) for more information on how to configure the CometChat SDK and implement various features using the same.

<hr/>


## Contributors :clap:
Thanks to the following people who have contributed to this project:
[👨‍💻 @adityagokula2210](https://github.com/adityagokula2210) 

<hr/>

## Contact :mailbox:
Contact us via real time support present in [CometChat Dashboard.](https://app.cometchat.io/)
<hr/>

## License

This project uses the following license: [License.md](LICENSE).
