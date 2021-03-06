#!/bin/sh
# Compile and run a demo project

set -e
cd "$(dirname $0)"

sh mvnw clean install jetty:run -DskipTests

# EOF
