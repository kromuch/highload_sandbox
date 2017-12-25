package model

case class User(id: Long,
                email: String,
                first_name: String,
                last_name: String,
                gender: String,
                birth_date: Long)
case class UserOptional(id: Option[Long],
                        email: Option[String],
                        first_name: Option[String],
                        last_name: Option[String],
                        gender: Option[String],
                        birth_date: Option[Long])

//class UserTable(tag: Tag) extends Table[User](tag, "User") {
//  val id = column[Long]("id")
//  val email = column[String]("email")
//  val firstName = column[String]("first_name")
//  val lastName = column[String]("last_name")
//  val gender = column[Char]("gender")
//  val birthDate = column[Long]("birth_date")
//
//  def * =
//    (id, email, firstName, lastName, gender, birthDate) <> (User.apply _ tupled, User.unapply)
//}
//object UserTable {
//  val table = TableQuery[UserTable]
//}
