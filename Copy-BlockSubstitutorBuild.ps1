param(
    [string]$BuildDir = "build\libs",
    [string]$TargetDir = "C:\Users\zxese\OneDrive\ModrinthApp\profiles\1.20.1 Mod Testing\mods"
)

# Resolve paths relative to the script location
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$buildPath = Join-Path $scriptDir $BuildDir
$targetPath = $TargetDir

if (-not (Test-Path $buildPath)) {
    Write-Error "Build directory not found: $buildPath"
    exit 1
}

if (-not (Test-Path $targetPath)) {
    Write-Host "Target directory does not exist. Creating: $targetPath"
    New-Item -ItemType Directory -Path $targetPath -Force | Out-Null
}

# Remove any existing blocksubstitutor jars in the target directory (both main and sources)
Get-ChildItem -Path $targetPath -Filter "blocksubstitutor-*.jar" -File -ErrorAction SilentlyContinue | ForEach-Object {
    Write-Host "Removing old file: $($_.FullName)"
    Remove-Item $_.FullName -Force
}

# Find built main jars (exclude -sources) matching blocksubstitutor-<version>.jar
$builtJars = Get-ChildItem -Path $buildPath -Filter "blocksubstitutor-*.jar" -File -ErrorAction SilentlyContinue |
    Where-Object { $_.Name -notlike "*-sources.jar" } |
    Sort-Object LastWriteTime -Descending

if (-not $builtJars -or $builtJars.Count -eq 0) {
    Write-Error "No built main JARs matching 'blocksubstitutor-*.jar' (excluding *-sources.jar) found in $buildPath"
    exit 1
}

$latestJar = $builtJars[0]

Write-Host "Copying '$($latestJar.FullName)' to '$targetPath'"
Copy-Item -Path $latestJar.FullName -Destination $targetPath -Force

Write-Host "Done."