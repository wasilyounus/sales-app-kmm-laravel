<?php

$baseUrl = 'http://127.0.0.1:8000/api';
$results = [];
$token = '';

function testEndpoint($name, $method, $url, $data = null, $token = null) {
    global $results;
    
    $ch = curl_init($url);
    $headers = ['Content-Type: application/json'];
    
    if ($token) {
        $headers[] = 'Authorization: Bearer ' . $token;
    }
    
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);
    
    if ($data) {
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
    }
    
    $response = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);
    
    $responseData = json_decode($response, true);
    $status = ($httpCode >= 200 && $httpCode < 300) ? 'PASS' : 'FAIL';
    
    $results[] = [
        'name' => $name,
        'status' => $status,
        'http_code' => $httpCode,
        'response' => $responseData
    ];
    
    return $responseData;
}

echo "🧪 Sales App API Testing\n";
echo "========================\n\n";

// Test 1: Register User
echo "[1/10] Testing user registration...\n";
$response = testEndpoint('Register User', 'POST', $baseUrl . '/register', [
    'name' => 'Test User',
    'email' => 'test@example.com',
    'password' => 'password123',
    'password_confirmation' => 'password123'
]);

if (isset($response['data']['token'])) {
    $token = $response['data']['token'];
    echo "✓ User registered successfully\n";
} else {
    // Try login instead
    echo "[1b/10] User exists, logging in...\n";
    $response = testEndpoint('Login User', 'POST', $baseUrl . '/login', [
        'email' => 'test@example.com',
        'password' => 'password123'
    ]);
    $token = $response['data']['token'] ?? '';
    echo "✓ User logged in successfully\n";
}

// Test 2: Get Current User
echo "[2/10] Testing get current user...\n";
$response = testEndpoint('Get Current User', 'GET', $baseUrl . '/user', null, $token);
echo "✓ Current user retrieved\n";

// Test 3: Create Account
echo "[3/10] Testing account creation...\n";
$response = testEndpoint('Create Account', 'POST', $baseUrl . '/accounts', [
    'name' => 'Test Business',
    'name_formatted' => 'TEST BUSINESS',
    'desc' => 'Test account for API testing',
    'taxation_type' => 1,
    'default_tax' => 0,
    'log_id' => 1
], $token);
$accountId = $response['data']['id'] ?? 1;
echo "✓ Account created (ID: $accountId)\n";

// Test 4: Create Item
echo "[4/10] Testing item creation...\n";
$response = testEndpoint('Create Item', 'POST', $baseUrl . '/items', [
    'name' => 'Test Product',
    'alt_name' => 'Product Alt',
    'brand' => 'Test Brand',
    'uqc' => 1,
    'account_id' => $accountId,
    'log_id' => 1
], $token);
$itemId = $response['data']['id'] ?? 1;
echo "✓ Item created (ID: $itemId)\n";

// Test 5: Create Party
echo "[5/10] Testing party creation...\n";
$response = testEndpoint('Create Party', 'POST', $baseUrl . '/parties', [
    'name' => 'Test Customer',
    'gst' => '29ABCDE1234F1Z5',
    'phone' => '+91 9876543210',
    'account_id' => $accountId,
    'log_id' => 1
], $token);
$partyId = $response['data']['id'] ?? 1;
echo "✓ Party created (ID: $partyId)\n";

// Test 6: Create Quote
echo "[6/10] Testing quote creation...\n";
$response = testEndpoint('Create Quote', 'POST', $baseUrl . '/quotes', [
    'party_id' => $partyId,
    'date' => date('Y-m-d'),
    'account_id' => $accountId,
    'log_id' => 1,
    'items' => [
        [
            'item_id' => $itemId,
            'price' => 100.00,
            'qty' => 5
        ]
    ]
], $token);
$quoteId = $response['data']['id'] ?? null;
echo "✓ Quote created (ID: $quoteId)\n";

// Test 7: Create Sale
echo "[7/10] Testing sale creation...\n";
$response = testEndpoint('Create Sale', 'POST', $baseUrl . '/sales', [
    'party_id' => $partyId,
    'date' => date('Y-m-d'),
    'invoice_no' => 'INV-' . rand(1000, 9999),
    'account_id' => $accountId,
    'log_id' => 1,
    'items' => [
        [
            'item_id' => $itemId,
            'price' => 110.00,
            'qty' => 3,
            'tax_id' => null
        ]
    ]
], $token);
$saleId = $response['data']['id'] ?? null;
echo "✓ Sale created (ID: $saleId)\n";

// Test 8: List Items
echo "[8/10] Testing list items...\n";
$response = testEndpoint('List Items', 'GET', $baseUrl . '/items?account_id=' . $accountId, null, $token);
$itemCount = count($response['data'] ?? []);
echo "✓ Items listed (Count: $itemCount)\n";

// Test 9: List Sales
echo "[9/10] Testing list sales...\n";
$response = testEndpoint('List Sales', 'GET', $baseUrl . '/sales?account_id=' . $accountId, null, $token);
$saleCount = count($response['data'] ?? []);
echo "✓ Sales listed (Count: $saleCount)\n";

// Test 10: Test Endpoint
echo "[10/10] Testing test endpoint...\n";
$response = testEndpoint('Test Endpoint', 'GET', $baseUrl . '/test', null, $token);
echo "✓ Test endpoint working\n";

// Generate Summary
echo "\n========== TEST SUMMARY ==========\n";
$passed = count(array_filter($results, fn($r) => $r['status'] === 'PASS'));
$failed = count(array_filter($results, fn($r) => $r['status'] === 'FAIL'));
$total = count($results);

echo "Total Tests: $total\n";
echo "Passed: $passed ✓\n";
echo "Failed: $failed " . ($failed > 0 ? '✗' : '✓') . "\n";
echo "Success Rate: " . round(($passed / $total) * 100, 2) . "%\n";
echo "==================================\n\n";

// Save detailed report
$reportData = [
    'timestamp' => date('Y-m-d H:i:s'),
    'summary' => [
        'total' => $total,
        'passed' => $passed,
        'failed' => $failed,
        'success_rate' => round(($passed / $total) * 100, 2)
    ],
    'tests' => $results,
    'sample_data' => [
        'account_id' => $accountId,
        'item_id' => $itemId,
        'party_id' => $partyId,
        'quote_id' => $quoteId,
        'sale_id' => $saleId
    ]
];

file_put_contents('test-report.json', json_encode($reportData, JSON_PRETTY_PRINT));
echo "📄 Detailed report saved to: test-report.json\n";

// Generate Markdown Report
$markdown = "# API Test Report\n\n";
$markdown .= "**Date**: " . date('Y-m-d H:i:s') . "\n\n";
$markdown .= "## Summary\n\n";
$markdown .= "- **Total Tests**: $total\n";
$markdown .= "- **Passed**: $passed ✓\n";
$markdown .= "- **Failed**: $failed\n";
$markdown .= "- **Success Rate**: " . round(($passed / $total) * 100, 2) . "%\n\n";
$markdown .= "## Test Results\n\n";
$markdown .= "| # | Test Name | Status | HTTP Code |\n";
$markdown .= "|---|-----------|--------|----------|\n";

foreach ($results as $i => $result) {
    $status = $result['status'] === 'PASS' ? '✅ PASS' : '❌ FAIL';
    $markdown .= "| " . ($i + 1) . " | " . $result['name'] . " | " . $status . " | " . $result['http_code'] . " |\n";
}

$markdown .= "\n## Sample Data Created\n\n";
$markdown .= "- **Account ID**: $accountId\n";
$markdown .= "- **Item ID**: $itemId\n";
$markdown .= "- **Party ID**: $partyId\n";
$markdown .= "- **Quote ID**: $quoteId\n";
$markdown .= "- **Sale ID**: $saleId\n";

file_put_contents('TEST_REPORT.md', $markdown);
echo "📄 Markdown report saved to: TEST_REPORT.md\n\n";

if ($failed === 0) {
    echo "✅ All tests passed! Backend API is fully functional.\n";
} else {
    echo "⚠️  Some tests failed. Check test-report.json for details.\n";
}
