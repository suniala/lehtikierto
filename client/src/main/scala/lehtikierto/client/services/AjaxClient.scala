package lehtikierto.client.services

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

import org.scalajs.dom

import upickle.Js
import upickle.Reader
import upickle.Writer
import upickle.json

object AjaxClient extends autowire.Client[Js.Value, Reader, Writer] {
  override def doCall(req: Request): Future[Js.Value] = {
    println(req)
    dom.ext.Ajax.post(
      url = "/api/" + req.path.mkString("/"),
      headers = Map("Content-Type" -> "application/json"),
      data = json.write(Js.Obj(req.args.toSeq: _*))).map(_.responseText)
      .map(json.read)
  }

  def read[R: Reader](p: Js.Value): R = upickle.readJs[R](p)
  def write[R: Writer](r: R): Js.Value = upickle.writeJs(r)
}
