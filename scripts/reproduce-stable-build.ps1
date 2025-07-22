#requires -Version 5.0
<#
.SYNOPSIS
    Воспроизводит stable-сборку проекта MoneyGame на Windows.
.DESCRIPTION
    Проверяет зависимости, собирает проект, копирует и переименовывает APK,
    (опционально) запускает эмулятор, устанавливает APK, делает скриншоты через Maestro.
#>

# Функции для логирования без эмодзи
function Log-Info    { Write-Host "INFO: $($args -join ' ')" -ForegroundColor Cyan }
function Log-Success { Write-Host "SUCCESS: $($args -join ' ')" -ForegroundColor Green }
function Log-Warn    { Write-Host "WARN: $($args -join ' ')" -ForegroundColor Yellow }
function Log-Error   { Write-Host "ERROR: $($args -join ' ')" -ForegroundColor Red }

# Проверка зависимостей
Log-Info "Проверка зависимостей..."

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Log-Error "Java не установлена. Установите JDK 21."
    exit 1
}
$javaVersion = (& java -version 2>&1)[0] -replace '.*version \"([^\"]+)\".*', '$1'
if ($javaVersion -notlike "21*") {
    Log-Warn "Рекомендуется Java 21, найдена версия: $javaVersion"
}

if (-not (Test-Path ".\\gradlew")) {
    Log-Error "gradlew не найден. Запустите скрипт из корня проекта."
    exit 1
}

# Проверка Android SDK
if (-not $env:ANDROID_SDK_ROOT) {
    $defaultSdk = "$env:USERPROFILE\\AppData\\Local\\Android\\Sdk"
    if (Test-Path $defaultSdk) {
        $env:ANDROID_SDK_ROOT = $defaultSdk
        Log-Info "ANDROID_SDK_ROOT не был установлен, используем $defaultSdk"
    } else {
        Log-Error "ANDROID_SDK_ROOT не установлен и SDK не найден по умолчанию. Установите Android SDK."
        exit 1
    }
}

# Добавляем platform-tools и emulator в PATH
$env:PATH += ";$env:ANDROID_SDK_ROOT\\platform-tools;$env:ANDROID_SDK_ROOT\\emulator"

# Сборка проекта
Log-Info "Очистка проекта..."
./gradlew clean --no-daemon
Log-Info "Запуск unit тестов..."
./gradlew test --no-daemon
Log-Info "Сборка debug APK..."
./gradlew assembleDebug --no-daemon

# Копирование и переименование APK
$apkPath = "app\\build\\outputs\\apk\\debug\\app-debug.apk"
if (-not (Test-Path $apkPath)) {
    Log-Error "APK не найден: $apkPath"
    exit 1
}
$version = (Select-String -Path "app\\build.gradle" -Pattern 'versionName\s+"([^"]+)"' | % { $_.Matches[0].Groups[1].Value })[0]
$versionCode = (Select-String -Path "app\\build.gradle" -Pattern 'versionCode\s+(\d+)' | % { $_.Matches[0].Groups[1].Value })[0]
$date = Get-Date -Format "yyyy-MM-dd_HH-mm"
$destDir = "releases\\debug"
New-Item -ItemType Directory -Force -Path $destDir | Out-Null
$destApk = "$destDir\\FinancialSuccess-v$version-$date-debug.apk"
Copy-Item $apkPath $destApk -Force
Log-Success "APK скопирован: $destApk"

# Символическая ссылка на latest-debug.apk (только в PowerShell 5+)
$latestLink = "$destDir\\latest-debug.apk"
if (Test-Path $latestLink) { Remove-Item $latestLink }
cmd /c mklink "$latestLink" "$([System.IO.Path]::GetFileName($destApk))" > $null

# Оставляем только 5 последних APK
$apks = Get-ChildItem $destDir -Filter "FinancialSuccess-v*-debug.apk" | Sort-Object LastWriteTime -Descending
if ($apks.Count -gt 5) {
    $apks | Select-Object -Skip 5 | Remove-Item
}

# Установка Maestro (если нужно)
if (-not (Get-Command maestro -ErrorAction SilentlyContinue)) {
    Log-Info "Установка Maestro..."
    $maestroUrl = "https://get.maestro.mobile.dev"
    $maestroInstaller = "$env:TEMP\\maestro-installer.ps1"
    Invoke-WebRequest $maestroUrl -OutFile $maestroInstaller
    powershell -ExecutionPolicy Bypass -File $maestroInstaller
    $env:PATH += ";$env:USERPROFILE\\.maestro\\bin"
}

# Проверка наличия эмулятора
function Start-Emulator {
    param(
        [string]$AvdName,
        [string]$DeviceProfile = "pixel_5",
        [string]$SystemImage = "system-images;android-34;google_apis;x86_64"
    )
    Log-Info "Запуск эмулятора $AvdName..."
    $avdmanager = "$env:ANDROID_SDK_ROOT\\cmdline-tools\\latest\\bin\\avdmanager.bat"
    $sdkmanager = "$env:ANDROID_SDK_ROOT\\cmdline-tools\\latest\\bin\\sdkmanager.bat"
    $emulator = "$env:ANDROID_SDK_ROOT\\emulator\\emulator.exe"

    # Установка system image, если нужно
    & $sdkmanager $SystemImage

    # Создание AVD, если не существует
    $avdList = & $avdmanager list avd
    if ($avdList -notmatch $AvdName) {
        echo "no" | & $avdmanager create avd --name $AvdName --package $SystemImage --device $DeviceProfile
    }

    # Запуск эмулятора
    Start-Process -FilePath $emulator -ArgumentList "-avd $AvdName -no-snapshot -no-boot-anim -noaudio -no-window" -PassThru | Out-Null

    # Ждем загрузки эмулятора
    $booted = $false
    for ($i=0; $i -lt 60; $i++) {
        Start-Sleep -Seconds 5
        $output = & adb shell getprop sys.boot_completed 2>&1
        if ($output -match "1") {
            $booted = $true
            break
        }
        Log-Info "Ожидание загрузки эмулятора... ($($i*5) сек)"
    }
    if (-not $booted) {
        Log-Error "Эмулятор не загрузился за 5 минут."
        exit 1
    }
    Log-Success "Эмулятор $AvdName запущен."
}

function Stop-Emulator {
    Log-Info "Остановка эмулятора..."
    & adb emu kill
    Start-Sleep -Seconds 5
}

function Install-Apk {
    param([string]$Apk)
    Log-Info "Установка APK на эмулятор..."
    & adb install -r $Apk
}

function Run-Maestro {
    param([string]$ConfigFile, [string]$OutputDir)
    Log-Info "Запуск Maestro сценария $ConfigFile..."
    & maestro test $ConfigFile --format junit --output $OutputDir
}

# Генерация скриншотов для телефона
$phoneAvd = "test_phone"
$phoneProfile = "pixel_5"
$phoneSystemImage = "system-images;android-34;google_apis;x86_64"
$phoneMaestroConfig = "maestro\\screenshots-minimal.yaml"
$phoneScreenshots = "screenshots\\phone"
New-Item -ItemType Directory -Force -Path $phoneScreenshots | Out-Null

Start-Emulator -AvdName $phoneAvd -DeviceProfile $phoneProfile -SystemImage $phoneSystemImage
Install-Apk -Apk $destApk
if (Test-Path $phoneMaestroConfig) {
    Run-Maestro -ConfigFile $phoneMaestroConfig -OutputDir $phoneScreenshots
}
Stop-Emulator

# Генерация скриншотов для планшета
$tabletAvd = "test_tablet"
$tabletProfile = "pixel_c"
$tabletSystemImage = "system-images;android-34;google_apis;x86_64"
$tabletMaestroConfig = "maestro\\screenshots-stable.yaml"
$tabletScreenshots = "screenshots\\tablet"
New-Item -ItemType Directory -Force -Path $tabletScreenshots | Out-Null

Start-Emulator -AvdName $tabletAvd -DeviceProfile $tabletProfile -SystemImage $tabletSystemImage
Install-Apk -Apk $destApk
if (Test-Path $tabletMaestroConfig) {
    Run-Maestro -ConfigFile $tabletMaestroConfig -OutputDir $tabletScreenshots
}
Stop-Emulator

# Финальный отчет
Log-Success "SUCCESS: Воспроизведение сборки stable завершено!"
Write-Host ""
Write-Host "Version: $version"
Write-Host "Version Code: $versionCode"
Write-Host "Date: $date"
Write-Host "APK: $destApk"
Write-Host "Phone screenshots: $phoneScreenshots"
Write-Host "Tablet screenshots: $tabletScreenshots"
Write-Host ""
Write-Host "APK files:"
Get-ChildItem $destDir
Write-Host ""
Write-Host "Phone screenshots:"
Get-ChildItem $phoneScreenshots
Write-Host ""
Write-Host "Tablet screenshots:"
Get-ChildItem $tabletScreenshots