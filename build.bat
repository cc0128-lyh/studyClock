@echo off
chcp 65001 >nul
title StudyClock Build

echo ========================================
echo  StudyClock - Full Build
echo ========================================
echo.

:: Step 1: Build backend
echo [1/4] Building backend JAR...
cd /d "%~dp0backend"
call mvn package -DskipTests -q
if %ERRORLEVEL% neq 0 (
  echo Backend build failed!
  exit /b 1
)
echo   Done.

:: Step 2: Build frontend
echo [2/4] Building frontend...
cd /d "%~dp0frontend"
call npx --no-install vite build
if %ERRORLEVEL% neq 0 (
  echo Frontend build failed!
  exit /b 1
)
echo   Done.

:: Step 3: Build JRE
echo [3/4] Building portable JRE...
call scripts\build-jre.bat
if %ERRORLEVEL% neq 0 (
  echo JRE build failed!
  exit /b 1
)
echo   Done.

:: Step 4: Package Electron
echo [4/4] Packaging Electron app...
call npx --no-install electron-builder
if %ERRORLEVEL% neq 0 (
  echo Packaging failed!
  exit /b 1
)

echo.
echo ========================================
echo  Build complete!
echo  Output: frontend\dist-electron\
echo ========================================
pause
