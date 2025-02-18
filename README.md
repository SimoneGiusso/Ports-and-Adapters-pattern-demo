# Introduction

This is an attempt to apply the **ports and adapters pattern** (also known as Hexagonal Architecture) with Spring, Java, and
Maven using as reference the
book [Hexagonal Architecture explained](https://store7710079.company.site/Hexagonal-Architecture-Explained-p655931616) by Alistair
Cockburn (the inventor of this pattern) and Juan Manuel Garrido de Paz (reading suggested). This demo project has also been
inspired by Juan's [project](https://github.com/jmgarridopaz/bluezone).

# Instructions to run it

> ⚠️ local environment setup tested
>
> Java version: 21.0.2, vendor: Eclipse Adoptium, runtime: /Users/simonegiusso/.sdkman/candidates/java/21.0.2-tem
>
> Default locale: en_US, platform encoding: UTF-8
>
> OS name: "mac os x", version: "15.2", arch: "aarch64", family: "mac"

Compile, run tests, and create a jar file:

`mvn clean install`

Build the Docker image and start up a local environment:

`docker compose up --force-recreate --build -d`

> ⚠️ If the app is configured to connect to Cassandra, the startup may fail. That's because the Cassandra container needs time to
> set up the database and at the beginning it may reject the connection. The app is expecting to find an existing Cassandra
> database. However, you can wait around 1 minute and run the command `docker compose up app -d`

You can change the `PORTS_DRIVEN_TIMESERIES_SOURCE` in [docker-compose.yml](sandbox/docker-compose.yml) file to decide which
source you want to use! Accepted values are `postgres` and `cassandra`.

Run the app with postgres and then with cassandra. In both cases run
`curl -X GET http://localhost:8080/assets/US0378331005/max-price`. You should get different results (data is different in
cassandra and postgres on purpose).

# Concepts and project structure

Ports and adapters is a pattern where the goal is to separate the application from the external world via the so-called ports.
Interactions with external actors happen only via ports. An adapter can be used in the middle between the port and the external
actor.

The main benefit of this pattern is that changes to external systems don't impact the app, isolating the business logic from
external technologies. For example, if you want to build a generic app that can be deployed in different environments where the
source of specific data needed could be Oracle, Postgres, an API, etc., this pattern may be for you.

## The app

The app has very simple logic, just to show the application of the pattern. It exposes the following endpoint:

`/assets/{isin}/max-price`

The app, given an asset identifier (ISIN), reads the timeseries from a source (available sources for this project are Cassandra
and Postgres) via adapters, computes the max price (logic), and returns it.

![Hexagonal Architecture Demo Project.svg](doc-images/Hexagonal%20Architecture%20Demo%20Project.svg)
*The timeseries source can be changed from Postgres to Cassandra without impacting any code present in the app (hexagon)*

## Structure

The project is divided into submodules:

- **app**: contains the logic and the ports' definition. The naming convention for the port interfaces is the one suggested by the
  book, but you can use other conventions if you wish. A port can be:
    - **driven**: the app calls an external system to read/write data.
    - **driving**: an external actor (also called driving actor) calls the app to read/write data.
- **driven/driving-adapters** modules: contain one module per adapter. Adapters implement the ports.
- **configurator**: contains the logic and technology to instantiate the drivers and the app. This is also where the configuration
  is stored (e.g., connection details to the database).
- other secondary common utilities modules.

> ⚠️ Driven and driving modules can contains folder/packages rather than submodules. However you start to have less clear separation
> between adapters (e.g. pom start to contain more dependencies). But it could be an possible alternative.

### Motivations behind this structure

If you look at the [app's `pom.xml`](app/pom.xml), you can see that it doesn't depend on any specific technology (neither Postgres
nor Cassandra).

> ⚠️ You can see a dependency on `spring-context`. This package offers the Spring annotations for dependency injection. Although
> it
> would be possible to remove this dependency, it will bring a more complex configuration (e.g., creating explicit beans in
`configurator`) which is not worth IMO. If in the future another dependency injection framework is used, it won't take much time
> removing/replacing the current annotations. Everything will still work in the app modules (tests are not impacted). To make sure
> that only Spring components annotations are used from the `spring-context` package (in this case only the `@Service`
> annotation),
> an [ArchUnit](https://www.archunit.org) rule has been written.

Only unit tests are present in this module. Having one module per adapter makes it easy to add/remove an adapter. Suppose your
company moves away from Postgres to use Oracle. Which changes are needed? You can just delete the `timeseries-source-postgres`
module and create a `timeseries-source-oracle` one. You may also need to change the configuration in
the [application.yaml](configurator/src/main/resources/application.yaml) file and a test, but that's it!

Finally, the `configurator` module is necessary to isolate the app from the adapters. It starts up the app and injects the
adapters into it. However, there is no code that instantiates the objects as Spring is used as a dependency injection framework.
As you can see, the [pom](configurator/pom.xml) has runtime dependencies on the app and adapters! Spring Boot and
autoconfiguration are used. A web server is bootstrapped as the access to the app is done via a rest adapter. The
`application.yaml` contains the configurations for the driven actors. One of the things to take into consideration in this case is
that, since Spring autoconfiguration instantiates objects also based on what it finds in the classpath, in this situation, it will
create Cassandra and Postgres related objects. When the app uses only Postgres as a driven actor, this is a problem since
Cassandra autoconfiguration attempts to connect to the database causing failure (and anyway creating useless beans). For this
reason, a logic has been implemented which runs before the Spring context is ready, to disable autoconfiguration classes based on
the enabled technologies, analyzing the source field in the `application.yaml` (
see [SpringStartUp.java](configurator/src/main/java/org/simonegiusso/configurator/SpringStartUp.java)).

This is just a configuration proposal. Configuration may change case by case.

# Advantages and costs of the approach

- **Flexibility**: The architecture allows for easy integration of new technologies or external systems without any changes
  to the core application. It follows the **open-closed principle**.
- **Testability**: The separation of concerns makes it easier to write unit tests for the core business logic. Integration tests
  will be in the adapter modules (and startup tests in the `configurator`).
- **Maintainability**: The clear separation between the core logic and external dependencies makes the codebase easier to maintain
  and understand. Moreover, different teams can develop section of codes independently

There are some disadvantages as well:

- **Complexity** (for certain aspects): The additional layers and modules can introduce complexity, especially for smaller
  projects where such separation might be overkill.
- **Not optimize app-size**: Unless you don't want to re-compile the app, before deploying it, the app contains more things than
  needed. Suppose you want to deploy the app with Postgres as datasource. The app will still contain the Cassandra driver even if
  it is not used to be able to use Cassandra when restarting it with the `PORTS_DRIVEN_TIMESERIES_SOURCE` parameter.
- **Configuration Management**: A bit more configuration is needed than usual, but here Spring comes to help. For example, we use
  a property to decide which driven actor to use. Therefore in the adapter modules, we need to be sure to activate beans only if
  that property has a certain value (otherwise we may have bean conflicts). This is done by the `@ConditionalOnProperty`
  annotation.
  Therefore, managing configurations can become cumbersome
