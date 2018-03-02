dojo.provide('ar.com.ada3d.widget.Dialog');
dojo.require('dijit.Dialog');
 
(function(){
    dojo.declare("ar.com.ada3d.widget.Dialog", dijit.Dialog, {
        postCreate: function(){
          this.inherited(arguments);
          dojo.query('form', dojo.body())[0].appendChild(this.domNode);
        },
        _setup: function() {
          this.inherited(arguments);
          if (this.domNode.parentNode.nodeName.toLowerCase() == 'body')
            dojo.query('form', dojo.body())[0].appendChild(this.domNode);       
        }       
    })
}());