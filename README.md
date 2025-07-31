( Update in progress )

# AI Job Matcher 🚀

A full-stack Spring Boot + React web app that scrapes jobs from a job portal, uses an AI model to evaluate their relevance to your profile. Currently it could send an email to yourself letting you know which job is suitable for you.

# 🧠 What It Does

This project automates the job search process with a bit of intelligence:
- 🔍 Scrapes job listings from a job portal using Selenium WebDriver.
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

# PC Requirements for AI Job Matcher

## Minimum Recommended Setup
- **CPU:** Intel i5 10th Gen or equivalent
- **RAM:** 16 GB
- **GPU:** Mid-range GPU with at least 4 GB VRAM (for basic AI acceleration)
- **Storage:** SSD for faster read/write speeds
- **OS:** Linux preferred; Windows with WSL2 possible but with overhead

This setup is suitable for light scraping (e.g., 5 jobs max), running smaller AI models like `mistral`, and casual local development.

---

## Smooth Sailing Setup (For Serious Usage)
- **CPU:** 6-core / 12-thread or better (e.g. AMD Ryzen 7 5800X, Intel i7-12700)
- **RAM:** 32 GB (handles Docker, JVM, Chrome, and Ollama comfortably)
- **GPU:** NVIDIA RTX 3060 or better (8+ GB VRAM recommended for faster AI inference)
- **Storage:** NVMe SSD for fast I/O
- **OS:** Linux is preferred for performance and compatibility

Recommended if you plan to scrape dozens or hundreds of jobs, run larger AI models, or want smooth, responsive performance without freezing during processing.

---

## Notes
- Your older CPU (like Intel i5-6600) will work for basic testing but expect heavy CPU usage and possible freezes when running AI inference or multiple services concurrently.
- Using GPU acceleration via Ollama helps but only if your GPU meets or exceeds minimum VRAM requirements.
- Consider upgrading hardware or offloading heavy AI processing to a cloud or more powerful machine for large-scale jobs.

# 📦 How to Run

## Software Requirement
- Docker Desktop ( https://www.docker.com/products/docker-desktop/ )
- Maven ( https://maven.apache.org/download.cgi )
- Git ( https://git-scm.com/ )
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


## ⚙️ Possible Configuration in `application-docker.properties`

Below are the essential configuration properties used to run the AI Job Matcher application in a Docker environment.

### 🌐 Web Scraper Configuration
- `app.initUrl` – Initial URL to start scraping from (JobsDB listing).
- `app.maxJobs` – Number of job postings to scrape in one batch.

### 🤖 AI Model Configuration
- `app.aiModel` – Name of the AI model used (e.g. `mistral`).
- `app.aiRoleplay` – Prompt that defines the AI’s personality and behavior.
- `app.aiTask` – Template prompt for generating the structured evaluation output.

### 👤 Candidate Profile
- `app.candidateProfile` – A paragraph describing the candidate to be evaluated against job listings.

### ✉️ Email Notification
- `app.emailFrom` – Gmail account used to send notifications.
- `app.emailPassword` – App-specific Gmail password (not your real password).
- `app.emailTo` – Recipient address for job matching results.

## 1. Backend (Spring Boot)
```bash
1. Git clone this backend repository
2. With a terminal like bash or powershell, change directory into it
3. Configure your own setting with application-template-docker.properties ( Mandatory: app.emailFrom, app.emailPassword, app.emailTo Optional: app.aiRoleplay , app.candidateProfile)
4. In src/main/resources, rename application-template-docker.properties to application-docker.properties
4. Package the program by command "mvn clean package"
5. run command "docker compose up"
6. Give it some time, docker will load ollama and mistral AI model, postgresql database and run the a batch job which will match the candidate profile with jobs from the init url


