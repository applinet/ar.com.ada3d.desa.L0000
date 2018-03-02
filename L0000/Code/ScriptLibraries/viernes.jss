function getQuediaEsHoy(){
	return @Today();
}

function quebase(dato){
	
	
	var baseactual:NotesDatabase = session.getCurrentDatabase();
	var quesoy = baseactual.getTitle() == "L8669" ? "si es esta " + dato:"No es";
	return quesoy
	
}

function nueva(){
	return "fernando"
}