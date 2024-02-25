import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion = "1.9.20"

	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
	kotlin("jvm") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
	kotlin("plugin.jpa") version kotlinVersion
	kotlin("kapt") version kotlinVersion
}

group = "me.lemphis"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.Embeddable")
	annotation("jakarta.persistence.MappedSuperclass")
}

noArg {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.Embeddable")
}

repositories {
	mavenCentral()
}

val asciidoctorExt: Configuration by configurations.creating
val snippetsDir by extra { file("build/generated-snippets") }
val srcDocsFilePath = "build/docs/asciidoc"
val destDocsFilePath = "build/resources/main/static/docs"
val copyDocumentTaskName = "copyDocument"
val jarName = "spring-template.jar"
val mysqlVersion = "8.0.29"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly("mysql:mysql-connector-java:$mysqlVersion")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
}

tasks.test {
	outputs.dir(snippetsDir)
}

tasks.asciidoctor {
	doFirst {
		delete {
			file(destDocsFilePath)
		}
	}
	dependsOn(tasks.test)
	inputs.dir(snippetsDir)
	configurations("asciidoctorExt")
	baseDirFollowsSourceFile()
}

val copyDocument = tasks.register<Copy>(copyDocumentTaskName) {
	dependsOn(tasks.asciidoctor)
	from(file(srcDocsFilePath))
	into(file(destDocsFilePath))
}

tasks.build {
	dependsOn(tasks.getByName(copyDocumentTaskName))
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.jar {
	enabled = false
}

tasks.bootJar {
	archiveFileName.set(jarName)
}
