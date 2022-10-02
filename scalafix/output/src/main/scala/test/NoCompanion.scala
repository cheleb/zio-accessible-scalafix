package test

import zio._
import zio.stream._
@Accessible
trait NoCompanion {
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
  protected def protectedSink: ZSink[Any, Nothing, Int, Nothing, Int]
  private def privateSink: ZSink[Any, Nothing, Int, Nothing, Int] = ZSink.sum
}
object NoCompanion {
  def testTask(i: Int): RIO[NoCompanion, Int] = ZIO.serviceWithZIO[NoCompanion](_.testTask(i))
  def testZIO(i: Int): ZIO[NoCompanion, Throwable, Int] = ZIO.serviceWithZIO[NoCompanion](_.testZIO(i))
  def testZIO2(i: Int): ZIO[NoCompanion, Throwable, Int] = ZIO.serviceWithZIO[NoCompanion](_.testZIO2(i))
  def testIO(i: Int): ZIO[NoCompanion, Throwable, Int] = ZIO.serviceWithZIO[NoCompanion](_.testIO(i))
  def testUIO(i: Int): URIO[NoCompanion, Int] = ZIO.serviceWithZIO[NoCompanion](_.testUIO(i))
  def testURIO(i: Int): URIO[NoCompanion with Scope, Int] = ZIO.serviceWithZIO[NoCompanion](_.testURIO(i))
  def testWithScope(i: Int): ZIO[NoCompanion with Scope, Throwable, Int] = ZIO.serviceWithZIO[NoCompanion](_.testWithScope(i))
  def testRIOWithScope(i: Int): RIO[NoCompanion with Scope, Int] = ZIO.serviceWithZIO[NoCompanion](_.testRIOWithScope(i))
  def stream: ZStream[NoCompanion, Nothing, Int] = ZStream.serviceWithStream[NoCompanion](_.stream)
  def sink: ZSink[NoCompanion, Nothing, Int, Nothing, Int] = ZSink.serviceWithSink[NoCompanion](_.sink)
}
