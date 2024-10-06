import java.util.Base64

plugins {
    alias(libs.plugins.jvm)
    `java-library`
    `maven-publish`
    `signing`
    id("tech.yanand.maven-central-publish") version "1.2.0"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    api(libs.commons.math3)
    implementation(libs.guava)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.javadoc)
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            
            artifact(tasks["javadocJar"])
            artifact(tasks["sourcesJar"])

            groupId = "io.github.hashmapsdata2value"
            artifactId = "hmd2v-lib"
            version = "1.0.0"

            pom {
                name.set("hmd2v-lib")
                description.set("hmd2v-lib")
                url.set("https://your.project.url")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("HashMapsdata2Value")
                        name.set("HMD2V")
                        email.set("yared@679labs.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/HashMapsData2Value/hmd2v-test-kt.git")
                    developerConnection.set("scm:git:ssh://github.com/HashMapsData2Value/hmd2v-test-kt.git")
                    url.set("https://github.com/HashMapsData2Value/hmd2v-test-kt")
                }
            }
        }
    }

    repositories {
        maven {
            name = "Local"
            url = uri(layout.buildDirectory.dir("repos/bundles").get().asFile.toURI())
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

val username = System.getenv("OSSHR_USERNAME") ?: ""
val password = System.getenv("OSSHR_PASSWORD") ?: ""

mavenCentral {
    repoDir = layout.buildDirectory.dir("repos/bundles")
    // Token for Publisher API calls obtained from Sonatype official,
    // it should be Base64 encoded of "username:password".
    authToken = Base64.getEncoder().encodeToString("$username:$password".toByteArray())
    // Whether the upload should be automatically published or not. Use 'USER_MANAGED' if you wish to do this manually.
    // This property is optional and defaults to 'AUTOMATIC'.
    publishingType = "AUTOMATIC"
    // Max wait time for status API to get 'PUBLISHING' or 'PUBLISHED' status when the publishing type is 'AUTOMATIC',
    // or additionally 'VALIDATED' when the publishing type is 'USER_MANAGED'.
    // This property is optional and defaults to 60 seconds.
    maxWait = 60
}