/*
rule = ZIOAccessible
 */
package test

import zio._

@Accessible
trait WithCompanionFull {
  def testIO(i: Int): IO[Throwable, Int]
  def test2(i: Int): ZIO[Any, Throwable, Int]
  def test3(i: Int): ZIO[Any, Throwable, Int]
  def testTask(i: Int): Task[Int]
}

object WithCompanionFull {
  val zozo = "bo"
  def nenai = "belle"
  def testIO(i: Int): ZIO[WithCompanionFull, Throwable, Int] = ZIO.serviceWithZIO[WithCompanionFull](_.testIO(i))
  def test2(i: Int): ZIO[WithCompanionFull, Throwable, Int] = ZIO.serviceWithZIO[WithCompanionFull](_.test2(i))
  def test3(i: Int): ZIO[WithCompanionFull, Throwable, Int] = ZIO.serviceWithZIO[WithCompanionFull](_.test3(i))
  def testTask(i: Int): RIO[WithCompanionFull, Int] = ZIO.serviceWithZIO[WithCompanionFull](_.testTask(i))
}

