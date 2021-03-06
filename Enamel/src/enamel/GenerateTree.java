package enamel;

import java.util.ArrayList;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;

public class GenerateTree {
	ListManager aList;
	DefaultMutableTreeNode result;

	GenerateTree() {
		this.aList = null;
		result = new DefaultMutableTreeNode("Your Story");;
	}

	public DefaultMutableTreeNode returnTree(ListManager orgList) {
		aList = new ListManager(orgList);
		aList.goHome();
		

		boolean skip = false;
		while (skip == false) {

			for (int u = 0; u < aList.currentList.size(); u++) {

				Node currentNode = aList.currentList.get(u);
				String key = currentNode.getKeyPhrase();
				String data = currentNode.getData();
				String label = currentNode.getLabel();
				String jKey = "";
				String jData = "";

				if (key == "#HEAD") {
					result.add(new DefaultMutableTreeNode(new NodeInfo(aList.currentList,0,"Location: Beginning of Story")));
				}



				else if (key == "#JUNCTION") {
					result.add(genJunc(currentNode, u));	
				} // end of JUNCTION
				
				else if(key == "/~NEXTT") {
					//result.add(new DefaultMutableTreeNode(new NodeInfo(aList.currentList,0,"Return to Main Story Line.")));
				}

				else {
					// generic node data
					if(key != "/~NEXTT") {
						result.add(new DefaultMutableTreeNode(new NodeInfo(aList.currentList,u,label)));
					}
					
					
				}

				// test if at the end of data structure (no more nodes left)
				if ((u == aList.currentList.size() - 1) && (currentNode.getKeyPhrase() != "#JUNCTION")) {
					skip = true;
				}
			}
		}

		return result;
	}
	
	private DefaultMutableTreeNode genButtons(int i, String name) {
		DefaultMutableTreeNode button = new DefaultMutableTreeNode(new NodeInfo(aList.currentList,0,"Button " + i + " (" + name + ")"));
		// for every node in the branch...
		for (int k = 0; k < aList.currentList.size(); k++) {
			Node branchNode = aList.currentList.get(k);
			String bKey = branchNode.getKeyPhrase();
			String blabel = branchNode.getLabel();
			
			if(bKey != "#BUTTON" && bKey != "/~skip:NEXTT"){
				button.add(new DefaultMutableTreeNode(new NodeInfo(aList.currentList,k,blabel)));
			}
			// reach last branch and last node
			if (bKey == "/~skip:NEXTT") {
				aList.currentList = branchNode.nextList;
				break;
			}
		}
		return button;
	}
	
	
	private DefaultMutableTreeNode genJunc(Node currentNode, int u) {
		DefaultMutableTreeNode junc = new DefaultMutableTreeNode(new NodeInfo(aList.currentList,u,"USER-INPUT"));
		
		// Now inside one of the Junctions
		int entrySetCount = 0;
		for (Entry<Integer, String> entry : currentNode.buttonsNames.entrySet()) {
			entrySetCount++;
			int i = entry.getKey();
			String name = entry.getValue();
			// set currentList to the branch so we can iterate through it
			aList.currentList = currentNode.buttons.get(i);
			
			//create Jtree branch for junction...
			junc.add(genButtons(i,name));
			
			
		}
		junc.add(new DefaultMutableTreeNode(new NodeInfo(aList.currentList,0,"End of USER-INPUT. Return to Main Story Line")));
		return junc;
	}
}
