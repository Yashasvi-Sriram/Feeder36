package triangle.feeder36.Calender;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import triangle.feeder36.Log.TLog;


/**
 * Date consists of three fields <br>
 * {@link #$YEAR}<br>
 * {@link #$MONTH}<br>
 * {@link #$DAY}<br>
 * <br>
 * <br>
 * Class used to handle dates.<br>
 * Supports handling, comparison, output-formatting, manipulation of dates<br>
 * supports dates from 01/01/0001 A.D. in Gregorian Calender.<br>
 * When ever I use Epoch, that means date is 01/01/0001.<br>
 * Do not access dates before the Epoch.<br>
 * <br>
 * <br>
 * <p>
 * The StdForm is set of the fields<br>
 * {@link #$STD_YEAR}<br> {@link #$STD_MONTH}<br> {@link #$STD_DAY}<br> {@link #$MONTH_EXTRA_DAYS}<br> {@link #$LEAP_EXTRA_DAYS}<br>
 * <br>
 * StdForm of a date is such that<br>
 * std_year * 365 + std_month * 30 + std_day + leap_extras + month_extras<br>
 * will give (no of days passed from Epoch) + 1<br>
 * <pre>
 * Ex : for
 *      01/01/0001 => 1
 *      02/01/0001 => 2
 * </pre>
 * This is seeing Gregorian Calender (which has so many irregularities)<br>
 * as a uniform calender model<br>
 * with NOISE of extra days in leap years<br>
 * and extra days due to different number of days in months<br>
 *
 * @author pandu
 * @version 1.0
 *
 */

public class Date {

    /**
     * Separator for fields in simple representation
     * */
    public static final String SIMPLE_REPR_SEPARATOR = "/";

    /**
     * 1 - +inf.
     * Initially set to -1
     */
    public int $YEAR = -1;
    /**
     * 1 - 12
     * Initially set to -1
     */
    public int $MONTH = -1;
    /**
     * 1 - 31
     * Initially set to -1
     */
    public int $DAY = -1;

    /**
     * To maintain NOISE due to leap years
     */
    public int $LEAP_EXTRA_DAYS = -1;
    /**
     * To maintain NOISE due to months
     */
    public int $MONTH_EXTRA_DAYS = -1;   // due to varying no days in months , equals
    /**
     * Number of standard years
     */
    public int $STD_YEAR = -1;
    /**
     * Number of standard months
     */
    public int $STD_MONTH = -1;
    /**
     * Number of standard days
     */
    public int $STD_DAY = -1;

    /**
     * Default Constructor sets the date to a non-realistic value
     *
     * @see #unsetDate()
     */
    public Date() {
        unsetDate();
    }

    /**
     * @param setToPresent if true the date object is set to the present date<br>
     *                     if false sets the date to a non-realistic value
     * @see #unsetDate()
     */
    public Date(boolean setToPresent) {
        if (setToPresent) {
            Calendar currentTime = Calendar.getInstance();
            this.$DAY = currentTime.get(Calendar.DAY_OF_MONTH);
            this.$MONTH = currentTime.get(Calendar.MONTH) + 1;
            this.$YEAR = currentTime.get(Calendar.YEAR);
        } else this.unsetDate();
    }

    public Date(int YEAR, int MONTH, int DAY){
        this.$YEAR = YEAR;
        this.$MONTH = MONTH;
        this.$DAY = DAY;

        if(!this.isValid()) this.unsetDate();
    }

    /**
     * Extracts Date Information from a string<br>
     * Formats allowed are :<br>
     * "DD<separator>MonMon<separator>YYYY<separator>...<br>
     * "DD<separator>MonMon<separator>YYYY"<br>
     *
     * @param dateString String in any of the above specified formats
     * @param separator  String that separates different parts of the date as specified in above formats
     *                   <p>
     *                   If the date is is NOT Valid or the String is NOT in the format then sets the date to a non-realistic value<br>
     *                   and logs the problem.<br>
     *                   NOTE : preferably do not use unknown separators, DEFINITELY NOT "."<br>
     * @see #unsetDate()
     * @see #isValid()
     */

    public Date(String dateString, String separator) {

        //Log.i(TLog.TAG, dateString + "==>" + separator);

        String error = "Date: not a proper Date object initialization with string " + dateString + " and with separator " + separator;

        if (dateString != null && !dateString.matches("") && separator != null && !separator.matches("")) {
            String[] dateComponents = dateString.split(separator);
            if (dateComponents.length >= 3) {
                try {
                    this.$YEAR = Integer.parseInt(dateComponents[2]);
                    this.$MONTH = Integer.parseInt(dateComponents[1]);
                    this.$DAY = Integer.parseInt(dateComponents[0]);
                    if (!this.isValid()) {
                        this.unsetDate();
                        Log.i(TLog.TAG, error);
                    }
                } catch (Exception e) {
                    Log.i(TLog.TAG, error);
                    unsetDate();
                }
            } else {
                Log.i(TLog.TAG, error);
                unsetDate();
            }
        } else {
            Log.i(TLog.TAG, error);
            unsetDate();
        }
    }

    /**
     * Extracts Date Information from a string<br>
     * Formats allowed are :<br>
     * DD<IN-separator>MonMon<IN-separator>YYYY<OUT-separator><br>
     *
     * @param dateString       String in any of the above specified formats
     * @param inlineSeparator  String that separates inner parts of the date as specified in above formats
     * @param outlineSeparator String that separates the date from other string as specified in above formats
     *                         <p>
     *                         If the date is is NOT Valid or the String is NOT in the format then sets the date to a non-realistic value<br>
     *                         and logs the problem.
     *                         NOTE : preferably do not use unknown separators, DEFINITELY NOT "."<br>
     * @see #unsetDate()
     * @see #isValid()
     */
    public Date(String dateString, String inlineSeparator, String outlineSeparator) {

        String error = "Date: not a proper Date object initialization with string " + dateString + " and with inline-separator " + inlineSeparator + " and with outline-separator " + outlineSeparator;

        if (dateString != null && !dateString.matches("") && inlineSeparator != null && !inlineSeparator.matches("") && outlineSeparator != null && !outlineSeparator.matches("")) {
            String[] outlineFilter = dateString.split(outlineSeparator);
            String[] dateComponents = outlineFilter[0].split(inlineSeparator);
            if (dateComponents.length >= 3) {
                try {
                    this.$YEAR = Integer.parseInt(dateComponents[2]);
                    this.$MONTH = Integer.parseInt(dateComponents[1]);
                    this.$DAY = Integer.parseInt(dateComponents[0]);
                    if (!this.isValid()) {
                        Log.i(TLog.TAG, error);
                        this.unsetDate();
                    }
                } catch (Exception e) {
                    Log.i(TLog.TAG, error);
                    unsetDate();
                }
            } else {
                Log.i(TLog.TAG, error);
                unsetDate();
            }
        } else {
            Log.i(TLog.TAG, error);
            unsetDate();
        }
    }

    /**
     * Copies the date and its StdForm<br>
     * If the date is is NOT Valid then sets the date to a non-realistic value and logs the problem.<br>
     *
     * @see #unsetDate()
     * @see #isValid()
     */
    public Date(Date reference) {
        this.$YEAR = reference.$YEAR;
        this.$MONTH = reference.$MONTH;
        this.$DAY = reference.$DAY;

        this.$LEAP_EXTRA_DAYS = reference.$LEAP_EXTRA_DAYS;     // due to leap years
        this.$MONTH_EXTRA_DAYS = reference.$MONTH_EXTRA_DAYS;   // due to varying no days in months , equals
        this.$STD_YEAR = reference.$STD_YEAR;
        this.$STD_MONTH = reference.$STD_MONTH;
        this.$STD_DAY = reference.$STD_DAY;

        if (!this.isValid()) {
            String error = "Date: not a proper Date Object Initialization with reference object DD/MM/YYYY " + reference.getDateString();
            unsetDate();
            Log.i(TLog.TAG, error);
        }
    }

    /**
     * Sets the Date and it's StdForm to a non-realistic value
     */
    public void unsetDate() {
        this.$YEAR = -1;
        this.$MONTH = -1;
        this.$DAY = -1;

        this.$LEAP_EXTRA_DAYS = -1;    // due to leap years
        this.$MONTH_EXTRA_DAYS = -1;   // due to varying no days in months , equals
        this.$STD_YEAR = -1;
        this.$STD_MONTH = -1;
        this.$STD_DAY = -1;
    }
    //

    // identifiers

    /**
     * returns whether this date object is valid or not<br>
     * 01/01/0001 in Gregorian Calender is the least date supported<br>
     */
    public boolean isValid() {
        if (this.$YEAR < 1) return false;
        if (this.$MONTH < 1 || this.$MONTH > 12) return false;
        switch (this.$MONTH % 2) {
            case 1:
                if (this.$MONTH >= 9) return !(this.$DAY < 1 || this.$DAY > 30);
                else return !(this.$DAY < 1 || this.$DAY > 31);
            case 0:
                if (this.$MONTH == 2) {
                    if (this.isLeapYear()) return !(this.$DAY < 1 || this.$DAY > 29);
                    else {
                        return !(this.$DAY < 1 || this.$DAY > 28);
                    }
                } else if (this.$MONTH >= 8) return !(this.$DAY < 1 || this.$DAY > 31);
                else if (this.$MONTH < 8) return !(this.$DAY < 1 || this.$DAY > 30);
            default:
                return false;
        }
    }

    /**
     * returns whether the date object is a leap year or not<br>
     */
    private boolean isLeapYear() {
        int P = this.$YEAR % 400;
        int Q = P % 100;
        int R = Q % 4;

        if (P == 0) {
            return true;
        }
        if (Q == 0) {
            return false;
        }
        if (R == 0) {
            return true;
        }
        return false;
    }
    //

    // formatter

    /**
     * returns String with the date in the format xth Mon YYYY<br>
     * If NOT valid returns The date is not properly set
     */
    public String formalRepresentation() {
        if (isValid()) {
            String suffix;
            String MonthName;
            MonthName = Constants.MONTH_NAMES[this.$MONTH - 1];
            if ((this.$DAY - 1) % 10 < 3) suffix = Constants.SUFFIXES[(this.$DAY - 1) % 10];
            else suffix = Constants.SUFFIXES[3];
            if (this.$DAY == 11 || this.$DAY == 12 || this.$DAY == 13)
                suffix = "th";
            return (this.$DAY + suffix + " " + MonthName + " " + this.$YEAR);
        }
        return "The date is not properly set ";
    }

    /**
     * returns String with the date in the format dd/mm/yyyy<br>
     * If NOT valid returns The date is not properly set<br>
     * <p>
     * Difference between this method and {@link #getDateString()} is that this method validates the date while the latter does not
     */
    public String simpleRepresentation() {
        if (this.isValid()) {
            String year_part, month_part, day_part;
            if(this.$YEAR < 10)year_part = "0" + this.$YEAR;
            else year_part = String.valueOf(this.$YEAR);
            if(this.$MONTH < 10)month_part = "0" + this.$MONTH;
            else month_part = String.valueOf(this.$MONTH);
            if(this.$DAY < 10)day_part = "0" + this.$DAY;
            else day_part = String.valueOf(this.$DAY);

            return day_part + Date.SIMPLE_REPR_SEPARATOR + month_part + Date.SIMPLE_REPR_SEPARATOR + year_part ;
        }
        return "The date is not properly set ";
    }

    /**
     * returns String with the date in the format xth Mon YYYY<br>
     * <p>
     * Difference between this method and {@link #simpleRepresentation()} is that this method DOES NOT validate the date while the latter does
     */
    public String getDateString() {
        return this.$DAY + "/" + this.$MONTH + "/" + this.$YEAR;
    }

    /**
     * returns the day of the date in the given format<br>
     * Ex : format == "EE" => ret == "Mon" or "Tue" ("Xxx")<br>
     *
     * @param format the format of the SimpleDateFormat class in java.text package<br>
     *               TODO: improve this function
     */
    public String getDay(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        java.util.Date d = new java.util.Date(this.$YEAR - 1900, this.$MONTH - 1, this.$DAY);
        String dayOfTheWeek = sdf.format(d);
        return dayOfTheWeek;
    }
    //

    /**
     * Prints the date and StdForm in the System console <b>without Validation</b><br>
     * For <b> Developing purpose </b><br>
     */
    public void printState() {
        System.out.print("\n year : ");
        System.out.print(this.$YEAR);
        System.out.print("\n month : ");
        System.out.print(this.$MONTH);
        System.out.print("\n day : ");
        System.out.print(this.$DAY);
        System.out.print("\n std_year : ");
        System.out.print(this.$STD_YEAR);
        System.out.print("\n std_month : ");
        System.out.print(this.$STD_MONTH);
        System.out.print("\n std_day : ");
        System.out.print(this.$STD_DAY);
        System.out.print("\n leap_extras : ");
        System.out.print(this.$LEAP_EXTRA_DAYS);
        System.out.print("\n month_extras : ");
        System.out.print(this.$MONTH_EXTRA_DAYS);
        System.out.print("\n");

    }

    /**
     * Prints the date in the System console <b>without Validation</b><br>
     * For <b> Developing purpose </b><br>
     */
    public void printDate() {
        System.out.print("\n");
        System.out.print(this.$DAY);
        System.out.print("/");
        System.out.print(this.$MONTH);
        System.out.print("/");
        System.out.print(this.$YEAR);
        System.out.print("\n");
    }

    // comparisons
    /**
     * returns whether first date param is in future to second date param<br>
     * <b>DOES NOT CHECK VALIDITY</b>
     * @param A Date Instance
     * @param B Date Instance
     * */
    public static boolean isFuture(Date A, Date B) {
        // returns A>B
        if (A.$YEAR > B.$YEAR) return true;
        else if (A.$YEAR < B.$YEAR) return false;
        if (A.$MONTH > B.$MONTH) return true;
        else if (A.$MONTH < B.$MONTH) return false;
        if (A.$DAY > B.$DAY) return true;
        else if (A.$DAY < B.$DAY) return false;
        return false;
    }

    /**
     * returns whether first date param is same as second date param<br>
     * <b>DOES NOT CHECK VALIDITY</b>
     * @param A Date Instance
     * @param B Date Instance
     * */
    public static boolean isSame(Date A, Date B) {
        return A.$DAY == B.$DAY && A.$MONTH == B.$MONTH && A.$YEAR == B.$YEAR;
    }

    /**
     * returns whether first date param is in past to second date param<br>
     * <b>DOES NOT CHECK VALIDITY</b>
     * @param A Date Instance
     * @param B Date Instance
     * */
    public static boolean isPast(Date A, Date B) {
        return Date.isFuture(B, A);
    }

    /**
     * returns whether this date instance is in future to date param<br>
     * <b>DOES NOT CHECK VALIDITY</b>
     * @param B Date Instance
     * */
    public boolean isFutureTo(Date B) {
        // returns this>B
        return Date.isFuture(this, B);
    }

    /**
     * returns whether this date instance is in past to date param<br>
     * <b>DOES NOT CHECK VALIDITY</b>
     * @param B Date Instance
     * */
    public boolean isPastTo(Date B) {
        // returns this<B
        return Date.isPast(this, B);
    }

    /**
     * returns whether this date instance is same as date param<br>
     * <b>DOES NOT CHECK VALIDITY</b>
     * @param B Date Instance
     * */
    public boolean isEqualTo(Date B) {
        // returns this==B
        return Date.isSame(this, B);
    }
    //

    // Helpers
    /**
     * Prepares StdForm from the date and returns true<br>
     * if date is NOT VALID then DOES NOTHING and returns false<br>
     * */
    private boolean prepareStdForm() {
        if (!this.isValid()) return false;
        // valid Date
        // day related issues
        this.$STD_DAY = this.$DAY;
        // month related issues
        this.$STD_MONTH = this.$MONTH - 1;
        if (this.$MONTH > 1)
            this.$MONTH_EXTRA_DAYS = Constants.STD_MONTH_EXTRAS_ARRAY[this.$MONTH - 2];//-2 bcz   // 1 for array start
        else this.$MONTH_EXTRA_DAYS = 0;
        // year related issues
        this.$STD_YEAR = this.$YEAR - 1;
        this.$LEAP_EXTRA_DAYS = this.$YEAR / 4 - this.$YEAR / 100 + this.$YEAR / 400;
        if (this.isLeapYear() && this.$MONTH < 3 && this.$LEAP_EXTRA_DAYS > 0)
            this.$LEAP_EXTRA_DAYS--;

        return true;
    }

    /**
     * returns day from epoch<br>
     * Epoch is 01/01/0001<br>
     * Do not access dates before the Epoch<br>
     * If the Date is 01/01/0001 the result is 0<br>
     * */
    public long daysFromEpoch() {
        if (this.prepareStdForm())
            return (this.$STD_YEAR) * Constants.DAYS_IN_STD_YEAR + (this.$STD_MONTH) * Constants.DAYS_IN_STD_MONTH + (this.$STD_DAY) + (this.$LEAP_EXTRA_DAYS) + (this.$MONTH_EXTRA_DAYS) - 1;// refer to std form definition
        // else return -1 , means invalid Date
        return -1;
    }
    //

    // difference
    /**
     * returns (first param - second param) in days with sign<br>
     * @param A Date Instance
     * @param B Date Instance
     * */
    public static long dayDifferenceSecondToFirst(Date A, Date B) {
        if (!A.isValid() || !B.isValid()) return 0;
        A.prepareStdForm();
        B.prepareStdForm();
        return (A.$STD_YEAR - B.$STD_YEAR) * Constants.DAYS_IN_STD_YEAR + (A.$STD_MONTH - B.$STD_MONTH) * Constants.DAYS_IN_STD_MONTH + (A.$STD_DAY - B.$STD_DAY) + (A.$LEAP_EXTRA_DAYS - B.$LEAP_EXTRA_DAYS) + (A.$MONTH_EXTRA_DAYS - B.$MONTH_EXTRA_DAYS);
    }

    // returns PARAM TO this in days with sign
    /**
     * returns (this date - second param) in days with sign<br>
     * @param param Date Instance
     * */
    public long dayDifferenceFrom(Date param) {
        return Date.dayDifferenceSecondToFirst(this, param);
    }

    // returns this TO PARAM in days with sign
    /**
     * returns (second param - this date) in days with sign<br>
     * @param param Date Instance
     * */
    public long dayDifferenceTo(Date param) {
        return Date.dayDifferenceSecondToFirst(param, this);
    }
    //

    // modifiers
    /**
     * Sets this date object to present date
     * */
    public void toPresent() {
        Calendar currentTime = Calendar.getInstance();
        this.$DAY = currentTime.get(Calendar.DAY_OF_MONTH);
        this.$MONTH = currentTime.get(Calendar.MONTH) + 1;
        this.$YEAR = currentTime.get(Calendar.YEAR);
    }

    /**
     * @param date sets this object to match the param if valid
     *             else sets to unrealistic value {@link Date#unsetDate()}
     * */
    public void toThis(Date date){
        if(!date.isValid())unsetDate();
        else {
            this.$YEAR = date.$YEAR;
            this.$MONTH = date.$MONTH;
            this.$DAY = date.$DAY;
        }
    }

    /**
     * If the date is valid then converts date to next date and returns true<br>
     * If NOT VALID then returns false
     * do not access dates before 01/01/0001<br>
     * if object is not valid return false and DOES NOTHING
     * */
    public boolean toTomorrow() {
        if (!this.isValid()) return false;
        if (this.isLeapYear()) {
            if (this.$DAY + 1 > Constants.DAYS_IN_MONTH_LY[this.$MONTH - 1]) {
                if (this.$MONTH + 1 > 12) {
                    this.$YEAR += 1;
                    this.$MONTH = 1;
                    this.$DAY = 1;
                } else {
                    this.$MONTH += 1;
                    this.$DAY = 1;
                    return true;
                }
            } else {
                this.$DAY += 1;
                return true;
            }
        } else {
            if (this.$DAY + 1 > Constants.DAYS_IN_MONTH_NLY[this.$MONTH - 1]) {
                if (this.$MONTH + 1 > 12) {
                    this.$YEAR += 1;
                    this.$MONTH = 1;
                    this.$DAY = 1;
                } else {
                    this.$MONTH += 1;
                    this.$DAY = 1;
                    return true;
                }
            } else {
                this.$DAY += 1;
                return true;
            }
        }
        return false;
    }

    /**
     * If the date is valid then converts date to previous date and returns true<br>
     * If NOT VALID then returns false<br>
     * do not access dates before 01/01/0001<br>
     * if object is not valid return false and DOES NOTHING
     * */
    public boolean toYesterday() {
        if (!this.isValid()) return false;
        if (this.isLeapYear()) {
            if (this.$DAY - 1 < 1) {
                if (this.$MONTH - 1 < 1) {
                    this.$YEAR -= 1;
                    this.$MONTH = 12;
                    this.$DAY = 31;
                } else {
                    this.$MONTH -= 1;
                    this.$DAY = Constants.DAYS_IN_MONTH_LY[this.$MONTH - 1];
                    return true;
                }
            } else {
                this.$DAY -= 1;
                return true;
            }
        } else {
            if (this.$DAY - 1 < 1) {
                if (this.$MONTH - 1 < 1) {
                    this.$YEAR -= 1;
                    this.$MONTH = 12;
                    this.$DAY = 31;
                } else {
                    this.$MONTH -= 1;
                    this.$DAY = Constants.DAYS_IN_MONTH_NLY[this.$MONTH - 1];
                    return true;
                }
            } else {
                this.$DAY -= 1;
                return true;
            }
        }
        return false;
    }

    /**
     * adds param number of days to this date<br>
     * do not access dates before 01/01/0001<br>
     * time complexity is LINEAR in no days to be added<br>
     * if object is not valid or param is negative return false and DOES NOTHING
     * */
    private boolean add(long noDays) {
        if (noDays < 0) return false;
        if (!this.isValid()) return false;
        for (int i = 0; i < noDays; i++) {
            this.toTomorrow();
        }
        return true;
    }

    /**
     * subtracts param number of days to this date<br>
     * do not access dates before 01/01/0001<br>
     * time complexity is LINEAR in no days to be added<br>
     * if object is not valid or param is negative return false and DOES NOTHING
     * */
    private boolean subtract(long noDays) {
        if (noDays < 0) return false;
        if (!this.isValid()) return false;
        for (int i = 0; i < noDays; i++) {
            this.toYesterday();
        }
        return true;
    }

    /**
     * adds param number of days(algebraic) to this date<br>
     * do not access dates before 01/01/0001<br>
     * time complexity is LINEAR in no days to be added<br>
     * if object is not valid or param is negative return false and DOES NOTHING<br>
     * if the object after algebraic addition is NOT valid return false and DOES NOTHING<br>
     * */
    public boolean addDays(long noDays) {
        if (!this.isValid()) return false;
        if (this.daysFromEpoch() + noDays < 0) return false;
        if (noDays == 0) return true;
        else if (noDays > 0) return this.add(noDays);
        else return this.subtract(-noDays);
    }
    //


//    public static void main(String[] args){
//
//    }


}
