import-module Microsoft.PowerShell.Utility

$baseUrl = "http://127.0.0.1:8000/api"
$results = @()
$token = ""

function Test-Endpoint {
    param($name, $method, $url, $body, $headers)
    try {
        $response = Invoke-RestMethod -Uri $url -Method $method -Headers $headers -Body $body -ErrorAction Stop
        $results += @{Name = $name; Status = "PASS"; Response = $response }
        return $response
    }
    catch {
        $results += @{Name = $name; Status = "FAIL"; Error = $_.Exception.Message }
        return $null
    }
}

Write-Host "Starting API Tests..." -ForegroundColor Cyan

# Test 1: Register
$headers = @{"Content-Type" = "application/json" }
$body = '{"name":"Test User","email":"test@example.com","password":"password123","password_confirmation":"password123"}'
$response = Test-Endpoint "Register User" "POST" "$baseUrl/register" $body $headers

if ($response) {
    $token = $response.data.token
    Write-Host "✓ Register: PASS" -ForegroundColor Green
}
else {
    # Try login instead
    $body = '{"email":"test@example.com","password":"password123"}'
    $response = Test-Endpoint "Login User" "POST" "$baseUrl/login" $body $headers
    if ($response) {
        $token = $response.data.token
        Write-Host "✓ Login: PASS" -ForegroundColor Green
    }
}

$authHeaders = @{"Content-Type" = "application/json"; "Authorization" = "Bearer $token" }

# Test 2: Get User
$response = Test-Endpoint "Get Current User" "GET" "$baseUrl/user" $null $authHeaders
if ($response) { Write-Host "✓ Get User: PASS" -ForegroundColor Green }

# Test 3: Create Account
$body = '{"name":"Test Co","name_formatted":"TEST CO","desc":"Test","taxation_type":1,"default_tax":0,"log_id":1}'
$response = Test-Endpoint "Create Account" "POST" "$baseUrl/accounts" $body $authHeaders
$accountId = if ($response) { $response.data.id } else { 1 }
if ($response) { Write-Host "✓ Create Account: PASS (ID: $accountId)" -ForegroundColor Green }

# Test 4: Create Item
$body = "{`"name`":`"Product A`",`"uqc`":1,`"account_id`":$accountId,`"log_id`":1}"
$response = Test-Endpoint "Create Item" "POST" "$baseUrl/items" $body $authHeaders
$itemId = if ($response) { $response.data.id } else { 1 }
if ($response) { Write-Host "✓ Create Item: PASS (ID: $itemId)" -ForegroundColor Green }

# Test 5: Create Party
$body = "{`"name`":`"Customer A`",`"account_id`":$accountId,`"log_id`":1}"
$response = Test-Endpoint "Create Party" "POST" "$baseUrl/parties" $body $authHeaders
$partyId = if ($response) { $response.data.id } else { 1 }
if ($response) { Write-Host "✓ Create Party: PASS (ID: $partyId)" -ForegroundColor Green }

# Test 6: Create Quote
$date = Get-Date -Format "yyyy-MM-dd"
$body = "{`"party_id`":$partyId,`"date`":`"$date`",`"account_id`":$accountId,`"log_id`":1,`"items`":[{`"item_id`":$itemId,`"price`":100,`"qty`":5}]}"
$response = Test-Endpoint "Create Quote" "POST" "$baseUrl/quotes" $body $authHeaders
if ($response) { Write-Host "✓ Create Quote: PASS" -ForegroundColor Green }

# Test 7: Create Sale
$body = "{`"party_id`":$partyId,`"date`":`"$date`",`"invoice_no`":`"INV-001`",`"account_id`":$accountId,`"log_id`":1,`"items`":[{`"item_id`":$itemId,`"price`":110,`"qty`":3,`"tax_id`":null}]}"
$response = Test-Endpoint "Create Sale" "POST" "$baseUrl/sales" $body $authHeaders
if ($response) { Write-Host "✓ Create Sale: PASS" -ForegroundColor Green }

# Test 8: List Items
$response = Test-Endpoint "List Items" "GET" "$baseUrl/items?account_id=$accountId" $null $authHeaders
if ($response) { Write-Host "✓ List Items: PASS (Count: $($response.data.Count))" -ForegroundColor Green }

# Test 9: List Sales
$response = Test-Endpoint "List Sales" "GET" "$baseUrl/sales?account_id=$accountId" $null $authHeaders
if ($response) { Write-Host "✓ List Sales: PASS (Count: $($response.data.Count))" -ForegroundColor Green }

# Generate Report
$passed = ($results | Where-Object { $_.Status -eq "PASS" }).Count
$failed = ($results | Where-Object { $_.Status -eq "FAIL" }).Count
$total = $results.Count

Write-Host "`n========== TEST SUMMARY ==========" -ForegroundColor Cyan
Write-Host "Total Tests: $total" -ForegroundColor White
Write-Host "Passed: $passed" -ForegroundColor Green
Write-Host "Failed: $failed" -ForegroundColor $(if ($failed -gt 0) { "Red" }else { "Green" })
Write-Host "==================================`n" -ForegroundColor Cyan

# Save detailed report
$reportPath = "test-report.json"
$results | ConvertTo-Json -Depth 5 | Out-File $reportPath
Write-Host "Detailed report saved to: $reportPath" -ForegroundColor Yellow
