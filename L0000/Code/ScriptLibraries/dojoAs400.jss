function getAS(){
	/*
	var jsonData = [];
	 
	var thisEntry = {};
	thisEntry.Status = 'Open';
	thisEntry.Name = 'John';
	jsonData.push(thisEntry);
	 
	thisEntry = {};
	thisEntry.Status = 'Closed';
	thisEntry.Name = 'Bill';
	jsonData.push(thisEntry);
	 
	thisEntry = {};
	thisEntry.Status = 'Closed';
	thisEntry.Name = 'Mike';
	jsonData.push(thisEntry);
	 
	thisEntry = {};
	thisEntry.Status = 'Open';
	thisEntry.Name = 'Jim';
	jsonData.push(thisEntry);
	 
	thisEntry = {};
	thisEntry.Status = 'Open';
	thisEntry.Name = 'Steve';
	jsonData.push(thisEntry);
	viewScope.put("selectAS", toJson(jsonData) );
	*/
	var dummyDoc:NotesDocument = database.createDocument();

	dummyDoc.replaceItemValue("TestField","");
	dummyDoc.replaceItemValue("count","");
	
	//var agent:NotesAgent = database.getAgent("a.readsql");
	var agent:NotesAgent = database.getAgent("a.dojoAs400");
	
	if(agent!=null){

		agent.runWithDocumentContext(dummyDoc);
		viewScope.put("selectAS", dummyDoc.getItemValueString("TestField") );
		viewScope.put("selectCount", dummyDoc.getItemValueString("count") );
	}
}
function setConfirmationMessage(message) {
	viewScope.confirmMessage = message;
	viewScope.MessageType = "Error"
	viewScope.MessageText = message;
}

function setInformationMessage(message) {
	viewScope.infoMessage = message;
}

function getEdificios(){
	var dummyDoc:NotesDocument = database.createDocument();

	dummyDoc.replaceItemValue("TestField","");
	dummyDoc.replaceItemValue("count","");
	
	//var agent:NotesAgent = database.getAgent("a.readsql");
	var agent:NotesAgent = database.getAgent("SdojoAs400_edificios");
	
	if(agent!=null){

		agent.runWithDocumentContext(dummyDoc);
		viewScope.put("selectAS", dummyDoc.getItemValueString("TestField") );
		viewScope.put("selectCount", dummyDoc.getItemValueString("count") );
	}
}

function getABM(){
	var dummyDoc:NotesDocument = database.createDocument();

	dummyDoc.replaceItemValue("TestField","");
	dummyDoc.replaceItemValue("count","");
	
	//var agent:NotesAgent = database.getAgent("a.readsql");
	var agent:NotesAgent = database.getAgent("SdojoAs400_abm");
	
	if(agent!=null){

		agent.runWithDocumentContext(dummyDoc);
		viewScope.put("selectAS", dummyDoc.getItemValueString("TestField") );
		viewScope.put("selectCount", dummyDoc.getItemValueString("count") );
	}
}

function getJoinEdif(){
	var dummyDoc:NotesDocument = database.createDocument();

	dummyDoc.replaceItemValue("TestField","");
	dummyDoc.replaceItemValue("count","");
	
	//var agent:NotesAgent = database.getAgent("a.readsql");
	var agent:NotesAgent = database.getAgent("SdojoAs400_join_edif");
	
	if(agent!=null){

		agent.runWithDocumentContext(dummyDoc);
		viewScope.put("selectAS", dummyDoc.getItemValueString("TestField") );
		viewScope.put("selectCount", dummyDoc.getItemValueString("count") );
	}
}
