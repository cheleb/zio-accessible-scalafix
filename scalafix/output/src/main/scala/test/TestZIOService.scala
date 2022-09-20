package test

import zio._
import zio.stream._
@Accessible
trait TestZIOService {
  def testTask(i: Int): Task[Int]
  def testZIO(i: Int): ZIO[Any, Throwable, Int]
  def testZIO2(i: Int): ZIO[Any, Throwable, Int]
  def testIO(i: Int): IO[Throwable, Int]
  def testUIO(i: Int): UIO[Int]
  def testURIO(i: Int): URIO[Scope, Int]
  def testWithScope(i: Int): ZIO[Scope, Throwable, Int]
  def testRIOWithScope(i: Int): RIO[Scope, Int]
  def stream: ZStream[Any, Nothing, Int]
  def sink: ZSink[Any, Nothing, Int, Nothing, Int]
}
object TestZIOService {
  def testTask(i: Int): RIO[TestZIOService, Int] = ZIO.serviceWithZIO[TestZIOService](_.testTask(i))
  def testZIO(i: Int): ZIO[TestZIOService, Throwable, Int] = ZIO.serviceWithZIO[TestZIOService](_.testZIO(i))
  def testZIO2(i: Int): ZIO[TestZIOService, Throwable, Int] = ZIO.serviceWithZIO[TestZIOService](_.testZIO2(i))
  def testIO(i: Int): ZIO[TestZIOService, Throwable, Int] = ZIO.serviceWithZIO[TestZIOService](_.testIO(i))
  def testUIO(i: Int): URIO[TestZIOService, Int] = ZIO.serviceWithZIO[TestZIOService](_.testUIO(i))
  def testURIO(i: Int): URIO[TestZIOService with Scope, Int] = ZIO.serviceWithZIO[TestZIOService](_.testURIO(i))
  def testWithScope(i: Int): ZIO[TestZIOService with Scope, Throwable, Int] = ZIO.serviceWithZIO[TestZIOService](_.testWithScope(i))
  def testRIOWithScope(i: Int): RIO[TestZIOService with Scope, Int] = ZIO.serviceWithZIO[TestZIOService](_.testRIOWithScope(i))
  def stream: ZStream[TestZIOService, Nothing, Int] = ZStream.serviceWithStream[TestZIOService](_.stream)
  def sink: ZSink[TestZIOService, Nothing, Int, Nothing, Int] = ZSink.serviceWithSink[TestZIOService](_.sink)
}
