Write-Host "================================================" -ForegroundColor Cyan
Write-Host "Cleaning up build artifacts (*.class)..." -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan

$classFiles = Get-ChildItem -Path . -Recurse -Filter *.class -File

if ($classFiles.Count -eq 0) {
    Write-Host "No .class files found. Already clean!" -ForegroundColor Green
} else {
    foreach ($file in $classFiles) {
        Remove-Item -Path $file.FullName -Force
        Write-Host "Removed: $($file.Name)" -ForegroundColor Yellow
    }
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host "Done! Cleaned $($classFiles.Count) compiled file(s)." -ForegroundColor Green
}
