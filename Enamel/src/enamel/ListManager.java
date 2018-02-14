package enamel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;

public class ListManager {

	ArrayList<Node> currentList;
	ArrayList<Node> home;
	int index;
	int cells;
	int buttons;
	
	/* #############################################################################
	 * CONSTRUCTORS
	 * #############################################################################*/

	public ListManager() {
	}
	
	public ListManager(ListManager copy) {
		this.currentList = copy.home;
		this.home = copy.home;
		this.index = 0;
		this.cells = copy.cells;
		this.buttons = copy.buttons;
	}

	public ListManager(int c, int b) {
		//Constructor for List manager given cell and button sizes as data
		this.cells = c;
		this.buttons = b;
		currentList = new ArrayList<Node>();
		home = currentList;
		Node root = new Node("#HEAD", "Cells " + cells + "\nButton " + buttons);
		currentList.add(root);
		index = 0;
	}

	public Node getNode() {
		if(index < currentList.size()) {
			return currentList.get(index);
		}else {
			throw new IllegalArgumentException("getNode index is out of bounds!");
		}
	}
	
	/* #############################################################################
	 * MUTATORS
	 * #############################################################################*/
	
	
	public void addNext(String k, String d) {
		//Cannot AddNext on JUNCTIONS
		if(getNode().getKeyPhrase().equals("#JUNCTION")) {
			System.out.println("Error: Can Not add nodes on JUNCTIONS! You are currently on a JUNCTION.");
			return;
		}
		
		// Creates node next to current position. Index increases to that node!
		currentList.add(index + 1, new Node(k, d));
		index++;
		printString("Added node:");
	}

	public void remove() {
		//Removes node as long as it is not a JUNCTION or BUTTON
		if(!getNode().keyPhrase.equals("#JUNCTION") && !getNode().keyPhrase.equals("#BUTTON")) {
			currentList.remove(index);
		}
	}

	/*
	 * Not using this yet because it might add confusion. 
	 * public void addLast(String k, String d) { currentList.add(new Node(k,d));
	 * System.out.println("Created Node: " +
	 * currentList.get(currentList.size()-1).getKeyPhrase() + " " +
	 * currentList.get(currentList.size()-1).getData()); }
	 */
	
	/* #############################################################################
	 * NAVIGATION
	 * #############################################################################*/
	
	public void next() {
		//Check index is valid
		if (index + 1 > currentList.size() - 1) {
			System.out.println("End of List!");
			return;
		}
		
		index++;
		if(getNode().keyPhrase.equals("/~skip:NEXTT")) {
			ArrayList<Node> oldList = currentList;
			currentList = getNode().nextList;
			index = 0;
			getNode().prevList = oldList;
			return;
		}
		
		if (getNode().keyPhrase.equals("#JUNCTION")) {
			System.out.println("You are currently on a junction... choose a branch to proceed.");
			return;
		}
		
		printString("Switched to node(next):");
		return;
	}

	public void prev() {	
		//Handles moving from BUTTON back to JUNCTION
		if (getNode().keyPhrase.equals("#BUTTON")) {
			currentList = getNode().prevList;
			index = currentList.size() - 1;
			printString("Switched from button to node:");
			return;
		}
		
		if(getNode().keyPhrase.equals("/~NEXTT")) {
			currentList = getNode().prevList;
			index = currentList.size() - 2;
			return;
		}
		
		//check if index is valid
		if (index - 1 < 0) {
			System.out.println("Already at root node!");
			return;
		}
		
		index--;
		printString("Switched to node(prev):");
		return;
	}

	public String getKeyPhrase() {
		return getNode().getKeyPhrase();
	}

	public String getData() {
		return getNode().getData();
	}
	
	public void goHome() {
		currentList = home;
		index = 0;
	}
	
	public ArrayList<Node> getNextList(){
		return getNode().nextList;
	}
	
	public void setIndex(int i) {
		if(i >= 0 && i < currentList.size()) {
			this.index = i;
		}
	}
	
	/* #############################################################################
	 * JUNCTION
	 * #############################################################################*/

	public void createJunction(HashMap<Integer, String> s) {
		// takes an input of strings for button choices. Order of string array matters! Each element corresponds to a button!
		
		//Only allow JUNCTION creation on the last Node
		if (index != currentList.size() - 1) {
			System.out.println("Can't create JUNCTION unless you are on the last node!");
			return;
		}
		
		//Limit JUNCTION creation when inside a button branch.
		if (currentList.get(0).getKeyPhrase().equals("#BUTTON")) {
			System.out.println("You cannot create another junction within a button branch!");
			return;
		}
		
		//Check if there are enough buttons
		if (s.size() > buttons) {
			throw new IndexOutOfBoundsException("There are more branches than the buttons set for this scenario!");
		}
		
		System.out.println("CREATING JUNCTION...");
		
		//NEXTT creation
		ArrayList<Node> nextt = new ArrayList<Node>();
		Node nexttHead = Node.button("/~NEXTT", "NEXTT path" , currentList);
		nextt.add(nexttHead);
		

		//JUNCTION Creation.
		HashMap<Integer, ArrayList<Node>> buttons = new HashMap<Integer, ArrayList<Node>>();
		for (Entry<Integer, String> n : s.entrySet()) {
			Node buttonTail = Node.tail(nextt);
			if (n.getValue() != null) {
				ArrayList<Node> newList = new ArrayList<Node>();
				Node buttonHead = Node.button(n.getValue(), currentList);
				newList.add(buttonHead);
				newList.add(buttonTail);
				buttons.put(n.getKey(),newList);
			}
		}
		currentList.add(index + 1, Node.junction(buttons, s, nextt));
		index++;
		
	}
	
	public void junctionGoto(int n) {
		if (getNode().getKeyPhrase().equals("#JUNCTION")) {
			if (n >= 0 && n < buttons) {
				currentList = getNode().getButtons().get(n);
				index = 0;

				printString("Switched from Junction to node:");
			} else {
				System.out.println("junctionGoTo Error: Out of Bounds");
			}
		}

	}
	
	public int junctionSearch(String s) {
		//Searches the current junction for value and gives it's key.
		if (getNode().getKeyPhrase().equals("#JUNCTION")) {
			HashMap<Integer,String> map = getNode().buttonsNames;
		    for (Entry<Integer, String> entry : map.entrySet()) {
		        if (s.equals(entry.getValue())) {
		            return entry.getKey();
		        }
		    }
		    System.out.println("Error junctionSearch: string not found in junction");
		    return -1;
		}
		System.out.println("Error junctionSearch: you are not on a Junction!");
		return -2;
	}
	public static int mapSearch(HashMap<Integer,String> map, String string) {
		//static method that searchs the value of the map and gives it's key.
		    for (Entry<Integer, String> entry : map.entrySet()) {
		        if (string.equals(entry.getValue())) {
		            return entry.getKey();
		        }
		    }
		    System.out.println("Error junctionSearch: string not found in junction");
		    return -1;
	}
		
	


	
	
	
	
	
	
	
	
	
	/* #############################################################################
	 * TESTING STUFF
	 * #############################################################################*/
	
	
	//This is for console testing
	public void printString() {
		System.out.println("CURRENT Node: " + getNode().getKeyPhrase() + " "+ '"' + getNode().getData() + '"'); 
	}
	public void printString(String s) {
		System.out.println(s + " " + getNode().getKeyPhrase() + " "+ '"' + getNode().getData() + '"'); 
	}
	

}
