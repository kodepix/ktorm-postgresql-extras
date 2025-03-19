package io.github.kodepix.ktorm.schema

import io.github.kodepix.*
import org.ktorm.schema.*
import java.sql.*
import java.sql.Types.*
import java.util.*


/**
 * Define a column typed of [IntSqlType].
 */
fun BaseTable<*>.id(name: String) = registerColumn(name, IdSqlType)

/**
 * [SqlType] implementation represents `int` SQL type.
 */
private object IdSqlType : SqlType<Id>(INTEGER, "int") {
    override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: Id) = ps.setInt(index, parameter.value)
    override fun doGetResult(rs: ResultSet, index: Int): Id = Id(rs.getInt(index))
}


/**
 * Define a column typed of [UUIDArraySqlType].
 */
fun BaseTable<*>.uuidArray(name: String) = registerColumn(name, UUIDArraySqlType)

/**
 * [SqlType] implementation represents `uuid[]` SQL type.
 */
private object UUIDArraySqlType : SqlType<UUIDArray>(OTHER, "uuid[]") {

    override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: UUIDArray) = ps.setObject(index, parameter)

    override fun doGetResult(rs: ResultSet, index: Int) = run {

        val sqlArray = rs.getArray(index)

        if (sqlArray != null)
            try {
                (sqlArray.array.cast() as Array<Any?>?)?.map { it as UUID }?.toTypedArray()
            } finally {
                sqlArray.free()
            }
        else
            null
    }
}

typealias UUIDArray = Array<UUID>


/**
 * Define a column typed of [IdArraySqlType].
 */
fun BaseTable<*>.idArray(name: String) = registerColumn(name, IdArraySqlType)

/**
 * [SqlType] implementation represents `int[]` SQL type.
 */
private object IdArraySqlType : SqlType<IdArray>(ARRAY, "int[]") {

    override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: IdArray) = ps.setObject(index, parameter.map(Id::value).toTypedArray())

    override fun doGetResult(rs: ResultSet, index: Int) = run {

        val sqlArray = rs.getArray(index)

        if (sqlArray != null)
            try {
                (sqlArray.array.cast() as Array<Any?>?)?.filterNotNull()?.map { Id(it as Int) }?.toTypedArray()
            } finally {
                sqlArray.free()
            }
        else
            null
    }
}

typealias IdArray = Array<Id>
