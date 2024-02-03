package com.example.data

import com.example.data.DatabaseSingleton.dbQuery
import com.example.data.Employee.managerId
import com.example.model.Employee as EmployeeDM
import com.example.model.ManagerResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class EmployeeDaoImpl : EmployeeDao {

  private fun resultToDm(row: ResultRow): EmployeeDM = EmployeeDM(
    id = row[Employee.id],
    name = row[Employee.name],
    managerId = row[Employee.managerId],
  )

  override suspend fun employee(): List<EmployeeDM> =
    dbQuery {
      Employee.selectAll().map {
        resultToDm(it)
      }
    }

  override suspend fun deleteAllEmployees(): Int =
    dbQuery { Employee.deleteAll() }

  override suspend fun employee(id: Int): EmployeeDM? =
    dbQuery {
      Employee.select { Employee.id eq id }.map { resultToDm(it) }
    }.singleOrNull()


  override suspend fun addEmployee(employeeDM: EmployeeDM): EmployeeDM? =
    dbQuery {
      Employee.insert {
        it[name] = employeeDM.name
        it[managerId] = employeeDM.managerId
      }
        .resultedValues
        ?.singleOrNull()
        ?.let { resultToDm(it) }
    }

  override suspend fun update(employeeDM: EmployeeDM): Boolean =
    dbQuery {
      Employee.update({ Employee.id eq employeeDM.id }) {
        it[name] = employeeDM.name
        it[managerId] = employeeDM.managerId
      } > 0
    }

  override suspend fun delete(id: Int): Boolean =
    dbQuery {
      Employee.deleteWhere { Employee.id eq id } >= 0 &&
        Employee.update({ managerId eq id }) {
          it[managerId] = null
        } >= 0
    }

  override suspend fun manager(id: Int): ManagerResponse {
    val managerEmployee = dbQuery {
      Employee.select { Employee.id eq id }.map { resultToDm(it) }
    }.single()

    val reportees = dbQuery {
      Employee.select { Employee.managerId eq id }.map { resultToDm(it) }
    }

    return ManagerResponse(managerEmployee, reportees)
  }

  override suspend fun updateManager(
    employeeId: Int,
    managerId: Int,
  ): Boolean =
    dbQuery {
      Employee.update({ Employee.id eq employeeId }) {
        it[Employee.managerId] = managerId
      } >= 0
    }
}

val employeeDao = EmployeeDaoImpl().also { dao ->
  CoroutineScope(Dispatchers.IO).launch{
    if (dao.employee().isEmpty())
      repeat(10_000) { dao.addEmployee(EmployeeDM(it, "Manik-$it")) }
  }
}
