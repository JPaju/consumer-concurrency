package fi.jpaju

import zio.*
import Domain.*

object Domain:
  case class User(id: Int, name: String)

object Service:
  def processUpdate(user: User): ZIO[Any, Nothing, Unit] =
    saveToDb(user) *> publishUpdate(user)

  private def saveToDb(user: User): UIO[Unit] =
    ZIO.sleep(10.millis) // Just to simulate some IO

  private def publishUpdate(user: User): UIO[Unit] =
    ZIO.sleep(10.millis) // Just to simulate some IO
