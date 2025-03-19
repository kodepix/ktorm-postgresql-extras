@file:Suppress("PropertyName")

package io.github.kodepix.ktorm.schema

import org.ktorm.schema.*
import java.time.*


/**
 * Base class of Ktorm's table objects with no fields configured.
 *
 * This class extends from [Table].
 *
 * @param tableName table's name
 * @param alias table's alias
 * @param schema table's schema
 */
abstract class TableSimple(tableName: String, alias: String? = null, schema: String? = null) : Table<Nothing>(tableName, alias, null, schema, Nothing::class)


/**
 * Base class of Ktorm's table objects with id ([Int]) primary key.
 *
 * This class extends from [TableSimple].
 *
 * @param tableName table's name
 * @param alias table's alias
 * @param schema table's schema
 */
abstract class TableIdentified(tableName: String, alias: String? = null, schema: String? = null) : TableSimple(tableName, alias, schema) {
    val id = id("id").primaryKey()
}


/**
 * Base class of Ktorm's table objects with fields:
 * - id ([Int]) primary key
 * - creation_timestamp ([LocalDateTime])
 *
 * This class extends from [TableIdentified].
 *
 * @param tableName table's name
 * @param alias table's alias
 * @param schema table's schema
 */
abstract class TableTimestamped(tableName: String, alias: String? = null, schema: String? = null) : TableIdentified(tableName, alias, schema) {
    val creation_timestamp = datetime("creation_timestamp")
}
