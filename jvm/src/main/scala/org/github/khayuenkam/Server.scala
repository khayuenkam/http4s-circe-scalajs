package org.github.khayuenkam

import java.util.concurrent.Executors

import cats.effect._
import cats.implicits._
import fs2.Stream
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.syntax.kleisli._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.circe._
import io.circe.syntax._

import scala.concurrent.ExecutionContext

object Server extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    HttpServer.serverStream[IO].compile.drain.as(ExitCode.Success)
}

object HttpServer {
  def serverStream[F[_]: ContextShift: ConcurrentEffect: Timer]: Stream[F, ExitCode] = {
    val blockingEc = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))

    BlazeServerBuilder[F]
      .bindHttp(port = 8080, host = "0.0.0.0")
      .withHttpApp(Routes[F](blockingEc).routes.orNotFound)
      .serve
  }
}

case class Routes[F[_]: Sync: ContextShift](blockingEc: ExecutionContext) extends Http4sDsl[F] {
  val users = List(
    User(email = "test@test.com", userName = "testUserName", name = "testName"),
    User(email = "abc@abc.com", userName = "abcUserName", name = "abcName")
  )

  val supportedStaticExtensions =
    List(".html", ".js", ".map", ".css", ".png", ".ico")

  val routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request @ GET -> Root / "index.html" =>
        StaticFile.fromResource("/index.html", blockingEc, Some(request)).getOrElseF(NotFound())
      case GET -> Root / "users" => Ok(users.asJson)
      case req if supportedStaticExtensions.exists(req.pathInfo.endsWith) =>
        StaticFile.fromResource[F](req.pathInfo, blockingEc, req.some).getOrElseF(NotFound())
    }
}