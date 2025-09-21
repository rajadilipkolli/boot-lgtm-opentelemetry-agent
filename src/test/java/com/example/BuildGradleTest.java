package com.example;

/*
 Testing stack:
 - JUnit 5 (Jupiter) from spring-boot-starter-test
 - Gradle TestKit (org.gradle:gradle-test-kit) for functional checks of Gradle tasks
*/

import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class BuildGradleTest {

    private static Path locateBuildScript() {
        Path groovy = Paths.get("build.gradle");
        if (Files.exists(groovy)) return groovy;
        Path kts = Paths.get("build.gradle.kts");
        if (Files.exists(kts)) return kts;
        throw new IllegalStateException("build.gradle(.kts) not found at project root");
    }

    private static String readBuildScript() throws IOException {
        return Files.readString(locateBuildScript());
    }

    @Test
    @DisplayName("Plugins and Java toolchain are configured as expected")
    void pluginsAndToolchainConfigured() throws IOException {
        String s = readBuildScript();
        assertTrue(s.contains("id 'org.springframework.boot' version '4.0.0-M3'"),
                "Expected Spring Boot plugin 4.0.0-M3");
        assertTrue(s.contains("id 'io.spring.dependency-management' version '1.1.7'"),
                "Expected Dependency Management plugin 1.1.7");
        assertTrue(s.contains("id 'java'"), "Expected Java plugin");
        assertTrue(s.matches("(?s).*JavaLanguageVersion\\.of\\(25\\).*"),
                "Expected Java toolchain 25");
    }

    @Test
    @DisplayName("Group and version are set")
    void groupAndVersionConfigured() throws IOException {
        String s = readBuildScript();
        assertTrue(s.contains("group = 'dev.tpcoder'"), "Expected group 'dev.tpcoder'");
        assertTrue(s.contains("version = '0.0.1-SNAPSHOT'"), "Expected version '0.0.1-SNAPSHOT'");
    }

    @Test
    @DisplayName("Repositories include mavenCentral, Spring milestone and snapshot")
    void repositoriesConfigured() throws IOException {
        String s = readBuildScript();
        assertTrue(s.contains("mavenCentral()"), "Expected mavenCentral()");
        assertTrue(s.contains("https://repo.spring.io/milestone"),
                "Expected Spring milestone repository");
        assertTrue(s.contains("https://repo.spring.io/snapshot"),
                "Expected Spring snapshot repository");
    }

    @Test
    @DisplayName("Agent configuration and OpenTelemetry dependencies are present")
    void agentConfigurationAndDeps() throws IOException {
        String s = readBuildScript();
        assertTrue(s.matches("(?s).*configurations\\s*\\{\\s*agent\\s*\\}.*"),
                "Expected 'agent' configuration");
        assertTrue(s.contains("io.opentelemetry:opentelemetry-api"),
                "Expected OpenTelemetry API implementation");
        assertTrue(s.contains("io.opentelemetry.javaagent:opentelemetry-javaagent:2.20.0"),
                "Expected OpenTelemetry Java agent v2.20.0");
    }

    @Test
    @DisplayName("Spring Boot starters and test dependencies are present")
    void springStartersPresent() throws IOException {
        String s = readBuildScript();
        assertTrue(s.contains("spring-boot-starter-actuator"), "Expected actuator starter");
        assertTrue(s.contains("spring-boot-starter-data-jdbc"), "Expected data-jdbc starter");
        assertTrue(s.contains("spring-boot-starter-webmvc"), "Expected webmvc starter");
        assertTrue(s.contains("spring-boot-starter-test"), "Expected spring-boot-starter-test");
        assertTrue(s.contains("spring-boot-testcontainers"), "Expected spring-boot-testcontainers");
        assertTrue(s.contains("org.testcontainers:junit-jupiter"),
                "Expected Testcontainers JUnit Jupiter");
        assertTrue(s.contains("org.testcontainers:postgresql"),
                "Expected Testcontainers PostgreSQL");
        assertTrue(s.contains("org.junit.platform:junit-platform-launcher"),
                "Expected JUnit Platform launcher as testRuntimeOnly");
    }

    @Test
    @DisplayName("Development-only and runtime dependencies are present")
    void devOnlyAndRuntimeDepsPresent() throws IOException {
        String s = readBuildScript();
        assertTrue(s.contains("spring-boot-docker-compose"), "Expected developmentOnly docker-compose");
        assertTrue(s.contains("org.postgresql:postgresql"), "Expected runtimeOnly PostgreSQL driver");
        assertTrue(s.contains("io.micrometer:micrometer-registry-prometheus"),
                "Expected runtimeOnly Prometheus registry");
    }

    @Test
    @DisplayName("Dependency management imports expected OpenTelemetry BOMs")
    void dependencyManagementBoms() throws IOException {
        String s = readBuildScript();
        assertTrue(s.contains("io.opentelemetry:opentelemetry-bom:1.54.1"),
                "Expected opentelemetry-bom 1.54.1");
        assertTrue(s.contains("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.20.0"),
                "Expected instrumentation-bom 2.20.0");
    }

    @Test
    @DisplayName("Test task uses JUnit Platform")
    void testTaskUsesJUnitPlatform() throws IOException {
        String s = readBuildScript();
        assertTrue(s.contains("useJUnitPlatform()"),
                "Expected useJUnitPlatform() on 'test' task");
    }

    @Test
    @DisplayName("copyAgent task is registered with proper rename and destination")
    void copyAgentTaskRegistrationAndConfig() throws IOException {
        String s = readBuildScript();
        assertTrue(s.contains("tasks.register('copyAgent', Copy)"),
                "Expected copyAgent task registration");
        assertTrue(
            s.matches("(?s).*from\\s+configurations\\.agent\\s*\\{\\s*rename\\s+\\\"opentelemetry-javaagent-.*\\\\.jar\\\",\\s*\\\"opentelemetry-javaagent\\.jar\\\"\\s*}\\s*.*"),
            "Expected rename pattern for javaagent jar"
        );
        assertTrue(s.contains("into layout.buildDirectory.dir(\"agent\")"),
                "Expected copy destination to build/agent");
    }

    @Test
    @DisplayName("bootJar dependsOn(copyAgent) and sets archiveFileName to app.jar")
    void bootJarConfiguration() throws IOException {
        String s = readBuildScript();
        assertTrue(
                s.matches("(?s).*tasks\\.named\\('bootJar'\\)\\s*\\{[^}]*dependsOn\\(\\s*copyAgent\\s*\\)[^}]*\\}.*"),
                "Expected bootJar to depend on copyAgent");
        assertTrue(
                s.matches("(?s).*tasks\\.named\\('bootJar'\\)\\s*\\{[^}]*archiveFileName\\s*=\\s*\\\"app\\.jar\\\"[^}]*\\}.*"),
                "Expected bootJar archiveFileName to be \"app.jar\"");
    }

    @Test
    @DisplayName("Resolution strategy caches changing/dynamic modules for 0 seconds")
    void resolutionStrategyCaching() throws IOException {
        String s = readBuildScript();
        assertTrue(s.contains("resolutionStrategy.cacheChangingModulesFor 0, 'seconds'"),
                "Expected cacheChangingModulesFor 0 seconds");
        assertTrue(s.contains("resolutionStrategy.cacheDynamicVersionsFor 0, 'seconds'"),
                "Expected cacheDynamicVersionsFor 0 seconds");
    }

    @Test
    @DisplayName("Functional: copyAgent task renames javaagent jar correctly and leaves others intact")
    void copyAgentTaskRenames(@TempDir Path tempDir) throws IOException {
        // Arrange: minimal, isolated project that reuses the same copyAgent logic
        Path libs = tempDir.resolve("libs");
        Files.createDirectories(libs);
        Files.writeString(libs.resolve("opentelemetry-javaagent-2.20.0.jar"), "dummy");
        Files.writeString(libs.resolve("other-lib-1.0.jar"), "dummy");

        String minimalBuild = ""
            + "configurations { agent }\n"
            + "dependencies {\n"
            + "    agent files('libs/opentelemetry-javaagent-2.20.0.jar')\n"
            + "    agent files('libs/other-lib-1.0.jar')\n"
            + "}\n"
            + "tasks.register('copyAgent', Copy) {\n"
            + "    from configurations.agent {\n"
            + "        rename \"opentelemetry-javaagent-.*\\\\.jar\", \"opentelemetry-javaagent.jar\"\n"
            + "    }\n"
            + "    into layout.buildDirectory.dir(\"agent\")\n"
            + "}\n";

        Files.writeString(tempDir.resolve("build.gradle"), minimalBuild);

        // Act
        BuildResult result = GradleRunner.create()
            .withProjectDir(tempDir.toFile())
            .withArguments("copyAgent", "--stacktrace")
            .build();

        // Assert
        Path agentDir = tempDir.resolve("build").resolve("agent");
        assertTrue(Files.exists(agentDir.resolve("opentelemetry-javaagent.jar")),
                "Renamed javaagent jar should exist");
        assertTrue(Files.exists(agentDir.resolve("other-lib-1.0.jar")),
                "Non-matching jar should be copied without renaming");
        assertFalse(Files.exists(agentDir.resolve("opentelemetry-javaagent-2.20.0.jar")),
                "Original versioned agent jar should not remain after rename");
        assertTrue(result.getOutput().contains("BUILD SUCCESS"),
                "Gradle build should succeed");
    }
}