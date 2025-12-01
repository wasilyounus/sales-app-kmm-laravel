$ErrorActionPreference = "Stop"

Write-Host "Testing Sales App API" -ForegroundColor Cyan
Write-Host "======================`n" -ForegroundColor Cyan

# 1. Register/Login
$headers = @{ "Content-Type" = "application/json" }
$registerBody = @{
    name = "Test User"
    email = "test@example.com"
    password = "password123"
    password_confirmation = "password123"
}
$registerJson = $registerBody | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://127.0.0.1:8000/api/register" -Method Post -Headers $headers -Body $registerJson
    $token = $response.data.token
    Write-Host "User registered" -ForegroundColor Green
} catch {
    $loginBody = @{
        email = "test@example.com"
        password = "password123"
    }
    $loginJson = $loginBody | ConvertTo-Json
    $response = Invoke-RestMethod -Uri "http://127.0.0.1:8000/api/login" -Method Post -Headers $headers -Body $loginJson
    $token = $response.data.token
    Write-Host "User logged in" -ForegroundColor Green
}

$authHeaders = @{
    "Content-Type" = "application/json"
    "Authorization" = "Bearer $token"
}

# 2. Create Account
$accountBody = @{
    name = "Test Business"
    name_formatted = "TEST BUSINESS"
    desc = "Test account"
    taxation_type = 1
    default_tax = 0
    log_id = 1
}
$accountJson = $accountBody | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://127.0.0.1:8000/api/accounts" -Method Post -Headers $authHeaders -Body $accountJson
$accountId = $response.data.id
Write-Host "Account created (ID: $accountId)" -ForegroundColor Green

# 3. Create Item
$itemBody = @{
    name = "Test Product"
    uqc = 1
    account_id = $accountId
    log_id = 1
}
$itemJson = $itemBody | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://127.0.0.1:8000/api/items" -Method Post -Headers $authHeaders -Body $itemJson
$itemId = $response.data.id
Write-Host "Item created (ID: $itemId)" -ForegroundColor Green

# 4. Create Party
$partyBody = @{
    name = "Test Customer"
    account_id = $accountId
    log_id = 1
}
$partyJson = $partyBody | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://127.0.0.1:8000/api/parties" -Method Post -Headers $authHeaders -Body $partyJson
$partyId = $response.data.id
Write-Host "Party created (ID: $partyId)" -ForegroundColor Green

# 5. Create Quote
$currentDate = Get-Date -Format "yyyy-MM-dd"
$quoteBody = @{
    party_id = $partyId
    date = $currentDate
    account_id = $accountId
    log_id = 1
    items = @(
        @{
            item_id = $itemId
            price = 100.00
            qty = 5
        }
    )
}
$quoteJson = $quoteBody | ConvertTo-Json -Depth 3

$response = Invoke-RestMethod -Uri "http://127.0.0.1:8000/api/quotes" -Method Post -Headers $authHeaders -Body $quoteJson
$quoteId = $response.data.id
Write-Host "Quote created (ID: $quoteId)" -ForegroundColor Green

# 6. Create Sale
$randomNum = Get-Random -Minimum 1000 -Maximum 9999
$invoiceNo = "INV-$randomNum"
$saleBody = @{
    party_id = $partyId
    date = $currentDate
    invoice_no = $invoiceNo
    account_id = $accountId
    log_id = 1
    items = @(
        @{
            item_id = $itemId
            price = 110.00
            qty = 3
            tax_id = $null
        }
    )
}
$saleJson = $saleBody | ConvertTo-Json -Depth 3

$response = Invoke-RestMethod -Uri "http://127.0.0.1:8000/api/sales" -Method Post -Headers $authHeaders -Body $saleJson
$saleId = $response.data.id
Write-Host "Sale created (ID: $saleId)" -ForegroundColor Green

# 7. List all data
$itemsUrl = "http://127.0.0.1:8000/api/items?account_id=$accountId"
$partiesUrl = "http://127.0.0.1:8000/api/parties?account_id=$accountId"
$quotesUrl = "http://127.0.0.1:8000/api/quotes?account_id=$accountId"
$salesUrl = "http://127.0.0.1:8000/api/sales?account_id=$accountId"

$items = Invoke-RestMethod -Uri $itemsUrl -Method Get -Headers $authHeaders
$parties = Invoke-RestMethod -Uri $partiesUrl -Method Get -Headers $authHeaders
$quotes = Invoke-RestMethod -Uri $quotesUrl -Method Get -Headers $authHeaders
$sales = Invoke-RestMethod -Uri $salesUrl -Method Get -Headers $authHeaders

Write-Host "`nSummary:" -ForegroundColor Cyan
Write-Host "  Items: $($items.data.Count)" -ForegroundColor Yellow
Write-Host "  Parties: $($parties.data.Count)" -ForegroundColor Yellow
Write-Host "  Quotes: $($quotes.data.Count)" -ForegroundColor Yellow
Write-Host "  Sales: $($sales.data.Count)" -ForegroundColor Yellow

Write-Host "`nAll tests passed!" -ForegroundColor Green
Write-Host "Sample data created successfully!" -ForegroundColor Cyan
