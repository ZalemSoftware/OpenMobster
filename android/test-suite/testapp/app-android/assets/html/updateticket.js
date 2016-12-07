function updateTicket(oid)
{
    var title;
    var comments;
    
    var titleData = mobileBean.getValue(channel,oid,"title");
    var commentData = mobileBean.getValue(channel,oid,"comment");
    
    var titleUI = new joGroup([
    				new joLabel("Title"),
    				new joFlexrow(title = new joInput(titleData))
    		   ]);
    		   
     					
    var commentsUI = new joGroup([
    				new joLabel("Comments"),
    				new joFlexrow(comments = new joInput(commentData))
    		   ]);
    
    var saveButton = new joButton("Save");		  		   
    var buttonUI = new joGroup([saveButton]);
    
    saveButton.selectEvent.subscribe(function(){
    	var inputTitle = title.getData();
    	var inputComment = comments.getData();
    	
    	//Validation
    	if(inputTitle == "")
    	{
    		scn.alert("Validation","Title is required");
    		return;
    	}
    	
    	if(inputComment == "")
    	{
    		scn.alert("Validation","Comment is required");
    		return;
    	}
    
    	mobileBean.updateBean(channel,oid,"title",inputTitle);
    	mobileBean.updateBean(channel,oid,"comment",inputComment);
    	mobileBean.commit();
    	
    	alert("jo-refresh");
     }
    );
    		   
	var card = new joCard([titleUI,commentsUI,buttonUI]).setTitle(appTitle);
		
	return card;
}