package cardsPackage;

class Card {
	private CardType type; //Assigns CardType to the card
	private int value; //Assigns card value to the card
	
	public Card() { //Constructor
		this.type = CardType.HEARTS;
		this.value = 0;
	}
	
	public CardType getType() { //CardType getter
		return type;
	}
	
	public int getValue() { //Card value getter
		return value;
	}
	
	public void setType(CardType type) { //CardType setter
		this.type = type;
	}
	
	public void setValue(int val) { //Card value setter
		this.value = val;
	}
	
	
}
