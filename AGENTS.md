# Repository Guidelines

## Project Structure & Module Organization
This project uses a standard Maven layout. Core logic lives under `src/main/java/org/easyit/dictmaker`, with `component/` housing reusable processors (cutter, renderer, writers) and `checker/` holding validators. Input assets belong in `src/main/resources/novels`, while bundled reference dictionaries and PDFs sit in `src/main/resources/dicts` and `src/main/resources/pdfs`. Generated JSON dictionaries are written to `output/`; keep large artifacts out of version control. Reserve `src/test/java` for JUnit 5 coverage of each component.

## Build, Test, and Development Commands
- `mvn clean compile` — resolves dependencies and verifies the code compiles against Java 21.
- `mvn test` — runs the JUnit 5 suite; the GitHub Action relies on the same goal before archiving the `output/` folder.
- `mvn clean package` — produces a runnable target jar; follow with `java -cp target/classes org.easyit.dictmaker.Maker` after adjusting `Maker.main` or calling `Maker.makeDictLocally("<path>")` to create dictionaries.

## Coding Style & Naming Conventions
Follow the existing Java style: four-space indentation, braces on the same line, and `UpperCamelCase` for classes such as `WordCard`, with `lowerCamelCase` for methods and fields. Prefer package-scoped helpers inside `org.easyit.dictmaker.component` and keep each class focused on one transformation stage. UTF-8 strings and Jackson data models should remain immutable where possible; expose configuration via method parameters instead of hard-coded paths.

## Testing Guidelines
Write unit tests in `src/test/java`, mirroring the main package structure. Name files with a `*Test` suffix (e.g., `BranchCutterTest`) and isolate fixtures under `src/test/resources` when needed. Aim to cover sentence parsing, pruning, rendering, and file output behaviours; run `mvn test` locally before opening a pull request.

## Commit & Pull Request Guidelines
Use short, imperative commit subjects in lowercase, matching the existing history (`add README.md`, `remove html render`). Squash noisy work-in-progress commits before pushing. Pull requests should describe the change, list any new inputs/outputs placed in `resources` or `output`, link related issues, and attach sample command output or screenshots from the GUI when applicable.

## Automation & Outputs
The `Process Files` workflow (`.github/workflows/process.yml`) runs on pushes to `main` and manual dispatch. It sets up Temurin JDK 21, executes `mvn -B test`, and uploads the `output/` directory as an artifact. Ensure your changes keep the workflow green and that any expected artifacts are small enough for GitHub’s retention limits.
