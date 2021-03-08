package models
import play.api.data._
import play.api.data.Forms._

case class Car(brand: String, model: String, year: String, registration: String)

object CarForm{
  val form: Form[Car] = Form(
    mapping(
      "brand" -> text,
      "model" -> text,
      "year" -> text,
      "registration" -> text
    )(Car.apply)(Car.unapply)
  )
}

object JsonFormats {
  import play.api.libs.json.Json
  implicit val userFormat = Json.format[Car] // Generates Writes and Reads for Feed and User thanks to Json Macros
}

