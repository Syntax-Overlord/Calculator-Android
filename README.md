# Calculator Android App

A simple calculator app built with **Kotlin** and **Jetpack Compose**.

It supports basic arithmetic, decimal input, scrolling display text, and a clean button-based UI.

## Features

- Basic operations: addition, subtraction, multiplication, and division
- Decimal input with `.`
- Large result support using floating-point math
- Scrollable display so long expressions do not overlap
- Material 3 Compose UI
- Light custom layout with separate screen and keypad areas

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX

## How to Run

### In Android Studio

1. Open the project folder.
2. Let Gradle sync finish.
3. Run the app on an emulator or a real Android device.

### With Gradle Wrapper

If you want to build from the terminal, use the Gradle wrapper in the project root:

```powershell
./gradlew assembleDebug
```

On Windows PowerShell, you can also use:

```powershell
.\gradlew.bat assembleDebug
```

## Notes

- The calculator uses `Double` math for parsing and evaluation.
- Floating-point math can have small precision differences for some values.
- If division by zero happens, the app shows `Zero Devision`.

## Project Structure

- `app/src/main/java/com/example/calculator/MainActivity.kt` — main UI and calculator logic
- `app/src/main/res/` — app resources and theme files

## Repository

GitHub: https://github.com/Syntax-Overlord/Calculator-Android

