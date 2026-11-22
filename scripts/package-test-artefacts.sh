#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

if [[ ! -f Coverage/index.html ]]; then
    echo "Run Java: Final coverage to generate Coverage/index.html first." >&2
    exit 2
fi

archive="$PWD/build/test-artefacts.zip"
mkdir -p build
stage=$(mktemp -d "${TMPDIR:-/tmp}/shopping-artefacts.XXXXXX")
trap 'rm -rf "$stage"' EXIT
mkdir -p "$stage/Tests" "$stage/Coverage"
cp -R src/test/java/onlineshopping "$stage/Tests/"
cp -R Coverage/. "$stage/Coverage/"
cp Dependencies.txt "$stage/"
(
    cd "$stage"
    zip -q -r test-artefacts.zip Tests Coverage Dependencies.txt -x '*/.DS_Store'
)
mv "$stage/test-artefacts.zip" "$archive"
echo "Test artefacts: $archive"
