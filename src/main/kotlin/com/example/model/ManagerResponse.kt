package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class ManagerResponse(
  val detail: Employee,
  val reportees: List<Employee>
)
