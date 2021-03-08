package controllers
import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }
import play.api.mvc.{ AbstractController, ControllerComponents }
import play.api.libs.json._
import reactivemongo.api.Cursor // Reactive Mongo imports
import play.modules.reactivemongo.{ // ReactiveMongo Play2 plugin
  MongoController,
  ReactiveMongoApi,
  ReactiveMongoComponents
}
import reactivemongo.play.json._, collection._ // BSON-JSON conversions/collection

class ApplicationUsingJsonReadersWriters @Inject() (
                                                     components: ControllerComponents,
                                                     val reactiveMongoApi: ReactiveMongoApi
                                                   ) extends AbstractController(components)
  with MongoController with ReactiveMongoComponents {

  implicit def ec: ExecutionContext = components.executionContext

  //Maps from BSON collection to JSON collection as a future
  def collection: Future[JSONCollection] = database.map(
    _.collection[JSONCollection]("Cars"))
  import models._
  import models.JsonFormats._

  def create = Action.async {
    val user = Car("Bmw", "M3", "2004", "EA04MXD")
    val futureResult = collection.flatMap(_.insert.one(user)) // insert the user
    futureResult.map(_ => Ok) // when the insert is performed, send a OK 200 result
  }

  def findByName(lastName: String) = Action.async {
    // let's do our query
    val cursor: Future[Cursor[Car]] = collection.map { // find all people with name `name`
      _.find(Json.obj("lastName" -> lastName)). // sort them by creation date
        sort(Json.obj("created" -> -1)). // perform the query and get a cursor of JsObject
        cursor[Car]()
    }
    // gather all the JsObjects in a list
    val futureUsersList: Future[List[Car]] =
      cursor.flatMap(_.collect[List](-1, Cursor.FailOnError[List[Car]]()))
    // everything's ok! Let's reply with the array
    futureUsersList.map { persons =>
      Ok(persons.toString)
    }
  }

}
