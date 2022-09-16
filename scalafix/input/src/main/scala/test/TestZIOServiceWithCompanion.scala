/*
rule = ZIOAccessible
 */
package test

import zio._

@Accessible
trait TestZIOServiceWithCompanion {
  def test(i: Int): ZIO[Any, Throwable, Int]
  def test2(i: Int): ZIO[Any, Throwable, Int]
}

object TestZIOServiceWithCompanion {
  val zozo = "bo"
}

