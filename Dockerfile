# Start with Ubuntu base
FROM ubuntu:22.04

# Set env
ENV DEBIAN_FRONTEND=noninteractive
ENV LANG=C.UTF-8

# Install system packages: Java, curl, unzip, Chrome (if needed)
RUN apt-get update && apt-get install -y \
    wget unzip curl gnupg ca-certificates git \
    openjdk-17-jdk maven \
    fonts-liberation libappindicator3-1 libasound2 \
    libatk-bridge2.0-0 libatk1.0-0 libcups2 libdbus-1-3 \
    libgdk-pixbuf2.0-0 libnspr4 libnss3 libx11-xcb1 \
    libxcomposite1 libxdamage1 libxrandr2 xdg-utils \
    && rm -rf /var/lib/apt/lists/*

ARG CHROME_VERSION=138.0.7204.168-1

# Optional: Install Chrome if you're using Selenium with ChromeDriver
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable=${CHROME_VERSION} && \
    apt-mark hold google-chrome-stable

ARG CHROME_DRIVER_VERSION=138.0.7204.168
# Install matching ChromeDriver
RUN wget -q https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/${CHROME_DRIVER_VERSION}/linux64/chromedriver-linux64.zip && \
    unzip chromedriver-linux64.zip && \
    mv chromedriver-linux64/chromedriver /usr/local/bin/chromedriver && \
    chmod +x /usr/local/bin/chromedriver && \
    rm -rf chromedriver-linux64.zip chromedriver-linux64

COPY target/AIJobMatcher-1.0-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]


