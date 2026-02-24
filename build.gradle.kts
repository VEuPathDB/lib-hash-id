import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin)
  alias(libs.plugins.dokka)
  `maven-publish`
}

group = "org.veupathdb.lib"
version = "1.2.1"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(libs.junit.api)
  testRuntimeOnly(libs.junit.engine)
  testRuntimeOnly(libs.junit.launcher)
  testImplementation(libs.mockk)
}

tasks.named<Test>("test") {
  useJUnitPlatform()
  testLogging {
    events.addAll(listOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
      org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
      org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
      org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR,
      org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED))

    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    showExceptions = true
    showCauses = true
    showStackTraces = true
    showStandardStreams = true
    enableAssertions = true
  }
}

java {
  targetCompatibility = JavaVersion.VERSION_17
  sourceCompatibility = JavaVersion.VERSION_17

  withSourcesJar()
  withJavadocJar()
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_17
  }
}

publishing {
  repositories {
    maven {
      name = "GitHub"
      url = uri("https://maven.pkg.github.com/veupathdb/lib-hash-id")
      credentials {
        username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
        password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
      }
    }
  }

  publications {
    create<MavenPublication>("maven") {
      from(components["java"])

      pom {
        name.set("HashID")
        description.set("128 bit ID based on the hash of a value.")
        url.set("https://github.com/VEuPathDB/lib-hash-id")

        scm {
          url.set("https://github.com/VEuPathDB/lib-hash-id.git")
        }

        developers {
          developer {
            name.set("Elizabeth Paige Harper")
            email.set("epharper@upenn.edu")
            url.set("https://github.com/Foxcapades")
            organization.set("VEuPathDB")
            organizationUrl.set("https://veupathdb.org")
          }
        }
      }
    }
  }
}