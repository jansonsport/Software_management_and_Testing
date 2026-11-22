JUnit test dependencies
=======================
JUnit 4.13.2, Hamcrest Core 1.3, and Mockito All 1.9.5 are already available
here. Mockito comes from the Week 4 solution project. Java 21 requires the
--add-opens setting already configured in the workspace for this version.
The older JUnit 4.11 jar is retained for reference.

Run individual tests from VS Code's Testing panel. From Tasks: Run Task,
choose Java: Baseline coverage or Java: Final coverage. Both work offline
using the local JaCoCo agent and standard JaCoCo HTML formatter.
Reports are written to build/coverage-baseline/index.html and
Coverage/index.html. An assertion failure still causes a nonzero exit
after the report is written.

Java: Download optional JaCoCo CLI installs the JaCoCo 0.8.16 CLI and agent
from Maven Central. The bundled 0.8.15 formatter already produces the standard
JaCoCo HTML report. No download or Maven installation is needed.

After reviewing the full run, use Java: Package test artefacts to build
build/test-artefacts.zip. Terminal equivalents from the project root:
  bash scripts/test-coverage.sh baseline
  bash scripts/test-coverage.sh final
  bash scripts/package-test-artefacts.sh
