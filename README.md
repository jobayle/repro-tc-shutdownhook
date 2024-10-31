# Reproduce a bug with TestContainers' Ryuk and Spring at shutdown

The problem is that when running IT with TestContainers, Spring's graceful shutdown is broken.

cause: TestContainers stopped too early

root cause: a JVM ShutDownHook terminates the Ryuk containers that stops all TestContainers

### Usage

Run `./gradlew test`

See how long the `ionShutdownHook` thread takes to run all shutdown actions.

### Reproduction

You cannot reproduce this issue with only one spring context, as the order of shutdown hooks seems to be good in that
scenario.

But as in most spring project, in integration tests, several Spring context are spawned:

* Using `@ActiveContext` (see [MachineController2IT](src/test/kotlin/bug/machine/MachineController2IT.kt))
* Using `@DirtiesContext` (see [MachineController3IT](src/test/kotlin/bug/machine/MachineController3IT.kt))
* Using property overrides (see [MachineController4IT](src/test/kotlin/bug/machine/MachineController4IT.kt))
* Using `@MockBean` (see [MachineController5IT](src/test/kotlin/bug/machine/MachineController5IT.kt))

For every new Spring context spawned, all shutdown hooks are duplicated (for each context to be gracefully stopped).

At shutdown, all these hooks are run sequentially.

Both HikariCP and the Redisson Redis client register shutdown hook, and the Redisson client has a Retry and RetryDelay
mechanism that worsen the problem, leading to shutdowns that are several minutes long.

### To investigate further

The issue disappears if we [configure the containers to be reused](https://java.testcontainers.org/features/reuse/).

In [TestContainerInitializer](src/test/kotlin/bug/integration/TestContainerInitializer.kt), add `.withReuse(true)` to
all containers.

export environment variable `TESTCONTAINERS_REUSE_ENABLE=true`

Rerun the test.
