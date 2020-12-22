package data;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileHandler {
    final static String DELIMITER = "/";

    public static void exportMapToFile(Point[] points){
        String tempTxtToWrite = "";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Speicherort der Map auswählen");
        fileChooser.showSaveDialog(null);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile().getAbsolutePath()));

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

    public static Point[] importFileToMap(){
        final int GET_X = 0;
        final int GET_Y = 1;
        final int GET_POINTNR = 2;

        ArrayList<Point> pointsMap = new ArrayList<Point>();
        String currentLine = "";
        String[] currentSplitLine;
        Point point;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Map auswählen");
        fileChooser.showOpenDialog(null);

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileChooser.getSelectedFile().getAbsolutePath()));
            while((currentLine = bufferedReader.readLine()) != null){
                currentSplitLine = currentLine.split(DELIMITER);

                point = new Point();
                point.setLayoutX(Double.parseDouble(currentSplitLine[GET_X]));
                point.setLayoutY(Double.parseDouble(currentSplitLine[GET_Y]));
                point.setPointNr(Integer.parseInt(currentSplitLine[GET_POINTNR]));
                pointsMap.add(point);
                System.out.println("Point Created with: " + currentLine);
            }

        } catch (FileNotFoundException fnfException) {
            fnfException.printStackTrace();
            JOptionPane.showMessageDialog(null, "Das ausgewählte File konnte leider nicht gefunden werden.",
                    "File not Found", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            JOptionPane.showMessageDialog(null, "Leider gabe es Probleme beim suchen des Files.",
                    "IO Exception", JOptionPane.ERROR_MESSAGE);
        }catch (NumberFormatException nrException){
            JOptionPane.showMessageDialog(null, "Die ausgewählte Map ist invalid.",
                    "Format Exception", JOptionPane.ERROR_MESSAGE);
            nrException.printStackTrace();
        } catch (NullPointerException nlException){
            JOptionPane.showMessageDialog(null, "Du hast kein gültiger Speicherort ausgewählt --> " +
                    "File wurde nicht exportiert", "Nothing Selected", JOptionPane.ERROR_MESSAGE );
        }

        return listToArray(pointsMap);
    }

    private static Point[] listToArray(ArrayList<Point> pointList){
        Point[] points = new Point[pointList.size()];
        for(int i = 0; i < points.length; i++){
            points[i] = pointList.get(i);
        }
        return points;
    }
}
