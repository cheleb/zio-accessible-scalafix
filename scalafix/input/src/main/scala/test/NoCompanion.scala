/*
rule = ZIOAccessible
 */
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
  def testGen[A](i: A): Task[A]
  def createReaderGroup(
    readerGroupName: String,
    builder: Int,
    streamNames: String*
  ): Task[Boolean]
}
class NoCompanionImpl extends NoCompanion {
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
  def testGen[A](i: A): Task[A] = ???
  def createReaderGroup(readerGroupName: String, builder: Int, streamNames: String*): Task[Boolean] = ???
}