package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateManagerRequest(
  val employeeId: Int,
  val managerId:Int,
)
