package com.example.helpers;

import io.cucumber.java.PendingException;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TimeConverter {

    /**
     * Calculates the fromTime and toTime based on the given dateTime and seconds.
     *
     * @param dateTime the initial date and time as a String in the format "yyyy-MM-dd HH:mm"
     * @param seconds  the number of seconds to adjust from dateTime
     * @return a Map containing fromTime and toTime as LocalDateTime objects
     * @throws IllegalArgumentException if the input dateTime format is invalid
     */
    public static Map<String, LocalDateTime> calculateFromTo(String dateTime, int seconds) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime baseTime = LocalDateTime.parse(dateTime, formatter);

        // Calculate 'from' and 'to' times by adjusting the base time with half of the given seconds
        LocalDateTime fromTime = baseTime.minusSeconds(seconds / 2);
        LocalDateTime toTime = baseTime.plusSeconds(seconds / 2);

        // Create a map to store and return the results
        Map<String, LocalDateTime> result = new HashMap<>();
        result.put("fromTime", fromTime);
        result.put("toTime", toTime);

        return result;
    }

    /**
     * Checks if the timeToCheck is after fromTime and before toTime.
     *
     * @param timeToCheck the time to check
     * @param fromTime      the start time
     * @param toTime        the end time
     * @return true if timeToCheck is after fromTime and before toTime; false otherwise
     * @throws IllegalArgumentException if fromTime is after toTime
     */
    public static boolean isBetween(LocalDateTime timeToCheck, LocalDateTime fromTime, LocalDateTime toTime) {
        if (fromTime.isAfter(toTime)) {
            throw new IllegalArgumentException("fromTime must be before toTime");
        }
        return timeToCheck.isAfter(fromTime) && timeToCheck.isBefore(toTime);
    }

    /**
     * Returns the number of days between two date-time strings in "yyyy-MM-dd HH:mm" format.
     * Adds an extra day if 'to' has time beyond midnight.
     *
     * @return Days between 'from' and 'to', including 'from' if not '00:00'
     * @throws PendingException if 'to' is not after 'from'
     */
    public static int calculateDaysBetween(String from, String to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime fromDateTime = LocalDateTime.parse(from, formatter);
        LocalDateTime toDateTime = LocalDateTime.parse(to, formatter);

        // Throw exception if 'to' is before 'from'
        if (toDateTime.isBefore(fromDateTime)) {
            throw new PendingException("Skipping test as 'to' date-time must be after 'from' date-time.");
        }

        // Return 0 if 'from' and 'to' are the exact same date and time
        if (fromDateTime.equals(toDateTime)) {
            return 0;
        }

        LocalDate fromDate = fromDateTime.toLocalDate();
        LocalDate toDate = toDateTime.toLocalDate();

        // Return 1 if 'from' and 'to' are the same date
        if (fromDate.equals(toDate)) {
            return 1;
        }

        int days = 0;
        while (fromDate.isBefore(toDate)) {
            fromDate = fromDate.plusDays(1);
            days++;
        }

        if (toDateTime.getHour() > 0 || toDateTime.getMinute() > 0) {
            days++;
        }

        return days;
    }

    /**
     * Convert String Last Read to seconds
     * @param lastRead, e.g., "12 days ago" or "3 years ago"
     */
    public static long convertToSeconds(String lastRead) {
        String[] parts = lastRead.split(" ");
        long number = Long.parseLong(parts[0]);
        String unit = parts[1];

        if (unit.startsWith("second")) {
            return number; // Already in seconds
        } else if (unit.startsWith("minute")) {
            return number * 60; // Convert minutes to seconds
        } else if (unit.startsWith("hour")) {
            return number * 60 * 60; // Convert hours to seconds
        } else if (unit.startsWith("day")) {
            return number * 60 * 60 * 24; // Convert days to seconds
        } else if (unit.startsWith("month")) {
            return number * 60 * 60 * 24 * 30; // Approximate a month as 30 days and convert to seconds
        } else if (unit.startsWith("year")) {
            return number * 60 * 60 * 24 * 365; // Approximate a year as 365 days and convert to seconds
        } else {
            throw new IllegalArgumentException("Unknown time unit: " + unit);
        }
    }

    public static long convertToUnixTimeMillis(LocalDateTime localDateTime) {
        // Convert LocalDateTime to ZonedDateTime
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());

        // Convert ZonedDateTime to Instant and get time in milliseconds
        return zonedDateTime.toInstant().toEpochMilli();
    }

    /**
     * Infra_timestamp is seconds from 2000-01-01 * 32768.
     * Method adds time from 1970 to 2000 and then calculate time.
     * @param infraTimestamp
     * @return time in "2020-09-09T23:55" format
     */
    public static LocalDateTime fromInfraTimestamp(String infraTimestamp) {
        return fromInfraTimestamp(infraTimestamp, "UTC");
    }

    public static LocalDateTime fromInfraTimestamp(String infraTimestamp, String timezone) {

        long infraTime = Long.parseLong(infraTimestamp);
        long since1970 = (infraTime  / 32768) + 946684800L; // 1970-01-01 - > 2020-09-09
        Instant instant = Instant.ofEpochSecond(since1970);
        LocalDateTime time = LocalDateTime.ofInstant(instant, ZoneId.of(timezone));
        return time;
    }

    public static String unixMillisToUtcString(final long milliseconds) {
        return Instant.ofEpochMilli(milliseconds)
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public static LocalDateTime fromTimestamp(String unixTime) {
        return fromTimestamp(unixTime, "UTC");
    }

    /**
     * Converts a Unix timestamp (in seconds) to LocalDateTime in the specified timezone.
     *
     * @param unixTime the Unix timestamp (seconds since epoch)
     * @param zoneId the timezone ID (e.g., "Europe/Stockholm")
     * @return the corresponding LocalDateTime
     * @throws IllegalArgumentException if the zoneId is invalid
     */
    public static LocalDateTime fromTimestamp(String unixTime, String zoneId) {
        try {
            ZoneId zone = ZoneId.of(zoneId);
            return Instant.ofEpochSecond(Long.parseLong(unixTime))
                    .atZone(zone)
                    .toLocalDateTime();
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid timezone: " + zoneId, e);
        }
    }

    private static long toTimestamp(final String time) {
        LocalDateTime dateTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        long unix = dateTime.toEpochSecond(ZoneOffset.UTC);

        return (unix - 946684800) * 32768;
    }

    /**
     * Takes a to-from-String and modifies it.
     * @param time eg "09:00- 17:00" or "11:00 - 14:00"
     * @return eg 09-17 or 11-14
     */
    public static String convertToFullHours(String time) {
        // Split the string by "-"
        String[] times = time.split("-");

        // Split the times by ":" and keep only the hour (the first part)
        String startHour = times[0].trim().split(":")[0];
        String endHour = times[1].trim().split(":")[0];

        return startHour + "-" + endHour;
    }

    /**
     * @return How many days last month had. Ie. a call in November returns 31-
     */
    public static int getDaysInLastMonth() {
        LocalDate currentDate = LocalDate.now();
        LocalDate firstDayOfLastMonth = currentDate.minusMonths(1).withDayOfMonth(1);
        return firstDayOfLastMonth.lengthOfMonth();
    }

    public static String getFirstDateThisMonth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstDayOfCurrentMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return firstDayOfCurrentMonth.format(formatter);
    }
    public static String getFirstDateLastMonth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstDayOfLastMonth = now.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return firstDayOfLastMonth.format(formatter);
    }

    /**
     * @param dateString like ""Mon Jan 22 09:50:28 CET 2024"
     */
    public static Date convertStringToDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);
            return Date.from(zonedDateTime.toInstant());
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing the date: " + e.getMessage());
            return null; // or handle the error as per your requirement
        }
    }

    public static boolean isAfterNow(Date date) {
        return date.after(new Date());
    }
    
    public static boolean firstIsAfterSecond(final String unixTime, final String stringTime) {
        LocalDateTime firstTime = fromTimestamp(unixTime);
        LocalDateTime secondTime = parseDateAndTime(stringTime);
        return firstTime.isAfter(secondTime);
    }

    /**
     * Is used for parsing 'oral' time description to LDT.
     * @param timeDescription Any type of description
     * @return A custom LDT for the description.
     */
    public static LocalDateTime parseTimeDescriptionToLDT(String timeDescription) {
        return switch (timeDescription) {
            case "now minus seven days" -> LocalDateTime.now().minusDays(7).withSecond(0).withNano(0);
            case "today 23:59" -> LocalDateTime.now().withHour(23).withMinute(59).withSecond(0).withNano(0);
            case "today" -> LocalDate.now().atStartOfDay();
            case "now" -> LocalDateTime.now();
            default -> throw new IllegalStateException("Unexpected value: " + timeDescription);
        };
    }

    /**
     * @param reportDuration like "2024-05-28 00:00 – 2024-06-04 00:00 (Europe/Stockholm)"
     * @return  LDT of '2024-05-28 00:00' or '2024-06-04 00:00'
     */
    public static LocalDateTime getReportTime(String timePart, String reportDuration) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        switch (timePart) {
            case "startTime" -> { return LocalDateTime.parse(reportDuration.substring(0, reportDuration.indexOf(" – ")), formatter); }
            case "endTime" -> { return LocalDateTime.parse(reportDuration.substring(reportDuration.indexOf(" – ") + 3, reportDuration.indexOf(" (")), formatter); }
            default -> { return null; }
        }
    }

    /**
     * @param firstDate ie 2024-05-22
     * @param secondDate ie 2024-03-20
     * @return returns first if the first date is after or the same as the second date. 2024-03-20.isAfter(2024-05-22) = false
     */
    public static boolean isSecondDateAfter(String firstDate, String secondDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate first = LocalDate.parse(firstDate, formatter);
        LocalDate second = LocalDate.parse(secondDate, formatter);
//        System.out.println(first + " -> " + second);
        return second.isAfter(first);
    }

    public static String convertToStandardizedDateTime(final String dateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return switch (dateTime) {
            case "NOW" -> LocalDateTime.now().format(formatter);
            case "MINUS-1-YEAR" -> LocalDateTime.now().minusYears(1).format(formatter);
            case "PLUS-1-YEAR" -> LocalDateTime.now().plusYears(1).format(formatter);
            case "MINUS-1-MONTH" -> LocalDateTime.now().minusMonths(1).format(formatter);
            case "PLUS-1-MONTH" -> LocalDateTime.now().plusMonths(1).format(formatter);
            case "MINUS-1-WEEK" -> LocalDateTime.now().minusWeeks(1).format(formatter);
            case "PLUS-1-WEEK" -> LocalDateTime.now().plusWeeks(1).format(formatter);
            case "MINUS-1-DAY" -> LocalDateTime.now().minusDays(1).format(formatter);
            case "PLUS-1-DAY" -> LocalDateTime.now().plusDays(1).format(formatter);
            default -> {
                try {
                    yield LocalDateTime.parse(dateTime, formatter).format(formatter);
                } catch (RuntimeException e) {
                    System.out.println("Failed to parse date: " + dateTime);
                    throw new RuntimeException(e);
                }
            }
        };
    }

    /**
     * @param s time as in '2024-07-24 12:39'
     */
    public static LocalDateTime parseDateAndTime(String s) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(s, formatter);
    }

    /**
     * @param date time as in '2024-07-24 12:39'
     */
    public static LocalDateTime parseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date).atStartOfDay();
    }

    public static String getNowTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime nowLDT = LocalDateTime.now();
        return LocalDateTime.now().format(formatter);
    }

    /**
     * Gets the current LocalDateTime in the specified timezone.
     *
     * @param zoneId the timezone ID (e.g., "Europe/Stockholm")
     * @return the LocalDateTime in the specified timezone
     * @throws IllegalArgumentException if the timezone is invalid
     */
    public static LocalDateTime getLocalDateTime(String zoneId) {
        try {
            ZoneId zone = ZoneId.of(zoneId);
            return ZonedDateTime.now(zone).toLocalDateTime();
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid timezone: " + zoneId, e);
        }
    }

    /**
     * Checks if the first LocalDateTime is more than 24 hours before the second LocalDateTime.
     *
     * @param first the first LocalDateTime
     * @param second the second LocalDateTime
     * @return true if first is more than 24 hours before second, false otherwise
     * @throws IllegalArgumentException if first is equal to or after second
     */
    public static boolean isMoreThan24HoursBefore(LocalDateTime first, LocalDateTime second) {
        if (!first.isBefore(second)) {
            throw new IllegalArgumentException("First LocalDateTime must be strictly before the second.");
        }
        return Duration.between(first, second).toHours() > 24;
    }

    /**
     * @return true if ldt1 is equal, or not more before than one minute.
     */
    public static boolean isNotMoreThanAMinuteBefore(LocalDateTime ldt1, LocalDateTime ldt2) {
        Duration duration = Duration.between(ldt1, ldt2);
        return !duration.isNegative() && duration.toMinutes() <= 1;
    }

    public static String deductTimezone(String timezone) {
        return switch (timezone) {
                case "Europe/Stockholm" -> "UTC";
                // build further as needed
            default -> throw new IllegalStateException("Unknown timezone: " + timezone);
        };
    }

    /**
     * @param calibrationInfo i.e., 'Calibration date: 2000-01-01'
     * @return LocalDateTime i.e., '2000-01-01T00:00'
     */
    public static LocalDateTime extractDateFromCalibrationInfo(String calibrationInfo) {
        // Define the expected prefix
        String prefix = "Calibration date: ";

        // Validate and extract the date part
        if (calibrationInfo == null || !calibrationInfo.startsWith(prefix)) {
            throw new IllegalArgumentException("Invalid format: Expected 'Calibration date: yyyy-MM-dd'");
        }

        String datePart = calibrationInfo.substring(prefix.length());

        // Parse into LocalDate
        LocalDate date = LocalDate.parse(datePart, DateTimeFormatter.ISO_LOCAL_DATE);

        // Convert to LocalDateTime at midnight
        return date.atStartOfDay();
    }

    /**
     * @param seconds Expected difference
     * @return true, if difference is <= seconds
     */
    public static boolean isWithinThisManySeconds(LocalDateTime ldt1, LocalDateTime ldt2, int seconds) {
        // Calculate the duration between the two LocalDateTime instances
        Duration duration = Duration.between(ldt1, ldt2);

        // Get the absolute value of the duration in seconds
        long secondsDifference = Math.abs(duration.getSeconds());
        return secondsDifference <= seconds;
    }

    public static long calculateSecondsBetween(LocalDateTime ldt1, LocalDateTime ldt2) {
        return Duration.between(ldt1, ldt2).getSeconds();
    }

    public static LocalDateTime parseToLDT(String timeToFormat, String requestedFormat) {
        return LocalDateTime.parse(timeToFormat, DateTimeFormatter.ofPattern(requestedFormat));
    }

    /**
     * @return true if the passed unix timestamp is older than 5 hours.
     */
    public boolean isOlderThan(long unixTimeStampMillis, long dueTime) {
        long fiveHoursInMillis = dueTime * 60 * 60 * 1000;

        return System.currentTimeMillis() - unixTimeStampMillis > fiveHoursInMillis;
    }


}
