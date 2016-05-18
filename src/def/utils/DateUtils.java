package def.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Simple utility class for Date processing.
 * @author ovoievodin
 */
public class DateUtils {
	
	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	
	/**
	 * Parse the input string, according to the specified time format, 
	 * returning today date with parsed time instant.
	 * A bit of Java 8 API here for parsing simplicity.
	 * @param source input string to parse.
	 * @return Parsed Date object, if the parsing succeeds<br>otherwise -> null.
	 * @author ovoievodin
	 */
	public static Date parseTime(String source) {
		try {
			LocalTime time = LocalTime.parse(source, timeFormatter);
			LocalDateTime date = LocalDateTime.now()
					.withHour(time.getHour())
					.withMinute(time.getMinute())
					.withSecond(0);
			
			return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
			
		} catch (Exception e) {
			System.err.println("Date parsing failed for input: " + source);
			return null;
		}
	}
}
