#!/bin/bash

set -e

echo "Stopping existing application (if running)..."

APP_NAME="my-java-app"
PID=$(pgrep -f $APP_NAME) || true

if [ -n "$PID" ]; then
    echo "Stopping application with PID: $PID"
    kill "$PID"
    sleep 5
else
    echo "No running application found."
fi

echo "Deploying new application..."

JAR_FILE="target/my-app.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found!"
    exit 1
fi

sleep 2

nohup java -jar "$JAR_FILE" > app.log 2>&1 &

echo "Application deployed successfully!"
