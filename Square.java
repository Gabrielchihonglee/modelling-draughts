import javax.swing.*;
import java.awt.*;

public class Square {
  private int xPos = -1;
  private int yPos = -1;
  private int piece = 0; // 0 for blank, 1 for red, 2 for white
  private ImageIcon squareImage;
  private JButton squareButton;

  public Square(int x, int y) {
    xPos = x;
    yPos = y;
    piece = getPiece();
    if (piece == 1) {
      squareImage = new ImageIcon("resources/red.png");
    } else if (piece == 2) {
      squareImage = new ImageIcon("resources/white.png");
    } else if (xPos % 2 == yPos % 2) {
      squareImage = new ImageIcon("resources/empty-black.png");
    } else {
      squareImage = new ImageIcon("resources/empty-white.png");
    }
    squareButton = new JButton(squareImage);
    //JButton squareButton = new JButton(xPos + ", " + yPos);
  }

  public int getXPos() {
    return xPos;
  }

  public int getYPos() {
    return yPos;
  }

  public void addSquareButton(JPanel panel) {
    panel.add(squareButton);
  }

  public void moveTo(Square target) {

  }

  private int getPiece() {
    if (xPos % 2 != yPos % 2) {
      if (yPos < 3) {
        return 1;
      }
      if (yPos > 4) {
        return 2;
      }
    }
    return 0;
  }
}
