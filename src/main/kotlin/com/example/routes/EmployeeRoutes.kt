package com.example.routes

import com.example.data.employeeDao
import com.example.model.Employee
import com.example.model.ManagerResponse
import com.example.model.UpdateManagerRequest
import com.example.model.employeeStorage
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.Accepted
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.employeeRouting() {

  route("employee/") {
    get {
      employeeDao.employee().let {
        if (it.isEmpty())
          return@let call.respond(OK, "No employee found")

        call.respond(employeeDao.employee())
      }
    }

    delete {
      employeeDao.deleteAllEmployees().let {
        call.respond(OK, "Done")
      }
    }

    get("{id?}") {
      val employeeId =
        call.parameters["id"]?.toInt() ?: return@get call.respondText(
          "Missing id",
          status = HttpStatusCode.BadRequest
        )

      val employee = employeeDao.employee(employeeId)
        ?: return@get call.respond(
          status = NotFound,
          "No employee with id $employeeId",
        )
      call.respond(employee)
    }

    post {
      val employee = call.receive<Employee>()
      employeeDao.addEmployee(employee)
      call.respond(
        HttpStatusCode.Created,
        "Employee Added Successfully"
      )
    }

     put {
      val employee = call.receive<Employee>()
      employeeDao.update(employee)
      call.respond(
        Accepted,
        "Employee Updated Successfully"
      )
    }

    delete("{id?}") {
      val employeeId =
        call.parameters["id"]?.toInt() ?: return@delete call.respondText(
          "Missing id",
          status = HttpStatusCode.BadRequest
        )

      val isRemoved = employeeDao.delete(employeeId)
      if (isRemoved) call.respond(Accepted, "Customer removed correctly")
      else call.respond(NotFound, "Employee Not Found")
    }

    put ("manager/") {
      val request = call.receive<UpdateManagerRequest>()

      employeeDao.updateManager(request.employeeId, request.managerId)
      call.respond(OK, "${request.employeeId} mapped to ${request.managerId}")
    }

    get("manager/{id?}") {
      val managerId =
        call.parameters["id"]?.toInt() ?: return@get call.respondText(
          "Missing Manager id",
          status = HttpStatusCode.BadRequest
        )
      call.respond(employeeDao.manager(managerId))
    }
  }
}