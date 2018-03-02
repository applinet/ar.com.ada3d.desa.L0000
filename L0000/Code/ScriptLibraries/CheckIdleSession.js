function checkIdleSession() {
   dojo.xhrGet({
     url : window.location.href.split('.nsf')[0] + ".nsf?opendatabase",
     handleAs : 'text',
     preventCache : true, 
     load : function(response, ioArgs) {
       if (response.indexOf('action="/names.nsf?Login"')!=-1){
         clearInterval();
         window.location.href = window.location.href.split('.nsf')[0] + ".nsf?Login&RedirectTo=" + window.location.href
       } 
     },
     error : function(response, ioArgs) {
    	 alert('Ha sucedido un error inesperado')
        // Do any custom error handling here that you want to.
     }
  })
};
// El codigo chequea cada 16 mins (16 mins * 60 segs por minuto * 1000 millisegundos = 960000).
dojo.addOnLoad(function(){
    setInterval("checkIdleSession(	)", 120000);
});