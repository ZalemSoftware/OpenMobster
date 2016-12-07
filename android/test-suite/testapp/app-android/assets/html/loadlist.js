function loadList()
{
	var beans = mobileBean.readAll(channel); //accesses the MobileBean service via the Javascript bridge
	beans = (""+beans).split(","); //converts the list of MobileBean oids into an array
	var length = beans.length;
	var listArray = new Array(length);
	for(var i=0; i<length; i++)
	{
		var oid = beans[i];
		var title = mobileBean.getValue(channel,oid,"title"); //extracts the value of the field named 'title'
		listArray[i] = {title:title,id:oid};
	}
	
	var list = new joMenu(listArray); //populates the Menu with the information extracted from the MobileBean instances
	return list;
}