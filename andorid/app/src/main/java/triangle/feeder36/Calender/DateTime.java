package triangle.feeder36.Calender;

import android.util.Log;

import triangle.feeder36.Log.TLog;

//time is internally implemented in 24hr
public class DateTime {
    /**
     * Separator for fields in simple representation
     * */
    public static final String SIMPLE_REPR_SEPARATOR = " ";

    public Date $DATE; // date object
    public Time $TIME; // time object

    // constructors
    public DateTime() {
        unsetDateTime();
    }

    public DateTime(boolean setToPresent) {
        if (setToPresent) {
            this.$DATE = new Date(true);
            this.$TIME = new Time(true);
        } else {
            unsetDateTime();
        }
    }

    //String Format = DD<date-sep>MonMon<date-sep>YYYY<date-time-sep>HH<time-sep>MinMin<time-sep>SS
    //note that time is in 24hrs
    //all three separators better be different
    //but strictly the dateSep and timeSep can be same and dateTimeSep should be different from these two ALWAYS
    public DateTime(String dateTimeString, String dateSep, String timeSep, String dateTimeSep) {

        String error = "DateTime: not a proper DateTime object initialization with "
                + "\n string " + dateTimeString
                + "\n and with date separator " + dateSep
                + "\n and with time separator " + timeSep
                + "\n and with date-time separator " + dateTimeSep;

        if (dateTimeString != null && !dateTimeString.matches("")
                && dateSep != null && !dateSep.matches("")
                && timeSep != null && !timeSep.matches("")
                && dateTimeSep != null && !dateTimeSep.matches("")
                ) {
            String[] date_time = dateTimeString.split(dateTimeSep);
            if (date_time.length == 2) {
                try {
                    this.$DATE = new Date(date_time[0], dateSep);
                    this.$TIME = new Time(date_time[1], timeSep);
                    if (!this.isValid()) {
                        this.unsetDateTime();
                        Log.i(TLog.TAG, error);
                    }
                } catch (Exception e) {
                    Log.i(TLog.TAG, error);
                    unsetDateTime();
                }
            } else {
                Log.i(TLog.TAG, error);
                unsetDateTime();
            }
        } else {
            Log.i(TLog.TAG, error);
            unsetDateTime();
        }
    }

    public DateTime(Date date){
        if (date.isValid()) {
            this.$DATE = date;
            this.$TIME = new Time();
            this.$TIME.toStartOfDay();
        } else {
            unsetDateTime();
        }
    }

    public DateTime(Date date, Time time) {
        if (date.isValid() && time.isValid()) {
            this.$DATE = date;
            this.$TIME = time;
        } else {
            unsetDateTime();
        }
    }

    public DateTime(DateTime reference) {
        if (reference.isValid()) {
            this.$DATE = new Date(reference.$DATE);
            this.$TIME = new Time(reference.$TIME);
        } else {
            unsetDateTime();
        }
    }

    public void unsetDateTime() {
        this.$DATE = new Date();
        this.$TIME = new Time();
    }
    //

    // identifiers
    public boolean isValid() {
        return this.$DATE.isValid() && this.$TIME.isValid();
    }

    //

    // formatter
    public String formal24Representation() {
        if (this.isValid())
            return this.$DATE.formalRepresentation() + " " + this.$TIME.get24HrFormat();
        else return "This date time is not properly set ";
    }

    public String formal12Representation() {
        if (this.isValid())
            return this.$DATE.formalRepresentation() + " " + this.$TIME.get12HrFormat();
        else return "This date time is not properly set ";
    }

    public String simpleRepresentation() {
        if (this.isValid())
            return this.$DATE.simpleRepresentation() + DateTime.SIMPLE_REPR_SEPARATOR + this.$TIME.simpleRepresentation();
        else return "This date time is not properly set ";
    }

    public String getDateTimeString() {
        return this.$DATE.getDateString() + " " + this.$TIME.getTimeString();
    }

    public String getDay(String format){
        return this.$DATE.getDay(format);
    }
    //

    // comparisons
    // These functions compare without validity check
    public static boolean isFuture(DateTime A, DateTime B) {
        // returns A>B
        if (Date.isFuture(A.$DATE, B.$DATE)) return true;
        else if (Date.isSame(A.$DATE, B.$DATE)) return Time.isFuture(A.$TIME, B.$TIME);
        return false;
    }

    public static boolean isSame(DateTime A, DateTime B) {
        // returns A==B
        return Date.isSame(A.$DATE, B.$DATE) && Time.isSame(A.$TIME, B.$TIME);
    }

    public static boolean isPast(DateTime A, DateTime B) {
        // returns boolean A<B
        return DateTime.isFuture(B, A);
    }

    public boolean isFutureTo(DateTime B) {
        // returns this>B
        return DateTime.isFuture(this, B);
    }

    public boolean isPastTo(DateTime B) {
        // returns this<B
        return DateTime.isPast(this, B);
    }

    public boolean isEqualTo(DateTime B) {
        // returns this==B
        return DateTime.isSame(this, B);
    }

    public boolean isFutureOrEqualTo(DateTime B){
        return DateTime.isSame(this,B) || DateTime.isFuture(this,B);
    }

    public boolean isPastOrEqualTo(DateTime B){
        return DateTime.isSame(this,B) || DateTime.isPast(this,B);
    }
    //

    // difference
    // return A - B in DateTimeDiff with sign
    public static DateTimeDiff dateTimeDifferenceSecondToFirst(DateTime A, DateTime B) {
        return new DateTimeDiff(Date.dayDifferenceSecondToFirst(A.$DATE, B.$DATE), Time.timeDifferenceSecondToFirst(A.$TIME, B.$TIME));
    }

    public DateTimeDiff dateTimeDifferenceFrom(DateTime B) {
        return DateTime.dateTimeDifferenceSecondToFirst(this, B);
    }

    public DateTimeDiff dateTimeDifferenceTo(DateTime A) {
        return DateTime.dateTimeDifferenceSecondToFirst(A, this);
    }
    //

    // modifiers

    public void toPresent() {
        this.$DATE.toPresent();
        this.$TIME.toPresent();
    }

    public void toThis(DateTime dateTime){
        if(!dateTime.isValid())unsetDateTime();
        else {
            this.$DATE.toThis(dateTime.$DATE);
            this.$TIME.toThis(dateTime.$TIME);
        }
    }

    /**
     * does its operation only if
     * the present object is valid &&
     * the object after algebraic addition is valid
     * otherwise
     * they do nothing to the object
     */
    public boolean addDaysSeconds(long days, long seconds) {
        if (!this.isValid()) return false;

        long days_due_to_seconds = this.$TIME.addSecondsReturnChangeInDates(seconds);
        boolean returnValue = this.$DATE.addDays(days + days_due_to_seconds);

        if (!returnValue)
            this.$TIME.addSecondsReturnChangeInDates(-seconds);   // this is for the corner case where change in time reduces date below
        // 01 01 0001
        // this preserves the INVARIANT mentioned above in /***/ comment
        return returnValue;
    }

    public boolean addDateTimeDiff(DateTimeDiff dateTimeDiff) {
        return this.addDaysSeconds(dateTimeDiff.$24hr, dateTimeDiff.$sec);
    }
    //

    // max min
    public static DateTime maximum(DateTime A, DateTime B) {
        if (DateTime.isFuture(A, B)) return A;
        else return B;
    }

    public static DateTime minimum(DateTime A, DateTime B) {
        if (DateTime.isFuture(A, B)) return B;
        else return A;
    }
    //


//    public static void main(String[] args){
//    }


}


//public class DateTime {

//    public DateTime(int YEAR, int MONTH, int DAY, int HOUR, int MINUTE) {
//        this.YEAR = YEAR;
//        this.MONTH = MONTH;
//        this.DAY = DAY;
//        this.HOUR = HOUR;
//        this.MINUTE = MINUTE;
//    }

//    // constructors ..................................................................

//    public Time getTime() {
//        Time time = new Time(this);
//        return time;
//    }
//
//    public Date getDateString() {
//        Date date = new Date(this);
//        return date;
//    }
//
//    public TimeInput getTimeInput() {
//        TimeInput time = new TimeInput(this);
//        return time;
//    }
//
//    public DateInput getDateInput() {
//        DateInput date = new DateInput(this);
//        return date;
//    }
//
//    // Getters and Setters ....................................................................
