package data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * a helper class for common tasks
 */
public class Util {
    /**
     * creates a string of the given array (Delimiter = FileHandler.DELIMITER)
     * @param array the array that gets converted
     * @return a String that looks like the array
     */
    public static String arrayToCleanString(int[] array){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if(i < array.length - 1){
                builder.append(FileHandler.DELIMITER);
            }
        }
        //System.out.println("new best seq: " + builder.toString());
        return builder.toString();
    }

    /**
     * searches the highest value in a char[]
     * @param charArray char[] to search the highest value in
     * @return the highest value in the given array
     */
    public static Long getCharArrayMaxValue(char[] charArray){
        long maxValue = 0;
        for (char c: charArray) {
            if(Character.getNumericValue(c) > maxValue){
                maxValue = Character.getNumericValue(c);
            }
        }
        return maxValue;
    }

    /**
     * adds at the first position of a char[] a 0
     * @param oldArray the array which you want to edit
     * @return the edited array
     */
    public static char[] putZeroInCharArray(char[] oldArray){
        char[] newArray = new char[oldArray.length + 1];

        newArray[0] = (char) 48;
        for (int i = 1; i < newArray.length; i++) {
            newArray[i] = oldArray[i-1];
        }
        return newArray;
    }

    /**
     * converts a ArrayList<Point> to a Point[]
     * @param pointList ArrayList<Point> to convert
     * @return converted Point[]
     */
    public static Point[] listToArray(ArrayList<Point> pointList){
        Point[] points = new Point[pointList.size()];
        for(int i = 0; i < points.length; i++){
            points[i] = pointList.get(i);
        }
        return points;
    }

    public static int[] stringArrayToIntArray(String[] s){
        int[] convertedArray = new int[s.length];
        for (int i = 0; i < s.length; i++) {
            convertedArray[i] = Integer.parseInt(s[i]);
        }
        return convertedArray;
    }

    public static boolean hasDuplicates(Point[] p) {
        Integer[] allSequences = new Integer[p.length];
        for (int i = 0; i < p.length; i++) {
            allSequences[i] = p[i].getSequence();
        }

        Set<Integer> tempSet = new HashSet<>();
        for (Integer i : allSequences) {
            if (!tempSet.add(i)) {
                return true;
            }
        }
        return false;
    }
}
