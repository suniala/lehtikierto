package lehtikierto.client.modules

import diode.data.Pot
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import lehtikierto.client.Routes.Loc
import lehtikierto.client.components._
import lehtikierto.client.services.RootModel
import lehtikierto.shared.{Magazine, Share, Subscription}

object Dashboard {

  case class Props(router: RouterCtl[Loc], proxy: ModelProxy[RootModel])

  case class State(
      magazinesWrapper: ReactConnectProxy[Pot[Seq[Magazine]]],
      subscriptionsWrapper: ReactConnectProxy[Pot[Seq[Subscription]]],
      shareWrapper: ReactConnectProxy[Pot[Seq[Share]]])

  // create the React component for Dashboard
  private val component = ScalaComponent.builder[Props]("Dashboard")
    // create and store the connect proxy in state for later use
    .initialStateFromProps(props => State(
        props.proxy.connect(m => m.magazines),
        props.proxy.connect(m => m.subscriptions),
        props.proxy.connect(m => m.shares)
    ))
    .renderPS { (_, props, state) =>
      <.div(
        <.h2("Dashboard"),
        state.subscriptionsWrapper(SubscriptionList(_)),
        state.shareWrapper(ShareList(_)),
        state.magazinesWrapper(MagazineList(_))
      )
    }
    .build

  def apply(router: RouterCtl[Loc], proxy: ModelProxy[RootModel]): Unmounted[Props, State, Unit] =
    component(Props(router, proxy))
}
