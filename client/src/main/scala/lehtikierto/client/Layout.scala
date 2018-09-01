package lehtikierto.client

import diode.react.ModelProxy
import japgolly.scalajs.react.BackendScope
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.extra.router.Resolution
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.VdomElement
import lehtikierto.client.SPAMain.Loc
import lehtikierto.client.components.GlobalStyles
import lehtikierto.client.modules.MainMenu
import lehtikierto.client.services.SPACircuit
import lehtikierto.shared.User
import scalacss.ScalaCssReact.scalacssStyleaToTagMod

object Layout {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(router: RouterCtl[Loc], res: Resolution[Loc], userProxy: ModelProxy[Option[User]])

  val todoCountWrapper = SPACircuit.connect(_.todos.map(_.items.count(!_.completed)).toOption)

  private class Backend($: BackendScope[Props, Unit]) {
    // FIXME: dispatch a message to check if we have a user
    //def mounted(props: Props) =
      // dispatch a message to refresh the todos
      //Callback.when(props.proxy.value.isEmpty)(props.proxy.dispatchCB(RefreshTodos))

    def render(props: Props) = {
      if (props.userProxy().isDefined) {
        val user = props.userProxy().get
        
        <.div(
          // here we use plain Bootstrap class names as these are specific to the top level layout defined here
          <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
            <.div(^.className := "container",
              <.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", "Lehtikierto, " + user.username)),
              <.div(^.className := "collapse navbar-collapse",
                todoCountWrapper(todoProxy => MainMenu(props.router, props.res.page, todoProxy)),
                <.ul(bss.navbar.right)(<.li(<.a(^.href := "/logout", "kirjaudu ulos")))
              )
            )
          ),
          // currently active module is shown in this container
          <.div(^.className := "container", props.res.render())
        )
      } else {
        <.div("Kirjaudu ensin!")
      }
    }
  }

  private val component = ScalaComponent.builder[Props]("Layout")
    .renderBackend[Backend]
    //.componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(ctl: RouterCtl[Loc], res: Resolution[Loc], userProxy: ModelProxy[Option[User]]): VdomElement =
    component(Props(ctl, res, userProxy))
}