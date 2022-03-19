package app.Service;

import app.Controllers.GameController;
import app.Models.AdventureCards.AdventureCard;
import app.Models.Enums.Rank;
import app.Models.General.Game;
import app.Models.General.Player;
import app.Models.General.Turn;
import app.Models.StoryCards.Quest;
import app.Objects.CardObjects;
//import Models.General.GamePlay;
import app.Models.General.ProgressStatus;

import java.util.ArrayList;
import java.util.UUID; //for game ID

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private Game currentGame;

    public Game createGame(int numOfPlayers) {
        this.currentGame = new Game();
        this.currentGame.setGameID(UUID.randomUUID().toString());
        this.currentGame.setNumOfPlayers(numOfPlayers); // first initialize the array of players
        this.currentGame.setProgressStatus(ProgressStatus.NEW);
        this.currentGame.setGame(this.currentGame); // storing the game
        
        // initialize cards
        CardObjects cards = new CardObjects();
        this.currentGame.setAdventureCards(cards.getAdventureCards());
        this.currentGame.setStoryCards(cards.getStoryCards());
        
        return this.currentGame;
    }

    // Connect other players to the current Game
    public Integer joinGame(String playerName) { // passes in the current game( a global variable in js)
        System.out.println("player joining");

        Player player = new Player(playerName);
        this.getCurrentGame().registerPlayer(player);
        return player.getId();
    }

    public Game getCurrentGame() {
        return this.currentGame; // current game taking place
    }

    public void startGame(GameController gameController) throws Exception {
        // the turn class --> in javascript file
        //Check if current game is not initialized..
        if (currentGame.getGameID()==null){
            System.out.println("Game does not exist");
            return;
        }
        
        if (this.currentGame.getProgressStatus().equals(ProgressStatus.NEW)) {
            this.currentGame.setProgressStatus(ProgressStatus.IN_PROGRESS);

            int currentPlayerIndex = 0;
            while (true) {
                this.currentGame.startNewRound();
                Player player = this.currentGame.getPlayers().get(currentPlayerIndex);

                currentPlayerIndex++;
                if (currentPlayerIndex == this.currentGame.getPlayers().size()) {
                    currentPlayerIndex = 0;
                }

                if (player.getRankInt().equals(Rank.ChampionKnight)) {
                    break;
                }

                break;
            }
            // while()
            // add participants
            // check who participated
            // interact with participants
            
            // handle turn
        }
        

        //Check if the status is finished
        if(currentGame.getProgressStatus().equals(ProgressStatus.FINISHED)){
            //exception
            System.out.println("Game ended");
            return;
        }

        checkWinner(currentGame);
    }

    /*
     * The goal is to become the 1st player to become a knight.
     */
    public Player checkWinner(Game currGame) {
        // return the winning player
        // Player winningPlayer;
        for (Player p : currGame.getPlayers()) {
            if (p.getNumShields() >= 7) {
                return p; // it has enough shields to be a knight
            }
        }
        return null;
    }



    // get the current player id (the last one to join)
    // basically the player number
    public int getCurrPlayerNum(){
        return this.currentGame.getUniquePlayerId();
    }


    // gets the player's cards given the player num (unique id)
    public ArrayList<String> getPlayerCards(String playerNum){
        if (this.currentGame == null){return null;}

        int playerN = Integer.parseInt(playerNum);

        if (this.currentGame.getPlayers().size() <= 0){
            System.out.println("no players in this game so cant get the cards");
            return null;
        }
        if (playerN > this.currentGame.getPlayers().size()){
            System.out.println("invalid player num so cant get cards");
            return null;
        }
        System.out.println(this.currentGame.getPlayers().get(playerN - 1).getCards());
        ArrayList<AdventureCard> currCards = this.currentGame.getPlayers().get(playerN - 1).getCards();

        // trying to send only the name of the cards since I keep getting randomly occurring errors
        ArrayList<String> cardNames = new ArrayList<String>();
        for (AdventureCard c: currCards){
            cardNames.add(c.getName());
        }
        return cardNames;
    }
    public AdventureCard getAdventureCard(int playerId){
        
        if (playerId <= 0 || playerId > this.currentGame.getPlayers().size()){return null;}
        AdventureCard card = currentGame.getLastCard();
        this.currentGame.getPlayers().get(playerId - 1).addCard(card);
        return card;
    }

    public int getPlayerRankBattlePts(String id){
        int playerId = Integer.parseInt(id);
        ArrayList<Player> players = this.currentGame.getPlayers();
        for (int i = 0; i < players.size(); i++){
            if (players.get(i).getUniqueId() == playerId){
                return (players.get(i).getRankPts());
            }
        }
        return 0;
    }

    // update the players shields
    public boolean updateShields(int playerId, int shields){
        //int playerId = Integer.parseInt(id);
        //int shields = Integer.parseInt(numOfShields);

        ArrayList<Player> players = this.currentGame.getPlayers();
        if (players.size() < playerId){return false;}
        for (int i = 0; i < players.size(); i++){
            if (players.get(i).getUniqueId() == playerId){
                players.get(i).updateShields(shields);
                return true;
            }
        }
        return false;

    }

    /**
     * Next step in the game
     */
    public void nextStep() {
        if (this.currentGame == null) return;

        if (this.currentGame.getProgressStatus() == ProgressStatus.NEW) {
            this.currentGame.setProgressStatus(ProgressStatus.IN_PROGRESS);
            this.currentGame.startNewRound();
        }

        // if game in progress...
        else if (this.currentGame.getProgressStatus() == ProgressStatus.IN_PROGRESS) {
            
        }
    }

    public void discardCards(String playerId, ArrayList<String> cards){
        // loop through each card and remove it from the player's card
        int id = Integer.parseInt(playerId);
        if (id > this.currentGame.getPlayers().size()){return;}
        Player player = this.currentGame.getPlayers().get(id - 1);

        System.out.println("Cards before: ");
        for (int i = 0; i < player.getCards().size(); i++){
            System.out.println("   " + player.getCards().get(i));
        }

        for (int i = 0; i < cards.size(); i++){
            System.out.println("deleting: " + cards.get(i));

            AdventureCard removedCard = player.discardCard(cards.get(i));
            // add it to the discarded cards in turn
            if (removedCard != null){
                this.currentGame.getCurrentTurn().addDiscardedCards(removedCard);
            }
        }

        System.out.println();
        Player playerAfter = this.currentGame.getPlayers().get(id - 1);
        System.out.println("Cards after: ");
        for (int i = 0; i < playerAfter.getCards().size(); i++){
            System.out.println("   " + playerAfter.getCards().get(i));
        }


    }


    public boolean joinQuest(String id){
        int playerId = Integer.parseInt(id);
        if (playerId > this.currentGame.getPlayers().size() || playerId - 1 < 0){return false;}
        // get player so we can use the addPlayer function in turns
        Player player = this.currentGame.getPlayers().get(playerId - 1);
        Turn turn = this.currentGame.getCurrentTurn();
        if (turn != null){
            turn.addParticipant(player);
            return true;
        }
        return false;
    }


    public void withdrawQuest(String id){
        int playerId = Integer.parseInt(id);
        if (playerId > this.currentGame.getPlayers().size() || playerId - 1 < 0){return;}
        this.currentGame.getCurrentTurn().withdrawParticipant(playerId);

    }
    public void settingSponsor(int playerId){
        for(Player p:currentGame.getPlayers()){
            if(p.getId()==playerId){
                currentGame.getCurrentQuest().setSponsor(p);
            }
        }

    }
    public Quest getCurrentQuest(){
        return currentGame.getCurrentQuest();
    }


}