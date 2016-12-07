function home()
{
	//Create the menu
	var list = loadList();
	var menu = new joCard([list]);
	menu.setTitle(appTitle);
	
	//Event Handling
	list.selectEvent.subscribe(function(id) 
	{
		var comment = mobileBean.getValue(channel,id,"comment");
		var title = mobileBean.getValue(channel,id,"title");
		scn.alert(title, comment, 
				[
					{label: "Update", action: function(){
						var card = updateTicket(id);
						stack.push(card);
					}, context: this},
					
					{label: "Close", action: function(){}, context: this},
					
					{label: "Delete", action: function(){
						var deletedBeanId = mobileBean.deleteBean(channel,id);
						alert("jo-refresh");
					}, context: this}
				]);
	}, this);
	
	return menu;
}