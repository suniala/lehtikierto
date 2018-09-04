package lehtikierto.shared

trait Api {
  def getUser(): Option[User]
  
  def getAllMagazines(): Seq[Magazine]
  
  def getSubscriptions(): Seq[Subscription]
  
  // message of the day
  def welcomeMsg(name: String): String

  // get Todo items
  def getAllTodos(): Seq[TodoItem]

  // update a Todo
  def updateTodo(item: TodoItem): Seq[TodoItem]

  // delete a Todo
  def deleteTodo(itemId: String): Seq[TodoItem]
}
