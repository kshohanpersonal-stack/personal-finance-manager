package com.shohankhan.ledgerly.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** All dates are stored as epoch days; display formatting happens here. */
object Dates {

    fun today(): Long = LocalDate.now().toEpochDay()

    fun nowMillis(): Long = System.currentTimeMillis()

    fun of(year: Int, month: Int, day: Int): Long = LocalDate.of(year, month, day).toEpochDay()

    fun localDate(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)

    fun epochMillis(epochDay: Long): Long =
        LocalDate.ofEpochDay(epochDay).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun epochDayOfMillis(millis: Long): Long =
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()

    private val mediumFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("d MMM yyyy", Locale.US)

    private val shortFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("d MMM", Locale.US)

    private val dayMonthFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("d MMMM", Locale.US)

    private val weekdayFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("EEE, d MMM", Locale.US)

    fun format(epochDay: Long): String = LocalDate.ofEpochDay(epochDay).format(mediumFormatter)

    fun formatShort(epochDay: Long): String = LocalDate.ofEpochDay(epochDay).format(shortFormatter)

    fun formatDayMonth(epochDay: Long): String = LocalDate.ofEpochDay(epochDay).format(dayMonthFormatter)

    fun formatWeekday(epochDay: Long): String = LocalDate.ofEpochDay(epochDay).format(weekdayFormatter)

    fun formatMonthYear(year: Int, month: Int): String =
        LocalDate.of(year, month, 1).format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US))

    fun formatTime(millis: Long): String =
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("h:mm a", Locale.US))

    /** "Today", "Tomorrow", "Yesterday" or a real date. */
    fun relativeLabel(epochDay: Long, todayEpochDay: Long = today()): String = when (epochDay) {
        todayEpochDay -> "Today"
        todayEpochDay + 1 -> "Tomorrow"
        todayEpochDay - 1 -> "Yesterday"
        else -> {
            val date = LocalDate.ofEpochDay(epochDay)
            val sameYear = date.year == LocalDate.ofEpochDay(todayEpochDay).year
            date.format(if (sameYear) shortFormatter else mediumFormatter)
        }
    }

    fun daysBetween(fromEpochDay: Long, toEpochDay: Long): Long = toEpochDay - fromEpochDay

    fun daysUntil(epochDay: Long, todayEpochDay: Long = today()): Long = epochDay - todayEpochDay

    /** Human phrasing for upcoming/overdue obligations. */
    fun duePhrase(epochDay: Long, todayEpochDay: Long = today()): String {
        val days = daysUntil(epochDay, todayEpochDay)
        return when {
            days == 0L -> "Due today"
            days == 1L -> "Due tomorrow"
            days < 0 -> "${-days} day${if (-days > 1) "s" else ""} overdue"
            days in 2..6 -> "Due in $days days"
            days in 7..13 -> "Due in ${days / 7} week"
            days in 14..59 -> "Due in ${days / 7} weeks"
            days in 60..364 -> "Due in ${days / 30} months"
            else -> "Due ${formatShort(epochDay)}"
        }
    }

    fun monthRange(year: Int, month: Int): LongRange {
        val start = LocalDate.of(year, month, 1)
        val end = start.withDayOfMonth(start.lengthOfMonth())
        return start.toEpochDay()..end.toEpochDay()
    }

    fun yearRange(year: Int): LongRange =
        LocalDate.of(year, 1, 1).toEpochDay()..LocalDate.of(year, 12, 31).toEpochDay()

    /** Inclusive range covering the calendar week that contains [epochDay]. */
    fun weekRange(epochDay: Long, weekStartsOnMonday: Boolean = true): LongRange {
        val date = LocalDate.ofEpochDay(epochDay)
        val dayOfWeek = date.dayOfWeek.value // 1 = Monday … 7 = Sunday
        val offset = if (weekStartsOnMonday) dayOfWeek - 1 else dayOfWeek % 7
        val start = date.minusDays(offset.toLong())
        return start.toEpochDay()..start.plusDays(6).toEpochDay()
    }

    fun startOfMonth(epochDay: Long): Long =
        LocalDate.ofEpochDay(epochDay).withDayOfMonth(1).toEpochDay()

    fun endOfMonth(epochDay: Long): Long {
        val date = LocalDate.ofEpochDay(epochDay)
        return date.withDayOfMonth(date.lengthOfMonth()).toEpochDay()
    }

    fun monthsBack(fromEpochDay: Long, months: Int): List<Pair<Int, Int>> {
        val result = ArrayList<Pair<Int, Int>>(months)
        var cursor = LocalDate.ofEpochDay(fromEpochDay).withDayOfMonth(1)
        repeat(months) {
            result.add(cursor.year to cursor.monthValue)
            cursor = cursor.minusMonths(1)
        }
        return result.asReversed()
    }

    fun greeting(hour: Int = java.time.LocalTime.now().hour): String = when (hour) {
        in 0..4 -> "Still up"
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Good night"
    }
}
