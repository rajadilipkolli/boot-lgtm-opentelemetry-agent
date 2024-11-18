@echo off

REM Exit immediately if a command fails
setlocal enabledelayedexpansion

REM Run Gradle clean and build
gradlew clean build || exit /b

REM Set environment variables
set OTEL_RESOURCE_ATTRIBUTES=service.name=coupon,service.instance.id=localhost:8080
set OTEL_LOGS_EXPORTER=otlp

REM Run the Java application with the required arguments
java -Dotel.metric.export.interval=500 -Dotel.bsp.schedule.delay=500 -javaagent:"./build/agent/opentelemetry-javaagent.jar" -jar ./build/libs/app.jar
