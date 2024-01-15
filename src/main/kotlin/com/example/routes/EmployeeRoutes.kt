package com.example.routes

import com.example.model.Employee
import com.example.model.ManagerResponse
import com.example.model.UpdateManagerRequest
import com.example.model.employeeStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.employeeRouting() {

  route("employee/") {
    get {
      if (employeeStorage.isNotEmpty()) call.respond(employeeStorage)
      else call.respondText("No employee found", status = HttpStatusCode.OK)
    }

    get("{id?}") {
      val employeeId =
        call.parameters["id"]?.toInt() ?: return@get call.respondText(
          "Missing id",
          status = HttpStatusCode.BadRequest
        )

      val employee = employeeStorage.find { it.id == employeeId }
        ?: return@get call.respondText(
          "No employee with id $employeeId",
          status = HttpStatusCode.NotFound
        )
      call.respond(employee)
    }

    post("add/") {
      val employee = call.receive<Employee>()
      employeeStorage.add(employee)
      call.respondText(
        "Employee Added Successfully",
        status = HttpStatusCode.Created
      )
    }

    delete("{id?}") {
      val employeeId =
        call.parameters["id"]?.toInt() ?: return@delete call.respondText(
          "Missing id",
          status = HttpStatusCode.BadRequest
        )

      employeeStorage.replaceAll { employee ->
        if (employee.managerId == employeeId) employee.copy(managerId = null)
        else
          employee
      }

      val isRemoved = employeeStorage.removeIf { it.id == employeeId }
      if (isRemoved) {
        call.respondText(
          "Customer removed correctly",
          status = HttpStatusCode.Accepted
        )
      } else {
        call.respondText("Employee Not Found", status = HttpStatusCode.NotFound)
      }
    }

    post("updatemanager/") {
      val request = call.receive<UpdateManagerRequest>()

      val manager =
        employeeStorage.find { it.id == request.managerId }
      val employee =
        employeeStorage.find { it.id == request.employeeId }

      if (manager == null)
        return@post call.respondText(
          "Manager is not found",
          status = HttpStatusCode.BadRequest
        )


      if (employee == null)
        return@post call.respondText(
          "Employee is not found",
          status = HttpStatusCode.BadRequest
        )

      employeeStorage.replaceAll {
        if (it.id == request.employeeId) it.copy(managerId = request.managerId) else it
      }

      call.respondText(
        "${employee.name} mapped to ${manager.name}",
        status = HttpStatusCode.OK
      )
    }

    post ("manager/{id?}"){
      val managerId =
        call.parameters["id"]?.toInt() ?: return@post call.respondText(
          "Missing Manager id",
          status = HttpStatusCode.BadRequest
        )

      val manager =
        employeeStorage.find { it.id == managerId }
          ?: return@post call.respondText(
            "Manager is not found",
            status = HttpStatusCode.BadRequest
          )

      val reportees = employeeStorage.filter { it.managerId == managerId }

      call.respond(
        ManagerResponse(detail = manager, reportees = reportees)
      )
    }
  }
}