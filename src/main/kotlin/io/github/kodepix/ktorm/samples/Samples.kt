@file:Suppress("ktlint:standard:property-naming", "LocalVariableName", "unused", "UNUSED_VARIABLE")

package io.github.kodepix.ktorm.samples

import io.github.kodepix.*
import io.github.kodepix.ktorm.database.*
import io.github.kodepix.ktorm.dsl.*
import io.github.kodepix.ktorm.schema.*
import org.ktorm.dsl.*
import org.ktorm.schema.*


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


internal fun fromSample() {

    val Books = object : TableTimestamped("book") {
        val title = varchar("title")
        val some_ids = idArray("some_ids")
        val some_uuids = uuidArray("some_uuids").nullable
    }

    from(Books)
        .select(Books.id, Books.title)
        .orderBy(Books.creation_timestamp.desc())
        .map { it[Books.id]!! to it[Books.title]!! }
}


internal fun updateSample() {

    val Books = object : TableTimestamped("book") {
        val title = varchar("title")
        val some_ids = idArray("some_ids")
        val some_uuids = uuidArray("some_uuids")
    }

    update(Books) {
        set(it.title, "some title")
        where { it.id eq Id(123) }
    }
}


internal fun insertOrUpdateReturningSample() {

    val Books = object : TableTimestamped("book") {
        val title = varchar("title")
        val some_ids = idArray("some_ids")
        val some_uuids = uuidArray("some_uuids")
    }

    val bookId = insertOrUpdateReturning(Books, Books.id) {
        set(it.title, "some title")
        set(it.some_ids, arrayOf(Id(1), Id(2)))
        set(it.some_uuids, arrayOf(uuid(), uuid()))
        onConflict(it.some_ids) {
            set(it.some_ids, arrayOf(Id(3), Id(4)))
        }
    }
}


internal fun insertOrUpdateSample() {

    val Books = object : TableTimestamped("book") {
        val title = varchar("title")
    }

    val bookId = insertOrUpdate(Books) {
        set(it.title, "some title")
        onConflict { doNothing() }
    }
}


internal fun bulkInsertSample() {

    val Books = object : TableTimestamped("book") {
        val title = varchar("title")
    }

    bulkInsert(Books, listOf("some title 1", "some title 2")) { table, model ->
        {
            set(table.title, model)
        }
    }
}


internal fun bulkInsertOrUpdateSample() {

    val Books = object : TableTimestamped("book") {
        val title = varchar("title")
    }

    bulkInsertOrUpdate(
        table = Books,
        models = listOf("some title 1", "some title 2"),
        conf = { onConflict(it.title) { doNothing() } }
    ) { table, model ->
        {
            set(table.title, model)
        }
    }
}
