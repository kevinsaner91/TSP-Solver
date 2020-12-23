package data;


import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Point extends Circle {
    private int sequence;
    private int pointNr;
    private Text text = new Text();

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

    public String getNrAsLetter(){
        char val = (char) (pointNr + 65);
        return Character.toString(val);
    }

    public static int getLetterAsNr(String letter){
        char val = letter.toCharArray()[0];
        return (val - 65);
    }
}
