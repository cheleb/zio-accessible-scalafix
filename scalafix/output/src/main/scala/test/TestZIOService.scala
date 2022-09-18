package test

import zio._
@Accessible
trait TestZIOService {
  def testTask(i: Int): Task[Int]
  def testIO(i: Int): IO[Throwable, Int]
  def test(i: Int): ZIO[Any, Throwable, Int]
  def test2(i: Int): ZIO[Any, Throwable, Int]
}
object TestZIOService {
  def testTask(i: Int): RIO[TestZIOService, Int] = ZIO.serviceWithZIO[TestZIOService](_.testTask(i))
  def testIO(i: Int): ZIO[TestZIOService, Throwable, Int] = ZIO.serviceWithZIO[TestZIOService](_.testIO(i))
  def test(i: Int): ZIO[TestZIOService, Throwable, Int] = ZIO.serviceWithZIO[TestZIOService](_.test(i))
  def test2(i: Int): ZIO[TestZIOService, Throwable, Int] = ZIO.serviceWithZIO[TestZIOService](_.test2(i))
}
