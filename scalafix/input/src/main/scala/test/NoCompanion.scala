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
  protected def protectedSink: ZSink[Any, Nothing, Int, Nothing, Int]
  private def privateSink: ZSink[Any, Nothing, Int, Nothing, Int] = ZSink.sum
}

