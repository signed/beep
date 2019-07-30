plugins {
    java
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
    testImplementation("org.assertj:assertj-core:3.13.0")
}
tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
