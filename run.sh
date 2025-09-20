#!/bin/bash

set -euo pipefail

# Check if we should skip the build step
if [ "$#" -gt 0 ] && [ "$1" = "--skip-build" ]; then
  echo "Skipping build step..."
else
  echo "Building the application..."
  ./gradlew clean build
fi

export OTEL_RESOURCE_ATTRIBUTES="service.name=coupon,service.instance.id=localhost:8080"
export OTEL_EXPORTER_OTLP_METRICS_DEFAULT_HISTOGRAM_AGGREGATION="explicit_bucket_histogram"
export OTEL_LOGS_EXPORTER="otlp"

java -Dotel.metric.export.interval=500 -Dotel.bsp.schedule.delay=500 -javaagent:"./build/agent/opentelemetry-javaagent.jar" -jar ./build/libs/app.jar
