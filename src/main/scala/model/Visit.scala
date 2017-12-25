package model

case class Visit(id: Long,
                 location: Long,
                 user: Long,
                 visited_at: Long,
                 mark: Int)
case class VisitOptional(id: Option[Long],
                 location: Option[Long],
                 user: Option[Long],
                 visited_at: Option[Long],
                 mark: Option[Int])
//class VisitTable(tag: Tag) extends Table[Visit](tag, "Visit") {
//  val id = column[Long]("id")
//  val location = column[Long]("location")
//  val user = column[Long]("user")
//  val visitedAt = column[Long]("visited_at")
//  val mark = column[Int]("mark")
//
//  def * =
//    (id, location, user, visitedAt, mark) <> (Visit.apply _ tupled, Visit.unapply)
//}
//object VisitTable {
//  val table = TableQuery[VisitTable]
//}
