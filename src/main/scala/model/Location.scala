package model

case class Location(distance: Long,
                    city: String,
                    place: String,
                    id: Long,
                    country: String)
case class LocationOptional(distance: Option[Long],
                    city: Option[String],
                    place: Option[String],
                    id: Option[Long],
                    country: Option[String])
//class LocationTable(tag: Tag) extends Table[Location](tag, "Location") {
//  val id = column[Long]("id")
//  val place = column[String]("place")
//  val country = column[String]("country")
//  val city = column[String]("city")
//  val distance = column[Long]("distance")
//
//  def * =
//    (id, place, country, city, distance) <> (Location.apply _ tupled, Location.unapply)
//}
//object LocationTable {
//  val table = TableQuery[LocationTable]
//}
