package data;


import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Point extends Circle {
    int sequence;
    int pointNr;
    Text text = new Text();

    public Text getText() {
        return text;
    }

    public void setText(Text txt) { this.text = txt; }

    public int getPointNr() {
        return pointNr;
    }

    public void setPointNr(int pointNr) {
        this.pointNr = pointNr;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
