# Start with Ubuntu base
FROM ubuntu:22.04

# Set env
ENV DEBIAN_FRONTEND=noninteractive
ENV LANG=C.UTF-8

# Install system packages: Java, curl, unzip, Chrome (if needed)
RUN apt-get update && apt-get install -y \
    wget unzip curl gnupg ca-certificates git \
    openjdk-17-jdk maven jq \
    fonts-liberation libappindicator3-1 libasound2 \
    libatk-bridge2.0-0 libatk1.0-0 libcups2 libdbus-1-3 \
    libgdk-pixbuf2.0-0 libnspr4 libnss3 libx11-xcb1 \
    libxcomposite1 libxdamage1 libxrandr2 xdg-utils \
    && rm -rf /var/lib/apt/lists/*

ARG CHROME_VERSION=138.0.7204.168-1

# Install the latest Chrome
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    apt-mark hold google-chrome-stable

# Dynamically fetch matching ChromeDriver version
RUN set -ex && \
    CHROME_VERSION=$(google-chrome --version | sed 's/Google Chrome //' | cut -d'.' -f1-3) && \
    CHROMEDRIVER_VERSION=$(curl -s "https://googlechromelabs.github.io/chrome-for-testing/last-known-good-versions-with-downloads.json" \
        | jq -r --arg ver "$CHROME_VERSION" '.channels.Stable.version as $v | .channels.Stable.downloads.chromedriver[] | select(.platform=="linux64") | .url' \
        | grep "$CHROME_VERSION") && \
    wget -q "$CHROMEDRIVER_VERSION" -O chromedriver.zip && \
    unzip chromedriver.zip && \
    mv chromedriver-linux64/chromedriver /usr/local/bin/ && \
    chmod +x /usr/local/bin/chromedriver && \
    rm -rf chromedriver.zip chromedriver-linux64


COPY target/aijobmatcher-0.0.1-SNAPSHOT.jar /aijobmatcher.jar
COPY wait-for-ollama.sh /wait-for-ollama.sh
RUN chmod +x /wait-for-ollama.sh

CMD ["/wait-for-ollama.sh", "java", "-jar", "aijobmatcher.jar", "--spring.profiles.active=docker"]


