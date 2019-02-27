import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
* Board is the main class for the draught game. It contains the main framework
* and logic for the game. Squares can be added to the board.
*
* @author Gabriel Lee
*/
public class Board extends JFrame implements ActionListener {
  private int clickCount = 0;
  private Square moveFrom;
  private Square moveTo;
  private Square[] squares = new Square[64];
  private int playerTurn = 1;
  private boolean validFrom = false;
  private boolean jumpStreak = false;

  public static void main(String[] args) {
    Board board = new Board();
  }

  private Board() {
    updateTitleTurn();
    setSize(800, 800);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setResizable(false);
    setVisible(true);

    JPanel panel = new JPanel();
    setContentPane(panel);

    GridLayout boardLayout = new GridLayout(8, 8);
    panel.setLayout(boardLayout);

    for (int i = 0; i < 64; i++) {
      squares[i] = new Square(i % 8, i / 8 % 8);
      panel.add(squares[i]);
      squares[i].addActionListener(this);
    }

    revalidate();
  }

  /**
  * Invoked when any square is clicked. Handles player's order, highlighting
  * posible targets, etc.
  *
  * @param e the ActionEvent passed from the ActionListener.
  */
  public void actionPerformed(ActionEvent e) {
    updateTitleTurn();
    if (clickCount == 0 && ((Square) e.getSource()).getPiece() == playerTurn) {
      moveFrom = (Square) e.getSource();
      for (int i = 0; i < 64; i++) {
        if (moveFrom.canMoveTo(squares[i])) {
          validFrom = true;
          squares[i].highlightSelect();
        }
      }
      for (int i = 0; i < 64; i++) {
        if (moveFrom.canJumpTo(squares[i])) {
          validFrom = true;
          squares[i].highlightSelect();
        }
      }
      if (validFrom) {
        clickCount = 1;
      } else if (jumpStreak) {
        jumpStreak = false;
        playerTurn ^= 1;
      }
    } else {
      moveTo = (Square) e.getSource();
      clickCount = 0;
      validFrom = false;
      for (int i = 0; i < 64; i++) {
        squares[i].removeSelect();
      }
      if (moveFrom != moveTo && (moveFrom.canMoveTo(moveTo) || moveFrom.canJumpTo(moveTo))) {
        moveFrom.moveTo(moveTo);
        //                      System.out.println(moveFrom.getXPos() + ", " + moveTo.getXPos());
        if (Math.abs(moveFrom.getXPos() - moveTo.getXPos()) == 2) {
          int middleXPos = (moveFrom.getXPos() + moveTo.getXPos()) / 2;
          int middleYPos = (moveFrom.getYPos() + moveTo.getYPos()) / 2;
          squares[middleXPos + middleYPos * 8].kill();
          for (int i = 0; i < 64; i++) {
            squares[i].resetJumpStatus();
          }
          jumpStreak = true;
          moveTo.doClick();
        } else {
          playerTurn ^= 1;
        }
      }
    }
    updateTitleTurn();
  }

  private void updateTitleTurn() {
    switch (playerTurn) {
      case 1:
        setTitle("Draughts: White's turn");
        break;
      case 0:
        setTitle("Draughts: Red's turn");
        break;
      default:
        System.out.println("TODO: Should be exception here..."); //TODO: add exception handler
        break;
    }
  }
}
