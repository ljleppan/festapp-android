package com.futurice.festapp.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CalendarUtil {
	
	private static final Map<Integer, String> weekDays = new HashMap<Integer, String>();
	private static final String DATE_PARSER_TEMPLATE = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";	//2001-07-04T12:08:56.235-0700
	private static final DateFormat DATE_PARSER = new SimpleDateFormat(DATE_PARSER_TEMPLATE);
	private static final DateFormat DB_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	static {
		weekDays.put(Calendar.MONDAY, "Maanantai");
		weekDays.put(Calendar.TUESDAY, "Tiistai");
		weekDays.put(Calendar.WEDNESDAY, "Keskiviikko");
		weekDays.put(Calendar.THURSDAY, "Torstai");
		weekDays.put(Calendar.FRIDAY, "Perjantai");
		weekDays.put(Calendar.SATURDAY, "Lauantai");
		weekDays.put(Calendar.SUNDAY, "Sunnuntai");
	}
	
	public static String getFullWeekdayName(int weekday) {
		return weekDays.get(weekday);
	}
	
	public static String getShortWeekdayName(int weekday) {
		return weekDays.get(weekday).substring(0, 1);
	}
	
	public static int getMinutesBetweenTwoDates(Date start, Date end) {
		long diff = Math.abs(end.getTime() - start.getTime());
		return (int) (diff / 1000 / 60);
	}

	public static Date parseDateFromString(String string) throws ParseException {
		return DATE_PARSER.parse(string);
	}

	/**
	 * Formats date for SQLite database compatible string
	 * @param date value to format
	 * @return formatted date string
	 */
	public static String dbFormat(Date date) {
		return DB_DATE_FORMATTER.format(date);
	}
	
	/**
	 * Parses an SQLite date string to Java date object
	 * @param string value to parse
	 * @return parsed date
	 * @throws ParseException in case the string is invalid
	 */
	public static Date getDbDate(String string) throws ParseException{
		return DB_DATE_FORMATTER.parse(string);
	}
}
