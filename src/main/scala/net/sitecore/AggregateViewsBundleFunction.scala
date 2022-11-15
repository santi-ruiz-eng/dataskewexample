package net.sitecore

import org.apache.flink.table.runtime.operators.bundle.MapBundleFunction
import org.apache.flink.util.Collector
import java.time.Instant
import scala.collection.JavaConverters._

class AggregateViewsBundleFunction extends MapBundleFunction[(String, Instant), Int, Message, Message] {

  override def addInput(value: Int, input: Message): Int = value + 1

  override def finishBundle(
      buffer: java.util.Map[(String, Instant), Int],
      out: Collector[Message]
  ): Unit = {
    val outputValues = buffer.asScala.map(Message.fromMap _).toSeq.sortBy(_.timestamp.toEpochMilli)(Ordering[Long].reverse)
    outputValues.foreach(out.collect)
  }
}