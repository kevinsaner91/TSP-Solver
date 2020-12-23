package data;

import javafx.concurrent.Task;
import javax.swing.*;
import java.time.LocalDateTime;

public class SolutionBruteForce {
    private final DistanceCalculator calculator = new DistanceCalculator();
    private final Point[] points;
    private final int[] bestSequence;
    private final Task<String[]> task;
    private int maxDigit;

    /**
     * Prepares the class for bruteforce
     * @param p point[] used to change the sequences and calculate the distances
     * @param t current task used to check if is canceled;
     */
    public SolutionBruteForce(Point[] p, Task<String[]> t){
        points = p;
        bestSequence = new int[p.length];
        task = t;
    }

    /**
     * bruteforce the best route through a Point[]
     * @return String[] containing the best route and its sequence.
     */
    public String[] bruteBestWay(){
        if(points.length > 14){
            JOptionPane.showMessageDialog(null, "Kann für maximal 14 Punkte berechnen! Anzahl Punkte: " + points.length,
                    "Overflow", JOptionPane.ERROR_MESSAGE);
            return new String[] {"Overflow"};
        }else {
            LocalDateTime startTime = LocalDateTime.now();
            System.out.println("Calculating");

            int bestResult = 999999999;
            boolean wasZero;
            char[] newSequences;

            long maxSeqNr = calcMaxNr(points.length);
            long minSeqNr = calcMinNr(points.length);

            for (long seqNr = minSeqNr; seqNr <= maxSeqNr; seqNr++) {
                if(!task.isCancelled()) {
                    wasZero = false;
                    newSequences = Long.toString(seqNr).toCharArray();

                    if(newSequences.length < points.length){
                        newSequences = Util.putZeroInCharArray(newSequences);
                    }

                    if (Util.getCharArrayMaxValue(newSequences) <= maxDigit) {
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
                        bestResult = calculateCurrentSequence(bestResult, points);
                    }
                }else{
                    System.out.println("<-- canceling task -->");
                    break;
                }
            }
            LocalDateTime endTime = LocalDateTime.now();
            System.out.println("\n<-- ---------------------------- -->");
            System.out.println("<-- " + "Start Time: " + startTime + " -->");
            System.out.println("<-- " + "End Time: " + endTime + " -->");
            System.out.println("<-- " + "Result: " + bestResult + " ");
            System.out.println("<-- ---------------------------- -->");

            return new String[]{Integer.toString(bestResult), Util.arrayToCleanString(bestSequence)};
        }
    }

    /**
     * Calculates the highest possible sequence for a Point[]
     * @param arrayLength length of the array
     * @return highest possible Sequence as a Long (ex. 9876543210)
     */
    public long calcMaxNr(int arrayLength){
        maxDigit = arrayLength - 1;
        StringBuilder maxString = new StringBuilder();

        for (int i = 0; i < arrayLength; i++) {
            maxString.append(maxDigit - i);
        }

        System.out.println("maxNr set to: " + Long.parseLong(maxString.toString()));
        return Long.parseLong(maxString.toString());
    }

    /**
     * Calculates the lowest possible sequence for a Point[]
     * @param arrayLength length of the array
     * @return lowest possible Sequence as a Long (ex. 0123456789)
     */
    public long calcMinNr(int arrayLength){
        StringBuilder minString = new StringBuilder();

        for (int i = 0; i < arrayLength; i++) {
            minString.append(i);
        }

        System.out.println("minNr set to: " + Long.parseLong(minString.toString()));
        return Long.parseLong(minString.toString());
    }

    /**
     * Calculates the route through a Point[]
     * Checks if the route is the current best
     * @param curBestRoute the current best route
     * @param p a Point[] to calculate the sequence of
     * @return best route (only changed if calculated route is better than previous best route)
     */
    public int calculateCurrentSequence(int curBestRoute, Point[] p){
        if(!Util.hasDuplicates(p)) {
            int tmpCalcVal = calculator.calculateDistance(p);
            if (tmpCalcVal < curBestRoute) {
                System.out.print(" .");
//                System.out.println("better");
                curBestRoute = tmpCalcVal;
                for (int i = 0; i < p.length; i++) {
                    bestSequence[i] = p[i].getSequence();
//                    System.out.println(bestSequence[i]);
                }
            }
        }
        return curBestRoute;
    }
}
