plugins {
    java
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    "testImplementation"("org.junit.jupiter:junit-jupiter-api:5.5.1")
    "testImplementation"("org.junit.jupiter:junit-jupiter-params:5.5.1")
    "testImplementation"("org.assertj:assertj-core:3.13.0")
    "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:5.5.1")
}
