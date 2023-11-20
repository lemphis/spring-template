import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion = "1.7.22"

	id("org.springframework.boot") version "3.0.6"
	id("io.spring.dependency-management") version "1.1.0"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
	kotlin("jvm") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
	kotlin("plugin.jpa") version kotlinVersion
	kotlin("plugin.allopen") version kotlinVersion
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

val asciidoctorExt: Configuration by configurations.creating
val snippetsDir by extra { file("build/generated-snippets") }
val srcDocsFilePath = "build/docs/asciidoc"
val destDocsFilePath = "src/main/resources/static/docs"
val copyDocumentTaskName = "copyDocument"
val jarName = "template.jar"
val querydslVersion = "5.0.0"
val mysqlVersion = "8.0.28"

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("com.querydsl:querydsl-jpa:${querydslVersion}:jakarta")
	kapt("com.querydsl:querydsl-apt:${querydslVersion}:jakarta")
	runtimeOnly("mysql:mysql-connector-java:$mysqlVersion")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
}

tasks.test {
	outputs.dir(snippetsDir)
}

tasks.asciidoctor {
	inputs.dir(snippetsDir)
	configurations("asciidoctorExt")
	dependsOn(tasks.test)
	doFirst {
		delete {
			file(destDocsFilePath)
		}
	}
}

tasks.register(copyDocumentTaskName, Copy::class) {
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
