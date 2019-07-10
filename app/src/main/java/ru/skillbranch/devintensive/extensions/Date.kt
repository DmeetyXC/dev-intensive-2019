package ru.skillbranch.devintensive.extensions

import ru.skillbranch.devintensive.extensions.TimeUnits.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

enum class TimeUnits(val size: Long) {
    SECOND(1000L),
    MINUTE(60 * SECOND.size),
    HOUR(60 * MINUTE.size),
    DAY(24 * HOUR.size);

    fun plural(value: Long): String {
        val remainder = value % 10
        val preLastDigit = value % 100 / 10
        val plurals = mapOf(
            SECOND to mapOf(
                PluralUnits.FEW to "секунды",
                PluralUnits.ONE to "секунду",
                PluralUnits.MANY to "секунд"
            ),
            MINUTE to mapOf(
                PluralUnits.FEW to "минуты",
                PluralUnits.ONE to "минуту",
                PluralUnits.MANY to "минут"
            ),
            HOUR to mapOf(
                PluralUnits.FEW to "часа",
                PluralUnits.ONE to "час",
                PluralUnits.MANY to "часов"
            ),
            DAY to mapOf(
                PluralUnits.FEW to "дня",
                PluralUnits.ONE to "день",
                PluralUnits.MANY to "дней"
            )
        )
        return when {
            (preLastDigit == 1L) -> "$value ${plurals[this]?.get(PluralUnits.MANY)}"
            (remainder in 2..4) -> "$value ${plurals[this]?.get(PluralUnits.FEW)}"
            (remainder == 1L) -> "$value ${plurals[this]?.get(PluralUnits.ONE)}"
            else -> "$value ${plurals[this]?.get(PluralUnits.MANY)}"
        }
    }

    private enum class PluralUnits {
        FEW, ONE, MANY
    }
}

val Int.sec get() = this * SECOND.size
val Int.min get() = this * MINUTE.size
val Int.hour get() = this * HOUR.size
val Int.day get() = this * DAY.size

val Long.asMin get() = this.absoluteValue / MINUTE.size
val Long.asHour get() = this.absoluteValue / HOUR.size
val Long.asDay get() = this.absoluteValue / DAY.size

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits = SECOND): Date {
    time += value * units.size
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val diff = ((date.time + 200) / 1000 - (time + 200) / 1000) * 1000

    return if (diff >= 0) {
        when (diff) {
            in 0.sec..1.sec -> "только что"
            in 2.sec..45.sec -> "несколько секунд назад"
            in 46.sec..75.sec -> "минуту назад"
            in 76.sec..45.min -> "${minutesAsPlulars(diff.asMin)} назад"
            in 46.min..75.min -> "час назад"
            in 76.min..22.hour -> "${hoursAsPlulars(diff.asHour)} назад"
            in 23.hour..26.hour -> "день назад"
            in 27.hour..360.day -> "${daysAsPlulars(diff.asDay)} назад"
            else -> "более года назад"
        }
    } else {
        when (diff) {
            in (-1).sec..0.sec -> "прямо сейчас"
            in (-45).sec..(-1).sec -> "через несколько секунд"
            in (-75).sec..(-45).sec -> "через минуту"
            in (-45).min..(-75).sec -> "через ${minutesAsPlulars(diff.asMin)}"
            in (-75).min..(-45).min -> "через час"
            in (-22).hour..(-75).min -> "через ${hoursAsPlulars(diff.asHour)}"
            in (-26).hour..(-22).hour -> "через день"
            in (-360).day..(-26).hour -> "через ${daysAsPlulars(diff.asDay)}"
            else -> "более чем через год"
        }
    }
}

private fun minutesAsPlulars(value: Long) = when (value.asPlurals) {
    Plurals.ONE -> "$value минуту"
    Plurals.FEW -> "$value минуты"
    Plurals.MANY -> "$value минут"
}

private fun hoursAsPlulars(value: Long) = when (value.asPlurals) {
    Plurals.ONE -> "$value час"
    Plurals.FEW -> "$value часа"
    Plurals.MANY -> "$value часов"
}

private fun daysAsPlulars(value: Long) = when (value.asPlurals) {
    Plurals.ONE -> "$value день"
    Plurals.FEW -> "$value дня"
    Plurals.MANY -> "$value дней"
}

enum class Plurals {
    ONE,
    FEW,
    MANY
}

val Long.asPlurals
    get() = when {
        this % 100L in 5L..20L -> Plurals.MANY
        this % 10L == 1L -> Plurals.ONE
        this % 10L in 2L..4L -> Plurals.FEW
        else -> Plurals.MANY
    }
