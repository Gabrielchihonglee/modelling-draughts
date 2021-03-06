import java.awt.*;
import javax.swing.*;

/**
* Represents a square on the board (one of the 64 squares). Extends the JButton
* class to inherit JButton's instance variables and methods.
*
* @author Gabriel Lee
*/
public class Square extends JButton {
  private int xPos = -1;
  private int yPos = -1;
  private int piece = -2; // 0 for red, 1 for white, 2 for red king, 3 for white king, -1 for blank
  private ImageIcon redIcon;
  private ImageIcon whiteIcon;
  private ImageIcon redKingIcon;
  private ImageIcon whiteKingIcon;
  private ImageIcon emptyBlackIcon;
  private ImageIcon emptyWhiteIcon;
  private ImageIcon selectIcon;
  private boolean jumpLeftDown = false;
  private boolean jumpLeftUp = false;
  private boolean jumpRightDown = false;
  private boolean jumpRightUp = false;

  /**
  * Constructor for the Square object.
  * (Kept for assignment specifications fulfillment purpose)
  * It will store the x and y position and calculate which piece should it be
  * holding based on it's position on the board by {@code initializePiece()}.
  * Then calls {@code update()} to display the piece on the square.
  *
  * @param x x-coordinate of the square on the board (left-most is 0)
  * @param y y-coordinate of the square on the board (top-most is 0)
  */
  public Square(int x, int y) {
    xPos = x;
    yPos = y;
    setupIcons(1);
    piece = initializePiece();
    update();
  }

  /**
  * A secodary constructor for the Square object.
  * Takes in the x and y position of the square, along with the graphics
  * version.
  *
  * @param x x-coordinate of the square on the board (left-most is 0)
  * @param y y-coordinate of the square on the board (top-most is 0)
  * @param graphicsVer version id of the graphics pack
  * @param pieceId id of the piece to be initialized on the square
  */
  public Square(int x, int y, int graphicsVer, int pieceId) {
    xPos = x;
    yPos = y;
    setupIcons(graphicsVer);
    if (pieceId == -2) { // default piece id
      piece = initializePiece();
    } else {
      piece = pieceId;
    }
    update();
  }

  /**
  * Accessor for the xPos variable.
  *
  * @return int of x position of the square on the board
  */
  public int getXPos() {
    return xPos;
  }

  /**
  * Accessor for the yPos variable.
  *
  * @return int of y position of the square on the board
  */
  public int getYPos() {
    return yPos;
  }

  /**
  * Accessor for the piece variable.
  *
  * @return int representing the piece type on the square on the board
  *  (-1, 0 and 1 represents default, red and white respectively)
  */
  public int getPiece() {
    return piece;
  }

  /**
  * Moves the piece in current square to target square.
  * Replaces target piece by current piece then reset current square's piece.
  * Then update display.
  *
  * @param target the target Square for the piece to move to
  */
  public void moveTo(Square target) {
    target.piece = piece;
    piece = -1;
    target.update();
    update();
  }

  /**
  * Performs a series of checks on if the piece on the current square can be
  * moved to the target square.
  *
  * @param target the target Square for the piece to move to
  * @return boolean for if the piece on the current square can be mvoed to the
  *  target square.
  */
  public boolean canMoveTo(Square target) {
    int targetXPos = target.getXPos();
    int targetYPos = target.getYPos();
    int targetPiece = target.getPiece();
    switch (piece) {
      case 0: // red
        if ((Math.abs(targetXPos - xPos) == 1) && (targetYPos == yPos + 1)) {
          if (targetPiece == -1) {
            return true;
          } else if ((targetPiece != piece) && (targetPiece != piece + 2)) {
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
          } else if ((targetPiece != piece) && (targetPiece != piece + 2)) {
            if (targetXPos < xPos) {
              jumpLeftUp = true;
            } else if (targetXPos > xPos) {
              jumpRightUp = true;
            }
          }
        }
        break;
      case 2: // red king
      case 3: // white king
        if ((Math.abs(targetXPos - xPos) == 1) && (Math.abs(targetYPos - yPos) == 1)) {
          if (targetPiece == -1) {
            return true;
          } else if ((targetPiece != piece) && (targetPiece != piece - 2)) {
            if (targetXPos < xPos) {
              if (targetYPos < yPos) {
                jumpLeftDown = true;
              } else {
                jumpLeftUp = true;
              }
            } else if (targetXPos > xPos) {
              if (targetYPos < yPos) {
                jumpRightDown = true;
              } else {
                jumpRightUp = true;
              }
            }
          }
        }
        break;
    }
    return false;
  }

  /**
  * Performs a series of checks on if the piece on the current square can be
  * jumped, over an opponent's piece, to the target square. It is different from
  * the {@link #canMoveTo(Square target)} method, as checks only for moving to next diagonal
  * position, but not jumping across an opponent's piece.
  *
  * @param target the target Square for the piece to move to
  * @return boolean for if the piece on the current square can be jumped to the
  *  target square.
  */
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
      case 2: // red king
      case 3: // white king
        if (jumpLeftUp && (targetXPos == xPos - 2) && (targetYPos == yPos + 2) && (targetPiece == -1)) {
          return true;
        }
        if (jumpRightUp && (targetXPos == xPos + 2) && (targetYPos == yPos + 2) && (targetPiece == -1)) {
          return true;
        }
        if (jumpLeftDown && (targetXPos == xPos - 2) && (targetYPos == yPos - 2) && (targetPiece == -1)) {
          return true;
        }
        if (jumpRightDown && (targetXPos == xPos + 2) && (targetYPos == yPos - 2) && (targetPiece == -1)) {
          return true;
        }
    }
    return false;
  }

  /**
  * Highlights the square by changing the icon to selected.png.
  */
  public void highlightSelect() {
    setIcon(selectIcon);
  }

  /**
  * Removes highlight from the square by calling update() to reset normal icons
  * (pieces or empty icons).
  */
  public void removeSelect() {
    update();
  }

  /**
  * Resets {@link #canJumpTo(Square target)}-used variables. Should be called
  * after each jump.
  */
  public void resetJumpStatus() {
    jumpLeftDown = false;
    jumpLeftUp = false;
    jumpRightDown = false;
    jumpRightUp = false;
  }

  /**
  * Removes the piece on the square.
  */
  public void kill() {
    piece = -1;
    update();
  }

  /**
  * Updates the icon of the square (pieces or blank icons) based on the square's
  * variable (piece and position).
  */
  private void update() {
    switch (piece) {
      case 0:
        if (yPos == 7) {
          piece = 2;
          setIcon(redKingIcon);
        } else {
          setIcon(redIcon);
        }
        break;
      case 1:
        if (yPos == 0) {
          piece = 3;
          setIcon(whiteKingIcon);
        } else {
          setIcon(whiteIcon);
        }
        break;
      case 2:
        setIcon(redKingIcon);
        break;
      case 3:
        setIcon(whiteKingIcon);
        break;
      default:
        if (xPos % 2 == yPos % 2) {
          setIcon(emptyBlackIcon);
        } else {
          setIcon(emptyWhiteIcon);
        }
        break;
    }
  }

  /**
  * Sets the icon for the square according to the graphics version selected.
  *
  * @param graphicsVer graphics version id
  * @return int representing the piece (-1, 0 and 1 represents default, red and
  *  white respectively).
  */
  private void setupIcons(int graphicsVer) {
    redIcon = new ImageIcon("resources/v" + graphicsVer + "/red.png");
    whiteIcon = new ImageIcon("resources/v" + graphicsVer + "/white.png");
    redKingIcon = new ImageIcon("resources/v" + graphicsVer + "/red-king.png");
    whiteKingIcon = new ImageIcon("resources/v" + graphicsVer + "/white-king.png");
    emptyBlackIcon = new ImageIcon("resources/v" + graphicsVer + "/empty-black.png");
    emptyWhiteIcon = new ImageIcon("resources/v" + graphicsVer + "/empty-white.png");
    selectIcon = new ImageIcon("resources/v" + graphicsVer + "/selected.png");
  }

  /**
  * Calculates the piece on the square based on the square's position.
  *
  * @return int representing the piece (-1, 0 and 1 represents default, red and
  *  white respectively).
  */
  private int initializePiece() {
    if (xPos % 2 != yPos % 2) { // x odd, y even OR x even, y odd
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
