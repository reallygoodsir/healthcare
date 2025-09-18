@echo off
setlocal

:: === CONFIGURATION ===
set MAVEN_HOME=d:\dddd\apache-maven-3.9.9
set CATALINA_HOME=d:\apache-tomcat-9.0.98
set PROJECT_DIR=D:\workspaces\healthcare
set WAR_NAME=healthcare.war
set APP_NAME=healthcare

:: Add Maven to PATH if not already
set PATH=%MAVEN_HOME%;%PATH%

echo ===============================
echo  Step 1: Clean Maven target dir
echo ===============================
cd /d %PROJECT_DIR%
call mvn clean

echo ===============================
echo  Step 2: Package application
echo ===============================
call mvn package

:: Check if WAR exists
if not exist "%PROJECT_DIR%\target\%WAR_NAME%" (
    echo ERROR: WAR file not found: %PROJECT_DIR%\target\%WAR_NAME%
    exit /b 1
)

echo ===============================
echo  Step 3: Stop Tomcat if running
echo ===============================
call "%CATALINA_HOME%\bin\catalina.bat" stop
timeout /t 5 >nul

echo ===============================
echo  Step 4: Clean Tomcat work dir
echo ===============================
rmdir /s /q "%CATALINA_HOME%\work"

echo ===============================
echo  Step 5: Clean Tomcat logs dir
echo ===============================
rmdir /s /q "%CATALINA_HOME%\logs"

echo ===============================
echo  Step 6: Clean deployed app dir
echo ===============================
rmdir /s /q "%CATALINA_HOME%\webapps\%APP_NAME%"
mkdir "%CATALINA_HOME%\webapps\%APP_NAME%"

echo ===============================
echo  Step 7: Deploy new WAR
echo ===============================
tar -xf "%PROJECT_DIR%\target\%WAR_NAME%" -C "%CATALINA_HOME%\webapps\%APP_NAME%"

echo ===============================
echo  Step 8: Start Tomcat
echo ===============================
call "%CATALINA_HOME%\bin\catalina.bat" start

echo ===============================
echo  Deployment finished!
echo ===============================

endlocal
pause