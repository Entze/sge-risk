default:
    @just --list

build:
    just build-typst
    just build-java

build-typst:
    make manual/RISK_AGENT_GUIDE.pdf manual/RULESET.pdf

build-java:
    ./gradlew shadowJar sourcesJar javadocJar

lint:
    just lint-all-typst

lint-all-typst:
    typstyle --check format-all manual/

lint-typst +FILES:
    typstyle --check {{ FILES }}

fix:
    just fix-all-just
    just fix-all-typst

fix-all-typst:
    typstyle --inplace format-all manual/

fix-typst +FILES:
    typstyle --inplace {{ FILES }}

fix-all-just:
    just --unstable --fmt

fix-just FILE:
    just --unstable --justfile {{ FILE }} --fmt
