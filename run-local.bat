@echo off
setlocal
cd /d "%~dp0"
if "%GREENWOOD_DB_URL%"=="" set GREENWOOD_DB_URL=jdbc:mysql://localhost:3306/SmartCampus?createDatabaseIfNotExist=true^&useSSL=false^&serverTimezone=UTC
if "%GREENWOOD_ADMIN_USERNAME%"=="" set GREENWOOD_ADMIN_USERNAME=admin
if "%GREENWOOD_ADMIN_PASSWORD%"=="" set GREENWOOD_ADMIN_PASSWORD=Greenwood@12345
where mvn >nul 2>nul
if errorlevel 1 (
  echo Maven was not found. Install Maven and Java 17+ first.
  pause
  exit /b 1
)
call mvn spring-boot:run
