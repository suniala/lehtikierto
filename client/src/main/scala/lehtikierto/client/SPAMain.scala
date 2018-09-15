package lehtikierto.client

import lehtikierto.client.CssSettings._
import lehtikierto.client.components.GlobalStyles
import lehtikierto.client.logger._
import org.scalajs.dom
import scalacss.ScalaCssReact._

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("SPAMain")
object SPAMain extends js.JSApp {
  @JSExport
  def main(): Unit = {
    log.warn("Application starting")
    // send log messages also to the server
    log.enableServerLogging("/logging")
    log.info("This message goes to server as well")

    // create stylesheet
    GlobalStyles.addToDocument()
    // tell React to render the router in the document body
    Routes.router()().renderIntoDOM(dom.document.getElementById("root"))
  }
}
