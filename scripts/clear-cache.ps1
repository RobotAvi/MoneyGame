# PowerShell script for clearing GitHub Actions caches
Write-Host "Clearing GitHub Actions caches..." -ForegroundColor Green

# Get list of all caches
Write-Host "Getting cache list..." -ForegroundColor Yellow
$caches = gh api repos/RobotAvi/MoneyGame/actions/caches --jq '.actions_caches[] | .key' 2>$null

if ($caches) {
    $cacheCount = ($caches | Measure-Object -Line).Lines
    Write-Host "Found caches: $cacheCount" -ForegroundColor Cyan
    
    $cleared = 0
    foreach ($key in $caches) {
        if ($key) {
            Write-Host "Deleting: $key" -ForegroundColor Yellow
            $url = "https://api.github.com/repos/RobotAvi/MoneyGame/actions/caches?key=$key"
            $headers = @{
                "Authorization" = "token $(gh auth token)"
                "Accept" = "application/vnd.github.v3+json"
            }
            
            try {
                $response = Invoke-RestMethod -Uri $url -Method DELETE -Headers $headers -ErrorAction Stop
                Write-Host "Deleted: $key" -ForegroundColor Green
                $cleared++
            } catch {
                Write-Host "Error: $key - $($_.Exception.Message)" -ForegroundColor Red
            }
        }
    }
    
    Write-Host "Deleted caches: $cleared from $cacheCount" -ForegroundColor Green
} else {
    Write-Host "No caches found" -ForegroundColor Yellow
}

# Check result
Write-Host "Checking after cleanup..." -ForegroundColor Yellow
$remaining = gh api repos/RobotAvi/MoneyGame/actions/caches --jq '.total_count' 2>$null
Write-Host "Remaining caches: $remaining" -ForegroundColor Cyan 