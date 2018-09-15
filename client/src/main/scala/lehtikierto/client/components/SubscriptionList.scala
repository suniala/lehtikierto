package lehtikierto.client.components

import diode.data.Pot
import diode.react.ReactPot._
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._
import lehtikierto.client.components.Bootstrap._
import lehtikierto.client.services.{DeleteSubscription, UpdateSubscriptions}
import lehtikierto.shared.Subscription
import scalacss.ScalaCssReact._

object SubscriptionList {
  @inline private def bss = GlobalStyles.bootstrapStyles

  private val SubscriptionList = ScalaComponent.builder[ModelProxy[Pot[Seq[Subscription]]]]("SubscriptionList")
    .render_P { proxy => {
        def renderItem(item: Subscription): VdomElement = {
          <.li(bss.listGroup.item,
            <.span(item.magazine.name),
            Button(Button.Props(proxy.dispatchCB(DeleteSubscription(item.id)), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Peru tilaus"))
        }

        Panel(
          Panel.Props("Tilaukseni"),
          proxy().renderPending(_ => <.p("Ladataan...")),
          proxy().renderFailed(_ => <.p("Tilauksien lataaminen epäonnistui!")),
          proxy().renderReady(m => <.ul(bss.listGroup.listGroup)(m.toTagMod(renderItem)))
        )
      }
    }
    .componentDidMount(scope =>
      Callback.when(scope.props.value.isEmpty)(scope.props.dispatchCB(UpdateSubscriptions())))
    .build

  def apply(proxy: ModelProxy[Pot[Seq[Subscription]]]): Unmounted[ModelProxy[Pot[Seq[Subscription]]], Unit, Unit] =
    SubscriptionList(proxy)
}
