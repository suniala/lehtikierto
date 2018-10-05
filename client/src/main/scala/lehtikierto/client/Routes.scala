package lehtikierto.client

import diode.react.{ReactConnectProps, ReactConnectProxy}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.Generic
import japgolly.scalajs.react.extra.router.{BaseUrl, Redirect, Resolution, Router, RouterConfig, RouterConfigDsl, RouterCtl}
import japgolly.scalajs.react.vdom.html_<^
import japgolly.scalajs.react.vdom.html_<^._
import lehtikierto.client.modules.{Dashboard, ShareStatusView, ShareView}
import lehtikierto.client.services.{RootModel, SPACircuit}
import lehtikierto.shared.{ShareId, User}
import org.scalajs.dom.html.Anchor
import japgolly.scalajs.react._

object Routes {
  sealed trait Loc
  case object DashboardLoc extends Loc
  case object ShareLoc extends Loc
  case class ShareStatusLoc(id: String) extends Loc
  case class ItemLoc(id: Int) extends Loc

  sealed trait Page
  case class ItemPage(id: Int) extends Loc

  sealed trait Module
  case class Module2(i: Int) extends Loc

  private val routerConfig: RouterConfig[Loc] = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._

    val itemLoc = ScalaComponent.builder[ItemLoc]("Item page")
      .render(p => <.div(s"Info for item #${p.props.id}"))
      .build
    val itemLoc2 = ScalaComponent.builder[Loc]("Item page2")
      .render(p => <.div(s"Info for item #${p.props}"))
      .build
    val itemPage = ScalaComponent.builder[Page]("Item page2")
      .render(p => <.div(s"Info for item #${p.props}"))
      .build

    def teeLoc() = {
      val a = "#item"
      val b = a / int.caseClass[ItemLoc]
      val c = dynamicRouteCT(b)
      val d0 = c ~> _
      val d = c ~> dynRender(a => itemLoc2(a))
    }

//    def teePage() = {
//      val a = "#item"
//      val b = a / int.caseClass[ItemPage]
//      val c = dynamicRouteCT(b)
//      val d0 = c ~> _
//      val d = c ~> dynRender(a => itemPage(a))
//    }


    // wrap/connect components to the circuit
    (staticRoute(root, DashboardLoc) ~> renderR(ctl => SPACircuit.wrap((m: RootModel) => m)(proxy => Dashboard(ctl, proxy)))
      | staticRoute("#jaa", ShareLoc) ~> renderR(_ => SPACircuit.wrap((m: RootModel) => m.magazines)(proxy => ShareView(proxy)))
      //| dynamicRouteCT("#item" / int.caseClass[ItemLoc]) ~> dynRender(a => itemLoc2(a))
      | dynamicRouteCT("two" / int.caseClass[Module2]) ~> dynRender(m => <.h3(s"Module #2 @ ${m.i}"))
      //| dynamicRouteCT("#jako" / string.caseClass[ShareStatusLoc]) ~> dynRender(itemPage(_))
      ).notFound(redirectToPage(DashboardLoc)(Redirect.Replace))
  }.renderWith(layout)

  private val userWrapper: ReactConnectProxy[Option[User]] = SPACircuit.connect(_.user.toOption)

  /**
    * Base layout for all pages.
    */
  private def layout(c: RouterCtl[Loc], r: Resolution[Loc]): Generic.UnmountedWithRoot[ReactConnectProps[Option[User]], _, _, _] = {
    userWrapper(userProxy => Layout(c, r, userProxy))
  }

  private val routerAndCtl = Router.componentAndCtl(BaseUrl.until_#, routerConfig)

  def router(): Router[Loc] = routerAndCtl._1

  def link(loc: Loc): html_<^.VdomTagOf[Anchor] = routerAndCtl._2.link(loc)

  def go(loc: Loc): Callback = routerAndCtl._2.set(loc)
}
