package uz.uzcard.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final String MM_YY = "MM/yy";

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(MM_YY);

    public static boolean checkExpiredDate(String expDate, LocalDate date) {
        return toFormatMMyy(date).equals(expDate);
    }

    public static String toFormatMMyy(LocalDate date) {
        return date.format(dateTimeFormatter);
    }

    public static LocalDate fromFormatMMyy(String date) {
        YearMonth yearMonth = YearMonth.parse(date, dateTimeFormatter);
        return LocalDate.from(yearMonth.atDay(1));
    }

}
