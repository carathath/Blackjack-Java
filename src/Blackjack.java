import cardsPackage.Cards;
import java.util.ArrayList;
import java.util.Scanner;

class UserInputException extends Exception{
	String msg;
	
	UserInputException(String err){
		msg = err;
	}
	
	public String toString() { // Overrides the toString method when called by System.out.println
		return ("\n--- Message: " + msg + " ---");
	}
}


public class Blackjack {
	
	static void print(Cards c, String s, int x) { //Used to handle print type
		if(x == 0) {
			System.out.print("\n[" + s + "] ==> ");
			c.print();
		}
		else if(x == 1) { //The house hides their second card until its time for them to play
			System.out.print("\n[" + s + "] ==> ");
			c.housePrint();
		}
	}
	
	public static void main(String[] args) {
		int points = 2000; // User points
		int userChoice = 0, validBet = 0, validChoice = 0; // User choice
		int bet = 0, outcome = 0; // Bet amount, Game outcome
		double betMultiplier = 1.5;
		Cards house = new Cards(); // Stores the House's cards
		Cards player = new Cards(); // Used as the first Cards type inside playerCards
		ArrayList<Cards> playerCards = new ArrayList<Cards>(); // Able to expand if user Split's
		
		playerCards.add(player); // Adds first Cards object into playerCards
		house.fillMaps(); // Fills the maps inside the Cards class to keep track of how many cards have been played
		
		//First Draw. Adds two cards to both the House and Player Cards object
		for(int i = 0; i < 2; i++) {
			playerCards.get(0).addCard();
			house.addCard();
		}
		
		System.out.println("Welcome to Llama's Blackjack!");
		
		while(userChoice != 5) {
			
			// Generates the card type and value for each Card in the Cards object
			for(int j = 0; j < 2; j++) {
				playerCards.get(0).typeGenerator(j);
				house.typeGenerator(j);
			}
			
			System.out.println("\nPoints: " + points); //Output amounts of points
			
			while(validBet == 0 && userChoice != 5) {
				try {
					
					if(points < 100) { //If user can not bet anymore
						System.out.println("You lose....");
						validChoice = 0;
						userChoice = 5;
						break;
					}
					
					System.out.println("\n\n|||||||||||||||||||||||||||||||||||||||||||||||");
					System.out.print("Bet amount ([5] to EXIT): ");
					Scanner betIn = new Scanner(System.in); //Input user bet amount
					bet = betIn.nextInt();
					
					if(bet == 5) { // If user wants to exit
						userChoice = bet;
						validChoice = 1; //Won't go into the next while loop
						throw new UserInputException("User exit.");
					}
					else if(bet > points) {
						validBet = 0;
						throw new UserInputException("***** Bet amount exceeds amount of points. *****");
					} else if(bet < 100) {
						validBet = 0;
						throw new UserInputException("***** Minimum bet amount is 100 points *****");
					}
					
					points -= bet;
					validBet = 1; // Will go to the next while loop
				}catch(UserInputException exp){
					System.out.println(exp);
				} //end of try-catch block
			} // End bet while-loop
			
			while(validChoice == 0 && userChoice != 5) {
				System.out.println("\nPoints: " + points +  " -- Bet amount: " + bet);
				
				//If the house has drawn 21 on their opening hand, jump to the House decision IF block
				if(house.getCardTotal() == 21) {
					break;
				}
				//If the player has drawn 21 in their opening hand, make the house play
				//to see if they can draw up to 21 also
				else if(playerCards.get(0).getCardTotal() == 21) {
					house.housePlay();
					break;
				}
				
				print(house, "House", 1); //Print the House's hand
				
				//For each stack of cards the player has
				//Would be 2 stacks if the user Split's
				for(Cards c: playerCards)
					print(c, "Player", 0);
				
				try { //See options available depending on the User's hand
					System.out.println("[1] Hit" + playerCards.get(0).optionsAvailable());
					System.out.print("Enter a choice: ");
					Scanner input = new Scanner(System.in); //Get user choice
					userChoice = input.nextInt();
					
					//If user choice is not available
					if(playerCards.get(0).choiceValidation(userChoice) == 0) {
						validChoice = 0;
						throw new UserInputException("***** Invalid user choice. Try again. *****");
					}
					else
						validChoice = 1;
					
					System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||");
				}catch(UserInputException exp) {
					System.out.println(exp);
					continue;
				} //End of try-catch block
				
				// Game Logic
				
				if(userChoice == 1) { // Hit
					playerCards.get(0).hit();
					outcome = playerCards.get(0).checkResult(playerCards.get(0), house);
					//If due to the hit, we go over 21
					if(outcome == 0) {
						validChoice = 1;
					}else // Keep going
						validChoice = 0;
				}
				else if(userChoice == 2) { // Double
					points -= bet;
					bet+=bet; //Double the bet amount
					playerCards.get(0).hit(); //Only allow one more hit
					house.housePlay(); //Let the house play
					break; //Break out of the while loop
				}
				else if(userChoice == 3) { // Split
					points -= bet; //Remove the new bet from player points
					bet += bet; //Double the bet
					Cards splitCard = new Cards(); // Generate second hand
					splitCard.addCard(); // This card will copy the second card from the first hand
					playerCards.add(splitCard); // Now the user has two hands to play with
					playerCards.get(0).split(playerCards.get(1)); // Split into two hands
					playerCards.get(0).deleteCard(1); // Remove the second card from the first hand
					
					//For each hand, draw until the player Stands or goes over 21
					for(int i = 0; i < playerCards.size(); i++) {
						System.out.println("\n*** Hand " + (i+1) + " ***");
						playerCards.get(i).handleSplit();
					}
					System.out.println("\n************************");
					house.housePlay();
					break; //break out of the while loop
				}
				else if(userChoice == 4) { // Stand
					house.housePlay(); //House plays
					break; //Break out of the while loop
				}
				
			} // End of validChoice while loop
			
			//House decisions
			if(userChoice!= 5) {
				
				print(house, "House", 0); //Reveal the House's cards
				
				for(Cards c: playerCards) { //For each hand the player has
				print(c, "Player", 0);
				
				outcome = c.checkResult(c, house); //Get the outcome
				
				if(outcome == 0) { // Bust, player hand over 21
					System.out.println("***** Bust. *****");
				}
				else if(outcome == 1) {// Player wins
					if(playerCards.size() == 2)
						bet/=2; //Apply the bet of one hand
					points+= bet; //Go back to starting value
					points+= (bet*betMultiplier); //User wins 1.5x the bet amount
					System.out.println("***** Won: " + (int)(bet * 1.5) + " *****");
				}
				else if(outcome == 2) // Dealer wins
					System.out.println("***** Dealer wins. *****");
				else if(outcome == 3) { // Push (tie)
					System.out.println("***** Push. *****");
					points += bet;
				}
				else if(outcome == 4)
					System.out.println("Error in checkResult()");
				}
				
			} // End of House decisions
			
			validBet = 0; //Reset bet validation
			validChoice = 0; //Reset user choice validation
				
			for(Cards c: playerCards) //Clear the cards for the player and house
				c.clearCards();
			house.clearCards();
			
			if(playerCards.size() > 1) //If there was a split, remove the second hand
				playerCards.remove(1);
		} // End of userChoice while-loop
		
	} // End of main

}
