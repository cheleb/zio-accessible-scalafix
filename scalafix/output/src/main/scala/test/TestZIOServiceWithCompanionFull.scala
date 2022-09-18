package test

import zio._

@Accessible
trait TestZIOServiceWithCompanionFull {
  def testTask(i: Int): Task[Int]
  def testIO(i: Int): IO[Throwable, Int]
  def test2(i: Int): ZIO[Any, Throwable, Int]
  def test3(i: Int): ZIO[Any, Throwable, Int]
}

object TestZIOServiceWithCompanionFull {
  val zozo = "bo"
  def nenai = "belle"
  def testIO(i: Int): ZIO[TestZIOServiceWithCompanionFull, Throwable, Int] = ZIO.serviceWithZIO[TestZIOServiceWithCompanionFull](_.testIO(i))
  def test2(i: Int): ZIO[TestZIOServiceWithCompanionFull, Throwable, Int] = ZIO.serviceWithZIO[TestZIOServiceWithCompanionFull](_.test3(i))
  def test3(i: Int): ZIO[TestZIOServiceWithCompanionFull, Throwable, Int] = ZIO.serviceWithZIO[TestZIOServiceWithCompanionFull](_.test3(i))
  def testTask(i: Int): RIO[TestZIOServiceWithCompanionFull, Int] = ZIO.serviceWithZIO[TestZIOServiceWithCompanionFull](_.testTask(i))
}

