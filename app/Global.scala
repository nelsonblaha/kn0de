import play.api._
import play.api.Logger

import models._
import models.auth._
import anorm._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Starting application...")
    InitialData.insert()
  }

}

object InitialData {

  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

  def insert() = {
    Logger.info("Inserting initial data...")

    if(Account.findAll.isEmpty)   {

      Seq(
        Account(NotAssigned, "user1@example.com", "password", "user1", NormalUser),
        Account(NotAssigned, "user2@example.com", "password", "user2", NormalUser),
        Account(NotAssigned, "user3@example.com", "password", "user3", NormalUser),
        Account(NotAssigned, "user4@example.com", "password", "user4", NormalUser),
        Account(NotAssigned, "user5@example.com", "password", "user5", NormalUser)
      ).foreach(Account.create)

      Seq(
        Sub(NotAssigned, "AskScience", "A sub about science", 1),
        Sub(NotAssigned, "Politics", "A sub about politics", 1),
        Sub(NotAssigned, "Technology", "A sub about technology", 1),
        Sub(NotAssigned, "Funny", "A sub about funny", 1),
        Sub(NotAssigned, "Stuff", "A sub about stuff", 1),
        Sub(NotAssigned, "Misc", "A sub about everything", 1)
      ).foreach(Sub.create)

      Subscription.create(6, 1)
      Subscription.create(6, 2)
      Subscription.create(6, 3)
      Subscription.create(7, 1)
      Subscription.create(7, 3)
      Subscription.create(8, 4)
      Subscription.create(8, 6)
      Subscription.create(8, 5)
      Subscription.create(8, 2)
      Subscription.create(9, 1)
      Subscription.create(9, 6)
      Subscription.create(9, 5)

    }

  }

}
        
