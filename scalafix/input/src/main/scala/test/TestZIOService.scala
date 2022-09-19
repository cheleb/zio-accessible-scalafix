/*
rule = ZIOAccessible
 */
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

