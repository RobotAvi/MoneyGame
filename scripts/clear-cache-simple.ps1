# Simple cache clearing script
Write-Host "Clearing GitHub Actions caches..." -ForegroundColor Green

# Get cache list
Write-Host "Getting cache list..." -ForegroundColor Yellow
$caches = gh api repos/RobotAvi/MoneyGame/actions/caches --jq '.actions_caches[] | .key'

if ($caches) {
    $cacheCount = ($caches | Measure-Object -Line).Lines
    Write-Host "Found caches: $cacheCount" -ForegroundColor Cyan
    
    $cleared = 0
    foreach ($key in $caches) {
        if ($key) {
            Write-Host "Deleting: $key" -ForegroundColor Yellow
            $result = gh api repos/RobotAvi/MoneyGame/actions/caches -X DELETE --field key="$key"
            if ($LASTEXITCODE -eq 0) {
                Write-Host "Deleted: $key" -ForegroundColor Green
                $cleared++
            } else {
                Write-Host "Error: $key" -ForegroundColor Red
            }
        }
    }
    
    Write-Host "Deleted caches: $cleared from $cacheCount" -ForegroundColor Green
} else {
    Write-Host "No caches found" -ForegroundColor Yellow
}

# Check result
Write-Host "Checking after cleanup..." -ForegroundColor Yellow
$remaining = gh api repos/RobotAvi/MoneyGame/actions/caches --jq '.total_count'
Write-Host "Remaining caches: $remaining" -ForegroundColor Cyan

if ($remaining -eq 0) {
    Write-Host "All caches cleared successfully!" -ForegroundColor Green
} else {
    Write-Host "Remaining caches: $remaining" -ForegroundColor Yellow
} 