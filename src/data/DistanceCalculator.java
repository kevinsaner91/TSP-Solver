/*
    Change Date:    <2020-11-04>
    Author:          Schweizer Micha
    Version:		1.1.0.0
    Description:    This class calculates the distance from the selected sequence.
*/
package data;

public class DistanceCalculator {

    public double calculateDistance(Point[] points){
        double finalDistance = 0;
        Point[] sortedPoints = sortPointsBySequence(points);

        for(int pointCount = 0; pointCount < points.length; pointCount++){
            if(pointCount < points.length - 1) {
                finalDistance += getDistanceBetweenPoints(sortedPoints[pointCount].getLayoutX(), sortedPoints[pointCount].getLayoutY(),
                        sortedPoints[pointCount + 1].getLayoutX(), sortedPoints[pointCount + 1].getLayoutY());
            }else{
                finalDistance += getDistanceBetweenPoints(sortedPoints[pointCount].getLayoutX(), sortedPoints[pointCount].getLayoutY(),
                        sortedPoints[0].getLayoutX(), sortedPoints[0].getLayoutY());
            }
        }
        finalDistance *= 10;
        return (double) Math.round(finalDistance * 100) / 100;
    }

    public double getDistanceBetweenPoints(double xFirstPoint, double yFirstPoint, double xSecondPoint, double ySecondPoint){
        double distance;

        double xLength = xSecondPoint - xFirstPoint;
        double yLength = ySecondPoint - yFirstPoint;

        distance = Math.sqrt(Math.pow(xLength, 2) + Math.pow(yLength, 2));
        return distance;
    }

    private Point[] sortPointsBySequence(Point[] points){
        Point[] sortedPoints = new Point[points.length];
        for(Point p : points){
            sortedPoints[p.getSequence()] = p;
        }
        return sortedPoints;
    }
}