import java.io.File

import model._
import org.json4s.native.JsonMethods.{compact, render}

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

import scala.util.parsing.json.JSON

object Actions {
  var users = scala.collection.mutable.ArrayBuffer[User]()
  var locations = scala.collection.mutable.ArrayBuffer[Location]()
  var visits = scala.collection.mutable.ArrayBuffer[Visit]()
  val zip: String = "/tmp/data/data.zip"
  val out: String = "/home/out"
  //val out: String = "C:\\Users\\kromu\\Desktop\\data"
  def parseUsers(file: String): Unit = {
    val source = scala.io.Source
      .fromFile(file)("UTF-8")
      .mkString
    class CC[T] { def unapply(a: Any): Option[T] = Some(a.asInstanceOf[T]) }

    object M extends CC[Map[String, Any]]
    object L extends CC[List[Any]]
    object S extends CC[String]
    object T extends CC[String]
    object U extends CC[String]
    object G extends CC[String]
    object D extends CC[Double]
    object B extends CC[Double]

    for {
      Some(M(map)) <- List(JSON.parseFull(source))
      L(users_cat) = map("users")
      M(user) <- users_cat
      S(email) = user("email")
      T(first_name) = user("first_name")
      U(last_name) = user("last_name")
      G(gender) = user("gender")
      B(birth_date) = user("birth_date")
      D(id) = user("id")
    } yield {
      users += User(id.toLong,
                    email,
                    first_name,
                    last_name,
                    gender,
                    birth_date.toLong)
    }
  }
  def parseVisits(file: String): Unit = {
    val source = scala.io.Source
      .fromFile(file)("UTF-8")
      .mkString
    class CC[T] { def unapply(a: Any): Option[T] = Some(a.asInstanceOf[T]) }

    object M extends CC[Map[String, Any]]
    object L extends CC[List[Any]]
    object T extends CC[Double]
    object U extends CC[Double]
    object G extends CC[Double]
    object D extends CC[Double]
    object B extends CC[Double]

    for {
      Some(M(map)) <- List(JSON.parseFull(source))
      L(visit_cat) = map("visits")
      M(visit) <- visit_cat
      T(mark) = visit("mark")
      U(user) = visit("user")
      G(visited_at) = visit("visited_at")
      B(location) = visit("location")
      D(id) = visit("id")
    } yield {
      visits += Visit(id.toLong,
                      location.toLong,
                      user.toLong,
                      visited_at.toLong,
                      mark.toInt)
    }
  }
  def parseLocations(file: String): Unit = {
    val source = scala.io.Source
      .fromFile(file)("UTF-8")
      .mkString
    class CC[T] { def unapply(a: Any): Option[T] = Some(a.asInstanceOf[T]) }

    object M extends CC[Map[String, Any]]
    object L extends CC[List[Any]]
    object T extends CC[Double]
    object U extends CC[String]
    object G extends CC[String]
    object D extends CC[Double]
    object B extends CC[String]

    for {
      Some(M(map)) <- List(JSON.parseFull(source))
      L(location_cat) = map("locations")
      M(location) <- location_cat
      T(distance) = location("distance")
      U(city) = location("city")
      G(place) = location("place")
      B(country) = location("country")
      D(id) = location("id")
    } yield {
      locations += Location(distance.toLong, city, place, id.toLong, country)
    }
  }
  def fill() = {
    Unzip1.unZipIt(zip,out)
//    val uz = new Unzip1
//    uz.unZip(zip, out)
    val d = new File(out)
    for (fname <- d.listFiles.toList) fillCat(fname)
    println("UNPACKED!")
  }
  def fillCat(file: File) = {
    file.getName.split("_")(0) match {
      case "users" => {
        parseUsers(file.getPath)
      }
      case "visits" => {
        parseVisits(file.getPath)
      }
      case "locations" => {
        parseLocations(file.getPath)
      }
      case _ => print()
    }
  }

  def packUserId(id: Long): (String, Boolean) = {
    if (users.filter(_.id == id).nonEmpty) {
      val elem: User = users.filter(_.id == id).head
      val json =
        ("id" -> elem.id) ~
          ("email" -> elem.email) ~
          ("first_name" -> elem.first_name) ~
          ("last_name" -> elem.last_name) ~
          ("gender" -> elem.gender) ~
          ("birth_date" -> elem.birth_date)

      (compact(render(json)), true)
    } else {
      ("", false)
    }
  }
  def packLocationId(id: Long): (String, Boolean) = {
    if (locations.filter(_.id == id).nonEmpty) {
      val elem: Location = locations.filter(_.id == id).head
      val json =
        ("id" -> elem.id) ~
          ("place" -> elem.place) ~
          ("country" -> elem.country) ~
          ("city" -> elem.city) ~
          ("distance" -> elem.distance)

      (compact(render(json)), true)
    } else {
      ("", false)
    }
  }
  def packVisitId(id: Long): (String, Boolean) = {
    if (visits.filter(_.id == id).nonEmpty) {
      val elem: Visit = visits.filter(_.id == id).head
      val json =
        ("id" -> elem.id) ~
          ("location" -> elem.location) ~
          ("user" -> elem.user) ~
          ("visited_at" -> elem.visited_at) ~
          ("mark" -> elem.mark)

      (compact(render(json)), true)
    } else {
      ("", false)
    }
  }
  def packVisitsById(distance: Option[String],
                     fromDate: Option[String],
                     toDate: Option[String],
                     country: Option[String],
                     id: Long): (String, Int) = {
    // 0 - 200
    // 1 - 400
    // 2 - 404
    if (users.filter(_.id == id).nonEmpty) {
      var temp = visits.filter(_.id == id)
      if (distance != None) {
        if (distance.get.forall(_.isDigit)) temp = temp.filter { a =>
          {
            locations
              .filter(b => b.id == a.location)
              .head
              .distance < distance.get.toLong
          }
        } else return ("", 1)
      }
      if (fromDate != None) {
        if (fromDate.get.forall(_.isDigit)) temp = temp.filter { a =>
          a.visited_at > fromDate.get.toLong
        } else return ("", 1)
      }
      if (toDate != None) {
        if (toDate.get.forall(_.isDigit)) temp = temp.filter { a =>
          a.visited_at < toDate.get.toLong
        } else return ("", 1)
      }
      if (country != None)
        temp = temp.filter(
          a =>
            locations
              .filter(b => b.id == a.location)
              .head
              .country == country.get.toString) //TODO буде сипатись, якщо країни не існує
      if (temp.isEmpty) return ("{\"visits\": []}", 0)
      val tt: scala.collection.mutable.ArrayBuffer[(Int, Long, String)] =
        scala.collection.mutable.ArrayBuffer[(Int, Long, String)]()
      temp.map { a =>
        {
          val ts = locations.filter(b => b.id == a.location).head.place
          tt += ((a.mark, a.visited_at, ts))
          a
        }
      }
      val ttL = tt.toList
      val json = ("visits" ->
        ttL.map { w =>
          (("mark" -> w._1) ~
            ("visited_at" -> w._2) ~
            ("place" -> w._3))
        })
      (compact(render(json)), 0)
    } else ("", 2)
  }
  def packAvg(fromDate: Option[String],
              toDate: Option[String],
              fromAge: Option[String],
              toAge: Option[String],
              gender: Option[String],
              id: Long): Double = {
    if (locations.filter(_.id == id).nonEmpty) {
      val time0 = scala.io.Source
        .fromFile("/tmp/data/options.txt")
        .getLines()
        .toList
        .head
        .toLong
      var temp = visits.filter(_.location == id)
      if (fromDate != None) {
        if (fromDate.get.forall(_.isDigit)) temp = temp.filter { a =>
          a.visited_at > fromDate.get.toLong
        } else return 0
      }
      if (toDate != None) {
        if (toDate.get.forall(_.isDigit)) temp = temp.filter { a =>
          a.visited_at < toDate.get.toLong
        } else return 0
      }
      if (fromAge != None) {
        if (fromAge.get.forall(_.isDigit)) temp = temp.filter { a =>
          (time0 - users
            .filter(_.id == a.user)
            .head
            .birth_date)%31536000 > fromAge.get.toLong
        } else return 0
      }
      if (toAge != None) {
        if (toAge.get.forall(_.isDigit)) temp = temp.filter { a =>
          (time0 - users
            .filter(_.id == a.user)
            .head
            .birth_date)%31536000 < toAge.get.toLong
        } else return 0
      }
      if (gender != None) {
        if (gender.get == "m") {
          temp =
            temp.filter(a => users.filter(_.id == a.user).head.gender == "m")
        } else if (gender.get == "f") {
          temp =
            temp.filter(a => users.filter(_.id == a.user).head.gender == "f")
        } else return 0
      }
      temp.foldLeft(0.0)((zero, value) => zero + value.mark) / temp.size
    } else
      0.0
  }
  def updateUser(json: String, id: Long): Int = {
    //0 - 200
    //1 - 400
    //2 - 404
    if (users.filter(_.id == id).nonEmpty) {
      implicit val formats: DefaultFormats.type = DefaultFormats
      try {
        val newUser = parse(json).extract[UserOptional]
        val indexInArray = users.indexOf(users.filter(_.id == id).head)
        val oldUser = users(indexInArray)
        def newEmail =
          if (newUser.email != None) newUser.email.get
          else oldUser.email
        def newFN =
          if (newUser.first_name != None) newUser.first_name.get
          else oldUser.first_name
        def newLN =
          if (newUser.last_name != None) newUser.last_name.get
          else oldUser.last_name
        def newGender =
          if (newUser.gender != None) newUser.gender.get
          else oldUser.gender
        def newBD =
          if (newUser.birth_date != None) newUser.birth_date.get
          else oldUser.birth_date
        val userToUpdate =
          User(oldUser.id, newEmail, newFN, newLN, newGender, newBD)
        users(indexInArray) = userToUpdate
        0
      } catch {
        case _: Throwable => 1
      }
    } else {
      2
    }
  }
  def updateLocations(json: String, id: Long): Int = {
    //0 - 200
    //1 - 400
    //2 - 404
    if (locations.filter(_.id == id).nonEmpty) {
      implicit val formats: DefaultFormats.type = DefaultFormats
      try {
        val newLocation = parse(json).extract[LocationOptional]
        val indexInArray = locations.indexOf(locations.filter(_.id == id).head)
        val oldLocation = locations(indexInArray)
        def newPlace =
          if (newLocation.place != None) newLocation.place.get
          else oldLocation.place
        def newCountry =
          if (newLocation.country != None) newLocation.country.get
          else oldLocation.country
        def newCity =
          if (newLocation.city != None) newLocation.city.get
          else oldLocation.city
        def newDistance =
          if (newLocation.distance != None) newLocation.distance.get
          else oldLocation.distance
        val locationToUpdate =
          Location(newDistance, newCity, newPlace, oldLocation.id, newCountry)
        locations(indexInArray) = locationToUpdate
        0
      } catch {
        case _: Throwable => 1
      }
    } else {
      2
    }
  }

  def updateVisits(json: String, id: Long): Int = {
    //0 - 200
    //1 - 400
    //2 - 404
    if (visits.filter(_.id == id).nonEmpty) {
      try {
        implicit val formats: DefaultFormats.type = DefaultFormats
        val newVisit = parse(json).extract[VisitOptional]
        val indexInArray = visits.indexOf(visits.filter(_.id == id).head)
        val oldVisit = visits(indexInArray)
        def newLocation =
          if (newVisit.location != None) newVisit.location.get
          else oldVisit.location
        def newUser =
          if (newVisit.user != None) newVisit.user.get else oldVisit.user
        def newVisitedAt =
          if (newVisit.visited_at != None) newVisit.visited_at.get
          else oldVisit.visited_at
        def newMark =
          if (newVisit.mark != None) newVisit.mark.get else oldVisit.mark
        val visitToUpdate =
          Visit(oldVisit.id, newLocation, newUser, newVisitedAt, newMark)
        visits(indexInArray) = visitToUpdate
        0
      } catch {
        case _: Throwable => 1
      }
    } else {
      2
    }
  }

  def createUser(json: String): Int = {
    //0 - 200
    //1 - 400
    try {
      implicit val formats: DefaultFormats.type = DefaultFormats
      val newUser = parse(json).extract[User]
      if (users.filter(_.id == newUser.id).nonEmpty) 1
      else {
        users += newUser
        0
      }
    } catch {
      case _: Throwable => 1
    }
  }
  def createLocation(json: String): Int = {
    //0 - 200
    //1 - 400
    try {
      implicit val formats: DefaultFormats.type = DefaultFormats
      val newLocation = parse(json).extract[Location]
      if (locations.filter(_.id == newLocation.id).nonEmpty) 1
      else {
        locations += newLocation
        0
      }
    } catch {
      case _: Throwable => 1
    }
  }
  def createVisit(json: String): Int = {
    //0 - 200
    //1 - 400
    try {
      implicit val formats: DefaultFormats.type = DefaultFormats
      val newVisit = parse(json).extract[Visit]
      if (visits.filter(_.id == newVisit.id).nonEmpty) 1
      else {
        visits += newVisit
        0
      }
    } catch {
      case _: Throwable => 1
    }
  }
}
