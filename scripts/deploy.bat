@echo off
setlocal

:: ==================== CONFIGURATION ====================
set MAVEN_HOME=d:\dddd\apache-maven-3.9.9
set PROJECT_DIR=D:\workspaces\healthcare
set WAR_NAME=healthcare.war
set APP_NAME=healthcare

if "%1"=="" (
    echo Usage: deploy.bat [dev ^| qa ^| prod]
    pause
    exit /b 1
)

set ENV=%1
set CATALINA_HOME=d:\environments\%ENV%\apache-tomcat-9.0.98

echo ===============================
echo  Deploying to %ENV% environment
echo ===============================

cd /d %PROJECT_DIR%
call mvn clean package

if not exist "%PROJECT_DIR%\target\%WAR_NAME%" (
    echo ERROR: WAR file not found!
    pause
    exit /b 1
)

echo Stopping Tomcat (if running)...
call "%CATALINA_HOME%\bin\catalina.bat" stop

:: Wait a bit and force kill if still running
timeout /t 3 >nul
taskkill /F /FI "WINDOWTITLE eq Tomcat" 2>nul

echo Cleaning old files...
rmdir /s /q "%CATALINA_HOME%\work" 2>nul
rmdir /s /q "%CATALINA_HOME%\logs" 2>nul
rmdir /s /q "%CATALINA_HOME%\webapps\%APP_NAME%" 2>nul
mkdir "%CATALINA_HOME%\webapps\%APP_NAME%"

echo Deploying new WAR...
tar -xf "%PROJECT_DIR%\target\%WAR_NAME%" -C "%CATALINA_HOME%\webapps\%APP_NAME%"

echo Starting Tomcat...
call "%CATALINA_HOME%\bin\catalina.bat" start

echo ===============================
echo  Deployment to %ENV% finished!
echo ===============================
pause
endlocal