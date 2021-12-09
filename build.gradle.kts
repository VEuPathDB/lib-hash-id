plugins {
  kotlin("jvm") version "1.6.0"
  `maven-publish`
}

group = "org.veupathdb.lib"
version = "1.0.0"

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib"))

  testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
  testImplementation("io.mockk:mockk:1.12.1")
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}

kotlin {
  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(16))
  }
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
  this.requiredServices

  kotlinOptions {
    jvmTarget = "16"
    freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
  }
}

tasks.register("release", proguard.gradle.ProGuardTask::class.java) {
  dependsOn(":jar")

  configuration("bld/proguard-rules.pro")

  libraryjars(files(configurations.compileClasspath.get().files))

  injars("build/libs/spigot-block-compression-$version-all.jar")
  outjars("build/libs/spigot-block-compression-$version-release.jar")
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["kotlin"])

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