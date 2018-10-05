package lehtikierto.client.modules

import diode.data.Pot
import diode.react.ModelProxy
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._
import lehtikierto.client.modules.ShareView.{Backend, Props, State}
import lehtikierto.client.services.{UpdateMagazines, UpdateShares}
import lehtikierto.shared.{Magazine, Share}

object ShareStatusView {

  case class Props(proxy: ModelProxy[Pot[Seq[Share]]])

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

  def apply(proxy: ModelProxy[Pot[Seq[Share]]]): Unmounted[Props, State, Backend] = component(Props(proxy))
}
