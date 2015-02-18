package quicket;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.NoninvertibleTransformException;
import javax.swing.*;
import javax.swing.event.*;
import quicket.Board;
import quicket.MouseAdapter;
import java.lang.Thread;

public class MouseAdapter extends java.awt.event.MouseAdapter {

   private Board board;

   public MouseAdapter(Board board){
      this.board = board;
   }

   public void mouseClicked(MouseEvent e){
      System.out.print("squeak.");
      try{
        System.out.println("  x:" + e.getX() + "   y:" + e.getY() + "  Number:" + board.getNumber(e.getX(), e.getY()));
      }catch(NoninvertibleTransformException te){
        System.out.println("Because, FUCK YOU.");
      }
   }

   public void mouseEntered(MouseEvent e){
       System.out.print("Mouse entered component " + e.getComponent());
   }

   public void mouseExited(MouseEvent e){
       System.out.print("Mouse exited component " + e.getComponent());
   }

}
