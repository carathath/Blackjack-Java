package cardsPackage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Iterator;
import java.util.Scanner;

public class Cards extends Card{
	Random rand = new Random(); // Used to generate random numbers
	int total = 0; // Keeps track of sum of cards
	static int numCards = 0; // How many cards have been drawn. When it is 156, reshuffle
	boolean firstDraw = true; //Determines if it is the first draw
	boolean acePresent = false; //Used to revalue an Ace when the total goes above 21
	int aceCounter = 0; // Counts number of aces
	static int [] choices = new int [2]; //If Double [0]/Split [1] is available. 0-not available, 1-available
	ArrayList<Card> Cards = new ArrayList<Card>(); //Starter two cards
	static HashMap<CardType, Integer> types = new HashMap<CardType, Integer>(); //Tracks how many cards of each type have been played (Max 78)
	static int [] clubs = new int[13]; //Each index stands for the amount of times that (index+1) number has appeared
	static int [] diamonds = new int[13];
	static int [] hearts = new int[13];
	static int [] spades = new int[13];
	
	public Cards() { //Constructor
		this.total = 0;
		choices[0] = 0;
		choices[1] = 0;
	}
	
	//Fills the HashMap that has a CardType key and integer value of how many cards
	//of that type have appeared
	public void fillMaps() {
		//For Card Types hashmap
		types.put(CardType.CLUBS, 0);
		types.put(CardType.DIAMONDS, 0);
		types.put(CardType.HEARTS, 0);
		types.put(CardType.SPADES, 0);
		
		//For card type arrays, makes all their values zero
		for(int i = 0; i < 13; i++) {
			clubs[i] = 0;
			diamonds[i] = 0;
			hearts[i] = 0;
			spades[i] = 0;
		}
	}
	
	public int getCardTotal() { //Return the total of the hand
		return this.total;
	}
	
	public int getCardSize() { //Return the amount of cards in the hand
		return this.Cards.size();
	}
	
	//
	public void addCard() { //Add a card to the Cards ArrayList
		Card newCard = new Card();
		Cards.add(newCard);
	}
	
	public void deleteCard(int index) { //Remove card at index from the Cards ArrayList
		this.total -= Cards.get(index).getValue(); //Remove the second card value from the total
		Cards.remove(index);
	}
	
	// Generate a random number for the card value
	public void numberGenerator(int i, CardType t) { // Number 1-13
		int randomNum = rand.nextInt((13 - 1) + 1) + 1;
		//If the number has already been drawn more than the available amount,
		//recurse to this function to generate a new number
		if(checkAmount(randomNum, t) == 0) //Checks to see if the card type and number value is acceptable
			numberGenerator(i, t);
		else {
			this.Cards.get(i).setValue(randomNum); //Apply the accepted value to the card
		}
	}
	
	// Generate a card type for the Card
	public void typeGenerator(int i) { // Number 1-4
		int randomType = rand.nextInt((4 - 1) + 1) + 1;
		CardType checkType = CardType.CLUBS; //Default value
		
		if(randomType == 1)
			checkType = CardType.CLUBS;
		else if(randomType == 2)
			checkType = CardType.DIAMONDS;
		else if(randomType == 3)
			checkType = CardType.HEARTS;
		else if(randomType == 4)
			checkType = CardType.SPADES;
		else
			System.out.println("Error in typeGenerator!");
		
		if(checkAmount(checkType) == 0) { //If we have already used all cards of that type
			typeGenerator(i); //Recurse to this function
		}
		else {
			this.Cards.get(i).setType(checkType); //Set the card type
			numberGenerator(i, checkType); //Generate a number for the card
			numCards++; // Increase the amount of cards drawn
			if(numCards >= 156) // If half or more of the card stack has been used
				this.fillMaps(); //Re-shuffle
		}
	}
	
	//Updates the amount of times a number has appeared for a certain card type
	public int checkAmount(int am, CardType t) {
		if(t == CardType.CLUBS && clubs[am-1] < 6)
		 	clubs[am-1] += 1;
		  else if(t == CardType.DIAMONDS && diamonds[am-1] < 6)
		 	diamonds[am-1] += 1;
		  else if(t == CardType.HEARTS && hearts[am-1] < 6)
		 	hearts[am-1] += 1;
		  else if(t == CardType.SPADES && spades[am-1] < 6)
		 	spades[am-1] += 1;
		  else
		 	return 0; //If it already appeared 6 times, signal to generate a new number
		
		if(am > 10) { // If am is a J, Q, or K
			if(acePresent && am+total > 21){ //If there is an Ace present and we go over 21
				revalue();
			}
			this.total += 10; // Add this amount to the total
		}
		
		else if(am == 1) { //If we draw an ace
			acePresent = true;
			aceCounter++; //Triggers the if statement
			
			if(aceCounter > 1 && total+11 > total && Cards.size() > 3) {
				revalue();
				this.total += 1;
			} 
			else if(this.total + 11 > 21) //If it would go over 21 if Ace value was 11
				this.total += 1;
			else
				this.total += 11; //Add 11 to the total
		}
		
		else {
			if(acePresent && am+total > 21){ //If there is an Ace present and we go over 21
				revalue();
			}
			this.total += am;
		}
		 
		return 1; // Does not go over the limit
	}
	
	// In case there was an Ace valued at 11, change it to one to prevent on going over 21
	public void revalue() {
		this.total = 0; // Reset the total
		for(Card c: this.Cards) //For each card in the hand
			if(c.getValue() > 10) //If J,Q, or K, add 10 to the total
				this.total += 10;
			else
				this.total+=c.getValue(); //Add its value
	}
	
	//Updates the amount of times a card type has appeared
	public int checkAmount(CardType t) {
		if(types.get(t) < 78) {
			types.put(t, types.get(t) + 1);
			return 1; // Does not go over the limit
		}

		return 0; // Would go over the limit
	}
	
	//Used to see what options are available for the user
	public String optionsAvailable() {
		String ret = "";

		if(firstDraw == true) { //If it is the first draw
			if(this.total >= 9 && this.total <= 11) { // If the total falls between 9-11
				ret += " [2] Double"; //User can Double
				choices[0] = 1; // Make Double an available choice
			}
			if(this.Cards.get(0).getValue() == this.Cards.get(1).getValue()) { //If both values are the same
				ret += " [3] Split"; //User can Split
				choices[1] = 1; //Make Split an available choice
			}
		}
		
		ret+=" [4] Stand"; // Always provide a Stand option
		
		return ret;
	}
	
	public int choiceValidation(int choice){
		
		if(choice > 5 || choice < 1) //If choice is not in the options range
			return 0;
		
		if(firstDraw == true) {
			if(choice == 2 && choices[0] != 1){//If Double when not available
				return 0;
			}
			else if(choice == 3 && choices[1] != 1)//If Split when not available
				return 0;
			else
				firstDraw = false;
		}
		else if(firstDraw == false)
			if(choice == 2 || choice == 3) { //if Double/Split after first draw
				return 0;
			}
		
		return 1; //Valid choice
	}
	
	public void hit() {
		this.addCard(); //Add a card to the hand
		this.typeGenerator(getCardSize()-1); //Generate a card type for that index
	}
	
	public void split(Cards c2) {
		int copyVal; //Copies value of second card from the first hand
		CardType ct; //Copies card type of second card from the first hand
		
		//If the second card from the first hand was an Ace, it is valued at 11
		if(this.Cards.get(1).getValue()==1)
			copyVal = 11;
		else
			copyVal = this.Cards.get(1).getValue(); // Copy second card value from the first hand
		
		ct = this.Cards.get(1).getType(); // Copy second card type from the first hand
		
		//Assign respective values to the second hand
		c2.Cards.get(0).setValue(copyVal);
		c2.Cards.get(0).setType(ct);
		c2.total += copyVal;
	}
	
	public void handleSplit() {
		int userChoice = 0;
		
		//If first card of the respective hand is an Ace
		if(this.Cards.get(0).getValue() == 1) {
			this.hit(); //Only draw one card
			return; //Return back to main;
		}
		
		this.hit(); //Add a second card to the respectve hand
		this.print(); //Display the hand
		
		while(userChoice != 4 && this.total < 21){
			
			System.out.print("[1] Hit [4] Stand: ");
			Scanner userIn = new Scanner(System.in); //Get user choice
			userChoice = userIn.nextInt();
			
			//If user does not go over 21 and decides to Hit
			if(userChoice == 1 && this.total < 21) {
				this.hit();
				System.out.println();
				this.print(); //Print hand
			}
			else if(userChoice != 4) { //If not Hit or Stand
				System.out.println("Invalid Choice");
				continue; //Continue to the next iteration of the loop
			}
			else if(this.total >= 21) //If user Bust's
				break;	
		} //End of while loop
	}
	
	public void housePlay() {
		//If the House draws an Ace and any other number card that takes it above 17
		//The House must stand
		if((this.Cards.get(0).getValue() == 1 || this.Cards.get(1).getValue() == 1) && this.total >= 17)
			return;
		else if(this.total >= 17) //If the total from the starting hand is 17 or higher
			return;
		else {
			while(this.total < 17) { //While the total is less than 17
				this.hit(); //Keep drawing a card
			}
		}
	}
	
	//Compares the User's hand to the House's
	public int checkResult(Cards p, Cards h) {
		if(p.total > 21) // Bust if User total is greater than 21
			return 0;
		//Player wins if their total is higher than the House or if the House drew more than 21
		else if(p.total > h.total || (h.total > 21 && p.total <= 21))
			return 1;
		else if(h.total > p.total) // House wins if their total is bigger than the Player's
			return 2;
		else if(p.total == h.total) // Push if both totals are equal
			return 3;
		
		return 4; // Error
	}
	
	public void clearCards() {
		if(this.Cards.size() > 2) { //Remove extra cards in Cards ArrayList to start a new game
			for(int i = this.Cards.size()-1; i > 1;i--) {
				this.Cards.remove(i);
			}
		}
		reset();
	}
	
	//Used to reset certain values before starting a new game
	public void reset() {
		this.total = 0;
		this.firstDraw = true;
		this.aceCounter = 0;
		this.acePresent = false;
		choices[0] = 0; choices[1] = 0;
	}
	
	//Print showing all cards
	public void print() {
		Iterator<Card> it = Cards.iterator();
		String letter = "X";
		
		for(Card x: Cards) {
			it.next();
			if(x.getValue() > 10 || x.getValue() < 2) {
				if(x.getValue() == 11)
					letter = "J";
				else if(x.getValue() == 12)
					letter = "Q";
				else if(x.getValue() == 13)
					letter = "K";
				else
					letter = "A";
				System.out.print("[ " + letter + " " + x.getType() + " ]");
			}
			else
				System.out.print("[ "+ x.getValue() + " " + x.getType() + " ]");
			
			if(it.hasNext())
				System.out.print(" ");
		}
		
		System.out.println("\nTotal: " + this.total);
	}
	
	//Hides the second card from the House until they finish drawing
	public void housePrint() {
		String let = "f";
		int val = this.Cards.get(0).getValue();
		CardType ctype = this.Cards.get(0).getType();
		
		if(val > 10 || val < 2) {
			if(val == 11)
				let = "J";
			else if(val == 12)
				let = "Q";
			else if(val == 13)
				let = "K";
			else
				let = "A";
			System.out.println(" [ " + let + " " + ctype + "] [ # ]");
		}
		else
			System.out.println("[ " + val + " " + ctype + "] [ # ]");
	}
}
