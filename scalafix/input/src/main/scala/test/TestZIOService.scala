/*
rule = ZIOAccessible
 */
package test

import zio._
@Accessible
trait TestZIOService {
  def testTask(i: Int): Task[Int]
  def testIO(i: Int): IO[Throwable, Int]
  def test(i: Int): ZIO[Any, Throwable, Int]
  def test2(i: Int): ZIO[Any, Throwable, Int]
}

