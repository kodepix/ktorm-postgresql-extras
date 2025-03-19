package io.github.kodepix.ktorm.samples

import io.github.kodepix.ktorm.database.*


internal fun configureDatabaseSample() {

    configureDatabase {
        // Apply migrations like:
        // Flyway.configure().dataSource(it).load().migrate()
    }
}

internal fun configureDatabaseWithParamsSample() {

    configureDatabase(
        url = "jdbc:postgresql://localhost:5482/mydb",
        username = "postgres",
        password = "postgres"
    ) {
        // Apply migrations like:
        // Flyway.configure().dataSource(it).load().migrate()
    }
}
