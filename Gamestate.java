package quicket;
import quicket.Dart;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Vector;

public class Gamestate{

    private class Player{
        private final String name;
        private final int marksToClose = 3;
        private Vector<Dart> journal;
        private Map<Dart.CricketNumber, Integer> toClose;
        private boolean eliminated;
        private double mpd;

        public Player(String name){
            this.name = name;
            this.eliminated = false;
            this.mpd = 0;
            this.toClose = new HashMap<Dart.CricketNumber, Integer>(7);
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
           journal.add(dart);
           if (dart.getNumber() == Dart.CricketNumber.MISS){
               mpd *= (journal.size() - 1) / journal.size();
           }else{
              if (toClose.get(dart.getNumber()) > dart.getMarks()){
                  toClose.put(dart.getNumber(), toClose.get(dart.getNumber()) - dart.getMarks());
              }else{
                 toClose.put(dart.getNumber(), new Integer(0));
              }
              mpd = (dart.getMarks() + mpd * (journal.size() - 1)) / journal.size();
           }
        }
             

        public void setEliminated(boolean eliminated){
            this.eliminated = eliminated;
        }

        public boolean isEliminated(){
            return eliminated;
        }

        public float getMpr(){
            return (float) (mpd / 3.0);
        }
    }

    private Player currentPlayer;     
    private int round;
    private int currentThrow;
    private boolean hasWinner;
    private ArrayList<Player> players;
    private Iterator<Player> playerIterator;
    private Map<Player, Integer> scores;
    private Player leadPlace;
    private Player secondPlace;

    public Gamestate(){
       round = 0;
       currentThrow = 0;
       hasWinner = false;
       players = new ArrayList<Player>(6);
       scores  = new HashMap<Player, Integer>(6);
       leadPlace = null;
       secondPlace = null;
    }

    public void addPlayer(String name){
       Player player = new Player(name);
       players.add(player);
       scores.put(player, new Integer(0));
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
       playerIterator = players.iterator();
       Player player = null;
       while (playerIterator.hasNext()){
          player =  playerIterator.next();
          if (!player.isEliminated()) break;
       }
       if (player == currentPlayer){
          hasWinner = true;
       }else{
          round++;
       }
    }


    public void nextPlayer(){
       Player player;
       if (!playerIterator.hasNext()){
          nextRound();
          return;
       }

       player = playerIterator.next();
       while(player.isEliminated() && playerIterator.hasNext()){
          player = playerIterator.next();
       }

       if (player.isEliminated()){
          nextRound();
       }
       return;
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

       if (canPoint(cn)){
           effectiveMarks = modifier.getMultiplier();
           competitiveMarks = effectiveMarks;
           int multiplier = effectiveMarks - currentPlayer.neededToClose(cn);
           assignPoints(cn, multiplier);
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
    }

}
