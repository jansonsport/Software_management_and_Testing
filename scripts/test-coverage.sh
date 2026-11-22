#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

case "${1:-final}" in
    baseline) report="build/coverage-baseline" ;;
    final) report="Coverage" ;;
    *) echo "Usage: bash scripts/test-coverage.sh [baseline|final]" >&2; exit 2 ;;
esac

agent="tools/jacoco/offline/jacocoagent.jar"
cli="tools/jacoco/jacococli.jar"
if [[ -s "$cli" && -s tools/jacoco/jacocoagent.jar ]]; then
    agent="tools/jacoco/jacocoagent.jar"
fi

run_dir=$(mktemp -d "${TMPDIR:-/tmp}/shopping-coverage.XXXXXX")
trap 'rm -rf "$run_dir"' EXIT
mkdir -p "$run_dir/main" "$run_dir/test"

libraries='junit4/junit-4.13.2.jar:junit4/hamcrest-core-1.3.jar:junit4/mockito-all-1.9.5.jar'
javac -g -encoding UTF-8 -d "$run_dir/main" src/main/java/onlineshopping/*.java
javac -g -encoding UTF-8 -cp "$run_dir/main:$libraries" \
    -d "$run_dir/test" src/test/java/onlineshopping/*.java

tests=()
for source in src/test/java/onlineshopping/*Test.java; do
    name=${source##*/}
    if [[ "${1:-final}" == baseline && "$name" == StructuralCoverageTest.java ]]; then
        continue
    fi
    tests+=("onlineshopping.${name%.java}")
done

# Keep producing the report if JUnit returns a failure status.
test_status=0
java "-javaagent:$agent=destfile=$run_dir/jacoco.exec,append=false,includes=onlineshopping.*" \
    --add-opens=java.base/java.lang=ALL-UNNAMED \
    -cp "$run_dir/main:$run_dir/test:$libraries" \
    org.junit.runner.JUnitCore "${tests[@]}" || test_status=$?

if [[ "$agent" == tools/jacoco/jacocoagent.jar ]]; then
    java -jar "$cli" report "$run_dir/jacoco.exec" \
        --classfiles "$run_dir/main" --sourcefiles src/main/java \
        --html "$report" --name "Online shopping (${1:-final})"
else
    java -cp 'tools/jacoco/offline/*' scripts/JaCoCoCoverageReport.java \
        "$run_dir/jacoco.exec" "$run_dir/main" src/main/java "$report" "Online shopping (${1:-final})"
fi
echo "Coverage report: $report/index.html"
exit "$test_status"
