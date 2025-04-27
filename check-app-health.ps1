# PowerShell script to check if the application is healthy
$maxAttempts = 30
$attempts = 0
$delay = 2
$success = $false

Write-Host "Starting application health check..."

while ($attempts -lt $maxAttempts -and -not $success) {
  try {
    $attempts = $attempts + 1
    Write-Host "Attempt $attempts/$maxAttempts - Checking application health..."
    
    $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 5
    
    if ($response.StatusCode -eq 200) {
      Write-Host "Application is healthy! Status code $($response.StatusCode)"
      $success = $true
    } else {
      Write-Host "Application returned status $($response.StatusCode), waiting..."
      Start-Sleep -Seconds $delay
    }
  } catch {
    Write-Host "Application not ready yet, retrying..."
    Start-Sleep -Seconds $delay
  }
}

if (-not $success) { 
  Write-Host "Application health check failed after $attempts attempts"
  exit 1
}