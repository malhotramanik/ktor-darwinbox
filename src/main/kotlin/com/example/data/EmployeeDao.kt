package com.example.data

import com.example.model.ManagerResponse
import com.example.model.Employee as EmployeeDM

interface EmployeeDao {

  suspend fun employee(): List<EmployeeDM>
  suspend fun employee(id: Int): EmployeeDM?
  suspend fun deleteAllEmployees(): Int
  suspend fun addEmployee(employeeDM: EmployeeDM): EmployeeDM?
  suspend fun update(employeeDM: EmployeeDM): Boolean
  suspend fun delete(id: Int): Boolean
  suspend fun manager(id: Int): ManagerResponse
  suspend fun updateManager(employeeId: Int, managerId: Int): Boolean

}