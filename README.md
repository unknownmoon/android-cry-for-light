# Cry for Light

> Note that this project is still in pre-stable stage, but it fits my personal demand now, and the main purpose of this little project is to try Android project.

[![GitHub version](https://badge.fury.io/gh/unknownmoon%2Fandroid-cry-for-light.svg)](https://badge.fury.io/gh/unknownmoon%2Fandroid-cry-for-light)

A humanitarian APP against sound activated light switches in some scenes.

<!-- MarkdownTOC -->

- [Intention](#intention)
- [Highlights](#highlights)
- [Downloads](#downloads)
- [Features \(Paused..\)](#features-paused)
    - [Feature Level - Core](#feature-level---core)
    - [Feature Level - Basic](#feature-level---basic)
    - [Feature Level - Enhancement](#feature-level---enhancement)
    - [Feature Level - Advanced](#feature-level---advanced)

<!-- /MarkdownTOC -->

<a name="intention"></a>
## Intention

In some scenarios, one may find that the sound activated lights are very annoying, since the small noise one is making cannot turn it on (may due to the frequencies of the sound), and one has to make __loud__ noise periodically to keep the lights on.

This app is then used to keep the lights on, by taking advantage of the sensors (mostly the light sensor) of one's Android phone. Once the app detects the luminance is lower than the configured threshold, it will scream, literally, with the configured sound.

<a name="highlights"></a>
## Highlights

__Warning:__ Only tested against Nexus 5X under Android 6.0.1 (API version 23), in __portrait__ mode.

___ALERT:___ The `settings` screen, namely the home screen, is not happy with the __landscape__ mode for the moment.

 - Use notification to `pause`/`resume`/`exit` the service, even the screen is locked. once it's started.
 - Functional after screen lock and clean the main screen from the recent task stack (the [Overview Screen](https://developer.android.com/images/components/recents.png)).
 - In the main screen, other than `start`/`exit` the service.
     + Display the current brightness in Luminance.
     + Set a brightness threshold to trigger the sound.
     + Set the maximum value of the brightness threshold to change the scale of the brightness threshold slider.
     + Set a sound level.
     + Choose a sound file.

<a name="downloads"></a>
## Downloads

> Too lazy to publish to Android market

Download APK file from [here](https://github.com/unknownmoon/android-cry-for-light/releases/download/v0.3.1/app-debug.apk) and install it.

<a name="features-paused"></a>
## Features (Paused..)

<a name="feature-level---core"></a>
### Feature Level - Core

 - [x] When the environment luminance is lower than a configurable threshold, a _trigger sound_ will be played till the environment luminance is back above the threshold.
 - [ ] When the environment luminance is lower than a configurable threshold, a _trigger sound_ will be played __continuously__ till the environment luminance is back above the threshold.

<a name="feature-level---basic"></a>
### Feature Level - Basic

 - [x] The core features are functional when the APP is active.
 - [x] Display the current luminance information for referencing to.
 - [x] The threshold can be configured easily configured by using slider.
 - [x] The _trigger sound_ can be selected from the system storage.

<a name="feature-level---enhancement"></a>
### Feature Level - Enhancement

 - [x] Features are functional when the APP is running either foreground or background.
 - [ ] `Trigger sound generator` can provide a range of sounds in different frequencies.
   - [ ] Controlled by sliders.
   - [ ] With a set of pre-set frequencies.

<a name="feature-level---advanced"></a>
### Feature Level - Advanced

 - [x] The APP is running as a background service, and displayed in the notification bar when running.
 - [x] The user can `pause`/`resume`/`stop` the service in the notification bar directly, while `stop` also stop the background service.
 - [ ] Automatically stare/stop service by sensing the user behaviours: pick up to stop, hand off to start (after 2 seconds delay perhaps).
