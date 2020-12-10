package data;

import javafx.concurrent.Task;

public class BruteForceTask extends Task<String> {
    private final Point[] points;

    public BruteForceTask(Point[] p){
        points = p;
    }
    @Override
    protected String call() throws Exception {
        return new SolutionBruteForce(points, this).bruteBestWay();
    }
}
