/*
    Change Date:    <2020-11-04>
    Author:          Schweizer Micha
    Version:		2.0.0.0
    Description:    This class creates a new random position for all wanted point.
*/
package data;

import java.util.*;

public class PositionCreator {
    ArrayList<Integer> usedXPos;
    ArrayList<Integer> usedYPos;

    Point[] points;

    final int X_SIZE = 65;
    final int Y_SIZE = 40;

    Random rnd = new Random();

    public Point[] createPointWithPos(int countpoint){

        usedXPos = new ArrayList<>();
        usedYPos = new ArrayList<>();

        points = new Point[countpoint];

        for (int pos = 0; pos < countpoint; pos++){
            points[pos] = new Point();
            points[pos].setLayoutX(createX());
            points[pos].setLayoutY(createY());
            points[pos].setPointNr(pos);
            points[pos].setSequence(pos); //set default sequence
        }

        return points;
    }

    private int createX(){
        int tempX;

        do{
            tempX = rnd.nextInt(X_SIZE);
        }while (usedXPos.contains(tempX) || tempX == 0);
        usedXPos.add(tempX);

        return tempX;
    }

    private int createY(){
        int tempY;

        do{
            tempY = rnd.nextInt(Y_SIZE);
        }while (usedYPos.contains(tempY) || tempY == 0);
        usedYPos.add(tempY);

        return tempY;
    }
}