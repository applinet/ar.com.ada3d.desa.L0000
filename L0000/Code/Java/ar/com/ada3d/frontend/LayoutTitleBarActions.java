package ar.com.ada3d.frontend;

import com.ibm.xsp.extlib.tree.impl.BasicLeafTreeNode;
import com.ibm.xsp.extlib.tree.impl.BasicNodeList;

public class LayoutTitleBarActions extends BasicNodeList {
private static final long serialVersionUID = 1L;

public LayoutTitleBarActions() {
	addLeaf("Nuevo Gasto", "btnClose");
	addLeaf("Alta de Proveedor", "btnSave");
	addLeaf("Pagar", "btnPagar");
	addLeaf("Recalcular", "btnClose");
	
}

private void addLeaf(String label, String boton) {
	
	BasicLeafTreeNode node = new BasicLeafTreeNode();
	node.setLabel(label);
	//node.setHref("/Project_View.xsp" );
	//node.setOnClick("alert('On Click');");
	//String script = "window.open(\"" + url.toString() + "\");";
	//String script = "$(\"[id$='#{id:btnClose}']\").get(0).click()" ; 
	String script = "dojo.query(\"[id$='" + boton + "']\")[0].click();";
    node.setOnClick(script);            

	//node.setOnClick("XSP.getElementById('#{id:btnClose}').click()");
	addChild(node);
}

}