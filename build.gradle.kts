plugins {
  kotlin("jvm") version "1.7.21"
  id("org.jetbrains.dokka") version "1.7.20"
  `maven-publish`
  `java-library`
}

group = "org.veupathdb.lib"
version = "1.1.0"

repositories {
  mavenCentral()
}

dependencies {
  dokkaJekyllPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.7.20")

  implementation(kotlin("stdlib"))

  testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
  testImplementation("io.mockk:mockk:1.13.2")
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
  targetCompatibility = JavaVersion.VERSION_1_8
  sourceCompatibility = JavaVersion.VERSION_1_8

  withSourcesJar()
  withJavadocJar()
}

kotlin {
  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(16))
  }
}

tasks.withType(PublishToMavenRepository::class.java).all {
  dependsOn(":release")
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
  this.requiredServices

  kotlinOptions {
    jvmTarget = "1.8"
    freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
  }
}

tasks.register("release", proguard.gradle.ProGuardTask::class.java) {
  dependsOn(":build")

  configuration("bld/proguard-rules.pro")

  libraryjars(files(configurations.compileClasspath.get().files))

  injars("build/libs/hash-id-${project.version}.jar")
  outjars("build/libs/hash-id-$version-release.jar")
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