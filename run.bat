@echo off

REM Exit immediately if a command fails
setlocal enabledelayedexpansion

REM Check if we should skip the build step
if "%1"=="--skip-build" (
  echo Skipping build step...
) else (
  echo Building the application...
  gradlew clean build || exit /b
)

REM Set environment variables
set OTEL_RESOURCE_ATTRIBUTES=service.name=coupon,service.instance.id=localhost:8080
set OTEL_LOGS_EXPORTER=otlp

REM Run the Java application with the required arguments
echo Starting application with OpenTelemetry agent...
java -Dotel.metric.export.interval=500 -Dotel.bsp.schedule.delay=500 -javaagent:"./build/agent/opentelemetry-javaagent.jar" -jar ./build/libs/app.jar
