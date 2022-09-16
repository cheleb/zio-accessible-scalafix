/*
rule = ZIOAccessible
 */
package test

import zio._
@Accessible
trait TestZIOService {
  def test(i: Int): ZIO[Any, Throwable, Int]
  def test2(i: Int): ZIO[Any, Throwable, Int]
}

