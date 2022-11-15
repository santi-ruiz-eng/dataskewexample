package net.sitecore

import org.apache.flink.api.common.functions.AggregateFunction
import java.time.Instant

class AggregateViews extends AggregateFunction[Message, (String, Instant, Int), (String, Instant, Int)] {
  override def createAccumulator() = ("", Instant.now, 0)
  override def add(value: Message, accumulator: (String, Instant, Int)) = (value.site, value.timestamp, accumulator._3 + value.views)
  override def getResult(accumulator: (String, Instant, Int)) = accumulator
  override def merge(a: (String, Instant, Int), b: (String, Instant, Int)) = (a._1, a._2, a._3 + b._3)
}