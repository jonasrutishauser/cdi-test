# [CDI Test](https://jonasrutishauser.github.io/cdi-test/)

A JUnit 5 extension for easy and efficient testing of CDI components.

[![GNU Lesser General Public License, Version 3, 29 June 2007](https://img.shields.io/github/license/jonasrutishauser/cdi-test.svg?label=License)](http://www.gnu.org/licenses/lgpl-3.0.txt)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.jonasrutishauser/cdi-test-api.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.jonasrutishauser%22%20a%3A%22cdi-test-api%22)
[![Build Status](https://img.shields.io/github/actions/workflow/status/jonasrutishauser/cdi-test/ci.yml.svg?label=Build)](https://github.com/jonasrutishauser/cdi-test/actions)
[![Coverage](https://img.shields.io/codecov/c/github/jonasrutishauser/cdi-test/master.svg?label=Coverage)](https://codecov.io/gh/jonasrutishauser/cdi-test)

## Reimplementation of [guhilling/cdi-test](https://github.com/guhilling/cdi-test/)
This is more or less a reimplementation of [guhilling/cdi-test](https://github.com/guhilling/cdi-test/) with some small differences:

- Clean CDI Lifecycle:
    - `@TestScoped` is already active when CDI is bootstrapped
    - Use standard scope events like `@Initialized`
- `@ApplicationScoped` beans are not destroyed after each test
- `@ApplicationScoped` beans which need to be `@TestScoped` can be declared in a file `testScoped.beans`.
  Initialization using the `@Initialized` event will still work as long as the event type is `Object`.

## Why should you use it?

If you're developing  [Jakarta EE (Java EE)](https://jakarta.ee) or just plain [CDI](http://weld.cdi-spec.org)
applications or libraries you'll probably want to unit test your code. If you don't you _really_ should.
With cdi-test there's no excuse not to do it ;-)

As CDI doesn't come with any "standard" unit test capabilities you need some way to test your components in your
[JUnit 5](https://junit.org/junit5/) tests.

## Highlights

cdi-test is targeted at running unit, component and integration tests at scale. It accomplishes this with:
- Only booting the cdi container once for all unit tests. This allows for running a huge number of tests even 
  in big projects where booting might take some time.
- cdi-test uses [Weld (the cdi reference implementation)](http://weld.cdi-spec.org) as cdi container. So you can 
  use the exact same cdi container as in your application runtime in case you're running e.g.
  [Wildfly](https://www.wildfly.org),
  [JBoss EAP](https://www.redhat.com/en/technologies/jboss-middleware/application-platform),
  [GlassFish](https://javaee.github.io/glassfish/),
  [Oracle WebLogic](https://www.oracle.com/middleware/technologies/weblogic.html) or
  [Open Liberty](https://openliberty.io/).
- cdi-test supports mocks and test alternatives for CDI beans. These can be activated per test class. So you can 
  freely choose what you want to test and need to mock test-by-test.

## Compatibility

- CDI 3 and CDI 4 with the relevant releases of [Weld](http://weld.cdi-spec.org)
- Java 11+
- [Jakarta EE 9](https://jakarta.ee/release/9/)
  and [Jakarta EE 10](https://jakarta.ee/release/10/)
  - Includes using EJBs with some restrictions. Injection and creation of ``@Singleton`` and ``@Stateless`` ejbs.
  - Supports JPA even with multiple persistence units. Inject EntityManager via ``@PersistenceContext``.
- [JUnit 5](https://junit.org/junit5/)
- [Microprofile Config](https://github.com/eclipse/microprofile-config) 3.x
  - Supports injecting test specific properties using annotations.
- [Mockito](https://site.mockito.org) is supported.
