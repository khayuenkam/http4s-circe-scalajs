package org.github.khayuenkam

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

case class User(email: String, userName: String, name: String)

object User {
  implicit val userEncoder: Encoder[User] = deriveEncoder[User]
  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
}