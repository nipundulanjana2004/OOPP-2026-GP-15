package undergraduate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** One session the undergraduate was ABSENT for and which no medical covers yet. */
public class AbsentSession {
    private final java.sql.Date date;
    private final String type;
    private final int number;

    public AbsentSession(java.sql.Date date, String type, int number) {
        this.date = date;
        this.type = type;
        this.number = number;
    }

    public java.sql.Date getDate() { return date; }
    public String getType() { return type; }
    public int getNumber() { return number; }

    /** Days left (including today) in which a medical can still be requested for this absence. */
    public long daysLeft(int windowDays) {
        return windowDays - ChronoUnit.DAYS.between(date.toLocalDate(), LocalDate.now());
    }

    @Override
    public String toString() {
        return date + "  -  " + type + " #" + number;
    }
}
