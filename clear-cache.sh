#!/bin/bash
set -e

# Встановлення Redis CLI, якщо його немає (Ubuntu)
if ! command -v redis-cli &> /dev/null
then
    echo "redis-cli not found! Installing..."
    sudo apt update && sudo apt install -y redis-tools
fi

echo "Clearing Redis cache..."
redis-cli FLUSHALL
echo "Redis cache cleared!"
