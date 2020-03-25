/** Collection of compound-interest and related financial
 *  computations.
 *  @Aayush Sutaria
 */
public class CompoundInterest {
    static final int THIS_YEAR = 2019;
    static int numYears(int targetYear) {
        int num = targetYear - THIS_YEAR - 1;
        return num;
    }

    static double futureValue(double presentValue, double rate,
                              int targetYear) {
        return presentValue * Math.pow((1 + rate / 100), numYears(targetYear));

    }

    static double futureValueReal(double presentValue, double rate,
                                  int targetYear, double inflationRate) {
        double sum = futureValue(presentValue, rate, targetYear);
        for (int i = 0; i < numYears(targetYear); i++) {
            sum = sum * (1.0 - inflationRate / 100);
        }
        return sum;
    }

    static double totalSavings(double perYear, int targetYear, double rate) {
        double sum = perYear;
        for (int i = 0; i < numYears(targetYear); i++) {
            sum = (sum * (1 + rate / 100)) + perYear;
        }
        return sum;
    }


    /** Returns totalSavings(PERYEAR, TARGETYEAR, RATE) converted to
     *  current year dollars, assuming a uniform inflation rate of
     *  INFLATIONRATE. */
    static double totalSavingsReal(double perYear, int targetYear, double rate,
                               double inflationRate) {
        double sum = totalSavings(perYear, targetYear, rate);
        for (int i = 0; i < numYears(targetYear); i++) {
            sum = sum * (1.0 - inflationRate / 100);
        }
        return sum;
    }

    /** Prints out the future inflation-adjusted value of a dollar in
     *  TARGETYEAR in a nice format, assuming yearly compounded
     *  interest at a rate of RETURNRATE, with annual inflation at
     *  INFLATIONRATE. */
    static void printDollarFV(int targetYear, double returnRate,
                              double inflationRate) {
        double nominalDollarValue = futureValue(1.0, returnRate, targetYear);
        double realDollarValue = futureValueReal(1.0, returnRate, targetYear, inflationRate);

        // Do not change anything in this method below this line
        String dollarSummary =
            String.format("Assuming a %.2f%% rate of return,"
                          + " a dollar saved today would be worth"
                          + " %.2f dollars in the year %d, or %.2f dollars"
                          + " adjusted for inflation.", returnRate,
                          nominalDollarValue, targetYear, realDollarValue);

        System.out.println(dollarSummary);
    }

    /** Prints out the future inflation-adjusted value of saving
     *  PERYEAR dollars per year at RETURNRATE until TARGETYEAR at
     *  INFLATIONRATE in a nice format. */
    static void printSavingsFV(int targetYear, double returnRate,
                               double inflationRate, double perYear) {

        double nominalSavings = totalSavings(perYear, targetYear, returnRate);
        double realSavings = totalSavingsReal(perYear, targetYear, returnRate, inflationRate);
        // Do not change anything in this method below this line

        String savingsSummary =
            String.format("Assuming a %.2f%% rate of return,"
                          + " in the year %d, after saving %.2f"
                          + " dollars per year, you'll have %.2f dollars or"
                          + " %.2f dollars adjusted for inflation.",
                          returnRate, targetYear, perYear,
                          nominalSavings, realSavings);

        System.out.println(savingsSummary);
    }


    /* Parameters for the analysis. */
    /** The year of interest. */
    static final int TARGET_YEAR = 2059;
    /** RETURN_RATE is the percentage rate that you expect to earn on
     *            average until targetYear.
     *  INFLATION_RATE is the average inflation rate until targetYear
     *  PER_YEAR is how much money you will save per year until targetYear */

    static final double RETURN_RATE = 10,
        INFLATION_RATE = 3,
        PER_YEAR = 10000;

    /** Print out future values for given parameters. */
    public static void main(String[] ignored) {
        printDollarFV(TARGET_YEAR, RETURN_RATE, INFLATION_RATE);
        System.out.println();
        printSavingsFV(TARGET_YEAR, RETURN_RATE, INFLATION_RATE, PER_YEAR);
    }
}
