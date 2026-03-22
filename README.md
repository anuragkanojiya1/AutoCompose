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
- 🔍 **Search bar available** to easily find your saved drafts by keyword or content.

### 📤 Gmail Integration
- Directly **send generated emails via Gmail** without leaving the app.
- Uses Android **Intent system** for seamless one-tap dispatch.

### 📊 Trends Dashboard (Powered by Supabase)
- Real-time tracking of:
  - Most **used AI models** (Gemini, Mistral, Llama, etc.)
  - **Global languages** selected by users
  - **Tone preferences** (formal, friendly, professional)
- Built using **Supabase Realtime** + custom analytics endpoint.
- Accessible in a dedicated **dashboard tab** within the app to show what’s trending globally.

### 🎯 Fast, Minimal UI
- Built using **Jetpack Compose** for a modern, clean, and fast experience.
- UI adapts to user context with language and tone selectors.
- Includes a **chat-like interface** for AI interactions.
- 🌗 **Supports both Light and Dark Mode** for a comfortable viewing experience.

---

## 📱Screenshots

<img src="https://github.com/user-attachments/assets/ceb4d201-0187-4eec-b7ab-6f518791b900" alt="Screenshot_20260104_000335" width="250"/>
<img width="250" alt="Screenshot_20260104_002019" src="https://github.com/user-attachments/assets/39aed387-f5d4-48df-bc94-ad44a28733e5" />
<img width="250" alt="Screenshot_20260104_002011" src="https://github.com/user-attachments/assets/ab5bdd1c-305c-4062-9086-ca7bd5238141" />
<img width="250" alt="Screenshot_20260104_002133" src="https://github.com/user-attachments/assets/98c13c82-63cf-4713-8e3b-f3807c1c18da" />
<img src="https://github.com/user-attachments/assets/6c53e71d-f04c-4e7f-83bc-0cf8633c916d" alt="Screenshot_20260104_000448" width="250"/>
<img src="https://github.com/user-attachments/assets/caacec99-7ccd-4d1b-b3b7-cedcfa0d1724" alt="Screenshot_20260104_001027" width="250"/>
<img width="250" alt="Screenshot_20260104_000502" src="https://github.com/user-attachments/assets/ebadaff9-bfe4-4b15-b58d-123d8ca6e59e" />

## 🗄️Backend info
- [**Dev.to post**](https://dev.to/anuragkanojiya/how-to-use-langgraph-within-a-fastapi-backend-amm)

## 🚀Demo
- [**Youtube Link**](https://youtu.be/JNPY4eGm26U)

## 🧰 Tech Stack

### 📱 Android App
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM
- **Networking**: Retrofit and okhttp
- **Local Storage**: Room Database
- **Voice Input**: Android Speech-to-Text
- **AI Integration**: Gemini, Groq (Mistral, LLama)
- **Realtime Analytics**: Supabase Realtime + Dashboard UI
- **Dark Mode Support**: Jetpack Compose `MaterialTheme` adaptive theming
- **Search Functionality**: Draft filtering via search bar

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
 ├── Supabase DB integration for trend tracking
 └── Deployed via Railway
```

## 🌍 Localization (language + region)

The app uses Android **locale qualifiers** so resources can target both **language** and **region** (see [Providing alternative resources](https://developer.android.com/guide/topics/resources/providing-resources#LocaleQualifier)).

### Folder layout (examples)

| Folder | Role |
|--------|------|
| `res/values/` | Default fallback; OAuth client IDs and strings that are identical everywhere |
| `res/values-en/` | English strings shared by all English regions not overridden below |
| `res/values-en-rGB/` | English (United Kingdom) overrides |
| `res/values-en-rUS/` | English (United States) overrides |
| `res/values-fr-rFR/` | French (France) overrides |
| `res/values-es-rES/` | Spanish (Spain) overrides |

### Resolution order

Android picks the **best match**, then falls back. For example, for **en-GB**: `values-en-rGB` → `values-en` → `values`. If a string is missing in a folder, it is inherited from the next fallback. **Keep secrets (e.g. OAuth IDs) only in `values/`** unless you truly need a regional variant.

### Runtime

- `Context.getString()` / `stringResource()` use the same merged locale as the rest of the app.
- Speech prompts use `voice_input_prompt` so UK/US/FR/ES users see region-appropriate copy where defined.
- `LocaleConfiguration.currentLocaleTag()` reflects the effective locale used for configuration (log tag `MainActivity` on startup).

---

## 📦 Installation & Setup

 Clone the Android project.
- Open in Android Studio.
- Run on emulator or device.

### 📧 Why AutoCompose?
✨ Say goodbye to writer’s block, especially in multilingual scenarios.
💡 Whether you're a student, entrepreneur, or professional — AutoCompose empowers you to communicate effectively across cultures and tones with AI at your fingertips.

## 🧑‍💻 Author
Anurag — Android app Developer | AI Enthusiast | Spring Boot

• [Twitter](https://x.com/AnuKanojiya829) • [LinkedIn](https://linkedin.com/in/anurag-kanojiya-101312286) • [GitHub](https://github.com/anuragkanojiya1)
