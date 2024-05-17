# How to contribute

Before we get started, thank you for considering contributing to `kotlinx.rpc`. It's awesome of you!

There are two ways you can contribute right now:

* Documentation
* Feedback/Issue reports

We don't include new code in this list right now, as the library is evolving rapidly, 
and we make a lot of changes, new features and other structure rearrangements. 
That makes it hard to provide proper attention to community pull requests, that provide new functionally.
As we go more stable over time, we will be open to any of your contributions.

Independently of how you'd like to contribute, please make sure you read and comply with the [Code of Conduct](CODE_OF_CONDUCT.md).

### Building the project

`kotlinx.rpc` is built with Gradle. 
Given it is multiplatform, you can build the library for the JVM, Native, and JavaScript.

To build the projects and produce the corresponding artifacts, use

`./gradlew assemble`

to run tests use

`./gradlew jvmTest` which runs all tests on the JVM. This is the minimum required for testing. If writing code
for other platforms, the corresponding tests for these should also be run. To see the list of tasks use

`./gradlew tasks`

To check your code for compliance with styleguide, use [Detekt](https://detekt.dev/) task:

`./gradlew detekt`

To check the binary compatibility of your changes, use [validator](https://github.com/Kotlin/binary-compatibility-validator) task:

`./gradlew apiCheck` or simply `./gradlew build`

To check code coverage of your changes, use [kover](https://github.com/Kotlin/kotlinx-kover/releases?page=2) task:

`./gradlew koverVerify`

For `kotlinx.rpc` to build correctly, 
a series of additional libraries/tools may need to be installed, based on the operating
system you're using for development:

**macOS**

If targeting macOS and/or iOS, install `Xcode` and `Xcode command line tools` on macOS.

#### Referencing artifacts locally

There are two ways to reference artifacts from the development `kotlinx.rpc` locally in another project, which is usually
used for debugging purposes. One of these is to publish to [Maven Local](https://docs.gradle.org/current/userguide/publishing_maven.html). The other
(and somewhat simpler), is to use the `includeBuild` functionality of Gradle. 
Reference the `kotlinx.rpc` project from your sample project
by adding the following line to your `settings.gradle(.kts)` file:

```groovy
    includeBuild("/PATH/TO/LIBRARY")
```

#### Importing into IntelliJ IDEA

To import into IntelliJ IDEA, open up the `kotlinx-rpc` project folder. 
IntelliJ IDEA should automatically detect that it is a Gradle project and import it. 
It's important that you make sure that all building and test operations
are delegated to Gradle under [Gradle Settings](https://www.jetbrains.com/help/idea/gradle-settings.html).

#### Building the documentation website

Our documentation is hosted at GitHub Pages: https://kotlin.github.io/kotlinx-rpc/

We use the [Writerside](https://www.jetbrains.com/writerside/) project to build the website.

The documentation project is located in the [pages](docs/pages) directory in the main repo.
To update the project, open this directory in your Writerside IDE 
and follow the [official guidelines](https://www.jetbrains.com/help/writerside/discover-writerside.html) 
on how to work with the project. 

### Pull Requests

Contributions are made using Github [pull requests](https://help.github.com/en/articles/about-pull-requests):

[//]: # (TODO change repo link to the repo)
1. Fork the `kotlinx.rpc` repository and work on your fork.
2. [Create](https://github.com/kotlin/kotlinx-rpc/compare) a new PR with a request to merge to the **main** branch. 
3. Ensure that the description is clear and refers to an existing ticket/bug if applicable. 
4. Make sure any code contributed is covered by tests and no existing tests are broken.

### Style guides

A few things to remember:

* Your code should conform to
  the official [Kotlin code style guide](https://kotlinlang.org/docs/reference/coding-conventions.html)
  except that star imports should be always enabled
  (ensure Preferences | Editor | Code Style | Kotlin, tab **Imports**, both `Use import with '*'` should be checked).
* Every new source file should have a copyright header.
* Every public API (including functions, classes, objects and so on) should be documented,
  every parameter, property, return types and exceptions should be described properly.
* A Public API that is not intended to be used by end-users that couldn't be made private/internal due to technical reasons,
  should be marked with `@InternalRPCApi` annotation.

### Commit messages

* Commit messages should be written in English
* They should be written in present tense using imperative mood ("Fix" instead of "Fixes", "Improve" instead of "Improved").
  Add the related bug reference to a commit message (bug number after a hash character between round braces).

See [How to Write a Git Commit Message](https://chris.beams.io/posts/git-commit/)

## Feedback/Issue Reports

Please use GitHub Issues of this project to submit issues, whether these are
bug reports or feature requests. Before doing so however, please take into consideration the following:

* Search for existing issues to avoid reporting duplicates.
* When submitting a bug report:
    * Test it against the most recently released version. It might have been already fixed.
    * Indicate the platform the issue relates to (JVM, Native, JavaScript), along with the operating system.
    * Include the code that reproduces the problem. Provide the complete reproducer code, yet minimize it as much as possible.
      If you'd like to write a unit test to reproduce the issue, even better. We love tests! However, don't be put off reporting any weird or rarely appearing issues just because you cannot consistently
      reproduce them.
    * If it's a behavioural bug, explain what behavior you've expected and what you've got.
* When submitting a feature request:
    * Explain why you need the feature &mdash; what's your use-case, what's your domain. Explaining the problem you face is more important than suggesting a solution.
      Report your problem even if you don't have any proposed solution. If there is an alternative way to do what you need, then show the code of the alternative.
      
