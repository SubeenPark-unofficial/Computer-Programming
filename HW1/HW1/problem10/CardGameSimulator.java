public class CardGameSimulator {
    private static final Player[] players = new Player[2];

    public static void simulateCardGame(String inputA, String inputB) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        Player playerA = new Player("A", inputA);
        Player playerB = new Player("B", inputB);

        Play game = new Play(playerA, playerB);

        // Initialize players' deck
        game.initGame();
        game.startGame();
        game.nextStep();
        printLoseMessage(game.getLooser());


    }

    private static void printLoseMessage(Player player) {
        System.out.printf("Player %s loses the game!\n", player);
    }
}

class Play{
    private Player playerA;
    private Player playerB;
    private Player cur_player;
    private Player next_player;
    private Card cur_card;
    private Player Looser;

    Play(Player playerA, Player playerB){
        this.playerA = playerA;
        this.playerB = playerB;
    }

    public void initGame(){
        this.playerA.initCardDeck();
        this.playerB.initCardDeck();
    }

    private void printCard(){
        System.out.println("Player " + cur_player.toString()+ ": " + cur_card.toString());
    }

    public void startGame(){
        this.cur_player = playerA;
        this.next_player = playerB;
        this.cur_card = new Card(playerA.select1stCard().getNumber(), playerA.select1stCard().getShape());
        this.cur_player.playCard(this.cur_card);
        this.cur_player.throwCard(this.cur_card);

    }

    public void nextStep(){
        // Cover cases with no cards!

        Card next_card = this.next_player.selectNextCard(this.cur_card);

        while (!this.endGame(next_card)){
            this.cur_card = new Card(next_card.getNumber(), next_card.getShape());
            this.cur_player = (this.next_player == playerB) ? playerB:playerA;
            this.next_player = (this.next_player == playerB) ? playerA:playerB;
            this.cur_player.playCard(this.cur_card);
            this.cur_player.throwCard(next_card);
            next_card = this.next_player.selectNextCard(this.cur_card);

        }

    }

    private boolean endGame(Card next_card){
        if (playerA.noCard() && playerB.noCard()){
            this.Looser = playerA;
            return true;
        }
        else if (Card.checkThrownCard(next_card)){
            this.Looser = next_player;
            return true;
        }
        else if (next_card.noMatching(this.cur_card)){
            this.Looser = next_player;
            return true;
        }
        return false;
    }

    public Player getLooser() {
        return Looser;
    }
}

class Player {
    private String name;
    private String inputStr;
    private Card[] deck;
    private int num_card;

    Player(String name, String inputStr){
        this.name = name;
        this.inputStr = inputStr;
        this.num_card = 10;
    }


    public void initCardDeck(){
        String[] deck_str = this.inputStr.split(" ");
        Card[] cards = new Card[10];
        for (int i = 0; i < 10; i++){
            Card card = new Card((int)(deck_str[i].charAt(0)) - (int)'0', deck_str[i].charAt(1));
            cards[i] = card;
        }
        this.deck = cards;
    }

    public Card select1stCard(){
        Card card_sel = this.deck[0];
        for (int i = 0; i < 10; i++){
            Card card_cur = this.deck[i];
            if (card_cur.getNumber() > card_sel.getNumber()){
                card_sel = card_cur;
                //System.out.println(card_sel.toString());
            }
            else if (card_cur.getNumber() == card_sel.getNumber() && card_sel.getShape() == 'O'){
                card_sel = card_cur;
            }
        }
        //System.out.println(card_sel.toString());
        return card_sel;
    }

    public Card selectNextCard(Card cur_card){
        Card card_sel = this.deck[0];
        for (int i = 0; i < 10; i++){
            if (this.deck[i].getNumber() == cur_card.getNumber()){
                card_sel = this.deck[i];
            }
        }
        if (card_sel.getNumber() != cur_card.getNumber()){
            for (int i = 0; i < 10; i++){
                if (this.deck[i].getShape() == cur_card.getShape()){
                    card_sel = this.deck[i];
                }
            }
            for (int i = 0; i < 10; i++){
                if (this.deck[i].getShape() == cur_card.getShape() && this.deck[i].getNumber() > card_sel.getNumber()){
                    card_sel = this.deck[i];
                }
            }
        }
        return card_sel;
    }

    public void throwCard(Card card){
        num_card--;
        for (int i = 0; i < 10; i++){
            if (this.deck[i].getNumber() == card.getNumber() && this.deck[i].getShape() == card.getShape()){
                deck[i].throwCard();
            }
        }
    }

    public boolean noCard(){
        return this.num_card == 0;
    }


    public void playCard(Card card) {
        System.out.printf("Player %s: %s\n", name, card);
    }

    @Override
    public String toString() {
        return name;
    }
}


class Card {
    private int number;
    private char shape;

    Card(int number, char shape){
        this.number = number;
        this.shape = shape;
    }

    public int getNumber(){
        return this.number;
    }

    public char getShape(){
        return this.shape;
    }

    public void throwCard(){
        this.number = -1;
        this.shape = '-';
    }

    public static boolean checkThrownCard(Card card){
        return card.number == -1 ? true:false;
    }

    public boolean noMatching(Card card){
        return (this.getNumber() != card.getNumber() && this.getShape() != card.getShape());
    }

    @Override
    public String toString() {
        return "" + number + shape;
    }
}


/* Needed functionality

1) initialize the card deck | inputA -> deck & inputB -> deck
    Class PlayGame: use Class player: initCardDeck
2) Decide First Card | choose card from initialized card deck & record current card to bit
    Class PlayGame: use Class player: decide1stCard
    Class PlayGame: cardOnTable
3) Record current player
    Class PlayGame: recordPlayer & swapPlayer
3) Decide other cards | choose cards
    if defeatable card exists, swapPlayer. Else, return loser
    Class PlayGame: returnLoser
    Class PlayGame: use Class player: nextCard
    Class PlayGame: swapPlayer

 */

/* Game Work Flow
1) Largest number -> O -> X
2) Same number -> same shape/largest number
3) No card to hand out -> previous player win
4) All cards used out -> B win
+) Print out sequence
+) Print winner
 */