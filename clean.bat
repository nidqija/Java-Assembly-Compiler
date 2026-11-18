@echo off
setlocal

echo ================================================
echo Cleaning up build artifacts (*.class)...
echo ================================================

set COUNT=0
for /r %%f in (*.class) do (
    del /f /q "%%f"
    echo Removed: %%~nxf
    set /a COUNT+=1
)

echo ================================================
echo Done! Cleaned %COUNT% compiled file(s).
echo ================================================
endlocal
