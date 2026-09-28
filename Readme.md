# TaskFlow – To-Do List App

## About the Project

TaskFlow is a simple and user-friendly Android To-Do List application developed using Kotlin and Jetpack Compose. It helps users organize their daily tasks, manage priorities, track deadlines, and monitor their progress through a clean and intuitive interface.

## Features

- *Add Tasks:* Create tasks with a title, description, priority, and due date.
- *Edit Tasks:* Update task details whenever needed.
- *Delete Tasks:* Remove tasks that are no longer required.
- *Task Completion:* Mark tasks as completed or pending.
- *Progress Tracking:* View the number and percentage of completed tasks.
- *Task Filtering:* Filter tasks by All, Pending, and Completed.
- *Priority Management:* Assign Low, Medium, or High priority to tasks.
- *Local Data Storage:* Save tasks locally so they remain available after closing and reopening the app.

## Technologies Used

- *Programming Language:* Kotlin
- *User Interface:* Jetpack Compose
- *UI Components:* Material Design 3
- *Development Environment:* Android Studio
- *Local Storage:* SharedPreferences
- *Platform:* Android

## Project Structure

```text
ToDoListApp/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/todolistapp/
│           │       └── MainActivity.kt
│           └── AndroidManifest.xml
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md