import java.util.ArrayList;
import java.util.Collections;

class Main {
  Player currentActivePlayer;
  ArrayList<AdventureCard> adventureCardsDeck;
  ArrayList<StoryCard> storyCardsDeck;

  ArrayList<Turn> turns;
  
  Main() {
    this.adventureCardsDeck = new ArrayList<AdventureCard>();
    this.storyCardsDeck = new ArrayList<StoryCard>();
    this.turns = new ArrayList<Turn>();
    
    this.initializeCards();
    //this.initializePlayers();
    this.beginGame();
  }

  public void acceptPlayer(Player player) {
    
  }

  public void initializeCards() {
    //Only one of each Ally Card
    this.adventureCardsDeck.add(new Ally("Sir Gawain", 10, -1, this));
    this.adventureCardsDeck.add(new Ally("King Pellinore", 10, -1, this));
    this.adventureCardsDeck.add(new Ally("Sir Percival", 5, -1, this));
    this.adventureCardsDeck.add(new Ally("Sir Tristan", 10, -1, this));
    this.adventureCardsDeck.add(new Ally("King Arthur", 10, 2, this));
    this.adventureCardsDeck.add(new Ally("Queen Guinevere", -1, 3, this));
    this.adventureCardsDeck.add(new Ally("Queen Iseult", -1, 2, this));
    this.adventureCardsDeck.add(new Ally("Sir Lancelot", 15, -1, this));
    this.adventureCardsDeck.add(new Ally("Sir Galahad", 15, -1, this));
    
    
    for (int i=0; i<11;i++){
      this.adventureCardsDeck.add(new Weapon("Horse",10));}
    for (int i=0; i<16;i++){
    this.adventureCardsDeck.add(new Weapon("Sword",10));}
    for (int i=0; i<6;i++){
    this.adventureCardsDeck.add(new Weapon ("Dagger",5));}
    for (int i=0; i<2;i++){
    this.adventureCardsDeck.add(new Weapon ("Excalibur",30));}
    for (int i=0; i<6;i++){
    this.adventureCardsDeck.add(new Weapon ("Lance", 20));}
    for (int i=0; i<8;i++){
    this.adventureCardsDeck.add(new Weapon ("Battle-ax",15));}

    for (int i=0; i<2;i++){
      this.adventureCardsDeck.add(new Test ("Test of the Questing Beast"));
      this.adventureCardsDeck.add(new Test ("Test of Temptation"));
      this.adventureCardsDeck.add(new Test ("Test of Valor"));
      this.adventureCardsDeck.add(new Test ("Test of Morgan Le Fey", 3));
    }
    for (int i = 0; i < 8; i++){
      this.adventureCardsDeck.add(new Foe("Thieves", 5));
      this.adventureCardsDeck.add(new Foe("Saxon Knight", 15, 25));
      if (i < 7){
        this.adventureCardsDeck.add(new Foe("Robber Knight", 15));
      }
      if (i < 6){
        this.adventureCardsDeck.add(new Foe("Evil Knight", 20, 30));
      }
      if (i < 5){
        this.adventureCardsDeck.add(new Foe("Saxons", 10, 20));
      }
      if (i < 4){
        this.adventureCardsDeck.add(new Foe("Mordred", 30));
        this.adventureCardsDeck.add(new Foe("Boar", 5, 15));
      }
      if (i < 3){
        this.adventureCardsDeck.add(new Foe("Black Knight", 25, 35));
      }
      if (i < 2){
        this.adventureCardsDeck.add(new Foe("Giant", 40));
        this.adventureCardsDeck.add(new Foe("Green Knight", 25, 40));
      }
    }
    this.adventureCardsDeck.add(new Foe("Dragon", 50, 70));
    
    for (int i=0; i<8; i++){
      this.adventureCardsDeck.add(new Amour("Amour", "", 10, 1));
    }


      
    Collections.shuffle(this.adventureCardsDeck);
    Collections.shuffle(this.storyCardsDeck);

    
  }

  public void initializePlayers() {
    
  }
  
  public void beginGame() {
    //Initialize Players, Give 12 adventure cards to each player
    Player player1 = new Player();
    Player player2 = new Player();
    for (int i=0;i<12;i++){
      AdventureCard card = adventureCardsDeck.remove(adventureCardsDeck.size()-1); 
      player1.cards.add(card);
    }
    
    for (int i=0;i<12;i++){
      AdventureCard card = adventureCardsDeck.remove(adventureCardsDeck.size()-1);
      player2.cards.add(card);
    }

    player1.printCards();

    System.out.println("-----------------");
    player2.printCards();
  }

  public String getCurrentTurnName() {
    if (this.turns.isEmpty()) return null;
    return this.turns.get(this.turns.size() - 1).name;
  }
  
  public static void main(String[] args) {
    new Main();
  }
}