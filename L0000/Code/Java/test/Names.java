package test;

import com.ibm.xsp.extlib.tree.impl.*;
import lotus.domino.*;
import javax.faces.context.FacesContext;
import java.util.*;



public class Names extends BasicNodeList {
	private static final long serialVersionUID = -6873644238872938295L;

	private transient View managerView = null;
	private final Set<String> processedNames = new HashSet<String>();

	public Names() throws NotesException {
		Database database = (Database)this.getVariableValue("database");
		this.managerView = database.getView("Managers");
		this.managerView.setAutoUpdate(false);

		ViewEntryCollection bosses = this.managerView.getAllEntriesByKey("fer", true);
		ViewEntry boss = bosses.getFirstEntry();
		while(boss != null) {
			this.processNode(String.valueOf(boss.getColumnValues().get(1)), null);

			ViewEntry tempEntry = boss;
			boss = bosses.getNextEntry(boss);
			tempEntry.recycle();
		}
		bosses.recycle();
	}

	private void processNode(String name, BasicTreeNode parent) throws NotesException {
		// Fetch the reports collection immediately so we can determine if this node is a leaf
		ViewEntryCollection reports = this.managerView.getAllEntriesByKey(name, true);

		// Create the node as appropriate - BasicTreeNode is the immediate common abstract parent of both node types
		BasicTreeNode node = reports.getCount() > 0 ? new BasicContainerTreeNode() : new BasicLeafTreeNode();
		node.setLabel(name);
		node.setSubmitValue(name);
		node.setRendered(true);
		//node.setOnClick(name); 
		node.setTitle("titulo");
		/*
		Map<String, Object> sessionScope = ExtLibUtil.getSessionScope();
		if(!sessionScope.containsKey("cachedTime")) {
		        // Some actual expensive operation goes here
		        sessionScope.put("cachedTime", name);
		}
		*/
		// Add the node to either the root of the tree (if it's a top-level person) or to the provided parent
		if(parent == null) {
			this.addChild(node);
		} else {
			// The parent should be a container node if we got here
			((BasicContainerTreeNode)parent).addChild(node);
		}

		// After a quick sanity check, it's time for recursion!
		if(!this.processedNames.contains(name)) {
			ViewEntry report = reports.getFirstEntry();
			while(report != null) {
				this.processNode(String.valueOf(report.getColumnValues().get(1)), node);

				ViewEntry tempReport = report;
				report = reports.getNextEntry(report);
				tempReport.recycle();
			}
			reports.recycle();
			this.processedNames.add(name);
		}
	}

	private Object getVariableValue(String varName) { 
		FacesContext context = FacesContext.getCurrentInstance(); 
		return context.getApplication().getVariableResolver().resolveVariable(context, varName); 
	} 
}