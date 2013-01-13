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

      Subscription.create(1, 1)
      Subscription.create(1, 2)
      Subscription.create(1, 3)
      Subscription.create(2, 1)
      Subscription.create(2, 3)
      Subscription.create(3, 4)
      Subscription.create(3, 6)
      Subscription.create(3, 5)
      Subscription.create(3, 2)
      Subscription.create(4, 1)
      Subscription.create(4, 6)
      Subscription.create(4, 5)

    }

    if(Item.totalNumberItems == 0) {

      Seq(
        Item(NotAssigned, "Lorem ipsum", 1, 1, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 2, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 3, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 4, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 5, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 6, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 1, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 2, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 3, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 4, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 5, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 6, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 1, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 2, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 3, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 4, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 5, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 6, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 1, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 2, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 3, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 4, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 5, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date()),
        Item(NotAssigned, "Lorem ipsum", 1, 6, 1, "http://example.com", "Lorem ipsum dolor set amet", new java.util.Date())
      ).foreach(Item.create)


    }

  }

}
        
