package data;

import javafx.concurrent.Task;

public class BruteForceTask extends Task<String[]> {
    private final Point[] points;

    public BruteForceTask(Point[] p){
        points = p;
    }
    @Override
    public String[] call() {
        return new SolutionBruteForce(points, this).bruteBestWay();
    }
}
