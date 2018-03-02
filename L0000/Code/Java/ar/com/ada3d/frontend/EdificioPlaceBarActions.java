package ar.com.ada3d.frontend;

import com.ibm.xsp.extlib.tree.impl.BasicLeafTreeNode;
import com.ibm.xsp.extlib.tree.impl.BasicNodeList;

public class EdificioPlaceBarActions extends BasicNodeList {
private static final long serialVersionUID = 1L;

public EdificioPlaceBarActions() {
	addLeaf("Modificar Fecha 1er Vto.");
	addLeaf("Fecha 2do Vto.");
	addLeaf("Intereses a Deudores");
	addLeaf("Recargo para el 2do. Vto.");
	
}

private void addLeaf(String label) {
	
	BasicLeafTreeNode node = new
	BasicLeafTreeNode();
	node.setLabel(label);
	//node.setHref("/Project_View.xsp" );
	//node.setOnClick("alert('On Click');");
	addChild(node);
}

}