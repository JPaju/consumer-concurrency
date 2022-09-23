package fi.jpaju

import zio.*
import zio.stream.*
import Domain.*

object EventCreator:
  val eventCount = 32000

  def sourceWithoutDuplicateKeys: UStream[User] = ZStream
    .fromIterable(1 to eventCount)
    .map(i => User(i, s"User $i"))

  def sourceWithDuplicateKeys(duplicateFactor: Int): UStream[User] =
    val eventsPerStream = eventCount / duplicateFactor
    (0 to duplicateFactor)
      .map(_ => ZStream.fromIterable(1 to eventsPerStream))
      .reduce(_ ++ _)
      .map(i => User(i, s"User $i"))
end EventCreator

object ConsumeStrategies:
  private val source: UStream[User] = EventCreator.sourceWithDuplicateKeys(5)

  // No parallelism whatsoever
  val sequential: UStream[Unit] = source.mapZIO(Service.processUpdate(_))

  // Parallelism is controlled solely by keys derived from the events
  val parallelByKey: UStream[Unit]  = source.mapZIOParByKey(_.id)(Service.processUpdate(_))
  val parallelByKey2: UStream[Unit] = source.groupByKey(_.id) { (key, usersWithSameKey) =>
    usersWithSameKey.mapZIO(Service.processUpdate(_))
  }

  // Limits the parallelism to certain level
  val parallelism                = 100
  val parallelByN: UStream[Unit] = source.mapZIOParUnordered(parallelism)(Service.processUpdate(_))
end ConsumeStrategies

object InMemoryBenchmark extends ZIOAppDefault:
// Change this to benchmark different strategies
  val strategyToBenchmark: UStream[Unit] = ConsumeStrategies.parallelByKey

  val run =
    for
      consumeDuration  <- strategyToBenchmark.runDrain.timed.map(_._1)
      formattedDuration = consumeDuration.toMillis
      msg               = s"Took $formattedDuration ms to process ${EventCreator.eventCount} events"
      _                <- ZIO.debug(msg)
    yield ()
