//Global variables
var channel = "testapp_ticket_channel";

function testMobileBeanRead()
{
	//read all the beans
	var beans = mobileBean.readAll(channel);
	
	//de-serialize the bean oids used for read each bean individually
	beans = (""+beans).split(",") //convert the list of oids into an array
	var length = beans.length;
	
	//Iterate each bean via oid
	for(var i=0; i<length; i++)
	{
		var oid = beans[i];
		
		//Read the 'title' property of the bean
		var title = mobileBean.getValue(channel,oid,"title");
		
		//Read the 'comment' property of the bean
		var comment = mobileBean.getValue(channel,oid,"comment");
		
		//Display
		document.writeln("-----------------------------------<br/>");
		document.writeln("Title: "+title+"<br/>");
		document.writeln("Comment: "+comment+"<br/>");
	} 
}

function testMobileBeanArray()
{
	//read all the beans
	var beans = mobileBean.readAll(channel);
	
	//de-serialize the bean oids used for read each bean individually
	beans = (""+beans).split(",") //convert the list of oids into an array
	var length = beans.length;
	
	for(var i=0; i<length; i++)
	{
		var oid = beans[i];
		
		//Access Array Length
		var arrayLength = mobileBean.arrayLength(channel,oid,"mockList");
		
		//Access the 'title' property
		var title = mobileBean.getValue(channel,oid,"title");
		
		//Display
		document.writeln("-----------------------------------<br/>");
		document.writeln("Title: "+title+"<br/>");
		document.writeln("ArrayLength: "+arrayLength+"<br/>");
		
		//Access Array Value
		var arrayValue = mobileBean.getValue(channel,oid,"mockList[0]");
		document.writeln("ArrayValue: "+arrayValue+"<br/>");
	}
}

function testMobileBeanUpdate()
{
	//read all the beans
	var beans = mobileBean.readAll(channel);
	
	//de-serialize the bean oids used for read each bean individually
	beans = (""+beans).split(",") //convert the list of oids into an array
	var length = beans.length;
	
	var oid = beans[0];
	
	//Update the Title and the Comment properties
	mobileBean.updateBean(channel,oid,"title", "title://updated");
	mobileBean.updateBean(channel,oid,"comment", "comment://updated");
	mobileBean.commit();
		
	//Read the 'title' property of the bean
	var title = mobileBean.getValue(channel,oid,"title");
	
	//Read the 'comment' property of the bean
	var comment = mobileBean.getValue(channel,oid,"comment");
	
	//Display
	document.writeln("-----------------------------------<br/>");
	document.writeln("Title: "+title+"<br/>");
	document.writeln("Comment: "+comment+"<br/>");
}

function testMobileBeanDelete()
{
	//read all the beans
	var beans = mobileBean.readAll(channel);
	
	//de-serialize the bean oids used for read each bean individually
	beans = (""+beans).split(",") //convert the list of oids into an array
	var length = beans.length;
	
	var oid = beans[0];
	
	//Delete the bean in question
	mobileBean.deleteBean(channel,oid);
}

function testMobileBeanAdd()
{
	//Add a new bean
	mobileBean.addBean(channel,"title","new://title");
	mobileBean.addBean(channel,"comment", "new://comment");
	var oid = mobileBean.commit();
	
	//Check the addition
	var title = mobileBean.getValue(channel,oid,"title");
	var comment = mobileBean.getValue(channel,oid,"comment");
	
	//Display
	document.writeln("-----------------------------------<br/>");
	document.writeln("Title: "+title+"<br/>");
	document.writeln("Comment: "+comment+"<br/>");
}

function testRPC()
{
	//Construct the payload in json format
	var payload = "{'firstName':'John', 'lastName':'Doe'}";
	
	//Make the invocation on a remote service identified by '/user/profile'
	var response = rpc.invoke("/user/profile",payload);
	document.writeln(response+"<br/>");
	
	//Process the json response
	response = eval('(' + response + ')');
	
	//Display
	document.writeln("Status: "+response.status+"<br/>");
	document.writeln("Message: "+response.statusMsg+"<br/>");
	document.writeln("Phone #"+response.phoneNumber+"<br/>");
	docment.writeln("<a href='api.html'>Back</a>");
}