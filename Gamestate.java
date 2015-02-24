package quicket;
import quicket.Dart;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Vector;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class Gamestate implements TableModel{

    private class Player{
        private final String name;
        private final int marksToClose = 3;
        private Vector<Dart> journal;
        private Vector<Integer> undoHelper;
        private Map<Dart.CricketNumber, Integer> toClose;
        private boolean eliminated;
        private double cumulativeMarks;
        private double cumulativeCompetetiveMarks;

        public Player(String name){
            this.name = name;
            this.eliminated = false;
            this.cumulativeMarks  = 0;
            this.cumulativeCompetetiveMarks = 0;
            this.journal    = new Vector<Dart>(60);
            this.undoHelper = new Vector<Integer>(60);
            this.toClose    = new HashMap<Dart.CricketNumber, Integer>(7);
            this.toClose.put(Dart.CricketNumber.TWENTY,    new Integer(marksToClose));
            this.toClose.put(Dart.CricketNumber.NINETEEN,  new Integer(marksToClose));
            this.toClose.put(Dart.CricketNumber.EIGHTEEN,  new Integer(marksToClose));
            this.toClose.put(Dart.CricketNumber.SEVENTEEN, new Integer(marksToClose));
            this.toClose.put(Dart.CricketNumber.SIXTEEN,   new Integer(marksToClose));
	    this.toClose.put(Dart.CricketNumber.FIFTEEN,   new Integer(marksToClose));
            this.toClose.put(Dart.CricketNumber.BULL,      new Integer(marksToClose));
        }

        public String getName(){
            return name;
        }

        public boolean isClosed(Dart.CricketNumber num){
            return (toClose.get(num) == 0);
        }

        public int neededToClose(Dart.CricketNumber num){
            return toClose.get(num);
        }

        public int totalNeededToClose(){
            int total = 0;
            for (int needed: toClose.values()){
                total += needed;
            }
            return total;
        }

        public void recordDart(Dart dart){
	   undoHelper.add(toClose.get(dart.getNumber()));
           journal.add(dart);
           if (dart.getNumber() != Dart.CricketNumber.MISS){
              if (toClose.get(dart.getNumber()) > dart.getMarks()){
                  toClose.put(dart.getNumber(), toClose.get(dart.getNumber()) - dart.getMarks());
              }else{
                 toClose.put(dart.getNumber(), new Integer(0));
              }
              cumulativeMarks             += dart.getMarks();
              cumulativeCompetetiveMarks  += dart.getCompetetiveMarks();
           }
        }
             

        public void setEliminated(boolean eliminated){
            this.eliminated = eliminated;
        }

        public boolean isEliminated(){
            return eliminated;
        }


        // remove last dart from the journal, correcting cumluative totals
	// and return a tuple of the Dart removed and the amount of marks
	// applied to pointing
	public Map.Entry<Dart, Integer> rollbackDart(){
	    final Map<Dart, Integer> ret = new HashMap<Dart, Integer>(1, 1.0f);
	    Dart removed = journal.remove(journal.size() - 1);
	    Integer prevToClose = undoHelper.remove(undoHelper.size() - 1);

            toClose.put(removed.getNumber(), prevToClose);
            cumulativeMarks -= removed.getMarks();
	    cumulativeCompetetiveMarks -= removed.getCompetetiveMarks();
            int pointingMarks = removed.getMarks() - prevToClose;
            ret.put(removed, new Integer((pointingMarks < 1)? 0 : pointingMarks));

	    return ret.entrySet().iterator().next();
	}


        public float getMpr(){
            if (journal.size() == 0) return Float.NaN;
            return (float) (cumulativeMarks / (journal.size() / 3.0) );
        }

        public float getCmpr(){
            if (journal.size() == 0) return Float.NaN;
            return (float) (cumulativeCompetetiveMarks / (journal.size() / 3.0) );
        }
    }

    private Player currentPlayer;     
    private int round;
    private int currentThrow;
    private boolean hasWinner;
    private ArrayList<Player> players;
    private Map<Player, Integer> scores;
    private Player leadPlace;
    private Player secondPlace;
    private Vector<TableModelListener> tableModelListeners;

    public Gamestate(){
       round = 0;
       currentThrow = 0;
       hasWinner = false;
       players = new ArrayList<Player>(6);
       scores  = new HashMap<Player, Integer>(6);
       tableModelListeners = new Vector<TableModelListener>(3);
       
       leadPlace = null;
       secondPlace = null;
    }

    public void addPlayer(String name){
       Player player = new Player(name);
       players.add(player);
       scores.put(player, new Integer(0));
    }


    public void startGame(){
       if (players.size() < 2) return;
       rankPlayers();
       nextRound();
    }

    public boolean canPoint(Dart.CricketNumber cn){
       for (Player player: players){
          if (player != currentPlayer && !player.isClosed(cn)) return true;
       }
       return false;
    }


    public void assignPoints(Dart.CricketNumber cn, int multiplier){
       for (Player player: players){
          if (player != currentPlayer && !player.isClosed(cn)){
             scores.put(player, scores.get(player) + cn.getPointValue() * multiplier);
          }
       }
    }

    public void undo(){
       if (currentThrow == 0){
          //case for turn rollback
	  int np = players.indexOf(currentPlayer);
	  currentThrow = 3;
	  if (np == 0){
	    round--;
	    currentPlayer = players.get(players.size() - 1);
          }else{
	    currentPlayer = players.get(np - 1);
	  }
       }

Map.Entry<Dart, Integer> removed = currentPlayer.rollbackDart();
       if (!removed.getValue().equals(0)){
	  for (Player p: players){
	     if (p != currentPlayer){
		scores.put(p, scores.get(p) - (removed.getKey().getNumber().getPointValue() * removed.getValue()));
		p.setEliminated(false);
             }
	  }
       }
       currentThrow--;
       checkForEliminated();
   }


    public void rankPlayers(){
       for (Player player: players){
          if (leadPlace == null){
             leadPlace = player;
          }else if (player.totalNeededToClose() < leadPlace.totalNeededToClose()){
             secondPlace = leadPlace;
             leadPlace = player;
          }else if (player.totalNeededToClose() == leadPlace.totalNeededToClose()
                    && scores.get(player) < scores.get(leadPlace)){
             secondPlace = leadPlace;
             leadPlace = player;
          }else if (secondPlace == null){
             secondPlace = player;
          }else if (player.totalNeededToClose() < secondPlace.totalNeededToClose()){
             secondPlace = player;
          }else if (player.totalNeededToClose() == secondPlace.totalNeededToClose()
                    && scores.get(player) < scores.get(secondPlace)){
             secondPlace = player;
          }
       }
    }
          
          

    public void checkForEliminated(){
       rankPlayers();
       if (leadPlace.totalNeededToClose() > 0) return;

       for (Player player: players){
           if (scores.get(player) > scores.get(leadPlace) ){
              player.setEliminated(true);
           }
       }
    }
              

    public void nextRound(){
       Iterator<Player> playerIterator = players.iterator();
       Player player = null;
       while (playerIterator.hasNext()){
          player =  playerIterator.next();
          if (!player.isEliminated()) break;
       }
       if (player == currentPlayer){
          hasWinner = true;
       }else{
          currentPlayer = player;
          round++;
       }
    }


    public void nextPlayer(){
       Player player;
       int np = players.indexOf(currentPlayer);

       currentThrow = 0;
       if (np == (players.size() - 1)){
          nextRound();
          return;
       }

       // avoid using an Iterator so we can undo
       // into a previous turn without skipping
       // over a player
       do {
         np++;
	 if (np < players.size()){
	    player = players.get(np);
	 }else{
	    nextRound();
	    return;
	 }
       } while ( player.isEliminated() );

       currentPlayer = player;
    }
        

    public void scoreDart(int num, int mod){
       Dart.CricketNumber cn;
       Dart.Modifier modifier;
       int     effectiveMarks;
       int     competitiveMarks;


       switch(mod){
         case 3:  modifier = Dart.Modifier.TRIPLE;
                  break;
         case 2:  modifier = Dart.Modifier.DOUBLE;
                  break;
         default: modifier = Dart.Modifier.NONE;
       }

       // first tokenize the number
       switch(num){
         case 20:  cn = Dart.CricketNumber.TWENTY;
                   break;
         case 19:  cn = Dart.CricketNumber.NINETEEN;
                   break;
         case 18:  cn = Dart.CricketNumber.EIGHTEEN;
                   break;
         case 17:  cn = Dart.CricketNumber.SEVENTEEN;
                   break;
         case 16:  cn = Dart.CricketNumber.SIXTEEN;
                   break;
         case 15:  cn = Dart.CricketNumber.FIFTEEN;
                   break;
         case 25:  cn = Dart.CricketNumber.BULL;
                   break;
         default:  cn = Dart.CricketNumber.MISS;
       }

       if (cn == Dart.CricketNumber.MISS){
           currentPlayer.recordDart(new Dart(Dart.CricketNumber.MISS, Dart.Modifier.NONE, 0, 0));
       }else if (canPoint(cn)){
           effectiveMarks = modifier.getMultiplier();
           competitiveMarks = effectiveMarks;
           int multiplier = effectiveMarks - currentPlayer.neededToClose(cn);
           if (multiplier > 0) assignPoints(cn, multiplier);
           if ( leadPlace.isClosed(cn) && secondPlace.isClosed(cn) ){
              competitiveMarks = (currentPlayer.neededToClose(cn) < effectiveMarks)?
                                               currentPlayer.neededToClose(cn) : effectiveMarks;
           }
           currentPlayer.recordDart(new Dart(cn, modifier, effectiveMarks, competitiveMarks) );
       }else if(currentPlayer.isClosed(cn) ){
           currentPlayer.recordDart(new Dart(Dart.CricketNumber.MISS, Dart.Modifier.NONE, 0, 0));
       }else{
           effectiveMarks = (currentPlayer.neededToClose(cn) < modifier.getMultiplier())?
                                                     currentPlayer.neededToClose(cn) : modifier.getMultiplier();
           currentPlayer.recordDart(new Dart(cn, modifier, effectiveMarks, effectiveMarks) );
       }
       checkForEliminated();

       if (++currentThrow % 3 == 0){
           nextPlayer();
       }

       for (TableModelListener l: tableModelListeners){
           l.tableChanged(new TableModelEvent(this));
       }
    }

    // TableModel interface methods
    public void addTableModelListener(TableModelListener l){
       tableModelListeners.add(l);
    }

    public Class<?> getColumnClass(int columnIndex){
        return String.class;
    }

    public int getColumnCount(){
        return ( players.size() + 1 );
    }

    public String getColumnName(int columnIndex){
        if ( columnIndex == 0){
           return "";
        }else{
           return players.get(columnIndex - 1).getName();
        }
    }

    public int getRowCount(){
        return 11;
    }

    public Object getValueAt(int rowIndex, int columnIndex){
        final String firstTab[] = { "Player", "20", "19", "18", "17", "16", "15", "Bull", "MPR", "CMPR", "Points" };
        final String glyph[] = { "0", "X", "/", "" };

        if (columnIndex == 0){
           return firstTab[rowIndex];
        }else{
           Player p = players.get(columnIndex - 1);
           switch (rowIndex){
              case 0:  return p.getName();
              case 1:  return glyph[p.neededToClose(Dart.CricketNumber.TWENTY)];
              case 2:  return glyph[p.neededToClose(Dart.CricketNumber.NINETEEN)];
              case 3:  return glyph[p.neededToClose(Dart.CricketNumber.EIGHTEEN)];
              case 4:  return glyph[p.neededToClose(Dart.CricketNumber.SEVENTEEN)];
              case 5:  return glyph[p.neededToClose(Dart.CricketNumber.SIXTEEN)];
              case 6:  return glyph[p.neededToClose(Dart.CricketNumber.FIFTEEN)];
              case 7:  return glyph[p.neededToClose(Dart.CricketNumber.BULL)];
              case 8:  return p.getMpr();
              case 9:  return p.getCmpr();
              case 10: return scores.get(p).toString();
              default: System.out.println("Bad access row:" + rowIndex + " columnIndex:" + columnIndex);
           }
         }
         return null;
    }



    // table is display-only
    public boolean  isCellEditable(int rowIndex, int columnIndex){
        return false;
    }

    public void removeTableModelListener(TableModelListener l){
        tableModelListeners.remove(l);
    }

    // not implemented for a read-only table
    public void setValueAt(Object aValue, int rowIndex, int columIndex){
        return;
    }
}
