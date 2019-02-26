import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
* Board is the main class for the draught game. It contains the main framework
* and logic for the game. Squares can be added to the board.
*
* @author Gabriel Lee
*/
public class Board implements ActionListener {
  private int clickCount = 0;
  private Square moveFrom;
  private Square moveTo;
  private Square[] squares = new Square[64];
  private int playerTurn = 1;

  public static void main(String[] args) {
    Board board = new Board("Draughts");
  }

  private Board(String windowTitle) {
    JFrame frame = new JFrame(windowTitle);
    frame.setSize(800, 800);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setVisible(true);

    JPanel panel = new JPanel();
    frame.setContentPane(panel);

    GridLayout boardLayout = new GridLayout(8, 8);
    panel.setLayout(boardLayout);

    for (int i = 0; i < 64; i++) {
      squares[i] = new Square(i % 8, i / 8 % 8);
      panel.add(squares[i]);
      squares[i].addActionListener(this);
    }

    frame.revalidate();
  }

  /**
  * Invoked when any square is clicked. Handles player's order, highlighting
  * posible targets, etc.
  *
  * @param e the ActionEvent passed from the ActionListener.
  */
  public void actionPerformed(ActionEvent e) {
    if (clickCount == 0 && ((Square) e.getSource()).getPiece() == playerTurn) {
      moveFrom = (Square) e.getSource();
      for (int i = 0; i < 64; i++) {
        if (moveFrom.canMoveTo(squares[i])) {
          squares[i].highlightSelect();
        }
      }
      for (int i = 0; i < 64; i++) {
        if (moveFrom.canJumpTo(squares[i])) {
          squares[i].highlightSelect();
        }
      }
      clickCount = 1;
    } else {
      moveTo = (Square) e.getSource();
      clickCount = 0;
      if (moveFrom != moveTo && (moveFrom.canMoveTo(moveTo) || moveFrom.canJumpTo(moveTo))) {
        moveFrom.moveTo(moveTo);
        if (Math.abs(moveFrom.getXPos() - moveTo.getXPos()) == 2) {
          int middleXPos = (moveFrom.getXPos() + moveTo.getXPos()) / 2;
          int middleYPos = (moveFrom.getYPos() + moveTo.getYPos()) / 2;
          squares[middleXPos + middleYPos * 8].kill();
        }
        playerTurn ^= 1;
      }
      for (int i = 0; i < 64; i++) {
        squares[i].removeSelect();
      }
    }
  }
}
