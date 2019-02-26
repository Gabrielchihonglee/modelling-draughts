import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Board implements ActionListener {
  private int clickCount = 0;
  private Square moveFrom;
  private Square moveTo;
  private Square squares[] = new Square[64];

  public static void main(String[] args) {
    Board board = new Board("Draughts");
  }

  public Board(String windowTitle) {
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
      squares[i].addSquareButton(panel);
      squares[i].addActionListener(this);
    }

    frame.revalidate();
  }

  public void actionPerformed(ActionEvent e) {
    if (clickCount == 0 && ((Square) e.getSource()).getPiece() != -1) {
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
          int middleXPos = (moveFrom.getXPos() + moveTo.getXPos())/2;
          int middleYPos = (moveFrom.getYPos() + moveTo.getYPos())/2;
          squares[middleXPos + middleYPos * 8].kill();
        }
      }
      for (int i = 0; i < 64; i++) {
        squares[i].removeSelect();
      }
    }
  }
}
