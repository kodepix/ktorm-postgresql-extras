package io.github.kodepix.ktorm.database

import com.zaxxer.hikari.*
import io.github.kodepix.*
import io.github.kodepix.ktorm.support.postgresql.*
import org.ktorm.database.*
import javax.sql.*


/**
 * Connect to a database using a HikariDataSource. The parameters will be taken from the config file.
 *
 * Usage:
 *
 * ```kotlin
 * configureDatabase {
 *     // Apply migrations like:
 *     // Flyway.configure().dataSource(it).load().migrate()
 * }
 * ```
 *
 * @param onDataSourceConfigured callback for additional use of configured [DataSource].
 *
 * @sample io.github.kodepix.ktorm.samples.configureDatabaseSample
 */
fun configureDatabase(onDataSourceConfigured: (DataSource) -> Unit = {}) {
    val config by extractConfig<DataSourceConfig>()
    configureDatabase(
        url = config.datasource.url,
        username = config.datasource.username,
        password = config.datasource.password,
        onDataSourceConfigured = onDataSourceConfigured
    )
}

private data class DataSourceConfig(val datasource: DataSourceConnectionConfig) {
    data class DataSourceConnectionConfig(val url: String, val username: String, val password: String)
}


/**
 * Connect to a database using params and a HikariDataSource.
 *
 * Usage:
 *
 * ```kotlin
 * configureDatabase(
 *     url = "jdbc:postgresql://localhost:5482/mydb",
 *     username = "postgres",
 *     password = "postgres"
 * ) {
 *     // Apply migrations like:
 *     // Flyway.configure().dataSource(it).load().migrate()
 * }
 * ```
 *
 * @param url JDBC url
 * @param username the username
 * @param password the password
 * @param onDataSourceConfigured callback for additional use of configured [DataSource].
 *
 * @sample io.github.kodepix.ktorm.samples.configureDatabaseWithParamsSample
 */
fun configureDatabase(url: String, username: String, password: String, onDataSourceConfigured: (DataSource) -> Unit = {}) = runUntilSuccess {

    db = Database.connect(
        dataSource = HikariDataSource().also {
            it.jdbcUrl = url
            it.username = username
            it.password = password
            onDataSourceConfigured(it)
        },
        dialect = object : SqlDialect {
            override fun createSqlFormatter(database: Database, beautifySql: Boolean, indentSize: Int) = PostgreSqlFormatterExtra(database, beautifySql, indentSize)
        }
    )
}

internal lateinit var db: Database
