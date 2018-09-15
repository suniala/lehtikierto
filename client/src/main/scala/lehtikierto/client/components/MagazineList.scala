package lehtikierto.client.components

import diode.data.Pot
import diode.react.ReactPot._
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._
import lehtikierto.client.components.Bootstrap._
import lehtikierto.client.services.{AddSubscription, UpdateMagazines}
import lehtikierto.shared.Magazine

object MagazineList {
  private val MagazineList = ScalaComponent.builder[ModelProxy[Pot[Seq[Magazine]]]]("MagazineList")
    .render_P { proxy =>
      Panel(Panel.Props("Kaikki lehdet"),
        proxy().renderPending(_ => <.p("Ladataan...")),
        proxy().renderFailed(_ => <.p("Lehtien lataaminen epäonnistui!")),
        proxy().renderReady(m => <.ul(m.toTagMod(
            magazine =>
              <.li(
                <.div(magazine.name),
                <.div(
                    Button(
                        Button.Props(proxy.dispatchCB(AddSubscription(magazine.id)), CommonStyle.danger),
                        "Tilaa"))))))
      )
    }
    .componentDidMount(scope =>
      Callback.when(scope.props.value.isEmpty)(scope.props.dispatchCB(UpdateMagazines()))
    )
    .build

  def apply(proxy: ModelProxy[Pot[Seq[Magazine]]]): Unmounted[ModelProxy[Pot[Seq[Magazine]]], Unit, Unit] =
    MagazineList(proxy)
}
