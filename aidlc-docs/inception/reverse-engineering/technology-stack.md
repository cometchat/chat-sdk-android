# Technology Stack

## Programming Languages
| Language | Version | Usage |
|----------|---------|-------|
| Java | 8+ (source compatibility) | 100% of SDK source code |

## Frameworks
| Framework | Version | Purpose |
|-----------|---------|---------|
| Android SDK | API 21-30 (min 21, compile/target 30) | Android platform |
| AndroidX Lifecycle | 1.1.1 | App lifecycle awareness (foreground/background detection) |

## Libraries
| Library | Version | Purpose |
|---------|---------|---------|
| OkHttp | 3.12.+ | HTTP client for REST API and WebSocket connections |
| CometChat Calls SDK | 4.0.0 (compileOnly) | Optional voice/video calling integration |

## Build Tools
| Tool | Version | Purpose |
|------|---------|---------|
| Gradle | 7.x (wrapper) | Build automation |
| Android Gradle Plugin | 7.4.2 | Android library build |
| Maven Publish Plugin | (bundled) | AAR artifact publishing |
| ProGuard | (bundled with AGP) | Code obfuscation for distributable variant |

## Testing Tools
| Tool | Version | Purpose |
|------|---------|---------|
| JUnit 4 | 4.13 | Unit testing (Android instrumentation) |
| JUnit 5 (Jupiter) | 5.10.2 | Unit testing (local JVM) |
| jqwik | 1.9.1 | Property-based testing |
| Espresso | 3.2.0 | Android UI testing |
| Robolectric | 4.3.1 (annotations) | Android unit testing without device |
| JSONAssert | 1.5.0 | JSON comparison in tests |

## Infrastructure
| Service | Purpose |
|---------|---------|
| CometChat REST API | Backend for all CRUD operations |
| CometChat WebSocket | Real-time event delivery |
| Cloudsmith Maven | AAR artifact distribution |
| GitHub Actions | CI/CD (instrumentation tests, DevSkim security scanning) |

## Android Configuration
| Setting | Value |
|---------|-------|
| Compile SDK | 30 (Android 11) |
| Min SDK | 21 (Android 5.0 Lollipop) |
| Target SDK | 30 (Android 11) |
| NDK ABI Filters | armeabi-v7a, arm64-v8a, x86, x86_64 |
| Namespace | com.cometchat.chat |
| Output | chat-sdk-android.aar |

## Publishing
| Target | URL |
|--------|-----|
| Local Maven | `$projectDir/distribution` |
| Cloudsmith | `https://api-g.cloudsmith.io/maven/cometchat/call-team` |
| Group ID | com.cometchat |
| Artifact ID | chat-sdk-android |
| Version | 5.0.0 |
