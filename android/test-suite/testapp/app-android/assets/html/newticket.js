//Function declaration
function newTicket()
{
    var title;
    var comments;
    
    var titleUI = new joGroup([
    				new joLabel("Title"),
    				new joFlexrow(title = new joInput(""))
    		   ]);
    		   
     					
    var commentsUI = new joGroup([
    				new joLabel("Comments"),
    				new joFlexrow(comments = new joInput(""))
    		   ]);
    
    var saveButton = new joButton("Save");		  		   
    var buttonUI = new joGroup([saveButton]);
    
    saveButton.selectEvent.subscribe(function(){
    	var inputTitle = title.getData();
    	var inputComment = comments.getData();
    	
    	//Validate
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
    
    	mobileBean.addBean(channel,"title",inputTitle);
    	mobileBean.addBean(channel,"comment",inputComment);
    	mobileBean.commit();
    	
    	alert("jo-refresh");
     }
    );
    		   
	var card = new joCard([titleUI,commentsUI,buttonUI]).setTitle(appTitle);
		
	return card;
}