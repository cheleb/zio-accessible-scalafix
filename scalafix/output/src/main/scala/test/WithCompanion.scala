package test

import zio._

@Accessible
trait WithCompanion {
  def testTask(i: Int): Task[Int]
  def testIO(i: Int): IO[Throwable, Int]
  def test2(i: Int): ZIO[Any, Throwable, Int]
  def test3(i: Int): ZIO[Any, Throwable, Int]
}

object WithCompanion {
  val zozo = "bo"
  def nenai = "belle"
  def testIO(i: Int): ZIO[WithCompanion, Throwable, Int] = ZIO.serviceWithZIO[WithCompanion](_.testIO(i))
  def test2(i: Int): ZIO[WithCompanion, Throwable, Int] = ZIO.serviceWithZIO[WithCompanion](_.test2(i))
  def test3(i: Int): ZIO[WithCompanion, Throwable, Int] = ZIO.serviceWithZIO[WithCompanion](_.test3(i))
  def testTask(i: Int): RIO[WithCompanion, Int] = ZIO.serviceWithZIO[WithCompanion](_.testTask(i))
}

