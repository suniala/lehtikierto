package lehtikierto.client.modules

import diode.data.Pot
import diode.react.ModelProxy
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import lehtikierto.shared.{Share, ShareId}

object ShareStatusView {

//  case class Props(id: ShareId, proxy: ModelProxy[Pot[Seq[Share]]])
  case class Props(id: ShareId)

  case class State()

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props): Callback =
      Callback.empty
      //Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(UpdateShares()))

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
    component(Props(id))
  def apply(id: ShareId): Unmounted[Props, State, Backend] =
    component(Props(id))
}
