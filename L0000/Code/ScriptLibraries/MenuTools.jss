/*
 * Esta libreria maneja el menu de arriba que vemos en el ccLayout --> var menuTools 
 * Tambien carga una viewScope con todos los menú en la funcion --> menuFull(menuName)
 * */
var menuTools = {
    "getMenu" : function(nsf:NotesDatabase, menuName:string) {
		try{
			if (!sessionScope.menuList) {
	            sessionScope.put("menuList", new java.util.HashMap());
	        }
	        if (sessionScope.menuList.containsKey(menuName)) {
	            return sessionScope.menuList.get(menuName);
	        }
	        
	        /* Devuelve las entradas de menú basandose en una vista que toma de configuración*/
	        var viewClave:NotesView = nsf.getView(getOpcionesClave("VIEW_MODULOS", "codigo")[0]); //vista 'vModulosTreeview'
	        var viewNav:NotesViewNavigator = viewClave.createViewNav();
	        var viewEntry = viewNav.getFirst();
	        var result = [];
	        var menuSelected:java.util.Vector = getCodigoMenuSelected();
	        if (menuSelected.isEmpty()&& userBean.accessRoles.toString().contains('[usrInitial]')) {
		        	return;
	        }
	        var strMenuAlias:String = "";
	        
	        while (viewEntry != null) {
	        	/* Revisa los padres de la vista 'vModulosTreeview'*/
	            var nextviewEntry = viewNav.getNextSibling(viewEntry);
	            var lineResult = {};
	            strMenuAlias = viewEntry.getColumnValues()[1];
	            
	            if (menuSelected.contains(strMenuAlias.split('|')[2]) || isUSRSEG()) { /*Esto hace lo mismo que explico mas abajo estos son padres */
		            lineResult.name = strMenuAlias.split('|')[0];
			        lineResult.items = [];
			        lineResult.page = [];
			        
			        //Solo va a cargar los hijos si el padre es autorizado
		            var child = viewNav.getChild(viewEntry);
		            while (child != null) {
		            	/* Revisa los hijos de la vista 'vModulosTreeview'*/
		            	 
		                var nextChild = viewNav.getNextSibling(child);
		                
		                /*  Cargo un atring con los datos de la vista
		                 *  La columna de la vista devuelve = Descripcion | pagina | codigo unico de modulo
		                 *  Al string lo divido en un array de 3 posiciones separando por |
		                 *  menuSelected lo carge arriba con los codigos que tiene el usuario seleccionados
		                 *  Comparo menuSelected si contiene array[2] que es el codigo, si es el mismo cargo el menu
		                 *  O tamabien cargo el menu si es usuario de seguridad   
		                 * */
		                strMenuAlias = child.getColumnValues()[1];
		                if (menuSelected.contains(strMenuAlias.split('|')[2]) || isUSRSEG()) { /* [2]= codigo que comparo con menuSelected del usuario */
		                	
			                lineResult.items.push(strMenuAlias.split('|')[0]); /* A items le cargo la descricion que voy a ver en el menu*/
			                lineResult.page.push(strMenuAlias.split('|')[1]);  /* A page le cargo la pagina linkeada que tiene este menu*/
		                }
		                child.recycle();
		                child = nextChild;
		            }
		            result.push(lineResult);
	            } //End si el menu está en los autorizados
		        
	            viewEntry.recycle();
	            viewEntry = nextviewEntry;
	        }
	        
	        viewNav.recycle();
            viewClave.recycle();

		} catch(e) {
	        addFacesMessage(e.toString(), null, "FATAL");
	        print(e);
		}
      
        // Se guarda en cache
        sessionScope.menuList.put(menuName, result);
        return result;
        
    }   
	



};


/*Devuelve un java vector con los menues autorizados del usuario actual logueado*/
function getCodigoMenuSelected():java.util.Vector{
	var docUsuario:NotesDocument = getUserDocument();
	if (docUsuario != null){
		return docUsuario.getItemValue("usr_MenuSelected_cod");
	}
	return new java.util.Vector // Devuelvo un Vector vacio para despues validar
}


function menuFull(menuName:string) {
	try{
		if (!viewScope.menuFull) {
            viewScope.put("menuFull", new java.util.HashMap());
        }else{
            return viewScope.menuFull.get(menuName);
        }
        
        /* Devuelve las entradas de menú basandose en una vista que toma de configuración*/
        var dbConf:NotesDatabase = getDbCfg();//base configuracion
        if (dbConf == null){
        	return;
        }
        /* Devuelve las entradas de menú basandose en una vista que toma de configuración*/
        var viewClave:NotesView = dbConf.getView(getOpcionesClave("VIEW_MODULOS_FULL", "codigo")[0]); //vista 'vModulosCheckBox'
        var viewNav:NotesViewNavigator = viewClave.createViewNav();
        var viewEntry = viewNav.getFirst();
        var result = [];
        var cicloChild = false;
        
        while (viewEntry != null) {
        	/* Revisa los padres de la vista 'vModulosTreeview'*/
            var nextviewEntry = viewNav.getNextSibling(viewEntry);
            var lineResult = {};
            
            lineResult.name = viewEntry.getColumnValues()[1];
	        lineResult.code = viewEntry.getColumnValues()[0];
       		result.push(lineResult);
       		lineResult = {};
	        dBar.info("1-" + viewEntry.getColumnValues()[1]);
       		
	        //Solo va a cargar los hijos si el padre es autorizado
            var child = viewNav.getChild(viewEntry);
            while (child != null) {
            	/* Revisa los hijos de la vista 'vModulosTreeview'*/
                                
		        dBar.info("2-" + child.getColumnValues()[1]);
                lineResult.name = child.getColumnValues()[1];
    	        lineResult.code = child.getColumnValues()[0];
           		result.push(lineResult);
           		lineResult = {};
           		           		
           		var subChild = viewNav.getChild(child);
           		if (subChild == null){
           			if (cicloChild){
           				var nextChild = pendingChild;
           				cicloChild = false;
           			}else{
		           		var nextChild = viewNav.getNextSibling(child);
           			}
	           		child.recycle();
                	child = nextChild;
           		}else{
           			if(!cicloChild){
	           			var pendingChild = viewNav.getNextSibling(child);
           			}
           			cicloChild = true;
           			child.recycle();
           			child = subChild;
           		}
           		
           		
            }
            
            viewEntry.recycle();
            viewEntry = nextviewEntry;
        }
        
        viewNav.recycle();
        viewClave.recycle();

	} catch(e) {
        addFacesMessage(e.toString(), null, "FATAL");
        print(e);
	}
  
    // Se guarda en cache
	 viewScope.menuFull.put(menuName, result);    
}   