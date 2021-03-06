@REM Compile and run examples of the jBook (https://jbook.ponec.net)

set "MVN_SCRIPT=.\mvnw.cmd"
call "%MVN_SCRIPT%" clean install jetty:run -DskipTests

@REM EOF

