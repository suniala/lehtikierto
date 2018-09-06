package lehtikierto.client.components

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scalacss.ScalaCssReact._
import lehtikierto.client.components.Bootstrap._
import lehtikierto.shared.Subscription
import lehtikierto.client.services.UpdateSubscriptions
import lehtikierto.client.services.DeleteSubscription

object SubscriptionList {
  @inline private def bss = GlobalStyles.bootstrapStyles

  val SubscriptionList = ScalaComponent.builder[ModelProxy[Pot[Seq[Subscription]]]]("SubscriptionList")
    .render_P { proxy => {
        def renderItem(item: Subscription) = {
          <.li(bss.listGroup.item,
            <.span(item.magazine.name),
            Button(Button.Props(proxy.dispatchCB(DeleteSubscription(item.id)), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Peru tilaus"))
        }

        Panel(
          Panel.Props("Tilaukseni"),
          proxy().renderPending(_ => <.p("Ladataan...")),
          proxy().renderFailed(ex => <.p("Tilauksien lataaminen epäonnistui!")),
          proxy().renderReady(m => <.ul(bss.listGroup.listGroup)(m.toTagMod(renderItem)))
        )
      }
    }
    .componentDidMount(scope =>
      Callback.when(scope.props.value.isEmpty)(scope.props.dispatchCB(UpdateSubscriptions())))
    .build

  def apply(proxy: ModelProxy[Pot[Seq[Subscription]]]) = SubscriptionList(proxy)
}
