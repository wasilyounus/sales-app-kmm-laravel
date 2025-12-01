@echo off
echo ================================
echo Testing Sales App API
echo ================================
echo.

REM Test 1: Register User
echo [1/6] Registering user...
curl -s -X POST http://127.0.0.1:8000/api/register ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"password123\",\"password_confirmation\":\"password123\"}" ^
  -o register.json
echo Done

REM Test 2: Login (get token)
echo [2/6] Logging in...
curl -s -X POST http://127.0.0.1:8000/api/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"test@example.com\",\"password\":\"password123\"}" ^
  -o login.json
echo Done

REM Extract token (you'll need to manually get the token from login.json)
echo.
echo Please check login.json for your token
echo Then run the remaining tests manually with:
echo.
echo SET TOKEN=your_token_here
echo.
echo [3/6] Create Account:
echo curl -X POST http://127.0.0.1:8000/api/accounts -H "Authorization: Bearer %%TOKEN%%" -H "Content-Type: application/json" -d "{\"name\":\"Test Business\",\"name_formatted\":\"TEST BUSINESS\",\"desc\":\"Test\",\"taxation_type\":1,\"default_tax\":0,\"log_id\":1}"
echo.
echo [4/6] Create Item:
echo curl -X POST http://127.0.0.1:8000/api/items -H "Authorization: Bearer %%TOKEN%%" -H "Content-Type: application/json" -d "{\"name\":\"Test Product\",\"uqc\":1,\"account_id\":1,\"log_id\":1}"
echo.
echo [5/6] Create Party:
echo curl -X POST http://127.0.0.1:8000/api/parties -H "Authorization: Bearer %%TOKEN%%" -H "Content-Type: application/json" -d "{\"name\":\"Test Customer\",\"account_id\":1,\"log_id\":1}"
echo.
echo [6/6] List Items:
echo curl http://127.0.0.1:8000/api/items?account_id=1 -H "Authorization: Bearer %%TOKEN%%"
echo.

pause
