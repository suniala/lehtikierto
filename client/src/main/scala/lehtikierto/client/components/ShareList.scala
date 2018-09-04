package lehtikierto.client.components

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import lehtikierto.client.components.Bootstrap._
import lehtikierto.shared.Share
import lehtikierto.client.services.UpdateShares

object ShareList {
  val ShareList = ScalaComponent.builder[ModelProxy[Pot[Seq[Share]]]]("ShareList")
    .render_P { proxy =>
      Panel(Panel.Props("Jakamani lehdet"),
        proxy().renderPending(_ => <.p("Ladataan...")),
        proxy().renderFailed(ex => <.p("Jaettujen lehtien lataaminen epäonnistui!")),
        proxy().renderReady(m => <.ul(m.toTagMod(
            Share => <.li(Share.magazine.name))))
      )
    }
    .componentDidMount(scope =>
      Callback.when(scope.props.value.isEmpty)(scope.props.dispatchCB(UpdateShares()))
    )
    .build

  def apply(proxy: ModelProxy[Pot[Seq[Share]]]) = ShareList(proxy)
}
