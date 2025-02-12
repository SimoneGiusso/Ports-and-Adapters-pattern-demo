FROM eclipse-temurin:21-jdk-alpine AS jre-build

COPY target/app.jar /app.jar

RUN <<EOF
jar xf app.jar
jdeps --ignore-missing-deps -q  \
    --recursive  \
    --multi-release 21  \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*'  \
    app.jar > required_dependencies.info
jlink --add-modules $(cat required_dependencies.info) \
    --strip-debug \
    --compress zip-6 \
    --no-header-files \
    --no-man-pages \
    --output /minimaljre
EOF

FROM alpine:3.21.3
ENV JAVA_HOME=/minimaljre
ENV PATH=$JAVA_HOME/bin:$PATH
COPY --from=jre-build /minimaljre /minimaljre
COPY --from=jre-build /app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]