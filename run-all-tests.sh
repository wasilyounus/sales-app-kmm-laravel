#!/bin/bash

# Setup and Run Tests Script

echo "================================"
echo "Test Environment Setup & Execution"
echo "================================"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

KOTLIN_PASSED=0
PHP_PASSED=0

echo ""
echo -e "${YELLOW}📋 Step 1: Setting up test environment...${NC}"

# Setup test database
echo "Setting up test database..."
cd backend
php artisan migrate:fresh --env=testing --force
cd ..

echo -e "${GREEN}✅ Test environment ready${NC}"

# Run tests
echo ""
echo "================================"
echo "Running Tests"
echo "================================"

# 1. Run Kotlin Tests
echo ""
echo -e "${YELLOW}📱 Running Compose App Tests (Kotlin)...${NC}"
cd compose-app
if ./gradlew test --warning-mode none; then
    echo -e "${GREEN}✅ Kotlin tests PASSED${NC}"
    KOTLIN_PASSED=1
else
    echo -e "${RED}❌ Kotlin tests FAILED${NC}"
fi
cd ..

# 2. Run PHP Tests
echo ""
echo -e "${YELLOW}🐘 Running Backend Tests (PHP)...${NC}"
cd backend
if php artisan test --without-tty; then
    echo -e "${GREEN}✅ PHP tests PASSED${NC}"
    PHP_PASSED=1
else
    echo -e "${RED}❌ PHP tests FAILED${NC}"
fi
cd ..

# Summary
echo ""
echo "================================"
echo "Test Results Summary"
echo "================================"
if [ $KOTLIN_PASSED -eq 1 ]; then
    echo -e "${GREEN}✅ Kotlin Tests: PASSED${NC}"
else
    echo -e "${RED}❌ Kotlin Tests: FAILED${NC}"
fi

if [ $PHP_PASSED -eq 1 ]; then
    echo -e "${GREEN}✅ PHP Tests: PASSED${NC}"
else
    echo -e "${RED}❌ PHP Tests: FAILED${NC}"
fi

echo ""
if [ $KOTLIN_PASSED -eq 1 ] && [ $PHP_PASSED -eq 1 ]; then
    echo -e "${GREEN}🎉 ALL TESTS PASSED!${NC}"
    exit 0
else
    echo -e "${RED}⚠️  SOME TESTS FAILED${NC}"
    echo ""
    echo "To see detailed output:"
    echo "  Kotlin: cd compose-app && ./gradlew test"
    echo "  PHP: cd backend && php artisan test"
    exit 1
fi
