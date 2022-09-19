package test

import zio._
import zio.stream._
@Accessible
trait TestZIOService {
  def testTask(i: Int): Task[Int]
  def testIO(i: Int): IO[Throwable, Int]
  def testUIO(i: Int): UIO[Int]
  def testURIO(i: Int): URIO[Scope, Int]
  def test(i: Int): ZIO[Any, Throwable, Int]
  def testWithScope(i: Int): ZIO[Scope, Throwable, Int]
  def testRIOWithScope(i: Int): RIO[Scope, Int]
  def test2(i: Int): ZIO[Any, Throwable, Int]
  def stream: ZStream[Any, Nothing, Int]
}
object TestZIOService {
  def testTask(i: Int): RIO[TestZIOService, Int] = ZIO.serviceWithZIO[TestZIOService](_.testTask(i))
  def testIO(i: Int): ZIO[TestZIOService, Throwable, Int] = ZIO.serviceWithZIO[TestZIOService](_.testIO(i))
  def testUIO(i: Int): URIO[TestZIOService, Int] = ZIO.serviceWithZIO[TestZIOService](_.testUIO(i))
  def testURIO(i: Int): URIO[TestZIOService with Scope, Int] = ZIO.serviceWithZIO[TestZIOService](_.testURIO(i))
  def test(i: Int): ZIO[TestZIOService, Throwable, Int] = ZIO.serviceWithZIO[TestZIOService](_.test(i))
  def testWithScope(i: Int): ZIO[TestZIOService with Scope, Throwable, Int] = ZIO.serviceWithZIO[TestZIOService](_.testWithScope(i))
  def testRIOWithScope(i: Int): RIO[TestZIOService with Scope, Int] = ZIO.serviceWithZIO[TestZIOService](_.testRIOWithScope(i))
  def test2(i: Int): ZIO[TestZIOService, Throwable, Int] = ZIO.serviceWithZIO[TestZIOService](_.test2(i))
  def stream: ZStream[TestZIOService, Nothing, Int] = ZStream.serviceWithStream[TestZIOService](_.stream)
}
