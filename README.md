# SGE-Risk
![GitHub Build Workflow Status](https://img.shields.io/github/actions/workflow/status/Entze/sge-risk/Java-CI.yaml?logo=github&style=for-the-badge)
![GitHub Manual Workflow Status](https://img.shields.io/github/actions/workflow/status/Entze/sge-risk/Manual-CI.yaml?color=lightgray&logo=github&style=for-the-badge)

![Gradle 7.6](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white&label=7.6) ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white&label=1.11)


SGE-Risk is a clone of the game "Risk" implemented in [SGE](https://github.com/Entze/Strategy-Game-Engine).

This program consists of a map generator and a library for agents to play the game.

## Installation

### Library

#### Gradle

##### Jitpack (no GitHub Account required)
Add the following to your `build.gradle`:

```build.gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation("com.github.Entze:sge-risk:v1.0.4")
}
```

##### GitHub Packages (GitHub Account required)
Add the following to your `build.gradle`:

```build.gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/Entze/sge-risk")
        credentials {
            username = project.findProperty("gpr.user") ?: findProperty("github.actor") ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") ?: findProperty("github.token") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("at.ac.tuwien.ifs.sge:sge-risk:1.0.4")
}
```

This *requires* that either `gpr.user` or `github.actor` are set in the gradle config or the environment variable
`GITHUB_ACTOR` is set (equivalently for the key/token).

Usually this can be done by adding a `gradle.properties` with the (unquoted) key value pairs like so:

```gradle.properties
github.actor=MyUserName
github.token=ghp_...
```


#### Maven
See the [GitHub packages](https://github.com/Entze/sge-risk/packages/) page for a guide how to add sge-risk as a
dependency in a maven project.

#### From Source
```bash
./gradlew jar shadowJar sourcesJar javadocJar
```

This produces four jar files (in `build/libs/`):

One executable

- `sge-risk-1.0.4-exe.jar` (Game-jar & Map Generator Executable)

And three jars usually used for development

- `sge-risk-1.0.4.jar` (Library)
- `sge-risk-1.0.4-sources.jar` (Sources)
- `sge-risk-1.0.4-javadoc.jar` (Documentation)

### Map Generator

#### Prebuilt Jar

Download the prebuilt jar from the GitHub Releases page. The jar is standalone and therefore it does not need to be
installed.

#### From Source

See [Installation>Library>From Source](#from-source).

## Usage

### Library
The javadoc provides documentation for all public methods. Otherwise, once the library is installed you should be able
to use the Library via the API.

There are also a general agent-guide and a ruleset available.

### Map Generator
The map generator compresses the whitespace in map strings.

To use:

```bash
java -jar sge-risk-1.0.4-exe.jar [MAPFILE] [TERRITORIESFILE]
```

The map file is an ascii image. `X`s represent the position of the troop indicator. The territories file lists the names
of the territories in order of their appearance in the map file (using the `X` as marker, left to right, top to bottom).

The result is printed on stdout.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[AGPL-3.0](https://choosealicense.com/licenses/agpl-3.0/)
