# CameraX Face Detection App

A modern Android camera application built with Jetpack Compose and CameraX, featuring real-time face
detection, camera switching (front/back), a blink effect on photo capture, and saving images to the
Media Store with a wide range of  aspect ratio.

## Features

- **Real-Time Face Detection**: Uses ML Kit to detect faces and provides visual feedback (red
  border and text) when a face is centered in the frame. [There are some minor alignment issues]
- **Camera Switching**: Toggle between front and back cameras with a single button.
- **Blink Effect**: Displays a brief white overlay when capturing a photo for visual feedback.
- **Image Capture**: Saves photos to the Media Store (`Pictures/CameraApp`) with a 3:2, 4:3, 16:9
  aspect ratio.
- **Permissions Handling**: Requests camera and storage permissions dynamically.

## Requirements

- Android Studio (latest stable version recommended)
- Minimum SDK: 24 (Android 7.0)
- A physical Android device with front and back cameras (emulators may not support face detection
  reliably)

## Setup Instructions

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   ```

2. **Open in Android Studio**:
    - Open the project in Android Studio.
    - Sync the project with Gradle to download dependencies.

3. **Add Dependencies**:
   Ensure the following are in your `app/build.gradle`:
   ```gradle
   dependencies {
       implementation "androidx.camera:camera-core:1.5.0-beta01"
       implementation "androidx.camera:camera-camera2:1.5.0-beta01"
       implementation "androidx.camera:camera-lifecycle:1.5.0-beta01"
       implementation "androidx.camera:camera-view:1.5.0-beta01"
       implementation "androidx.camera:camera-mlkit:1.5.0-beta01"
       implementation "com.google.mlkit:face-detection:16.1.3"
       implementation "androidx.compose.ui:ui:1.7.0"
       implementation "androidx.compose.material3:material3:1.1.0"
       implementation "androidx.activity:activity-compose:1.9.2"
       implementation "io.coil-kt:coil-compose:2.7.0"
   }
   ```

4. **Update AndroidManifest.xml**:
   Add the necessary permissions:
   ```xml
   <uses-permission android:name="android.permission.CAMERA" />
   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="30" />
   ```

5. **Run the App**:
    - Connect a physical Android device.
    - Build and run the app from Android Studio.
    - Grant camera and storage permissions when prompted.

## Usage

1. **Open the App**:
    - The camera preview starts with the back camera by default.
    - A text instruction ("Center your face in the frame") appears at the bottom.

2. **Capture a Photo**:
    - Press the "Capture" button to take a photo.
    - A brief white blink effect appears for 150ms.
    - The photo is saved to `Pictures/MyApp` in the Media Store with a 3:2 aspect ratio.

3. **Switch Cameras**:
    - Press the "Switch Camera" button to toggle between front and back cameras.

4. **Toggle Between HighQuality Photos**
    - Pressing **HQ** button can toggle between high and low resolution photos.

5. **Switch Ratio**
    - Pressing the ratio button expands to wide range of ratio to choose from.
        - currently **3:2** , **4:3** & **16:9** is available

## Debugging

- **Face Detection Issues**:
    - Check Logcat for `"FaceDetection"` logs to verify detection attempts.
    - Ensure good lighting and a clear view of the face (1–2 feet from the camera).
    - Use a physical device, as emulators may not support face detection

## Project Structure

- **MainActivity.kt**: Contains the main Composable (`CameraScreen`) with camera setup, face
  detection, and UI logic.
- **ImageSaver.kt**: Handles photo capture and saving to the Media Store and Local File Under -**Pictures/CameraApp**.
- **theme**: Defines the app’s Compose theme.
