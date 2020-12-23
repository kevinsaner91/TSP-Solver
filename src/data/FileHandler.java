package data;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileHandler {
    public final static int POINTS = 0;
    public final static int BEST_SEQ = 1;
    public final static int BEST_SCORE = 2;

    private final static int GET_X = 0;
    private final static int GET_Y = 1;
    private final static int GET_POINT_NR = 2;

    public final static String DELIMITER = "/";

    public static void exportMapToFile(Point[] points, int[] bestSeq, int bestScore){
        String tempTxtToWrite;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Speicherort der Map auswählen");
        fileChooser.showSaveDialog(null);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile().getAbsolutePath()));

            StringBuilder builder = new StringBuilder();
            if(bestSeq != null) {
                for (int i = 0; i < bestSeq.length; i++) {
                    builder.append(bestSeq[i]);
                    if(i != bestSeq.length - 1){
                        builder.append(DELIMITER);
                    }
                }
            }
            builder.append("\n");
            if(bestScore != 0) {
                builder.append(bestScore);
            }
            writer.write(builder.toString() + "\n");

            for (Point p: points) {
                tempTxtToWrite = p.getLayoutX() / 10+ DELIMITER + p.getLayoutY() / 10+ DELIMITER + p.getPointNr() + "\n";
                writer.write(tempTxtToWrite);
            }

            writer.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            JOptionPane.showMessageDialog(null, "Leider gabe es Probleme beim suchen des Files",
                    "IO Exception", JOptionPane.ERROR_MESSAGE);
        }
        catch (NullPointerException nlException){
            JOptionPane.showMessageDialog(null, "Du hast kein gültiger Speicherort ausgewählt --> " +
                    "File wurde nicht gespeichert", "Nothing Selected", JOptionPane.ERROR_MESSAGE );
        }
    }

    /**
     * imports a map file and parses it to the return values
     * @return returns an object list that contains at 0 the imported points[] || never null,
     * at 1 the best calculated seq[] || can be null and
     * at 2 the best calculated score || can be null
     * in case of an exception it returns null
     */
    public static ArrayList<Object> importFileToMap(){

        ArrayList<Object> textFileList = new ArrayList<>();
        ArrayList<Point> pointsList = new ArrayList<>();
        int bestScore;
        int[] bestSeq;

        String currentLine;
        String[] currentSplitLine;
        Point point;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Map auswählen");
        fileChooser.showOpenDialog(null);

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileChooser.getSelectedFile().getAbsolutePath()));

            bestSeq = Util.stringArrayToIntArray(bufferedReader.readLine().split(DELIMITER));

            System.out.println("Imported bestSeq" + Arrays.toString(bestSeq));
            String tmpBestScore = bufferedReader.readLine();
            if(!tmpBestScore.equals("")) {
                bestScore = Integer.parseInt(tmpBestScore);
            }else{
                bestScore = -1;
            }
            System.out.println("Imported bestScore: " + bestScore);

            while((currentLine = bufferedReader.readLine()) != null){
                currentSplitLine = currentLine.split(DELIMITER);

                point = new Point();
                point.setLayoutX(Double.parseDouble(currentSplitLine[GET_X]));
                point.setLayoutY(Double.parseDouble(currentSplitLine[GET_Y]));
                point.setPointNr(Integer.parseInt(currentSplitLine[GET_POINT_NR]));
                pointsList.add(point);
                System.out.println("Point Created with: " + currentLine);
            }

        } catch (FileNotFoundException fnfException) {
            fnfException.printStackTrace();
            JOptionPane.showMessageDialog(null, "Das ausgewählte File konnte leider nicht gefunden werden.",
                    "File not Found", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            JOptionPane.showMessageDialog(null, "Leider gabe es Probleme beim suchen des Files.",
                    "IO Exception", JOptionPane.ERROR_MESSAGE);
            return null;
        }catch (NumberFormatException nrException){
            nrException.printStackTrace();
            JOptionPane.showMessageDialog(null, "Die ausgewählte Map ist invalid.",
                    "Format Exception", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (NullPointerException nlException){
            JOptionPane.showMessageDialog(null, "Du hast kein gültiger Speicherort ausgewählt --> " +
                    "File wurde nicht exportiert", "Nothing Selected", JOptionPane.ERROR_MESSAGE );
            return null;
        }

        textFileList.add(pointsList);
        textFileList.add(bestSeq);
        textFileList.add(bestScore);
        return textFileList;
    }
}
