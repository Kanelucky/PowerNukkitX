plugins {
    id("java")
}

group = "org.powernukkitx.codegen"
version = "nightly-SNAPSHOT"

dependencies {
    implementation(project(":"))
    implementation(libs.javapoet)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
