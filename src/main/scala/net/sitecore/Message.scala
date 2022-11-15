package net.sitecore

import java.time.Instant
import java.time.ZoneOffset
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter

case class Message (
    typeOfEntity: String,
    typeOfEvent: String,
    site: String,
    page: String,
    guestID: String,
    sessionID: String,
    timestamp: Instant,
    views: Int
)

object Message
{
  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  def fromLine(line: String): Message = {
      val l = line.split(',')
      val timestamp = LocalDateTime.parse(l(6), formatter).toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES)
      Message(l(0), l(1), l(2), l(3), l(4), l(5), timestamp, 1)
  }

  def fromMap(item: ((String, Instant), Int)): Message = {
    Message("", "", item._1._1, "", "", "", item._1._2, item._2)
  }
}