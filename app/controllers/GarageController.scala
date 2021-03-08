package controllers

import DAO.MyGarageCollection
import models.{Car, CarForm}
import play.api.libs.json._
import play.api.mvc._
import models.JsonFormats._
import reactivemongo.bson.BSONObjectID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GarageController @Inject()(cc: ControllerComponents, myGarageCollection: MyGarageCollection, implicit val ec: ExecutionContext) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def displayCarForm = Action { implicit request =>
    Ok(views.html.addCarToGarage(CarForm.form))
  }

  def addCar() = Action { implicit request =>
    CarForm.form.bindFromRequest().fold({
      formWithErrors => BadRequest(views.html.addCarToGarage(formWithErrors))
    },
      widget => {myGarageCollection.create(widget); Ok(Json.toJson(widget))}
    )
  }

  def listCar() = Action async {
    myGarageCollection.readAll().map(result => Ok(Json.toJson(result)))
  }

  def updateCar(id: BSONObjectID) = Action.async(parse.json) {
    _.body.validate[Car].map { result =>
      myGarageCollection.update(id, result).map {
        case Some(feed) => Ok(Json.toJson(feed))
        case _ => NotFound
      }
    }.getOrElse(Future.successful(BadRequest("Invalid update")))
  }
}




