package net.sitecore

import org.apache.flink.streaming.api.scala.extensions._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.scala._
import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.api.java.functions.KeySelector
import java.time.Duration
import java.time.Instant
import org.apache.flink.table.runtime.operators.bundle.MapBundleOperator
import org.apache.flink.table.runtime.operators.bundle.trigger.CountBundleTrigger

object FixedJob {
  def main(args: Array[String]) {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // Valid path example (for Mac): file:////Users/santiago/Documents/Workspace/dataskewexample/src/main/resources/skewedData.csv
    val windowResult = env.readTextFile("<<FILL ME IN>>")
      .name("Messages")
      .map(Message.fromLine(_))
      .filter(m => m.typeOfEntity == "EVENT" && m.typeOfEvent == "VIEW")
      .name("FilteredEvents")
      .transform(
        "PreAggregation",
        new MapBundleOperator[(String, Instant), Int, Message, Message](
          new AggregateViewsBundleFunction,
          new CountBundleTrigger(100),
          new KeySelector[Message,(String, Instant)] {
            override def getKey(in: Message) = (in.site, in.timestamp)
          }
        )
      )
      .assignTimestampsAndWatermarks(WatermarkStrategy
        .forBoundedOutOfOrderness[Message](Duration.ofSeconds(60))
        .withTimestampAssigner(new SerializableTimestampAssigner[Message] {
          override def extractTimestamp(element: Message, recordTimestamp: Long): Long = element.timestamp.toEpochMilli()
      }))
      .keyingBy(_.site)
      .window(TumblingEventTimeWindows.of(Time.minutes(1)))
      .aggregate(new AggregateViews)
      .name("ViewEventsAggregation")

    windowResult print

    env.execute("Event Views MapBundle Example");
  }
}