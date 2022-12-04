# Introduction

![A chat bubble with zeros and ones and a green background][100]

This repository contains **forked** source code for Google's TalkBack, which is a screen
reader for blind and visually-impaired users of Android. For usage instructions,
see
[TalkBack User Guide](https://support.google.com/accessibility/android/answer/6283677?hl=en).

### How to Build

To build TalkBack, run ./build.sh, which will produce an apk file.

### How to Install

Install the apk onto your Android device in the usual manner using adb.

### How to Run

With the apk now installed on the device, the TalkBack service should now be
present under Settings -> Accessibility, and will be off by default. To turn it
on, toggle the switch preference to the on position.

### How to use with ADB

```shell
# Activate
adb shell settings put secure enabled_accessibility_services com.android.talkback4d/com.developer.talkback.TalkBackDevService

# #Deactivate
adb shell settings put secure enabled_accessibility_services null

# General format
# All commands take the format of a broadcast
adb shell am broadcast  -a com.a11y.adb.[ACTION] [OPTIONS]

BROADCAST () { adb shell am broadcast "$@"; }

# Perform actions
BROADCAST -a com.a11y.adb.previous       # default granularity
BROADCAST -a com.a11y.adb.next           # default granularity

BROADCAST -a com.a11y.adb.previous -e mode headings # move tp previous heading
BROADCAST -a com.a11y.adb.next -e mode headings     # move to next heading

# Toggle settings
BROADCAST -a com.a11y.adb.toggle_speech_output # show special toasts for spoken text
BROADCAST -a com.a11y.adb.perform_click_action
BROADCAST -a com.a11y.adb.volume_toggle # special case that toggles between 5% and 50%
```

## All parameters
- [Action list][0]
- [Action parameter list in the SelectorController enum][1]
- [Developer settings][2]
- [Volume specific controls][3]

### Personal Notes

```jenv local 1.8``` <-- Need to do this if build fails
```./build.sh && adb -s $PIXEL7 install ./build/outputs/apk/phone/debug/talkback-phone-debug.apk```

My computer is using jenv to manage the JAVA_HOME etc so be ready with that

Java 11 is not a problem, just use it everywhere. Running `java -version` was insightful along with `jenv versions`

'com.android.talkback/com.google.android.marvin.talkback.TalkBackService', 'com.google.android.apps.accessibility.voiceaccess/com.google.android.apps.accessibility.voiceaccess.JustSpeakService:com.android.talkback/com.google.android.marvin.talkback.TalkBackService'

## TODO
- Organise gestures by action
- Add curtain
  - Activate via ADB
- Dev tools: Colour contrast check
- Dev tools: Touch target size check
- Dev tools: Developer-friendly details on curtain (add to announcements)

## FIXED
- Menus lacking dark mode / styling
- Back button in menus?

[0]: https://github.com/qbalsdon/talkback/blob/master/talkback/src/main/java/com/google/android/accessibility/talkback/adb/A11yAction.kt
[1]: https://github.com/qbalsdon/talkback/blob/master/talkback/src/main/java/com/google/android/accessibility/talkback/selector/SelectorController.java#L116
[2]: https://github.com/qbalsdon/talkback/blob/master/talkback/src/main/java/com/google/android/accessibility/talkback/adb/ToggleDeveloperSetting.kt
[3]: https://github.com/qbalsdon/talkback/blob/master/talkback/src/main/java/com/google/android/accessibility/talkback/adb/VolumeControl.kt

[100]: /images/icon_512.png "TalkBack for developers"
{: height="200px"}
