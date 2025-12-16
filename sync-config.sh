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

# Parse arguments for target and migration mode
MIGRATION_MODE="standard"
choice=""

for arg in "$@"; do
    case $arg in
        1|2|3|4)
            choice=$arg
            ;;
        fresh)
            MIGRATION_MODE="fresh"
            ;;
    esac
done

# Only show menu if no choice was passed as argument
if [ -z "$choice" ]; then
    echo ""
    echo "Select Target to Run:"
    echo "1) Web Only (Backend + Frontend)"
    echo "2) Desktop"
    echo "3) Android"
    echo "4) iOS"
    echo ""
    echo -n "Enter choice [1-4] (default: 1): "
    read input_choice
    
    # Use input_choice if provided, otherwise default to existing choice (empty) causing fallback to 1 below
    if [ -n "$input_choice" ]; then
        choice=$input_choice
    fi
fi

# Default to option 1 (Web Only) if no input or timeout
if [ -z "$choice" ]; then
    choice=1
    echo "Using default: 1 (Web Only)"
fi

case $choice in
    1)
        target="web"
        echo ""
        echo "🚀 Starting Backend Services (web only)..."
        echo "   (Press Ctrl+C to stop all)"
        ;;
    2)
        target="desktop"
        GRADLE_TASK=":desktopApp:run --continuous"
        echo ""
        echo "🚀 Starting Services & App (desktop)..."
        echo "   (Press Ctrl+C to stop all)"
        ;;
    3)
        target="android"
        GRADLE_TASK=":androidApp:installDebug"
        echo "⚠️  Note: Android target will install the app. Ensure an emulator or device is connected."
        echo ""
        echo "🚀 Starting Services & App (android)..."
        echo "   (Press Ctrl+C to stop all)"
        ;;
    4)
        target="ios"
        GRADLE_TASK=""
        echo "⚠️  Note: iOS target will open Xcode. Run the app from there."
        echo ""
        echo "🚀 Starting Services & App (ios)..."
        echo "   (Press Ctrl+C to stop all)"
        ;;
    *)
        echo "❌ Invalid choice"
        exit 1
        ;;
esac

echo ""
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
echo "🐘 Checks for migration mode..."
if [ "$MIGRATION_MODE" == "fresh" ]; then
    echo "⚠️  FRESH MIGRATION REQUESTED: Wiping database and seeding..."
    (cd backend && php artisan migrate:fresh --seed)
else
    echo "🐘 Running standard migration..."
    (cd backend && php artisan migrate)
fi

# Run npm run dev
echo "📦 Starting npm run dev (logs -> backend/npm.log)..."
pushd backend > /dev/null
npm run dev > npm.log 2>&1 &
NPM_PID=$!
popd > /dev/null

# Run php artisan serve
echo "🐘 Starting Laravel Server (${API_HOST}:${API_PORT}) (logs -> backend/laravel.log)..."
pushd backend > /dev/null
php artisan serve --host=${API_HOST} --port=${API_PORT} > laravel.log 2>&1 &
LARAVEL_PID=$!
popd > /dev/null

# ==========================================
# 4. Run App
# ==========================================

if [ "$target" == "web" ]; then
    echo "✅ Backend services running (web only mode)"
    echo "   Laravel: http://${API_HOST}:${API_PORT}"
    echo "   Press Ctrl+C to stop services."
    echo ""
    # Keep script running indefinitely until Ctrl+C
    while true; do
        sleep 1
    done
elif [ "$target" == "ios" ]; then
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
    if [ "$target" == "android" ]; then
        echo "✅ App Installed. Services are still running."
        echo "   Press Ctrl+C to stop services."
        wait
    fi
fi
