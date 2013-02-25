package models

trait RedisKeys   {

  implicit object SubHash  {

    val subName = "subName"
    val subDescription = "subDescription"
    val createdBy = "createdBy"
    val createdAt = "createdAt"

  }

  implicit def subHash(implicit subId: Long) = s"sub:$subId"

  implicit def nextItemId(implicit subId: Long) = s"sub:$subId:nextId"

  implicit def subscriptionsForUser(implicit userId: Long) = s"subscriptions:$userId"

  implicit def subscribersForSub(implicit subId: Long) = s"sub:$subId:subscribers"

  implicit def defaultSubscriptions = "subscriptions:default"

}
