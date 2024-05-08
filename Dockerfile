# for local ci builds
FROM stl5/ktor-test-image
WORKDIR rpc/
COPY . .