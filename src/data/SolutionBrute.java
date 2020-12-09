package data;

import javafx.concurrent.Task;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.Arrays;

public class SolutionBrute{
    private final DistanceCalculator calculator = new DistanceCalculator();
    private final Point[] points;
    private final Task<String> task;
    private int maxDigit;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /**
     * Prepares the class for bruteforce
     * @param p point[] used to change the sequences and calculate the distances
     * @param t current task used to check if is canceled;
     */
    public SolutionBrute(Point[] p, Task<String> t){
        points = p;
        task = t;
    }

    /**
     * bruteforce the best route through a Point[]
     * @return String containing the best route.
     */
    public String bruteBestWay(){
        if(points.length > 14){
            JOptionPane.showMessageDialog(null, "Kann für maximal 14 Punkte berechnen! Anzahl Punkte: " + points.length,
                    "Overflow", JOptionPane.ERROR_MESSAGE);
            return "Overflow";
        }else {
            startTime = LocalDateTime.now();

            int bestRoute = 999999999;
            boolean wasZero;
            char[] newSequences;

            long maxSeqNr = calcMaxNr(points);

            for (long seqNr = 0; seqNr < maxSeqNr; seqNr++) {
                if(!task.isCancelled()) {
                    wasZero = false;
                    newSequences = Long.toString(seqNr).toCharArray();

                    if (getCharArrayMaxValue(newSequences) < maxDigit) {
                        for (int pNr = 0; pNr < points.length; pNr++) {
                            int newSeq;
                            try {
                                newSeq = Character.getNumericValue(newSequences[pNr]);
                            } catch (ArrayIndexOutOfBoundsException e) {
                                if (wasZero) {
                                    break;
                                } else {
                                    newSeq = 0;
                                    wasZero = true;
                                }
                            }
                            points[pNr].setSequence(newSeq);
                        }
                        bestRoute = calculateCurrentSequence(bestRoute, points);
                    }
                }else{
                    System.out.println("<-- canceling task -->");
                    break;
                }
            }
            endTime = LocalDateTime.now();
            System.out.println("<-- ---------------------------- -->");
            System.out.println("<-- " + "Start Time: " + startTime + " -->");
            System.out.println("<-- " + "End Time: " + endTime + " -->");
            System.out.println("<-- " + "Result: " + bestRoute + " ");
            System.out.println("<-- ---------------------------- -->");
            return Integer.toString(bestRoute);
        }
    }

    /**
     * Calculates the highest possible sequence for a Point[]
     * @return highest possible Sequence as a Long (ex. 9876543210)
     */
    public long calcMaxNr(Point[] p){
        maxDigit = p.length - 1;
        StringBuilder maxString = new StringBuilder();

        for (int i = 0; i < p.length; i++) {
            maxString.append(maxDigit - i);
        }

        System.out.println("maxNr = " + Long.parseLong(maxString.toString()));
        return Long.parseLong(maxString.toString());
    }

    /**
     * Calculates the route through a Point[]
     * Checks if the route is the current best
     * Creates a clean output for the calculation
     * @param bestRoute the current best route
     * @param p a Point[] to calculate the sequence of
     * @return best route (only changed if calculated route is better than previous best route)
     */
    public int calculateCurrentSequence(int bestRoute, Point[] p){
        int currentRoute;

        if (!PositionCreator.hasDuplicates(p)) {
            System.out.println("<-.->");
            System.out.println("calculating...");

            Arrays.asList(p).forEach(point -> System.out.print("| " + point.getSequence() + " |"));
            System.out.println();

            currentRoute = calculator.calculateDistance(p);
            System.out.println("Current route: " + currentRoute);
            if (currentRoute < bestRoute) {
                System.out.println("is better");
                bestRoute = currentRoute;
            }
            System.out.println("<-.->");
        }
        return bestRoute;
    }

    /**
     * searches the highest value in an char[]
     * @param charArray char[] to search the highest value in
     * @return the highest value in the given array
     */
    public Long getCharArrayMaxValue(char[] charArray){
        long maxValue = 0;
        for (char c: charArray) {
            if(Character.getNumericValue(c) > maxValue){
                maxValue = Character.getNumericValue(c);
            }
        }
        return maxValue;
    }
}
