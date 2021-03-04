package controllers

import play.api.mvc._
import javax.inject._
import scala.concurrent.{ ExecutionContext, Future }

import play.api.Logger
import play.api.mvc.{ AbstractController, ControllerComponents }
import play.api.libs.json._

// Reactive Mongo imports
import reactivemongo.api.Cursor

// ReactiveMongo Play2 plugin
import play.modules.reactivemongo.{
  MongoController,
  ReactiveMongoApi,
  ReactiveMongoComponents
}

@Singleton
class AppController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index() = Action {
    implicit request: Request[AnyContent] =>
      Ok("App works!")
  }

}
