package triangle.feeder36.Calender;

import android.util.Log;

import java.util.Calendar;

import triangle.feeder36.Log.TLog;


// by default it stores 24hr time
// starts from 00:00:00 ends at 23:59:59
// xy:pq:rs to xy:pq:rs with seconds difference of 86400 is defined A DAY
public class Time {

    public static final String SIMPLE_REPR_SEPARATOR = ":";

    public int $HOUR = -1;     // 0 - 23
    public int $MINUTE = -1;    // 0 - 59
    public int $SECOND = -1;    // 0 - 59

    // constructors
    public Time() {
        unsetTime();
    }

    public Time(boolean setToPresent) {
        if (setToPresent) {
            Calendar currentTime = Calendar.getInstance();
            this.$HOUR = currentTime.get(Calendar.HOUR_OF_DAY);  // 24hr format
            this.$MINUTE = currentTime.get(Calendar.MINUTE);
            this.$SECOND = currentTime.get(Calendar.SECOND);
        }
    }

    public Time(int HOUR,int MINUTE,int SECOND){
        this.$HOUR = HOUR;
        this.$MINUTE = MINUTE;
        this.$SECOND = SECOND;
    }

    //String Format = "HH<separator>MM<separator>SS<separator>...
    //String Format = "HH<separator>MM<separator>SS"
    //if not in any of these then the default initialization
    public Time(String timeString, String separator) {

        //Log.i(TLog.TAG, timeString + "==>" + separator);

        String error = "Time: not a proper Time object initialization with string " + timeString + " and with separator " + separator;

        if (timeString != null && !timeString.matches("") && separator != null && !separator.matches("")) {
            String[] timeComponents = timeString.split(separator);
            if (timeComponents.length >= 3) {
                try {
                    this.$SECOND = Integer.parseInt(timeComponents[2]);
                    this.$MINUTE = Integer.parseInt(timeComponents[1]);
                    this.$HOUR = Integer.parseInt(timeComponents[0]);
                    if (!this.isValid()) {
                        this.unsetTime();
                        Log.i(TLog.TAG, error);
                    }
                } catch (Exception e) {
                    Log.i(TLog.TAG, error);
                    unsetTime();
                }
            } else {
                Log.i(TLog.TAG, error);
                unsetTime();
            }
        } else {
            Log.i(TLog.TAG, error);
            unsetTime();
        }
    }

    //String Format = HH<IN-separator>MM<IN-separator>SS<OUT-separator>
    //if not in any of these then the default initialization
    public Time(String timeString, String inlineSeparator, String outlineSeparator) {

        String error = "Time: not a proper Time object initialization with string " + timeString + " and with inline-separator " + inlineSeparator + " and with outline-separator " + outlineSeparator;

        if (timeString != null && !timeString.matches("") && inlineSeparator != null && !inlineSeparator.matches("") && outlineSeparator != null && !outlineSeparator.matches("")) {
            String[] outlineFilter = timeString.split(outlineSeparator);
            String[] timeComponents = outlineFilter[0].split(inlineSeparator);
            if (timeComponents.length >= 3) {
                try {
                    this.$SECOND = Integer.parseInt(timeComponents[2]);
                    this.$MINUTE = Integer.parseInt(timeComponents[1]);
                    this.$HOUR = Integer.parseInt(timeComponents[0]);
                    if (!this.isValid()) {
                        Log.i(TLog.TAG, error);
                        this.unsetTime();
                    }
                } catch (Exception e) {
                    Log.i(TLog.TAG, error);
                    unsetTime();
                }
            } else {
                Log.i(TLog.TAG, error);
                unsetTime();
            }
        } else {
            Log.i(TLog.TAG, error);
            unsetTime();
        }
    }

    public Time(Time reference) {
        this.$HOUR = reference.$HOUR;
        this.$MINUTE = reference.$MINUTE;
        this.$SECOND = reference.$SECOND;

        if (!this.isValid()) {
            String error = "Time: not a proper Time Object Initialization with reference object " + reference.getTimeString();
            unsetTime();
            Log.i(TLog.TAG, error);
        }
    }

    // DEFAULT INITIALIZATION
    private void unsetTime() {
        this.$HOUR = -1;
        this.$MINUTE = -1;
        this.$SECOND = -1;
    }
    //

    // identifiers
    // 00:00:00 - 23:59:59 are supported
    public boolean isValid() {
        if (this.$HOUR < 0 || this.$HOUR > 23) return false;
        if (this.$MINUTE < 0 || this.$MINUTE > 59) return false;
        if (this.$SECOND < 0 || this.$SECOND > 59) return false;
        return true;
    }
    //

    // formatter
    public String get12HrFormat() {
        if (this.isValid()) {

            int hour = this.$HOUR;
            if (hour > 12) hour -= 12;
            else if (hour == 0) hour = 12;

            String _hour = String.valueOf(hour);
            if (hour < 10) _hour = "0" + _hour;

            String _min = String.valueOf(this.$MINUTE);
            if (this.$MINUTE < 10) _min = "0" + _min;

            String _sec = String.valueOf(this.$SECOND);
            if (this.$SECOND < 10) _sec = "0" + _sec;

            String AM_PM = "AM";
            if (this.$HOUR >= 12) AM_PM = "PM";

            return (_hour + ":" + _min + ":" + _sec + " " + AM_PM);
        }
        return "The time is not properly set ";
    }

    public String get24HrFormat() {
        if (this.isValid()) {

            String hrPart, minPart, secPart;

            if (this.$HOUR < 10) hrPart = "0" + String.valueOf(this.$HOUR);
            else hrPart = String.valueOf(this.$HOUR);

            if (this.$MINUTE < 10) minPart = "0" + String.valueOf(this.$MINUTE);
            else minPart = String.valueOf(this.$MINUTE);

            if (this.$SECOND < 10) secPart = "0" + String.valueOf(this.$SECOND);
            else secPart = String.valueOf(this.$SECOND);

            return hrPart + ":" + minPart + ":" + secPart;
        }
        return "The time is not properly set ";
    }

    public String get24HrFormatWithoutSeconds() {
        if (this.isValid()) {

            String hrPart, minPart, secPart;

            if (this.$HOUR < 10) hrPart = "0" + String.valueOf(this.$HOUR);
            else hrPart = String.valueOf(this.$HOUR);

            if (this.$MINUTE < 10) minPart = "0" + String.valueOf(this.$MINUTE);
            else minPart = String.valueOf(this.$MINUTE);

            if (this.$SECOND < 10) secPart = "0" + String.valueOf(this.$SECOND);
            else secPart = String.valueOf(this.$SECOND);

            return hrPart + ":" + minPart ;
        }
        return "The time is not properly set ";
    }

    public String simpleRepresentation() {
        if (this.isValid()) {
            String hour_part, min_part, sec_part;
            if(this.$HOUR < 10)hour_part = "0" + this.$HOUR;
            else hour_part = String.valueOf(this.$HOUR);
            if(this.$MINUTE < 10)min_part = "0" + this.$MINUTE;
            else min_part = String.valueOf(this.$MINUTE);
            if(this.$SECOND < 10)sec_part = "0" + this.$SECOND;
            else sec_part = String.valueOf(this.$SECOND);

            return hour_part + Time.SIMPLE_REPR_SEPARATOR + min_part + Time.SIMPLE_REPR_SEPARATOR + sec_part;
        }
        return "The time is not properly set ";
    }

    public String getTimeString() {
        return this.$HOUR + ":" + this.$MINUTE + ":" + this.$SECOND;
    }
    //

    // comparisons
    // These functions compare without validity check
    // the time is considered to be in one day only
    public static boolean isFuture(Time A, Time B) {
        // returns A>B
        if (A.$HOUR > B.$HOUR) return true;
        else if (A.$HOUR < B.$HOUR) return false;
        if (A.$MINUTE > B.$MINUTE) return true;
        else if (A.$MINUTE < B.$MINUTE) return false;
        if (A.$SECOND > B.$SECOND) return true;
        else if (A.$SECOND < B.$SECOND) return false;
        return false;
    }

    public static boolean isSame(Time A, Time B) {
        return A.$SECOND == B.$SECOND && A.$MINUTE == B.$MINUTE && A.$HOUR == B.$HOUR;
    }

    public static boolean isPast(Time A, Time B) {
        return Time.isFuture(B, A);
    }

    public boolean isFutureTo(Time B) {
        // returns this>B
        return Time.isFuture(this, B);
    }

    public boolean isPastTo(Time B) {
        // returns this<B
        return Time.isPast(this, B);
    }

    public boolean isEqualTo(Time B) {
        return Time.isSame(this, B);
    }
    //

    // difference
    // return A - B in seconds with sign
    // the time is considered to be in one day only
    public static long timeDifferenceSecondToFirst(Time A, Time B) {
        return (A.$HOUR - B.$HOUR) * Constants.SECONDS_IN_HOUR + (A.$MINUTE - B.$MINUTE) * Constants.SECONDS_IN_MINUTE + (A.$SECOND - B.$SECOND);
    }

    public long timeDifferenceFrom(Time B) {
        return Time.timeDifferenceSecondToFirst(this,B);
    }

    public long timeDifferenceTo(Time A) {
        return Time.timeDifferenceSecondToFirst(A,this);
    }
    //

    //modifiers
    public void toPresent(){
        Calendar currentTime = Calendar.getInstance();
        this.$HOUR = currentTime.get(Calendar.HOUR_OF_DAY);  // 24hr format
        this.$MINUTE = currentTime.get(Calendar.MINUTE);
        this.$SECOND = currentTime.get(Calendar.SECOND);
    }

    public void toStartOfDay(){
        this.$HOUR = 0;  // 24hr format
        this.$MINUTE = 0;
        this.$SECOND = 0;
    }

    public void toThis(Time time){
        if(!time.isValid())unsetTime();
        else {
            this.$HOUR = time.$HOUR;
            this.$MINUTE = time.$MINUTE;
            this.$SECOND = time.$SECOND;
        }
    }

    //adds seconds to this time circularly
    //returns a value(initially 0) which is incremented by 1 every time the time crosses (or reaches) 00:00:00
    private long add(long additional_seconds) {
        if (additional_seconds < 0) return 0;

//        Log.i(TLog.TAG, String.valueOf(additional_seconds));

        //
        long days = (additional_seconds / Constants.SECONDS_IN_DAY);
        int _day = (int) (additional_seconds % Constants.SECONDS_IN_DAY);
        // _day [0,86399] therefore no extra days will come
        int hours = _day / Constants.SECONDS_IN_HOUR;
        int _hour = _day % Constants.SECONDS_IN_HOUR;
        int minutes = _hour / Constants.SECONDS_IN_MINUTE;
        int seconds = _hour % Constants.SECONDS_IN_MINUTE;

        int prev_sec = this.$SECOND;
        int prev_min = this.$MINUTE;
        int prev_hour = this.$HOUR;

        this.$SECOND = (prev_sec + seconds) % Constants.SECONDS_IN_MINUTE;
        int extra_minute = (prev_sec + seconds) / Constants.SECONDS_IN_MINUTE;

        this.$MINUTE = (prev_min + minutes + extra_minute) % Constants.MINUTES_IN_HOUR;
        int extra_hour = (prev_min + minutes + extra_minute) / Constants.MINUTES_IN_HOUR;

        this.$HOUR = (prev_hour + hours + extra_hour) % Constants.HOURS_IN_DAY;

        if (this.$HOUR < prev_hour) {
            days++;// due to day change
        }
        else if(prev_hour == this.$HOUR && this.$MINUTE < prev_min){
            days++;// due to day change
        }
        else if(prev_hour == this.$HOUR && prev_min == this.$MINUTE && this.$SECOND < prev_sec) {
            days++;// due to day change
        }
        return (days);
    }

    //subtracts seconds to this time circularly
    //returns a value(initially 0) which is decremented by 1 every time the time crosses 00:00:00
    private long subtract(long negative_seconds) {
        if (negative_seconds < 0) return 0;
        //
        long days = negative_seconds / Constants.SECONDS_IN_DAY;
        int remaining_seconds = (int) (negative_seconds % Constants.SECONDS_IN_DAY);

        // instead of subtracting add the 1 day - remaining_seconds
        // remaining_seconds [0,86399]
        remaining_seconds = Constants.SECONDS_IN_DAY - remaining_seconds;
        days++;

        days -= this.add(remaining_seconds); // as we are returning -days

        return (-days);
    }

    /**
     * does its operation only if
     * the present object is valid &&
     * the object after algebraic addition is valid
     * otherwise
     * they do nothing to the object
     */
    //adds algebraic seconds to this time circularly
    //returns a value which is no of changes in the dates occurred (00:00:00 is considered the start of a day)
    public long addSecondsReturnChangeInDates(long seconds) {
        if (!this.isValid()) return 0;
        if (seconds == 0) return 0;
        else if (seconds > 0) return this.add(seconds);
        else return this.subtract(-seconds);
    }
    //


//    public static void main(String[] args){
//    }


}

