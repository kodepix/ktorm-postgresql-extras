@file:Suppress("unused")

package io.github.kodepix.ktorm.expression

import io.github.kodepix.ktorm.expression.LowerExpressionType.*
import org.ktorm.expression.*
import org.ktorm.schema.*


/**
 * The lower function, translated to lower(column) in PostgreSQL.
 */
fun lower(value: String) = LowerExpression(LOWER, ArgumentExpression(value, VarcharSqlType), VarcharSqlType)

/**
 * The lower function, translated to lower(column) in PostgreSQL.
 */
fun lower(column: ColumnDeclaring<String>) = LowerExpression(LOWER, column.asExpression(), column.sqlType)


/**
 * Lower expression.
 *
 * @property type the expression's type
 * @property argument argument passed to the function
 */
data class LowerExpression(
    val type: LowerExpressionType,
    val argument: ScalarExpression<*>,
    override val sqlType: SqlType<String>,
    override val isLeafNode: Boolean = false,
    override val extraProperties: Map<String, Any> = emptyMap()
) : ScalarExpression<String>()

enum class LowerExpressionType(private val value: String) {

    /**
     * The lower function, translated to lower(column) in PostgreSQL.
     */
    LOWER("lower");

    override fun toString() = value
}
