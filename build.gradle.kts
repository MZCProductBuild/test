
plugins {
	java
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"

	// jib
	id("com.google.cloud.tools.jib") version "3.4.5"

}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val ecrToken = System.getenv("ECR_TOKEN")

jib {


	from {
		image = "196648750246.dkr.ecr.ap-northeast-2.amazonaws.com/air-bss/base-image:latest"

		auth {
			username = "AWS"
			password = ecrToken

		}

	}
	to {
		image = "196648750246.dkr.ecr.ap-northeast-2.amazonaws.com/air-bss/test:${System.getenv("BUILD_VERSION") ?: "latest"}"

		auth {
			username = "AWS"
			password = ecrToken

		}
	}

	container {
		creationTime = "USE_CURRENT_TIMESTAMP"  // 이걸 넣어야 빌드시간이 제대로 찍힘

		jvmFlags =
			listOf(
				"-Dserver.port=8080",
				"-Dfile.encoding=UTF-8",
				"-Djava.awt.headless=true"
			)

		ports = listOf("8080")

		// format = "OCI"
		mainClass = "com.example.demo.TestApplication"

		environment = mapOf(
			"SERVER_PORT" to (System.getenv("SERVER_PORT") ?: "8080")
		)

		creationTime = "USE_CURRENT_TIMESTAMP"
	}
}
