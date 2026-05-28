@echo off
cd /d "%~dp0.."

set ELECTRON_MIRROR=https://npmmirror.com/mirrors/electron/
:: Disable code signing (personal app, no certificate needed)
set CSC_LINK=
set CSC_KEY_PASSWORD=
:: Use local cache dir to avoid cross-disk rename issues
if not exist "cache\electron" mkdir cache\electron
set ELECTRON_CACHE=%cd%\cache\electron

echo Building StudyClock package...
echo Using mirror: %ELECTRON_MIRROR%
pnpm exec electron-builder --win --x64
if %ERRORLEVEL% neq 0 (
  echo Packaging failed!
  pause
  exit /b %ERRORLEVEL%
)
echo Done! Output in dist-electron/
pause
