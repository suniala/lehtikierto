package lehtikierto.client.modules

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import lehtikierto.client.components.Bootstrap._
import lehtikierto.client.components._
import lehtikierto.client.logger._
import lehtikierto.client.services._
import lehtikierto.shared._

import scalacss.ScalaCssReact._

object ShareView {
  case class Props()

  case class State()

  class Backend($: BackendScope[Props, State]) {
    def render(p: Props, s: State) =
      Panel(Panel.Props("Mikä lehti?"), <.div("Tähän!"))
  }

  val component = ScalaComponent.builder[Props]("ShareView")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply() = component(Props())
}
