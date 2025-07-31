( Update in progress )

# AI Job Matcher 🚀

A full-stack Spring Boot + React web app that scrapes jobs from JobsDB, uses an AI model to evaluate their relevance to your profile. Currently it could send an email to yourself letting you know which job is suitable for you.

# 🧠 What It Does

This project automates the job search process with a bit of intelligence:
- 🔍 Scrapes job listings from JobsDB using Selenium WebDriver.
- 🧩 Matches jobs to your background using a local LLM model (Ollama).
- 🧠 Evaluates each job’s fit with a custom prompt and returns a verdict.
- ✅ Stores job details and AI decisions in a PostgreSQL database.
- 🌐 Displays the results through a React frontend with filters and status flags.

# 🛠️ Tech Stack

## Backend ( Refactoring / Improvement in progress )
- **Spring Boot** (Java)
- **Selenium WebDriver** – For scraping dynamic content
- **PostgreSQL** – Persistent storage
- **Ollama** – Local LLM for job relevance evaluation
- **REST API** – For frontend integration

## Frontend ( Work in progress )
- **React**
- Basic table UI with optional filters (e.g., Full-time / Part-time)

## DevOps / Infra (Planned)
- Dockerizing backend
- React frontend served separately
- Kubernetes deployment (future goal)

# ⚙️ Features

- AI-based matching verdicts (markdown formatted)
- Shortlist toggle using boolean flag
- Filtering by job type (Full-time / Part-time)
- Easy navigation and access to original job URLs

## 🤖 AI Model Setup
This project uses Ollama to run a local LLM (like LLaMA2 or Mistral) that processes each job description.

Example prompt:

"Given the following job description and the candidate's profile, explain whether the job is suitable, and why."

🧪 Sample Verdict Output
✅ This job is a strong fit. The candidate has 10+ years of Java backend experience and is currently studying AWS and React, which aligns with the job's cloud-native architecture...

🧠 Future Improvements
Save shortlisted jobs

Full pagination scraping

Deploy via Docker + Kubernetes

Switch to OpenAI API or HuggingFace for more accurate LLM evaluation


# 📦 How to Run

## Requirement
- Docker Desktop
- Maven
- Git
- A working Gmail app token password ( see below )

### 🔐 How to Get a Gmail App Password

If you're using Gmail with an app (like a Java or Python program) and **2-Step Verification** is enabled, you'll need to generate an **App Password**.

1. Go to [Google Account Security Settings](https://myaccount.google.com/security).
2. Under **"Signing in to Google"**, ensure that:
   - **2-Step Verification** is turned **ON**.
3. After enabling 2FA, a new option **"App passwords"** will appear below.
4. Click on **App passwords**.
5. Sign in again if prompted.
6. Under **Select app**, choose `Mail`.
7. Under **Select device**, choose `Other (Custom name)` and name it (e.g., `Java App`).
8. Click **Generate**.
9. Google will show a **16-character password**.
10. **Copy this password** — this is your app-specific token.
11. Use this generated password in your app instead of your regular Gmail password.

> 💡 **Important:** Keep this token secure. You won't need to remember it — just store it safely in your app config or secrets manager.


## 1. Backend (Spring Boot)
```bash
1. Git clone this backend repository
2. With a terminal like bash or powershell, change directory into it
3. Configure your own setting with application-template-docker.properties ( Mandatory: app.emailFrom, app.emailPassword, app.emailTo Optional: app.aiRoleplay , app.candidateProfile)
4. Package the program by command "mvn clean package"
5. run command "docker compose up"
6. Give it some time, docker will load ollama and mistral AI model, postgresql database and run the a batch job which will match the candidate profile with jobs from the init url


