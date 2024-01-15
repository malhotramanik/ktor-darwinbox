package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Employee(
  val id: Int,
  val name: String,
  val managerId: Int? = null,
)

val employeeStorage = mutableListOf<Employee>()