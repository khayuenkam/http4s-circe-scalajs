package org.github.khayuenkam

import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.raw.Event
import io.circe.parser.decode

object Main {
  def main(args: Array[String]): Unit = {
    val xhr = new XMLHttpRequest()

    xhr.open("GET", "/users")
    xhr.onload = { (_: Event) =>
      if (xhr.status == 200) {
        val usersEither = decode[List[User]] (xhr.responseText)
        usersEither.fold(
          error => println(error),
          users => users.foreach { user =>
            println(user.email)
            println(user.userName)
            println(user.name)
          }
        )
      }
    }
    xhr.send()
  }
}
