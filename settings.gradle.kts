pluginManagement {
    plugins {
        kotlin("jvm") version "2.1.21"
    }
}
rootProject.name = "spring-boot-microservice"

include(
    "config-server",
    "eureka-server",
    "api-gateway",
    "book-service",
    "category-service")
