package lehtikierto.client

import diode.react.{ReactConnectProps, ReactConnectProxy}
import japgolly.scalajs.react.component.Generic
import japgolly.scalajs.react.extra.router.{BaseUrl, Redirect, Resolution, Router, RouterConfig, RouterConfigDsl, RouterCtl}
import japgolly.scalajs.react.vdom.html_<^
import japgolly.scalajs.react.vdom.html_<^._
import lehtikierto.client.modules.{Dashboard, ShareView}
import lehtikierto.client.services.{RootModel, SPACircuit}
import lehtikierto.shared.User
import org.scalajs.dom.html.Anchor

object Routes {
  sealed trait Loc
  case object DashboardLoc extends Loc
  case object ShareLoc extends Loc

  private val routerConfig: RouterConfig[Loc] = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._

    // wrap/connect components to the circuit
    (staticRoute(root, DashboardLoc) ~> renderR(ctl => SPACircuit.wrap((m: RootModel) => m)(proxy => Dashboard(ctl, proxy)))
      | staticRoute("#jaa", ShareLoc) ~> renderR(_ => SPACircuit.wrap((m: RootModel) => m.magazines)(proxy => ShareView(proxy)))
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
}
