@echo off
setlocal
cd /d "%~dp0"
echo ================================================
echo Greenwood Eco School LMS - Local Verification
echo ================================================
echo.
where mvn >nul 2>nul
if errorlevel 1 (
  echo ERROR: Maven was not found on PATH.
  echo Install Maven, then run this file again.
  pause
  exit /b 1
)
echo [1/2] Running clean test suite...
call mvn clean test
if errorlevel 1 (
  echo.
  echo TESTS FAILED. Fix the first error shown above.
  pause
  exit /b 1
)
echo.
echo [2/2] Starting Spring Boot application...
call mvn spring-boot:run
exit /b %errorlevel%
