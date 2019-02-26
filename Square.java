import javax.swing.*;
import java.awt.*;

public class Square extends JButton {
  private int xPos = -1;
  private int yPos = -1;
  private int piece = -1; // 0 for red, 1 for white, other for blank
  private ImageIcon redIcon = new ImageIcon("resources/red.png");
  private ImageIcon whiteIcon = new ImageIcon("resources/white.png");
  private ImageIcon emptyBlackIcon = new ImageIcon("resources/empty-black.png");
  private ImageIcon emptyWhiteIcon = new ImageIcon("resources/empty-white.png");
  private ImageIcon selectIcon = new ImageIcon("resources/selected.png");
  //private JButton squareButton;
  private int clickCount = 0;

  public Square(int x, int y) {
    xPos = x;
    yPos = y;
    piece = initializePiece();
    //squareButton = new JButton();
    update();
    //JButton squareButton = new JButton(xPos + ", " + yPos);
  }

  public int getXPos() {
    return xPos;
  }

  public int getYPos() {
    return yPos;
  }

  public int getPiece() {
    return piece;
  }

  public void addSquareButton(JPanel panel) {
    panel.add(this);
  }

  public void moveTo(Square target) {
    target.piece = piece;
    piece = -1;
    target.update();
    update();
  }

  public boolean canMoveTo(Square target) {
    switch (piece) {
      case 0: // red
        if ((target.getXPos() == xPos - 1 || target.getXPos() == xPos + 1) && (target.getYPos() == yPos + 1) && (target.getPiece() == -1)) {
          return true;
        }
        break;
      case 1: // white
        if ((target.getXPos() == xPos - 1 || target.getXPos() == xPos + 1) && (target.getYPos() == yPos - 1) && (target.getPiece() == -1)) {
          return true;
        }
        break;
    }
    return false;
  }

  public void highlightSelect() {
    setIcon(selectIcon);
  }

  public void removeSelect() {
    update();
  }

  private void update() {
    if (piece == 0) {
      setIcon(redIcon);
    } else if (piece == 1) {
      setIcon(whiteIcon);
    } else if (xPos % 2 == yPos % 2) {
      setIcon(emptyBlackIcon);
    } else {
      setIcon(emptyWhiteIcon);
    }
  }

  private int initializePiece() {
    if (xPos % 2 != yPos % 2) {
      if (yPos < 3) {
        return 0;
      }
      if (yPos > 4) {
        return 1;
      }
    }
    return -1;
  }
}
