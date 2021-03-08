package controllers

import DAO.MyGarageCollection
import javax.inject._
import play.api.mvc._

@Singleton
class HomeController @Inject()(cc: ControllerComponents, myGarageCollection: MyGarageCollection) extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index("It works"))
  }


}
