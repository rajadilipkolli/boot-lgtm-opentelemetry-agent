# PowerShell script to generate traffic for testing OpenTelemetry
# Windows equivalent of generate-traffic.sh

Write-Host "Starting to generate traffic to test OpenTelemetry..."
Write-Host "Press Ctrl+C to stop"

while ($true) {
    try {
        Write-Host "Sending request to application..."
        Invoke-WebRequest -Uri "http://localhost:8080/api/v1/coupons" -UseBasicParsing | Out-Null
    } catch {
        Write-Host "Failed to connect to application: $_"
        Write-Host "Waiting before retry..."
    }
    
    Start-Sleep -Seconds 5
}