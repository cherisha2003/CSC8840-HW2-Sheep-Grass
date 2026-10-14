#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
mkdir -p build
find framework Homework2026 -name '*.java' > sources.txt
javac -encoding UTF-8 -nowarn -d build @sources.txt 2> compile.log
echo "Compilation completed. Warnings (if any) are saved in compile.log."
SCENARIO=${1:-5}
EVENTS=${2:-3000}
java -cp build Homework2026.PredatorPrey.CKillariGiribabu.SheepGrassCellSpace "$SCENARIO" "$EVENTS"
