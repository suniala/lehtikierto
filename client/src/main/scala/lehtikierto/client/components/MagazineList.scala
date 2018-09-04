package lehtikierto.client.components

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import lehtikierto.client.components.Bootstrap._
import lehtikierto.shared.Magazine
import lehtikierto.client.services.UpdateMagazines

object MagazineList {
  val MagazineList = ScalaComponent.builder[ModelProxy[Pot[Seq[Magazine]]]]("MagazineList")
    .render_P { proxy =>
      Panel(Panel.Props("Kaikki lehdet"),
        proxy().renderPending(_ => <.p("Ladataan...")),
        proxy().renderFailed(ex => <.p("Lehtien lataaminen epäonnistui!")),
        proxy().renderReady(m => <.ul(m.toTagMod(
            magazine => <.li(magazine.name))))
      )
    }
    .componentDidMount(scope =>
      Callback.when(scope.props.value.isEmpty)(scope.props.dispatchCB(UpdateMagazines()))
    )
    .build

  def apply(proxy: ModelProxy[Pot[Seq[Magazine]]]) = MagazineList(proxy)
}
