#!/bin/bash
set -e

echo "Waiting for Ollama to load mistral:latest model..."

until curl -s http://ollama:11434/api/tags | jq -e '.models[] | select(.model=="mistral:latest")' > /dev/null; do
  echo "Model not ready yet..."
  sleep 2
done

echo "Ollama is ready!"
exec "$@"
