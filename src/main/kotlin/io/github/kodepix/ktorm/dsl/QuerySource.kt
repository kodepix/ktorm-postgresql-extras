package io.github.kodepix.ktorm.dsl

import io.github.kodepix.ktorm.database.*
import org.ktorm.dsl.*
import org.ktorm.schema.*


/**
 * Wrap the specific table as a [QuerySource].
 *
 * Usage:
 *
 * ```kotlin
 * val Books = object : TableTimestamped("book") {
 *     val title = varchar("title")
 *     val some_ids = idArray("some_ids")
 *     val some_uuids = uuidArray("some_uuids").nullable
 * }
 *
 * from(Books)
 *     .select(Books.id, Books.title)
 *     .orderBy(Books.creation_timestamp.desc())
 *     .map { it[Books.id]!! to it[Books.title]!! }
 * ```
 *
 * @sample io.github.kodepix.ktorm.samples.fromSample
 */
@KtormDsl
fun from(table: BaseTable<*>) = db.from(table)
