package lehtikierto.client.modules

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import lehtikierto.client.SPAMain.{DashboardLoc, Loc, ShareLoc}
import lehtikierto.client.components.Icon._
import lehtikierto.client.components._
import scalacss.ScalaCssReact._

object MainMenu {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(router: RouterCtl[Loc], currentLoc: Loc)

  private case class MenuItem(idx: Int, label: Props => VdomNode, icon: Icon, location: Loc)

  private val menuItems = Seq(
    MenuItem(1, _ => "Dashboard", Icon.dashboard, DashboardLoc),
    MenuItem(2, _ => "Jaa lehti", Icon.check, ShareLoc)
  )

  private class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props): VdomElement = {
      <.ul(bss.navbar)(
        // build a list of menu items
        menuItems.toVdomArray(item =>
          <.li(^.key := item.idx, (^.className := "active").when(props.currentLoc == item.location),
          props.router.link(item.location)(item.icon, " ", item.label(props))
        ))
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("MainMenu")
    .renderBackend[Backend]
    .build

  def apply(ctl: RouterCtl[Loc], currentLoc: Loc): VdomElement =
    component(Props(ctl, currentLoc))
}
