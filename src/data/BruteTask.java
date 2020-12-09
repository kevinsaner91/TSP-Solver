package data;

import javafx.concurrent.Task;

public class BruteTask extends Task<String> {
    private final Point[] points;

    public BruteTask(Point[] p){
        points = p;
    }
    @Override
    protected String call() throws Exception {
        return new SolutionBrute(points, this).bruteBestWay();
    }
}
