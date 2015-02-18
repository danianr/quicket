package quicket;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import javax.swing.event.*;
import quicket.Slice;
import java.lang.Thread;

public class SliceTest {

   private static class Board extends JPanel {
      private int diameter = 500;
      private int xoffset  = 0;
      private int yoffset  = 0;
      private final int n;

      int x[] = { 220, 250, 280 };

      public Board(int n){
         this.n = n;
      }

      public void paint(Graphics g){
         Graphics2D g2 = (Graphics2D) g;
         g2.setColor(Color.black);
         g2.fillRect(xoffset, yoffset, diameter, diameter);
	 Slice sl = new Slice(this.n, 250);
	 g2.setColor(Color.red);
	 g2.fill(sl);
      }
   }
          

   private static void createAndShowGUI(int n) {
      JFrame frame = new JFrame("Quicket v3.0");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      JLabel label = new JLabel("Quicket");
      frame.getContentPane().add(label);
      Board board = new Board(n);
      frame.getContentPane().add(board);
      frame.pack();
      frame.setVisible(true);

   }

   public static void main(String[] args) {

      final int n = Integer.parseInt(args[0]);
      //Schedule a job for the event-dispatching thread:
      //creating and showing this application's GUI.

      javax.swing.SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            createAndShowGUI(n);
         } });
      try{
         Thread.sleep(4000);
      }catch (InterruptedException e){
      }
      System.exit(0);
   }
}
