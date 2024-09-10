package com.example.medimap.roomdb;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Converters {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @TypeConverter
    public static LocalDate fromString(String value) {
        return value == null ? null : LocalDate.parse(value, formatter);
    }

    @TypeConverter
    public static String localDateToString(LocalDate date) {
        return date == null ? null : date.format(formatter);
    }

    @TypeConverter
    public static Date localDateToDate(LocalDate localDate){
        ///Mine
        //return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        //His
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    @TypeConverter
    public static LocalDate DateToLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
