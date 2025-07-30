# PowerShell script for clearing caches via GitHub CLI
Write-Host "Clearing caches via GitHub CLI..." -ForegroundColor Green

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
            $result = gh api repos/RobotAvi/MoneyGame/actions/caches -X DELETE --field key="$key" 2>$null
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
$remaining = gh api repos/RobotAvi/MoneyGame/actions/caches --jq '.total_count' 2>$null
Write-Host "Remaining caches: $remaining" -ForegroundColor Cyan 