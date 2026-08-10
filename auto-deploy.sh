#!/bin/bash
set -e

IMAGE="ghcr.io/vamshi1309/hospitalmanagementsystem-backend"
VERSION=$(cat VERSION)          # current version number, e.g. "3"
NEXT_VERSION=$((VERSION + 1))

echo "Step 1: Preserving old 'latest' as v$VERSION"
docker pull $IMAGE:latest                      # get the currently-live image
docker tag $IMAGE:latest $IMAGE:v$VERSION       # tag old image with old version
docker push $IMAGE:v$VERSION                   # push it so it's archived

echo "Step 2: Building new code as 'latest'"
docker build -t $IMAGE:latest .                # new code overwrites latest
docker push $IMAGE:latest

echo "$NEXT_VERSION" > VERSION                  # bump version for next time

echo "Step 3: Restarting local stack"
docker compose down
docker compose pull
docker compose up -d

echo "Done. Old image archived as v$VERSION, new code deployed as latest"