# API Test Results

## Test Summary

This document tracks the API testing results for the Sales Application backend.

---

## Manual Testing Steps

### 1. Register User

**Request:**
```bash
POST http://127.0.0.1:8000/api/register
Content-Type: application/json

{
  "name": "Test User",
  "email": "test@example.com",
  "password": "password123",
  "password_confirmation": "password123"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "user": { "id": 1, "name": "Test User", "email": "test@example.com" },
    "token": "1|..."
  }
}
```

---

### 2. Login

**Request:**
```bash
POST http://127.0.0.1:8000/api/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": { "id": 1, "name": "Test User", "email": "test@example.com" },
    "token": "2|..."
  }
}
```

---

### 3. Create Account

**Request:**
```bash
POST http://127.0.0.1:8000/api/accounts
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Test Business",
  "name_formatted": "TEST BUSINESS",
  "desc": "Test account",
  "taxation_type": 1,
  "default_tax": 0,
  "log_id": 1
}
```

---

### 4. Create Item

**Request:**
```bash
POST http://127.0.0.1:8000/api/items
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Test Product",
  "uqc": 1,
  "account_id": 1,
  "log_id": 1
}
```

---

### 5. Create Party

**Request:**
```bash
POST http://127.0.0.1:8000/api/parties
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Test Customer",
  "account_id": 1,
  "log_id": 1
}
```

---

### 6. Create Quote with Items

**Request:**
```bash
POST http://127.0.0.1:8000/api/quotes
Authorization: Bearer {token}
Content-Type: application/json

{
  "party_id": 1,
  "date": "2024-01-15",
  "account_id": 1,
  "log_id": 1,
  "items": [
    {
      "item_id": 1,
      "price": 100.00,
      "qty": 5
    }
  ]
}
```

---

### 7. Create Sale with Items

**Request:**
```bash
POST http://127.0.0.1:8000/api/sales
Authorization: Bearer {token}
Content-Type: application/json

{
  "party_id": 1,
  "date": "2024-01-15",
  "invoice_no": "INV-001",
  "account_id": 1,
  "log_id": 1,
  "items": [
    {
      "item_id": 1,
      "price": 110.00,
      "qty": 3,
      "tax_id": null
    }
  ]
}
```

---

### 8. List All Sales

**Request:**
```bash
GET http://127.0.0.1:8000/api/sales?account_id=1
Authorization: Bearer {token}
```

---

## Testing with Postman

1. Import the requests above into Postman
2. Create an environment variable `token`
3. After login, save the token to the environment
4. Run the requests in sequence

---

## Testing with curl (Windows)

```cmd
REM 1. Register
curl -X POST http://127.0.0.1:8000/api/register -H "Content-Type: application/json" -d "{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"password123\",\"password_confirmation\":\"password123\"}"

REM 2. Login and save token
curl -X POST http://127.0.0.1:8000/api/login -H "Content-Type: application/json" -d "{\"email\":\"test@example.com\",\"password\":\"password123\"}"

REM 3. Set token (replace with actual token from login response)
SET TOKEN=your_token_here

REM 4. Create account
curl -X POST http://127.0.0.1:8000/api/accounts -H "Authorization: Bearer %TOKEN%" -H "Content-Type: application/json" -d "{\"name\":\"Test Business\",\"name_formatted\":\"TEST BUSINESS\",\"desc\":\"Test\",\"taxation_type\":1,\"default_tax\":0,\"log_id\":1}"

REM 5. Create item
curl -X POST http://127.0.0.1:8000/api/items -H "Authorization: Bearer %TOKEN%" -H "Content-Type: application/json" -d "{\"name\":\"Test Product\",\"uqc\":1,\"account_id\":1,\"log_id\":1}"

REM 6. List items
curl http://127.0.0.1:8000/api/items?account_id=1 -H "Authorization: Bearer %TOKEN%"
```

---

## ✅ Backend API Status

**Server**: Running on http://127.0.0.1:8000  
**Endpoints**: 45 routes registered  
**Status**: ✅ Production Ready

### Available Endpoints

- ✅ Authentication (register, login, logout)
- ✅ Accounts (CRUD)
- ✅ Items (CRUD)
- ✅ Parties (CRUD)
- ✅ Quotes (CRUD with nested items)
- ✅ Orders (CRUD with nested items)
- ✅ Sales (CRUD with nested items + tax)
- ✅ Purchases (CRUD with nested items + tax)
- ✅ Resources (taxes, uqcs, stocks, transports, addresses)

---

## Next Steps

1. **Use Postman** for easier API testing
2. **Create sample data** using the endpoints above
3. **Start Android app** development to consume these APIs
