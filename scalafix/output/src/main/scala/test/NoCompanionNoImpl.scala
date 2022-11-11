package test

import zio._
import zio.stream._
@Accessible
trait NoCompanionNoImpl {
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
class NoCompanionNoImplImpl extends NoCompanionNoImpl {
  def testTask(i: Int): Task[Int] = ???
  def testZIO(i: Int): ZIO[Any, Throwable, Int] = ???
  def testZIO2(i: Int): ZIO[Any, Throwable, Int] = ???
  def testIO(i: Int): IO[Throwable, Int] = ???
  def testUIO(i: Int): UIO[Int] = ???
  def testURIO(i: Int): URIO[Scope, Int] = ???
  def testWithScope(i: Int): ZIO[Scope, Throwable, Int] = ???
  def testRIOWithScope(i: Int): RIO[Scope, Int] = ???
  def stream: ZStream[Any, Nothing, Int] = ???
  def sink: ZSink[Any, Nothing, Int, Nothing, Int] = ???
}

object NoCompanionNoImpl {
  def testTask(i: Int): RIO[NoCompanionNoImpl, Int] = ZIO.serviceWithZIO[NoCompanionNoImpl](_.testTask(i))
  def testZIO(i: Int): ZIO[NoCompanionNoImpl, Throwable, Int] = ZIO.serviceWithZIO[NoCompanionNoImpl](_.testZIO(i))
  def testZIO2(i: Int): ZIO[NoCompanionNoImpl, Throwable, Int] = ZIO.serviceWithZIO[NoCompanionNoImpl](_.testZIO2(i))
  def testIO(i: Int): ZIO[NoCompanionNoImpl, Throwable, Int] = ZIO.serviceWithZIO[NoCompanionNoImpl](_.testIO(i))
  def testUIO(i: Int): URIO[NoCompanionNoImpl, Int] = ZIO.serviceWithZIO[NoCompanionNoImpl](_.testUIO(i))
  def testURIO(i: Int): URIO[NoCompanionNoImpl with Scope, Int] = ZIO.serviceWithZIO[NoCompanionNoImpl](_.testURIO(i))
  def testWithScope(i: Int): ZIO[NoCompanionNoImpl with Scope, Throwable, Int] = ZIO.serviceWithZIO[NoCompanionNoImpl](_.testWithScope(i))
  def testRIOWithScope(i: Int): RIO[NoCompanionNoImpl with Scope, Int] = ZIO.serviceWithZIO[NoCompanionNoImpl](_.testRIOWithScope(i))
  def stream: ZStream[NoCompanionNoImpl, Nothing, Int] = ZStream.serviceWithStream[NoCompanionNoImpl](_.stream)
  def sink: ZSink[NoCompanionNoImpl, Nothing, Int, Nothing, Int] = ZSink.serviceWithSink[NoCompanionNoImpl](_.sink)
}
