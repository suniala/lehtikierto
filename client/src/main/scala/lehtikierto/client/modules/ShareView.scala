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
import diode.NoAction

object ShareView {
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(proxy: ModelProxy[Pot[Seq[Magazine]]])

  case class State(magazine: Option[Magazine], year: Option[Int])

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(UpdateMagazines()))

    def render(p: Props, s: State) = {
      val proxy = p.proxy
      val years = Seq(2018, 2017) // TODO: calculate appropriate years

      def renderItem(item: Magazine) = {
        <.li(
          bss.listGroup.item,
          <.span(item.name),
          Button(Button.Props($.modState(s => s.copy(magazine = Some(item))), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Valitse"))
      }

      def renderYear(year: Int) = {
        <.li(
          bss.listGroup.item,
          <.span(year),
          Button(Button.Props($.modState(s => s.copy(year = Some(year))), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Valitse"))
      }

      <.div(
        SmartPanel(
          SmartPanel.Props(Some(1), s.magazine, s.magazine.fold("Mikä lehti?")(_.name)),
          proxy().renderPending(_ => <.p("Ladataan...")),
          proxy().renderFailed(ex => <.p("Lehtien lataaminen epäonnistui!")),
          proxy().renderReady(m => <.ul(bss.listGroup.listGroup)(m.toTagMod(renderItem)))),
        SmartPanel(
          SmartPanel.Props(s.magazine, s.year, s.year.fold("Mikä vuosi?")(_.toString())),
          <.ul(bss.listGroup.listGroup)(years.toTagMod(renderYear))))
    }
  }

  def showIfGiven(option: Option[_]): (VdomNode) => VdomNode =
    (child: VdomNode) => option match {
      case Some(_) => child
      case _       => VdomArray.empty()
    }

  val component = ScalaComponent.builder[Props]("ShareView")
    .initialState(State(None, None))
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(proxy: ModelProxy[Pot[Seq[Magazine]]]) = component(Props(proxy))
}

object SmartPanel {
  case class Props(prev: Option[_], curr: Option[_], heading: String, style: CommonStyle.Value = CommonStyle.default)

  val component = ScalaComponent.builder[Props]("SmartPanel")
    .renderPC((_, p, c) =>
      Panel(Panel.Props(p.heading, p.style), 
          if (p.prev.isDefined && !p.curr.isDefined) c
          else VdomArray.empty()))
    .build

  def apply(props: Props, children: VdomNode*) = component(props)(children: _*)
  def apply() = component
}
