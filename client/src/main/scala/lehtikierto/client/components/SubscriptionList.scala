package lehtikierto.client.components

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import lehtikierto.client.components.Bootstrap._
import lehtikierto.shared.Subscription
import lehtikierto.client.services.UpdateSubscriptions
import lehtikierto.client.services.DeleteSubscription

object SubscriptionList {
  val SubscriptionList = ScalaComponent.builder[ModelProxy[Pot[Seq[Subscription]]]]("SubscriptionList")
    .render_P { proxy =>
      Panel(Panel.Props("Tilaukseni"),
        proxy().renderPending(_ => <.p("Ladataan...")),
        proxy().renderFailed(ex => <.p("Tilauksien lataaminen epäonnistui!")),
        proxy().renderReady(m => <.ul(m.toTagMod(
            subscription =>
              <.li(
                <.div(subscription.magazine.name),
                <.div(
                    Button(
                        Button.Props(proxy.dispatchCB(DeleteSubscription(subscription.id)), CommonStyle.danger),
                        "Peru tilaus"))))))
      )
    }
    .componentDidMount(scope =>
      Callback.when(scope.props.value.isEmpty)(scope.props.dispatchCB(UpdateSubscriptions()))
    )
    .build

  def apply(proxy: ModelProxy[Pot[Seq[Subscription]]]) = SubscriptionList(proxy)
}
