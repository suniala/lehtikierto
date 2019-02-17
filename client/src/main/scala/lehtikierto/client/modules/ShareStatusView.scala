package lehtikierto.client.modules

import diode.data.Pot
import diode.react.ModelProxy
import diode.react.ReactPot._
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._
import lehtikierto.client.components.Bootstrap.Panel
import lehtikierto.client.services.{FetchShareStatus, ReceiveShareStatus}
import lehtikierto.shared.{ShareId, ShareStatus}

object ShareStatusView {

  case class Props(id: ShareId, proxy: ModelProxy[Pot[ShareStatus]])

  case class State()

  private val component = ScalaComponent.builder[Props]("ShareStatusView")
    .initialState(State())
    .render_P { props: Props =>
      Panel(Panel.Props("Message of the day"),
        props.proxy().renderPending(_ => <.p("Ladataan...")),
        props.proxy().renderFailed(_ => <.p("Jaettujen numeroiden lataaminen epäonnistui!")),
        props.proxy().renderReady(m => <.p("valmis: " + m.sampleStatusInfo))
      )
    }
    .componentDidMount(scope => {
      scope.props.proxy.dispatchCB(ReceiveShareStatus(None)) >>
        scope.props.proxy.dispatchCB(FetchShareStatus(scope.props.id))
    })
    .build

  def apply(id: ShareId, proxy: ModelProxy[Pot[ShareStatus]]): Unmounted[Props, State, Unit] =
    component(Props(id, proxy))
}
