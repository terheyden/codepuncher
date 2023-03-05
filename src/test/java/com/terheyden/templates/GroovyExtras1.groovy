package com.terheyden.templates

import javax.annotation.Nullable
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

import static org.apache.commons.lang3.StringUtils.isBlank
//////////////////////////////////////////////////
// TIME RELATED

/**
 * An easy way to format and print a date/time.
 */
Optional<String> formatDateTime(@Nullable TemporalAccessor dateTime, @Nullable String format) {

    if (dateTime == null) {
        println "formatDateTime: dateTime is null";
        return Optional.empty();
    }

    if (isBlank(format)) {
        println "formatDateTime: format is null";
        return Optional.empty();
    }

    try {

        return Optional.of(DateTimeFormatter.ofPattern(format).format(dateTime));

    } catch (Exception e) {
        println "Exception formatting date: " + e;
        return Optional.empty();
    }
}

/**
 * An easy way to format and print a date/time.
 */
Optional<String> formatDateTime(
    @Nullable String dateTime,
    @Nullable String format) {

    if (isBlank(dateTime)) {
        println "formatDateTime: dateTime is blank";
        return Optional.empty();
    }

    if (isBlank(format)) {
        println "formatDateTime: format is blank";
        return Optional.empty();
    }

    try {

        return parseDateTime(dateTime)
            .flatMap(date -> formatDateTime(date, format));

    } catch (Exception e) {
        println "Exception formatting date: " + e;
        return Optional.empty();
    }
}

ZonedDateTime todayMidnightUtc() {
    return ZonedDateTime.now(ZoneOffset.UTC).withHour(0).withMinute(0).withSecond(0).withNano(0);
}

ZonedDateTime todayMidnightDefaultLocale() {
    return ZonedDateTime.now(ZoneId.systemDefault()).withHour(0).withMinute(0).withSecond(0).withNano(0);
}

/**
 * Create a {@link ZonedDateTime} equal to today's date (according to the given {@link ZoneId})
 * and at midnight.
 * @param zoneId The {@link ZoneId} to use. If null, the default locale's zone will be used
 * @return A {@link ZonedDateTime} equal to today's date (according to the given {@link ZoneId})
 */
ZonedDateTime todayMidnight(ZoneId zoneId) {
    return ZonedDateTime.now(zoneId).withHour(0).withMinute(0).withSecond(0).withNano(0);
}

Optional<ZonedDateTime> parseDateTime(@Nullable String dateTimeStr, @Nullable ZoneId zoneId) {

    if (isBlank(dateTimeStr)) {
        println "parseDateTime: dateTimeStr is blank";
        return Optional.empty();
    }

    if (zoneId == null) {
        println "parseDateTime: zoneId is null";
        return Optional.empty();
    }

    try {

        println "Parsing date/time string: {}", dateTimeStr;
        return Optional.of(ZonedDateTime.parse(dateTimeStr).withZoneSameInstant(zoneId));

    } catch (Exception e) {
        println "Exception parsing date/time string: {}", dateTimeStr, e;
        return Optional.empty();
    }
}

Optional<ZonedDateTime> parseDateTime(@Nullable String dateTimeStr) {

    if (isBlank(dateTimeStr)) {
        println "parseDateTime: dateTimeStr is blank";
        return Optional.empty();
    }

    try {

        println "Parsing date/time string: {}", dateTimeStr;
        return Optional.of(ZonedDateTime.parse(dateTimeStr));

    } catch (Exception e) {
        println "Exception parsing date/time string: {}", dateTimeStr, e;
        return Optional.empty();
    }
}

Optional<ZonedDateTime> changeTimeZone(
    @Nullable ZonedDateTime zonedDateTime,
    @Nullable String newZoneId) {

    if (isBlank(newZoneId)) {
        println "changeTimeZone: newZoneId is blank";
        return Optional.empty();
    }

    return changeTimeZone(zonedDateTime, ZoneId.of(newZoneId));
}

Optional<ZonedDateTime> changeTimeZone(
    @Nullable ZonedDateTime zonedDateTime,
    @Nullable ZoneId newZoneId) {

    if (zonedDateTime == null) {
        println "changeTimeZone: zonedDateTime is null";
        return Optional.empty();
    }

    if (newZoneId == null) {
        println "changeTimeZone: newZoneId is null";
        return Optional.empty();
    }

    try {

        return Optional.of(zonedDateTime.withZoneSameInstant(newZoneId));

    } catch (Exception e) {
        println "Exception changing time zone: " + e;
        return Optional.empty();
    }
}

Optional<ZonedDateTime> changeTimeZone(
    @Nullable ZonedDateTime zonedDateTime,
    @Nullable ZoneOffset newZoneOffset) {

    if (zonedDateTime == null) {
        println "changeTimeZone: zonedDateTime is null";
        return Optional.empty();
    }

    if (newZoneOffset == null) {
        println "changeTimeZone: newZoneOffset is null";
        return Optional.empty();
    }

    try {

        return Optional.of(zonedDateTime.withZoneSameInstant(newZoneOffset));

    } catch (Exception e) {
        println "Exception changing time zone: " + e;
        return Optional.empty();
    }
}

Optional<ZonedDateTime> changeTimeZone(
    @Nullable String zonedDateTime,
    @Nullable String newZoneId) {

    if (isBlank(zonedDateTime)) {
        println "changeTimeZone: zonedDateTime is blank";
        return Optional.empty();
    }

    return changeTimeZone(ZonedDateTime.parse(zonedDateTime), newZoneId);
}

Optional<ZonedDateTime> changeTimeZone(
    @Nullable String zonedDateTime,
    @Nullable ZoneId newZoneId) {

    if (isBlank(zonedDateTime)) {
        println "changeTimeZone: zonedDateTime is blank";
        return Optional.empty();
    }

    return changeTimeZone(ZonedDateTime.parse(zonedDateTime), newZoneId);
}

Optional<ZonedDateTime> changeTimeZone(
    @Nullable String zonedDateTime,
    @Nullable ZoneOffset newZoneOffset) {

    if (isBlank(zonedDateTime)) {
        println "changeTimeZone: zonedDateTime is blank";
        return Optional.empty();
    }

    return changeTimeZone(ZonedDateTime.parse(zonedDateTime), newZoneOffset);
}
