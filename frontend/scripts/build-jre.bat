@echo off
setlocal enabledelayedexpansion

:: Use JAVA_HOME from environment, or set your JDK path here
if "%JAVA_HOME%"=="" (
  echo ERROR: JAVA_HOME is not set.
  echo Please set JAVA_HOME to your JDK 17+ installation path, e.g.:
  echo   set JAVA_HOME=C:\Program Files\Java\jdk-17
  exit /b 1
)

set JLINK="%JAVA_HOME%\bin\jlink.exe"
set OUTPUT=%~dp0..\jre

echo Using JAVA_HOME: %JAVA_HOME%

if not exist "%JAVA_HOME%\bin\jlink.exe" (
  echo ERROR: jlink not found at %JAVA_HOME%\bin\jlink.exe
  exit /b 1
)

echo Cleaning old JRE...
if exist "%OUTPUT%" rmdir /s /q "%OUTPUT%"

echo Building minimal JRE...
%JLINK% ^
  --add-modules java.base,java.sql,java.desktop,java.management,java.naming,java.logging,java.instrument,java.xml,jdk.unsupported,jdk.management,java.scripting,jdk.management.agent,java.security.jgss,java.security.sasl,jdk.security.auth,java.compiler,java.net.http,java.transaction.xa ^
  --output "%OUTPUT%" ^
  --strip-debug --compress=2 --no-header-files --no-man-pages
if %ERRORLEVEL% neq 0 (
  echo Jlink failed!
  exit /b 1
)

echo JRE built at %OUTPUT%
