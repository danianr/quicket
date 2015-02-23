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

   private final Board board;
   private final Gamestate gamestate;

   public MouseAdapter(Board board, Gamestate gamestate){
      this.board = board;
      this.gamestate = gamestate;
   }

   public void mouseClicked(MouseEvent e){
      System.out.print("squeak.");
      try{
        System.out.println("  x:" + e.getX() + "   y:" + e.getY() + "  Number:" + board.getNumber(e.getX(), e.getY()));
        if (board.isDoubleRing(e.getX(), e.getY())){
           System.out.println("Double " + board.getNumber(e.getX(), e.getY()) );
           gamestate.scoreDart(board.getNumber(e.getX(), e.getY()), 2);
        }else if (board.isTripleRing(e.getX(), e.getY())){
           System.out.println("Triple  " + board.getNumber(e.getX(), e.getY()) );
           gamestate.scoreDart(board.getNumber(e.getX(), e.getY()), 3);
        }else{
           gamestate.scoreDart(board.getNumber(e.getX(), e.getY()), 1);
        }
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
