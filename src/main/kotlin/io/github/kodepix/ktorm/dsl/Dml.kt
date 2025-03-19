@file:Suppress("unused")

package io.github.kodepix.ktorm.dsl

import io.github.kodepix.*
import io.github.kodepix.ktorm.database.*
import io.github.kodepix.ktorm.schema.*
import org.ktorm.database.*
import org.ktorm.dsl.*
import org.ktorm.schema.*
import org.ktorm.support.postgresql.*


/**
 * Construct an update expression in the given closure, then execute it and return the effected row count.
 *
 * Usage:
 *
 * ```kotlin
 * val Books = object : TableTimestamped("book") {
 *     val title = varchar("title")
 *     val some_ids = idArray("some_ids")
 *     val some_uuids = uuidArray("some_uuids")
 * }
 *
 * update(Books) {
 *     set(it.title, "some title")
 *     where { it.id eq Id(123) }
 * }
 * ```
 *
 * @param table the table to be updated
 * @param block the DSL block, an extension function of [UpdateStatementBuilder], used to construct the expression
 * @return the effected row count
 *
 * @sample io.github.kodepix.ktorm.samples.updateSample
 */
@KtormDsl
fun <T : BaseTable<*>> update(table: T, block: UpdateStatementBuilder.(T) -> Unit) = db.update(table, block)


/**
 * Insert a record to the table, determining if there is a key conflict while it's being inserted, automatically
 * performs an update if any conflict exists, and finally returns the specific column.
 *
 * By default, the column used in the `on conflict` statement is the primary key you already defined in the schema definition.
 *
 * Usage:
 *
 * ```kotlin
 * val Books = object : TableTimestamped("book") {
 *     val title = varchar("title")
 *     val some_ids = idArray("some_ids")
 *     val some_uuids = uuidArray("some_uuids")
 * }
 *
 * val bookId = insertOrUpdateReturning(Books, Books.id) {
 *     set(it.title, "some title")
 *     set(it.some_ids, arrayOf(Id(1), Id(2)))
 *     set(it.some_uuids, arrayOf(uuid(), uuid()))
 *     onConflict(it.some_ids) {
 *         set(it.some_ids, arrayOf(Id(3), Id(4)))
 *     }
 * }
 * ```
 *
 * Generated SQL:
 *
 * ```sql
 * insert into book (id, title, some_ids, some_uuids)
 * values (?, ?, ?, ?)
 * on conflict (some_ids) do update set some_ids = ?
 * returning id
 * ```
 *
 * @param table the table to be inserted
 * @param returning the column to return
 * @param block the DSL block used to construct the expression
 * @return the returning column's value
 *
 * @sample io.github.kodepix.ktorm.samples.insertOrUpdateReturningSample
 */
@KtormDsl
fun <T : BaseTable<*>, C : Any> insertOrUpdateReturning(table: T, returning: Column<C>, block: InsertOrUpdateStatementBuilder.(T) -> Unit) = db.insertOrUpdateReturning(table, returning, block)


/**
 * Insert a record to the table, determining if there is a key conflict while it's being inserted, and automatically
 * performs an update if any conflict exists.
 *
 * By default, the column used in the `on conflict` statement is the primary key you already defined in the schema definition.
 *
 * Usage:
 *
 * ```kotlin
 * val Books = object : TableTimestamped("book") {
 *     val title = varchar("title")
 * }
 *
 * val bookId = insertOrUpdate(Books) {
 *     set(it.title, "some title")
 *     onConflict { doNothing() }
 * }
 * ```
 *
 * Generated SQL:
 *
 * ```sql
 * insert into book (id, title)
 * values (?, ?)
 * on conflict do nothing
 * ```
 *
 * @param T
 * @param table the table to be inserted
 * @param block the DSL block used to construct the expression
 * @return the effected row count
 *
 * @sample io.github.kodepix.ktorm.samples.insertOrUpdateSample
 */
@KtormDsl
fun <T : BaseTable<*>> insertOrUpdate(table: T, block: InsertOrUpdateStatementBuilder.(T) -> Unit) = db.insertOrUpdate(table, block)


/**
 * Bulk insert records, if models list is not empty, to the table and return the effected row count.
 *
 * The usage is almost the same as [batchInsert], but this function is implemented by generating a special SQL
 * using PostgreSQL bulk insert syntax, instead of based on JDBC batch operations. For this reason, its performance
 * is much better than [batchInsert].
 *
 * Usage:
 *
 * ```kotlin
 * val Books = object : TableTimestamped("book") {
 *     val title = varchar("title")
 * }
 *
 * bulkInsert(Books, listOf("some title 1", "some title 2")) { table, model ->
 *     {
 *         set(table.title, model)
 *     }
 * }
 * ```
 *
 * Generated SQL is like:
 *
 * ```sql
 * insert into book (title) values (?), (?), (?)...
 * ```
 *
 * @param table the table to be inserted
 * @param models list of data models
 * @param function function applied to each model
 * @return the effected row count
 * @see batchInsert
 *
 * @sample io.github.kodepix.ktorm.samples.bulkInsertSample
 */
@KtormDsl
fun <TTable : BaseTable<*>, TModel> bulkInsert(
    table: TTable,
    models: Collection<TModel>,
    function: (TTable, TModel) -> AssignmentsBuilder.() -> Unit
) {
    if (models.isNotEmpty())
        db.bulkInsert(table) {
            models.forEach { model -> item(function(it, model)) }
        }
}


/**
 * Bulk insert records to the table, if models list is not empty, determining if there is a key conflict while inserting each of them,
 * and automatically performs updates if any conflict exists.
 *
 * By default, the column used in the `on conflict` statement is the primary key you already defined in the schema definition.
 *
 * Usage:
 *
 * ```kotlin
 * val Books = object : TableTimestamped("book") {
 *     val title = varchar("title")
 * }
 *
 * bulkInsertOrUpdate(
 *     table = Books,
 *     models = listOf("some title 1", "some title 2"),
 *     conf = { onConflict(it.title) { doNothing() } }
 * ) { table, model ->
 *     {
 *        set(table.title, model)
 *    }
 * }
 * ```
 *
 * Generated SQL:
 *
 * ```sql
 * insert into book (title)
 * values (?), (?)
 * on conflict do nothing
 * ```
 *
 * @param table the table to be inserted
 * @param models list of data models
 * @param conf configure statement
 * @param function function applied to each model
 * @return the effected row count
 *
 * @sample io.github.kodepix.ktorm.samples.bulkInsertOrUpdateSample
 */
@KtormDsl
fun <TTable : BaseTable<*>, TModel> bulkInsertOrUpdate(
    table: TTable,
    models: Collection<TModel>,
    conf: BulkInsertOrUpdateStatementBuilder<TTable>.(TTable) -> Unit,
    function: (TTable, TModel) -> AssignmentsBuilder.() -> Unit
) {
    if (models.isNotEmpty())
        db.bulkInsertOrUpdate(table) {
            models.forEach { model -> item(function(it, model)) }
            conf(this, it)
        }
}


/**
 * Delete the record in the [this] that matches the given [id].
 *
 * @param id primary key
 * @return the effected row count
 */
@KtormDsl
fun TableIdentified.delete(id: Id) = db.delete(this) { it.id eq id }


/**
 * Execute the specific callback function in a transaction and returns its result if the execution succeeds,
 * otherwise, if the execution fails, the transaction will be rollback.
 *
 * Note:
 *
 * - Any exceptions thrown in the callback function can trigger a rollback.
 * - This function is reentrant, so it can be called nested. However, the inner calls don’t open new transactions
 * but share the same ones with outers.
 * - Since version 3.3.0, the default isolation has changed to null (stands for the default isolation level of the
 * underlying datastore), not [TransactionIsolation.REPEATABLE_READ] anymore.
 *
 * @param isolation transaction isolation, null for the default isolation level of the underlying datastore
 * @param func the executed callback function
 * @return the result of the callback function
 */
@KtormDsl
fun <T> useTransaction(isolation: TransactionIsolation? = null, func: (Transaction) -> T) = db.useTransaction(isolation, func)

/**
 * Execute the specific callback function in a transaction with default isolation level of the underlying datastore and returns its result if the execution succeeds,
 * otherwise, if the execution fails, the transaction will be rollback.
 *
 * Note:
 *
 * - Any exceptions thrown in the callback function can trigger a rollback.
 * - This function is reentrant, so it can be called nested. However, the inner calls don’t open new transactions
 * but share the same ones with outers.
 *
 * @param func the executed callback function
 * @return the result of the callback function
 */
@KtormDsl
fun <T> useDefaultTransaction(func: () -> T) = useTransaction(null) { func() }


/**
 * Performs a select query with specific columns, and transforms resulting rows to model.
 *
 * Note that the specific columns can be empty, that means `select *` in SQL.
 *
 * @param columns columns for select
 * @param transform resulting transform
 */
@KtormDsl
fun <R> TableSimple.read(vararg columns: ColumnDeclaring<*>, transform: (row: QueryRowSet) -> R) = from(this)
    .select(*columns)
    .map(transform)


/**
 * Finds a row by [id], performs a select query with specific columns, and transforms resulting row to model.
 *
 * Note that the specific columns can be empty, that means `select *` in SQL.
 *
 * Returns `null` if row not found.
 *
 * @param id primary key
 * @param columns columns for select
 * @param transform resulting transform
 */
@KtormDsl
fun <R> TableIdentified.find(id: Id, vararg columns: ColumnDeclaring<*>, transform: (row: QueryRowSet) -> R) = from(this)
    .select(*columns)
    .where { TableIdentified::id.get(this) eq id }
    .map(transform)
    .singleOrNull()


/**
 * Simple nullable column indicator. Doesn't do anything.
 */
val <C : Any> Column<C>.nullable get() = this // nothing to do yet
