package DAO
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.{Cursor, ReadPreference}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import models._
import models.JsonFormats._
import reactivemongo.play.json._
import collection._
import reactivemongo.api.commands.{WriteResult}
import reactivemongo.bson.{BSONDocument, BSONObjectID} // BSON-JSON conversions/collection

class MyGarageCollection @Inject() (implicit ec: ExecutionContext, val reactiveMongoApi: ReactiveMongoApi){

  //Maps from BSON collection to JSON collection as a future
  def collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection[JSONCollection]("Cars"))

  def create(car: Car): Future[Unit] = {
    collection.flatMap(_.insert.one(car).map(_ => {})) // inserts a car
  }

  def readAll(): Future[Seq[Car]] = {
    collection.flatMap(_.find(BSONDocument())
      .cursor[Car](ReadPreference.primary)
      .collect[Seq](100, Cursor.FailOnError[Seq[Car]]()))
  }

  def readByRegistration(reg: String): Future[Option[Car]] = {
    collection.flatMap(_.find(BSONDocument("registration" -> reg)).one[Car])
  }

  def update( id: BSONObjectID, car: Car): Future[Option[Car]] = {
    collection.flatMap(_.findAndUpdate(BSONDocument("_id" -> id), BSONDocument(f"$$set" -> BSONDocument(
      "brand" -> car.brand,
      "model" -> car.model,
      "year" -> car.year),
      "registration" -> car.registration
    ),true
    )
    .map(_.result[Car])
    )
  }
}
