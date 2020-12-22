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
    @FXML TableView<Object>             tblOrder;
    @FXML TableView<Object>             tblCity;
    @FXML TableColumn<Object, String>   clOrder;
    @FXML TableColumn<Object, String>   clCity;

    public static int DEFAULT_NR_POINTS = 10;

    private Point   currentPressedPoint = null;
    private Point   lastPoint           = null;
    private Point[] points;

    private ArrayList<Line>                 lines = new ArrayList<>();
    private final ObservableList<Object>    orderList = FXCollections.observableArrayList();
    private final ObservableList<Object>    cityList = FXCollections.observableArrayList();

    private int     firstButtonNr;
    private boolean isFirst = true;

    private Task<String> backGroundCalculator;

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
            //currentPressedPoint.getText().setText(Integer.toString(lastPoint.getSequence() + 1));
        }

        orderList.add(tblCity.getSelectionModel().getSelectedItem());
        List<Object> newList = new ArrayList<>(cityList);
        newList.remove(tblCity.getSelectionModel().getSelectedIndex());
        cityList.setAll(newList);

        createLineForCurPoint(currentPressedPoint, lastPoint);

        tblOrder.setItems(orderList);
        btnCalculate.setDisable(PositionCreator.hasDuplicates(points));
    }

    private void createLineForCurPoint(Point pressedPoint, Point previousPoint) {
        Line line;
        if (pressedPoint.getSequence() > 0) {
            line = new Line();
            line.setEndX(pressedPoint.getLayoutX());
            line.setEndY(pressedPoint.getLayoutY());

            line.setStartX(previousPoint.getLayoutX());
            line.setStartY(previousPoint.getLayoutY());

            lines.add(line);
        }
        if (pressedPoint.getSequence() == points.length - 1){
            line = new Line();
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
        backGroundCalculator = new BruteForceTask(points);
        backGroundCalculator.setOnSucceeded(event -> {
            lblBestRoute.setText("Result: " + backGroundCalculator.getValue() + "px");
            btnCalculateBestRoute.setDisable(false);
            btnCancel.setDisable(true);
            backGroundCalculator = null;
        });
        backGroundCalculator.setOnRunning(event -> {
            lblBestRoute.setText("State: calculating. . .");
            btnCalculateBestRoute.setDisable(true);
            btnCancel.setDisable(false);
        });
        backGroundCalculator.setOnFailed(event -> {
            event.getSource().getException().printStackTrace();
            lblBestRoute.setText("State: failed ):");
            btnCalculateBestRoute.setDisable(false);
            btnCancel.setDisable(true);
            backGroundCalculator = null;
        });
        backGroundCalculator.setOnCancelled(event -> {
            lblBestRoute.setText("State: canceled ):");
            btnCalculateBestRoute.setDisable(false);
            btnCancel.setDisable(true);
            backGroundCalculator = null;
        });

        new Thread(backGroundCalculator).start();
    }

    public void btnCancelPressed(){
        backGroundCalculator.cancel();
    }

    public void btnResetPressed () {
        gridPane.getChildren().clear();
        setGrid();
        tblCity.getItems().clear();
        tblOrder.getItems().clear();
        lines = new ArrayList<>();
    }

    public void btnReconnectPressed(){
        restart();
        for (Point point : points) {
            gridPane.getChildren().add(point);
            gridPane.getChildren().add(point.getText());
        }
    }

    public void btnGeneratePressed () {
        if (Integer.parseInt(txtNumber.getText()) < 27) {
            restart();
            DEFAULT_NR_POINTS = Integer.parseInt(txtNumber.getText());
            createButtons();
        } else {
            JOptionPane.showMessageDialog(null, "Kann nicht mehr als 26 punkte generieren", "to big",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void btnImportPressed () {
        points = FileHandler.importFileToMap();
        restart();
        loadButtons();
    }

    public void btnExportPressed () {
        FileHandler.exportMapToFile(points);
    }

    private void restart(){
        isFirst                 = true;
        lastPoint              = null;
        currentPressedPoint = null;
        lines                   = new ArrayList<>();
        
        tblOrder.getItems().clear();
        gridPane.getChildren().clear();
        tblCity.setDisable(false);
        
        List<Point> tempPointList = Arrays.asList(points);
        tempPointList.forEach(point -> {
            point.setSequence(point.getPointNr()); //reset the point's sequence
            point.getText().setText(Integer.toString(point.getSequence())); 
        });
        
        points = (Point[]) tempPointList.toArray();
        setTable();
        setGrid();
    }
}
