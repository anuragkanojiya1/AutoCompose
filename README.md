# 💌 AutoCompose — AI-Powered Multilingual Email Agent

AutoCompose is an intelligent email agent built for speed, multilingual fluency, and seamless user experience. Whether you're writing a formal email in Japanese or a casual one in Spanish, AutoCompose instantly crafts high-quality emails in multiple tones and languages with voice input, draft saving, and even one-tap Gmail integration.

---

## 🚀 Features

### 🧠 AI Email Generation
- Powered by **Gemini**, **Mistral**, **Llama**, and **LangGraph**.
- Supports multiple languages: **English, French, Spanish, Japanese**.
- Choose your desired **tone**: formal, friendly and professional.
- Advanced **LangGraph** workflows to optimize generation flows.

### 💬 Voice Input
- Speak instead of typing! Dictate your email content via **voice input**.
- Works smoothly across supported Android devices.

### ✉️ Draft System (Smart Drafts)
- Frequently sent emails are automatically saved as **smart drafts**.
- Stored locally via **Room Database** for **offline access** and reusability.
- Quickly edit and reuse previous emails without retyping them.

### 📤 Gmail Integration
- Directly **send generated emails via Gmail** without leaving the app.
- Uses Android **Intent system** for seamless one-tap dispatch.

### 🎯 Fast, Minimal UI
- Built using **Jetpack Compose** for a modern, clean, and fast experience.
- UI adapts to user context with language and tone selectors.
- Includes a **chat-like interface** for AI interactions.

---

## 📱Screenshots
<img src="https://github.com/user-attachments/assets/dafd666e-2f5b-43d6-9205-2b6927c219c1" alt="Screenshot_20250408_183732" width="250"/>
<img src="https://github.com/user-attachments/assets/a1263b49-0c22-427a-a953-41e1b9b0f12e" alt="Screenshot_20250408_183741" width="250"/>
<img src="https://github.com/user-attachments/assets/446c877e-57b4-4f85-b40a-51c7b86fd7a7" alt="Screenshot_20250408_183942" width="250"/>
<img src="https://github.com/user-attachments/assets/07af1e86-3961-4ce4-bec0-487748bb6de2" alt="Screenshot_20250408_183957" width="250"/>

## 🗄️Backend info
- [**Dev.to post**](https://dev.to/anuragkanojiya/how-to-use-langgraph-within-a-fastapi-backend-amm)

## 🚀Demo
- [**Youtube Link**](https://www.youtube.com/watch?v=JNPY4eGm26U&t=81s&ab_channel=AnuragKanojiya)

## 🧰 Tech Stack

### 📱 Android App
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM
- **Networking**: Retrofit and okhttp
- **Local Storage**: Room Database
- **Voice Input**: Android Speech-to-Text
- **AI Integration**: Gemini, Groq (Mistral, LLama)

### 🧪 AI/Backend
- **Backend Framework**: FastAPI
- **AI Pipeline**: LangGraph + Google Generative AI + Groq LLMS
- **Deployment**: [Railway.app](https://railway.app)
- **Environment Management**: Python-dotenv
- **API Schema**: Pydantic
- **Models Used**:
  - `gemini-1.5-pro`
  - `mistral-saba-24b`
  - `llama-3.1-8b-instant`

---

## 🏗 Architecture Overview
```
Frontend (Android - Kotlin)
 ├── MVVM Structure
 │   ├── ViewModel
 │   ├── Repository
 │   └── UI(Jetpack Compose)
 ├── RoomDB
 └── Speech services: Transcribes user's voice input

Backend (Python - FastAPI)
 ├── LangGraph for flow orchestration
 ├── Gemini + Groq models (Gemini, Mistral, Llama)
 └── Deployed via Railway
```

## 📦 Installation & Setup

1. Download the app directly from apk: [apk.zip](https://github.com/user-attachments/files/19651040/apk.zip)
2. Clone the Android project.
- Open in Android Studio.
- Run on emulator or device.

### 📧 Why AutoCompose?
✨ Say goodbye to writer’s block, especially in multilingual scenarios.
💡 Whether you're a student, entrepreneur, or professional — AutoCompose empowers you to communicate effectively across cultures and tones with AI at your fingertips.

## 🧑‍💻 Author
Anurag — Android app Developer | AI Enthusiast | Spring Boot

• [Twitter](https://x.com/AnuKanojiya829) • [LinkedIn](https://linkedin.com/in/anurag-kanojiya-101312286) • [GitHub](https://github.com/anuragkanojiya1)
