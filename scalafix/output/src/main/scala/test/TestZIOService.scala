package test

import zio._
@Accessible
trait TestZIOService {
  def test(i: Int): ZIO[Any, Throwable, Int]
  def test2(i: Int): ZIO[Any, Throwable, Int]
}
object TestZIOService {
  def test(i: Int): ZIO[TestZIOService, Throwable, Int] = ZIO.serviceWithZIO[TestZIOService](_.test(i))
  def test2(i: Int): ZIO[TestZIOService, Throwable, Int] = ZIO.serviceWithZIO[TestZIOService](_.test2(i))
}
