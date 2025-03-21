package io.github.kodepix.ktorm.support.postgresql

import io.github.kodepix.ktorm.expression.*
import org.ktorm.database.*
import org.ktorm.expression.*
import org.ktorm.support.postgresql.*


/**
 * [PostgreSqlFormatter] implementation with extra functionality.
 *
 * Added processing of [BinaryExpression], [LowerExpression].
 */
open class PostgreSqlFormatterExtra(database: Database, beautifySql: Boolean, indentSize: Int) : PostgreSqlFormatter(database, beautifySql, indentSize) {

    override fun visitUnknown(expr: SqlExpression) = when (expr) {

        is BinaryExpression<*> -> {

            if (expr.left.removeBrackets)
                visit(expr.left)
            else {
                write("(")
                visit(expr.left)
                removeLastBlank()
                write(") ")
            }

            writeKeyword("${expr.type} ")

            if (expr.right.removeBrackets)
                visit(expr.right)
            else {
                write("(")
                visit(expr.right)
                removeLastBlank()
                write(") ")
            }

            expr
        }

        is LowerExpression -> {

            write("${expr.type}(")
            visit(expr.argument)
            removeLastBlank()
            write(") ")

            expr
        }

        else -> super.visitUnknown(expr)
    }
}
