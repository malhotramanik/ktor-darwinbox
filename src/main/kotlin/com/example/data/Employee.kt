package com.example.data

import org.jetbrains.exposed.sql.Table


object Employee : Table() {
  val id = integer("id").autoIncrement()
  val name = varchar("name", 128)
  val managerId = integer("managerId").nullable()

  override val primaryKey = PrimaryKey(id)
}