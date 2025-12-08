#!/bin/bash

# ==========================================
# 1. Sync Configuration
# ==========================================

# Load root .env variables
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
else
    echo "❌ Root .env file not found!"
    exit 1
fi

# Construct valid URL
NEW_APP_URL="${API_PROTOCOL}://${API_HOST}:${API_PORT}"

# Update backend/.env
if [ -f backend/.env ]; then
    # Escape slashes for sed
    ESCAPED_URL=$(echo "$NEW_APP_URL" | sed 's/\//\\\//g')
    
    # Update APP_URL
    sed -i '' "s/^APP_URL=.*/APP_URL=${ESCAPED_URL}/" backend/.env
    
    echo "✅ Configuration Synced:"
    echo "   Backend URL set to: ${NEW_APP_URL}"
else
    echo "❌ backend/.env not found!"
    exit 1
fi

echo ""

# ==========================================
# 2. Target Selection
# ==========================================

echo "Select Target to Run:"
echo "1) Desktop"
echo "2) Android"
echo "3) iOS"
read -p "Enter choice [1-3]: " TARGET_CHOICE

case $TARGET_CHOICE in
    1)
        TARGET="desktop"
        GRADLE_TASK=":desktopApp:run"
        ;;
    2)
        TARGET="android"
        GRADLE_TASK=":androidApp:installDebug"
        echo "⚠️  Note: Android target will install the app. Ensure an emulator or device is connected."
        ;;
    3)
        TARGET="ios"
        GRADLE_TASK=""
        echo "⚠️  Note: iOS target will open Xcode. Run the app from there."
        ;;
    *)
        echo "❌ Invalid choice"
        exit 1
        ;;
esac

echo ""
echo "🚀 Starting Services & App ($TARGET)..."
echo "   (Press Ctrl+C to stop all)"
echo ""

# ==========================================
# 3. Run Services
# ==========================================

# Function to kill background processes on exit
cleanup() {
    echo ""
    echo "🛑 Stopping services..."
    kill $(jobs -p) 2>/dev/null
    exit
}
trap cleanup SIGINT

# Run migrations
echo "🐘 Running migrations and seeds..."
(php artisan migrate:fresh --seed)

# Run npm run dev
echo "📦 Starting npm run dev..."
(cd backend && npm run dev) &

# Run php artisan serve
echo "🐘 Starting Laravel Server (${API_HOST}:${API_PORT})..."
(cd backend && php artisan serve --host=${API_HOST} --port=${API_PORT}) &

# Wait a moment for services to spin up
sleep 3

# ==========================================
# 4. Run App
# ==========================================

if [ "$TARGET" == "ios" ]; then
    echo "🍎 Opening iOS Project in Xcode..."
    open compose-app/iosApp/iosApp.xcodeproj
    echo "✅ Xcode opened. Services are running in background."
    echo "   Press Ctrl+C to stop services."
    wait
else
    echo "☕ Starting Gradle Task: $GRADLE_TASK"
    cd compose-app

    ./gradlew $GRADLE_TASK

    # Keep script running if gradle finishes (e.g. installDebug) so services stay up
    if [ "$TARGET" == "android" ]; then
        echo "✅ App Installed. Services are still running."
        echo "   Press Ctrl+C to stop services."
        wait
    fi
fi
