import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._

trait ApiRoutes {
  val route = {
    pathPrefix(Segment) { str: String =>
      pathPrefix(LongNumber) { id: Long =>
        {
          pathEndOrSingleSlash {
            get {
              if (str == "users") {
                val temp = Actions.packUserId(id)
                if (temp._2) complete(temp._1)
                else complete(NotFound)
              } else {
                if (str == "locations") {
                  val temp = Actions.packLocationId(id)
                  if (temp._2) complete(temp._1)
                  else complete(NotFound)
                } else {
                  if (str == "visits") {
                    val temp = Actions.packVisitId(id)
                    if (temp._2) complete(temp._1)
                    else complete(NotFound)
                  } else complete(BadRequest)
                }
              }
            }
          } ~
            pathPrefix("visits") {
              parameters('fromDate.?, 'toDate.?, 'country.?, 'toDistance.?) {
                (toDistance, fromDate, toDate, country) =>
                  get {
                    if (str == "users") {
                      val temp = Actions.packVisitsById(toDistance,
                                                        fromDate,
                                                        toDate,
                                                        country,
                                                        id)
                      if (temp._2 == 0) {
                        complete(temp._1)
                      } else if (temp._2 == 1)
                        complete(BadRequest)
                      else if (temp._2 == 2)
                        complete(NotFound)
                      else
                        complete(BadRequest)
                    } else
                      complete(BadRequest)
                  }
              }
            } ~
            pathPrefix("avg") {
              parameters('fromDate.?,
                         'toDate.?,
                         'fromAge.?,
                         'toAge.?,
                         'gender.?) {
                (fromDate, toDate, fromAge, toAge, gender) =>
                  get {
                    if (str == "locations") {
                      val temp = Actions.packAvg(fromDate,
                                                 toDate,
                                                 fromAge,
                                                 toAge,
                                                 gender,
                                                 id)
                      if (temp != 0.0)
                        complete("{\"avg\": " + f"$temp%1.5f" + "}")
                      else complete(NotFound)
                    } else
                      complete(BadRequest)
                  }
              }
            } ~ {
            str match {
              case "users" =>
                post {
                  entity(as[String]) { json =>
                    {
                      val temp = Actions.updateUser(json, id)
                      if (temp == 0) complete("{}")
                      else if (temp == 1) complete(BadRequest)
                      else complete(NotFound)
                    }
                  }
                }

              case "locations" =>
                post {
                  entity(as[String]) { json =>
                    {
                      val temp = Actions.updateLocations(json, id)
                      if (temp == 0) complete("{}")
                      else if (temp == 1) complete(BadRequest)
                      else complete(NotFound)
                    }
                  }
                }
              case "visits" =>
                post {
                  entity(as[String]) { json =>
                    {
                      val temp = Actions.updateVisits(json, id)
                      if (temp == 0) complete("{}")
                      else if (temp == 1) complete(BadRequest)
                      else complete(NotFound)
                    }
                  }
                }
              case _ => {
                post {
                  complete(BadRequest)
                }
              }
            }
          }
        }

      } ~
        pathPrefix("new") {
          str match {
            case "users" =>
              post {
                entity(as[String]) { user =>
                  {
                    val temp = Actions.createUser(user)
                    if (temp == 0) complete("{}")
                    else complete(BadRequest)
                  }

                }
              }

            case "locations" =>
              post {
                entity(as[String]) { location =>
                  {
                    val temp = Actions.createLocation(location)
                    if (temp == 0) complete("{}")
                    else complete(BadRequest)
                  }
                }
              }
            case "visits" =>
              post {
                entity(as[String]) { visit =>
                  {
                    val temp = Actions.createVisit(visit)
                    if (temp == 0) complete("{}")
                    else complete(BadRequest)
                  }
                }
              }
            case _ => {
              post {
                complete(BadRequest)
              }
            }
          }
        }
    }
  }
}
