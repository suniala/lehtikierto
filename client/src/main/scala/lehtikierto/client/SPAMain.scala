package lehtikierto.client

import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom
import lehtikierto.client.components.GlobalStyles
import lehtikierto.client.logger._
import lehtikierto.client.modules._
import lehtikierto.client.services.SPACircuit

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import CssSettings._
import scalacss.ScalaCssReact._
import lehtikierto.client.services.RootModel

@JSExportTopLevel("SPAMain")
object SPAMain extends js.JSApp {

  // Define the locations (pages) used in this application
  sealed trait Loc

  case object DashboardLoc extends Loc

  case object ShareLoc extends Loc
  
  case object TodoLoc extends Loc

  // configure the router
  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._

    val todoWrapper = SPACircuit.connect(_.todos)
    // wrap/connect components to the circuit
    (staticRoute(root, DashboardLoc) ~> renderR(ctl => SPACircuit.wrap((m: RootModel) => m)(proxy => Dashboard(ctl, proxy)))
      | staticRoute("#jaa", ShareLoc) ~> renderR(ctl => ShareView())
      | staticRoute("#todo", TodoLoc) ~> renderR(ctl => todoWrapper(Todo(_)))
      ).notFound(redirectToPage(DashboardLoc)(Redirect.Replace))
  }.renderWith(layout)

  val userWrapper = SPACircuit.connect(_.user.toOption)
  
  // base layout for all pages
  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    userWrapper(userProxy => Layout(c, r, userProxy))
  }
  
  @JSExport
  def main(): Unit = {
    log.warn("Application starting")
    // send log messages also to the server
    log.enableServerLogging("/logging")
    log.info("This message goes to server as well")

    // create stylesheet
    GlobalStyles.addToDocument()
    // create the router
    val router = Router(BaseUrl.until_#, routerConfig)
    // tell React to render the router in the document body
    router().renderIntoDOM(dom.document.getElementById("root"))
  }
}
