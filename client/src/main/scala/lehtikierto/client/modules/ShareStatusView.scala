package lehtikierto.client.modules

import diode.data.Pot
import diode.react.ModelProxy
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import lehtikierto.client.services.UpdateShares
import lehtikierto.shared.{Share, ShareId}

/*
 * TODO: ideas for implementation:
 * 1. get share info via RefTo
 * 2. get status info via Async Virtual Collections
  */
object ShareStatusView {
  case class Props(id: ShareId, proxy: ModelProxy[Pot[Seq[Share]]])

  case class State()

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props): Callback =
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(UpdateShares()))

    def render(p: Props, s: State): VdomElement = {
      <.span("Hep!")
    }
  }

  private val component = ScalaComponent.builder[Props]("ShareStatusView")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(id: ShareId, proxy: ModelProxy[Pot[Seq[Share]]]): Unmounted[Props, State, Backend] =
    component(Props(id, proxy))
}
