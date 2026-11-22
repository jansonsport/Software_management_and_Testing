#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p junit4 tools/jacoco

fetch() {
    local artifact="$1" destination="$2"
    [[ -s "$destination" ]] && return 0
    if curl --fail --silent --show-error --location --connect-timeout 10 \
        "https://repo.maven.apache.org/maven2/$artifact" --output "$destination.part" &&
        jar tf "$destination.part" > /dev/null; then
        mv "$destination.part" "$destination"
    else
        rm -f "$destination.part"
        echo "Could not download $destination from Maven Central." >&2
        return 1
    fi
}

fetch org/jacoco/org.jacoco.agent/0.8.16/org.jacoco.agent-0.8.16-runtime.jar tools/jacoco/jacocoagent.jar
fetch org/jacoco/org.jacoco.cli/0.8.16/org.jacoco.cli-0.8.16-nodeps.jar tools/jacoco/jacococli.jar
