package GUI;

import data.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javax.swing.*;
import java.util.*;

public class Controller {
    @FXML Pane                          gridPane;
    @FXML Button                        btnCalculate;
    @FXML Button                        btnCalculateBestRoute;
    @FXML Button                        btnCancel;
    @FXML Label                         lblDistance;
    @FXML Label                         lblBestRoute;
    @FXML TextField                     txtNumber;
    @FXML CheckBox                      checkShowLines;
    @FXML CheckBox                      checkShowCalcResult;
    @FXML CheckBox                      checkShowCalcLines;
    @FXML TableView<Object>             tblOrder;
    @FXML TableView<Object>             tblCity;
    @FXML TableColumn<Object, String>   clOrder;
    @FXML TableColumn<Object, String>   clCity;

    public static int DEFAULT_NR_POINTS = 10;

    private Point   currentPressedPoint = null;
    private Point   lastPoint           = null;
    private Point[] points;
    private int[]   bestCalculatedSeq   = null;
    private int     bestCalculatedScore = 0;

    private ArrayList<Line>                 lines = new ArrayList<>();
    private ArrayList<Line> calculatedLines = new ArrayList<>();
    private final ObservableList<Object>    orderList = FXCollections.observableArrayList();
    private final ObservableList<Object>    cityList = FXCollections.observableArrayList();

    private int     firstButtonNr;
    private boolean isFirst = true;

    private Task<String[]> backgroundCalculator;

    public void initialize(){
        addCityListener();
        setGrid();
        createButtons();
        setTable();
        setFactory(clOrder);
        setFactory(clCity);
    }

    public void setFactory(TableColumn column) {
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SetTableView, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SetTableView, String> n) {
                return new ReadOnlyObjectWrapper(n.getValue().getId());
            }
        });
    }

    private void setGrid(){
        gridPane.setStyle("-fx-background-color: #707070");
        for (int x = 10; x < 720; x += 10){
            for (int y = 10; y < 470; y += 10) {
                Circle circle = new Circle();
                circle.setRadius(0.5);
                circle.setCenterX(x);
                circle.setCenterY(y);
                gridPane.getChildren().add(circle);
            }
        }
    }

    public void createButtons() {
        PositionCreator positionCreator = new PositionCreator();
        points = positionCreator.createPointWithPos(DEFAULT_NR_POINTS);
        loadButtons();
    }

    private void loadButtons () {
        for (Point point : points) {
            Text text = new Text(point.getNrAsLetter());
            text.setFill(Color.YELLOW);
            text.setLayoutX((point.getLayoutX() * 10) - 20);
            text.setLayoutY(point.getLayoutY() * 10);
            point.setText(text);

            point.setLayoutX(point.getLayoutX() * 10);
            point.setLayoutY(point.getLayoutY() * 10);
            point.setRadius(7);
            gridPane.getChildren().add(point);
            gridPane.getChildren().add(point.getText());
        }
    }

    public void focused(){
        for (Point p : points){
            p.setFill(Color.BLACK);
        }
    }

    public void clearSelection(){
        tblCity.getSelectionModel().clearSelection();
    }

    private void addCityListener(){
        tblCity.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                TableView.TableViewSelectionModel<? extends Object> selectionModel = tblCity.getSelectionModel();
                ObservableList<TablePosition> selectedCells = selectionModel.getSelectedCells();
                if(!selectedCells.isEmpty()) {
                    TablePosition tablePosition = selectedCells.get(0);

                    String val = tablePosition.getTableColumn().getCellData(newValue).toString();
                    cityChosen(Point.getLetterAsNr(val));
                }
            }
        });
    }

    public void cityChosen(int cityNr){
        focused();

        lastPoint = currentPressedPoint;
        currentPressedPoint = points[cityNr];
        currentPressedPoint.setFill(Color.ORANGE);

        if (isFirst) {
            currentPressedPoint.setSequence(0); /*currentPressedPoint.getText().setText("0");*/
            firstButtonNr = currentPressedPoint.getPointNr();
            isFirst = false;
        }else{
            currentPressedPoint.setSequence(lastPoint.getSequence() + 1);
        }

        orderList.add(tblCity.getSelectionModel().getSelectedItem());
        List<Object> newList = new ArrayList<>(cityList);
        newList.remove(tblCity.getSelectionModel().getSelectedIndex());
        cityList.setAll(newList);

        createLineForCurPoint(currentPressedPoint, lastPoint);

        tblOrder.setItems(orderList);
        btnCalculate.setDisable(Util.hasDuplicates(points));
    }

    private void createLineForCurPoint(Point pressedPoint, Point previousPoint) {
        Line line;
        if (pressedPoint.getSequence() > 0) {
            line = new Line();
            line.setStrokeWidth(3);
            line.setEndX(pressedPoint.getLayoutX());
            line.setEndY(pressedPoint.getLayoutY());

            line.setStartX(previousPoint.getLayoutX());
            line.setStartY(previousPoint.getLayoutY());

            lines.add(line);
        }
        if (pressedPoint.getSequence() == points.length - 1){
            line = new Line();
            line.setStrokeWidth(3);
            line.setStartX(pressedPoint.getLayoutX());
            line.setStartY(pressedPoint.getLayoutY());

            line.setEndX(points[firstButtonNr].getLayoutX());
            line.setEndY(points[firstButtonNr].getLayoutY());

            lines.add(line);
        }
        drawLinesIfActivated();
    }

    public void drawLinesIfActivated(){
        if(checkShowLines.isSelected()){
            gridPane.getChildren().removeAll(lines);
            gridPane.getChildren().addAll(lines);
        }else{
            gridPane.getChildren().removeAll(lines);
        }
    }

    private void setTable() {
        tblCity.getItems().clear();
        for (int i = 0; i < points.length; i++) {
            cityList.add(new SetTableView(points[i].getNrAsLetter()));
        }
        tblCity.setItems(cityList);
    }

    public void btnCalculatePressed () {
        DistanceCalculator distanceCalculator = new DistanceCalculator();
        lblDistance.setText("Distanz: " + distanceCalculator.calculateDistance(points) + "px");
        tblCity.setDisable(true);
    }

    public void btnCalculateBestRoutePressed(){
        backgroundCalculator = new BruteForceTask(points);

        backgroundCalculator.setOnSucceeded(event -> {
            if(backgroundCalculator.getValue()[0].contains("-1")) {
                lblBestRoute.setText("Overflow");
            }else{
                if (checkShowCalcResult.isSelected()) {
                    lblBestRoute.setText("Result: " + backgroundCalculator.getValue()[0] + "px");
                } else {
                    lblBestRoute.setText("Done (:");
                }
                bestCalculatedScore = Integer.parseInt(backgroundCalculator.getValue()[0]);
                bestCalculatedSeq = Util.stringArrayToIntArray(backgroundCalculator.getValue()[1].split(FileHandler.DELIMITER));
                createCalculatedLines();
            }

            btnCalculateBestRoute.setDisable(false);
            btnCancel.setDisable(true);
            backgroundCalculator = null;
        });

        backgroundCalculator.setOnRunning(event -> {
            lblBestRoute.setText("State: calculating. . .");
            btnCalculateBestRoute.setDisable(true);
            btnCancel.setDisable(false);
        });

        backgroundCalculator.setOnFailed(event -> {
            event.getSource().getException().printStackTrace();
            lblBestRoute.setText("State: failed ):");
            btnCalculateBestRoute.setDisable(false);
            btnCancel.setDisable(true);
            backgroundCalculator = null;
        });

        backgroundCalculator.setOnCancelled(event -> {
            lblBestRoute.setText("State: canceled ):");
            btnCalculateBestRoute.setDisable(false);
            btnCancel.setDisable(true);
            backgroundCalculator = null;
        });

        new Thread(backgroundCalculator).start();
    }

    private void createCalculatedLines() {
        if(bestCalculatedSeq[0] != -1){
            bestCalculatedSeq = new int[points.length];
            System.out.println(Arrays.toString(bestCalculatedSeq));
            Line line;
            for (int i = 0; i < bestCalculatedSeq.length; i++) {
                line = new Line();
                line.setStyle("-fx-stroke: RED");

                line.setStartX( points[ bestCalculatedSeq[i] ].getLayoutX() );
                line.setStartY( points[ bestCalculatedSeq[i] ].getLayoutY() );

                if(i < bestCalculatedSeq.length - 1) {
                    line.setEndX( points[ bestCalculatedSeq[i + 1] ].getLayoutX() );
                    line.setEndY( points[ bestCalculatedSeq[i + 1] ].getLayoutY() );
                }else{
                    //last line
                    line.setEndX( points[ bestCalculatedSeq[0] ].getLayoutX() );
                    line.setEndY( points[ bestCalculatedSeq[0] ].getLayoutY() );
                }
                calculatedLines.add(line);
            }
            drawCalculatedLinesIfSelected();
        }
    }

    public void drawCalculatedLinesIfSelected(){
        if(checkShowCalcLines.isSelected()){
            gridPane.getChildren().removeAll(calculatedLines);
            gridPane.getChildren().addAll(calculatedLines);
        }else{
            gridPane.getChildren().removeAll(calculatedLines);
        }
    }

    public void btnCancelPressed(){
        backgroundCalculator.cancel();
    }

    public void btnResetPressed () {
        gridPane.getChildren().clear();
        setGrid();
        tblCity.getItems().clear();
        tblOrder.getItems().clear();
        lines = new ArrayList<>();
        calculatedLines = new ArrayList<>();
    }

    public void btnReconnectPressed(){
        restart();
        for (Point point : points) {
            gridPane.getChildren().add(point);
            gridPane.getChildren().add(point.getText());
        }
    }

    public void btnGeneratePressed () {
        try {
            if (Integer.parseInt(txtNumber.getText()) < 27 && Integer.parseInt(txtNumber.getText()) > 1) {
                restart();
                DEFAULT_NR_POINTS = Integer.parseInt(txtNumber.getText());
                createButtons();
                setTable();
            } else {
                JOptionPane.showMessageDialog(null, "Die angegebende Zahl ist nicht im erlaubten Bereich ( 2 - 26 )", "invalid",
                        JOptionPane.ERROR_MESSAGE);
            }
        }catch (NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Anzahl zu generiende Punkte ist ungültig", "NumberFormatException", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void checkShowCalcResultToggled(){
        if(bestCalculatedScore > 0){
            if(checkShowCalcResult.isSelected()){
                lblBestRoute.setText("Result: " + bestCalculatedScore + "px");
            }else{
                lblBestRoute.setText("done");
            }
        }
    }

    public void btnImportPressed () {
        ArrayList<Object> importedFile = FileHandler.importFileToMap();
        if(importedFile != null){
            points = Util.listToArray((ArrayList<Point>) importedFile.get(FileHandler.POINTS));
            bestCalculatedSeq = (int[]) importedFile.get(FileHandler.BEST_SEQ);
            bestCalculatedScore = (int) importedFile.get(FileHandler.BEST_SCORE);

            if(bestCalculatedScore != -1 && checkShowCalcResult.isSelected()){
                lblBestRoute.setText("Result: " + bestCalculatedScore + "px");
            }

            restart();
            loadButtons();
            createCalculatedLines();
        }
    }

    public void btnExportPressed () {
        FileHandler.exportMapToFile(points, bestCalculatedSeq, bestCalculatedScore);
    }

    private void restart(){
        isFirst                 = true;
        lastPoint              = null;
        currentPressedPoint = null;
        lines                   = new ArrayList<>();
        calculatedLines = new ArrayList<>();
        
        tblOrder.getItems().clear();
        gridPane.getChildren().clear();
        tblCity.setDisable(false);
        
        List<Point> tempPointList = Arrays.asList(points);
        tempPointList.forEach(point -> {
            point.setSequence(point.getPointNr()); //reset the point's sequence
            point.getText().setText(point.getNrAsLetter());
        });
        
        points = (Point[]) tempPointList.toArray();
        setTable();
        setGrid();
    }
}
