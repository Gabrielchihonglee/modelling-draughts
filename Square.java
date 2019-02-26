import javax.swing.*;
import java.awt.*;

public class Square extends JButton {
  private int xPos = -1;
  private int yPos = -1;
  private int piece = -1; // 0 for red, 1 for white, other for blank
  private ImageIcon redIcon = new ImageIcon("resources/red.png");
  private ImageIcon whiteIcon = new ImageIcon("resources/white.png");
  private ImageIcon redKingIcon = new ImageIcon("resources/red-king.png");
  private ImageIcon whiteKingIcon = new ImageIcon("resources/white-king.png");
  private ImageIcon emptyBlackIcon = new ImageIcon("resources/empty-black.png");
  private ImageIcon emptyWhiteIcon = new ImageIcon("resources/empty-white.png");
  private ImageIcon selectIcon = new ImageIcon("resources/selected.png");
  private boolean jumpLeftDown, jumpLeftUp, jumpRightDown, jumpRightUp = false;

  public Square(int x, int y) {
    xPos = x;
    yPos = y;
    piece = initializePiece();
    update();
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
    int targetXPos = target.getXPos();
    int targetYPos = target.getYPos();
    int targetPiece = target.getPiece();
    switch (piece) {
      case 0: // red
        if ((Math.abs(targetXPos - xPos) == 1) && (targetYPos == yPos + 1)) {
          if (targetPiece == -1) {
            return true;
          } else if (targetPiece == 1) {
            if (targetXPos < xPos) {
              jumpLeftDown = true;
            } else if (targetXPos > xPos) {
              jumpRightDown = true;
            }
          }
        }
        break;
      case 1: // white
        if ((Math.abs(targetXPos - xPos) == 1) && (targetYPos == yPos - 1)) {
          if (targetPiece == -1) {
            return true;
          } else if (targetPiece == 0) {
            if (targetXPos < xPos) {
              jumpLeftUp = true;
            } else if (targetXPos > xPos) {
              jumpRightUp = true;
            }
          }
        }
        break;
    }
    return false;
  }

  public boolean canJumpTo(Square target) {
    int targetXPos = target.getXPos();
    int targetYPos = target.getYPos();
    int targetPiece = target.getPiece();
    switch (piece) {
      case 0: // red
        if ((Math.abs(targetXPos - xPos) == 2) && (targetYPos == yPos + 2) && (targetPiece == -1)) {
          if (jumpLeftDown && (targetXPos < xPos)) {
            return true;
          }
          if (jumpRightDown && (targetXPos > xPos)) {
            return true;
          }
        }
        break;
      case 1: // white
        if ((Math.abs(targetXPos - xPos) == 2) && (targetYPos == yPos - 2) && (targetPiece == -1)) {
          if (jumpLeftUp && (targetXPos < xPos)) {
            return true;
          }
          if (jumpRightUp && (targetXPos > xPos)) {
            return true;
          }
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
    jumpLeftDown = false;
    jumpLeftUp = false;
    jumpRightDown = false;
    jumpRightUp = false;
  }

  public void kill() {
    piece = -1;
  }

  private void update() {
    if (piece == 0) {
      if (yPos == 7) {
        setIcon(redKingIcon);
      } else {
        setIcon(redIcon);
      }
    } else if (piece == 1) {
      if (yPos == 0) {
        setIcon(whiteKingIcon);
      } else {
        setIcon(whiteIcon);
      }
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
