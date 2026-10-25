# Green Campus Garden — SE SPPU CEP

Beginner-friendly native Android app written in Java. Features: dashboard, plant registration, plant list, plant details, plant ID lookup, watering record, local persistence.

## Open in Android Studio
1. Extract the ZIP and choose **Open** on the `GreenCampusGarden` folder.
2. Let Android Studio sync Gradle (JDK 17, Android SDK 35).
3. Run on an Android emulator or connected phone.
4. Use **Build > Build APK(s)** to create a debug APK.

## Build on GitHub without Android Studio
1. Create a new GitHub repository.
2. Upload the **contents** of this folder, including `.github/workflows/build-apk.yml` (GitHub web uploads may hide dotfolders; use Git or ensure the workflow file is uploaded).
3. Open **Actions > Build Android APK > Run workflow** (or push to `main`).
4. After the workflow succeeds, download the `GreenCampusGarden-debug-apk` artifact from that workflow run.

## Important limitations
- Plant data stays on one device via Android SharedPreferences. No Firebase or multi-user login in this version.
- QR **ID lookup** is implemented; an in-app camera QR scanner and QR code generator are NOT implemented. Create printed QR codes containing IDs like `GCG-001` using an external QR generator and read with any QR reader, then type the ID in the app.
- Watering reminders are not scheduled notifications; watering can be recorded manually.
- Debug APK is for testing, not Play Store publishing.
