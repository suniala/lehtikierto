package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import com.google.inject.Inject
import lehtikierto.shared.Api
import play.api.Configuration
import play.api.Environment
import play.api.mvc.{Action, AnyContent, Controller}
import services.ApiService
import upickle.Js

object Router extends autowire.Server[Js.Value, upickle.Reader, upickle.Writer] {
  def read[R: upickle.Reader](p: Js.Value): R = upickle.readJs[R](p)
  def write[R: upickle.Writer](r: R): Js.Value = upickle.writeJs(r)
}

class Application @Inject() (implicit val config: Configuration, env: Environment) extends Controller {
  val apiService = new ApiService()

  def index = Action {
    Ok(views.html.index("SPA tutorial"))
  }

  def autowireApi(path: String): Action[String] = Action.async(parse.tolerantText) {
    implicit request =>
      println(s"Request path: $path")

      // call Autowire route
      Router.route[Api](apiService)({
        val unpickledBody = upickle.json.read(request.body).asInstanceOf[Js.Obj].value.toMap
        val pathParts = path.split("/")
        autowire.Core.Request(pathParts, unpickledBody)
      }).map(resValue => {
        Ok(upickle.json.write(resValue)).as("application/json")
      })
  }

  def logging: Action[AnyContent] = Action(parse.anyContent) {
    implicit request =>
      request.body.asJson.foreach { msg =>
        println(s"CLIENT - $msg")
      }
      Ok("")
  }
}
