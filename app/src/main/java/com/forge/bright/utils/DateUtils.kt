package com.forge.bright.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class MessageTemporalTag(val tag: String, val time: String)
object DateUtils {
    fun getMessageTime(datetimeInMillis: Long): MessageTemporalTag {
        val now = System.currentTimeMillis()
        val diff = now - datetimeInMillis
        val zoneId = ZoneId.systemDefault()
        val messageDate = Instant.ofEpochMilli(datetimeInMillis).atZone(zoneId).toLocalDate()
        val today = LocalDate.now(zoneId)

        // 2. Handle specific date labels
        val dateTag = when (messageDate) {
            today -> if (diff < 60000) "now" else "Today"
            today.minusDays(1) -> "Yesterday"
            else -> ""
        }

        val formatter = DateTimeFormatter.ofPattern("h:mm.ss a").withZone(zoneId)
        val time = formatter.format(Instant.ofEpochMilli(datetimeInMillis))
        return MessageTemporalTag(dateTag, time)
    }

    fun readableTimeOrTag(dateTimeMillisInUTC: Long): String {
        val value = getMessageTime(System.currentTimeMillis())
        return value.tag
    }

    fun readableDateTimeTag(dateTimeMillisInUTC: Long): String {
        val value = getMessageTime(System.currentTimeMillis())
        return if (value.tag == "now") value.tag else value.time
    }

    fun readableTime(dateTimeMillisInUTC: Long): String {
        val value = getMessageTime(dateTimeMillisInUTC)
        return value.time
    }
}