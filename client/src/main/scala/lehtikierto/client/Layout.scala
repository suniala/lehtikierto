package lehtikierto.client

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.{Resolution, RouterCtl}
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import lehtikierto.client.Routes.Loc
import lehtikierto.client.components.GlobalStyles
import lehtikierto.client.modules.MainMenu
import lehtikierto.client.services.FetchUser
import lehtikierto.shared.User
import scalacss.ScalaCssReact.scalacssStyleaToTagMod

object Layout {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(router: RouterCtl[Loc], res: Resolution[Loc], userProxy: ModelProxy[Option[User]])

  private class Backend($: BackendScope[Props, Unit]) {
    def mounted(props: Props): Callback =
      Callback.when(props.userProxy.value.isEmpty)(props.userProxy.dispatchCB(FetchUser))

    def render(props: Props): VdomElement = {
      if (props.userProxy().isDefined) {
        val user = props.userProxy().get
        
        <.div(
          // here we use plain Bootstrap class names as these are specific to the top level layout defined here
          <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
            <.div(^.className := "container",
              <.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", "Lehtikierto, " + user.username)),
              <.div(^.className := "collapse navbar-collapse",
                MainMenu(props.router, props.res.page),
                <.ul(bss.navbar, bss.navbarRight)(<.li(<.a(^.href := "/logout", "kirjaudu ulos")))
              )
            )
          ),
          // currently active module is shown in this container
          <.div(^.className := "container", props.res.render())
        )
      } else {
        // FIXME: login form
        // FIXME: distinguish between "no user yet", "fetching", "got user", "did not get user"
        <.div("Kirjaudu ensin!")
      }
    }
  }

  private val component = ScalaComponent.builder[Props]("Layout")
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(ctl: RouterCtl[Loc], res: Resolution[Loc], userProxy: ModelProxy[Option[User]]): VdomElement =
    component(Props(ctl, res, userProxy))
}