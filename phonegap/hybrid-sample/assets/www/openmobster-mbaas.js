window.sync = function(){};

window.sync.echo = function(message,successCallback,errorCallback)
{
	return cordova.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'echo', //Tell plugin, which action must be performed
    [message] //Passing a list of arguments to the Plugin
	);
};

window.sync.json = function(input,successCallback,errorCallback)
{
	return cordova.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'json', //Tell plugin, which action must be performed
    [input] //Passing a list of arguments to the Plugin
	);
};

window.sync.newBean = function(channel,state,successCallback,errorCallback)
{
	return cordova.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'newBean', //Tell plugin, which action must be performed
    [channel,state] //Passing a list of arguments to the Plugin
	);
};

window.sync.readall = function(channel,properties,successCallback,errorCallback)
{
	return cordova.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'readall', //Tell plugin, which action must be performed
    [channel,properties] //Passing a list of arguments to the Plugin
	);
};

window.sync.readBean = function(channel,id,properties,successCallback,errorCallback)
{
	return cordova.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'readBean', //Tell plugin, which action must be performed
    [channel,id,properties] //Passing a list of arguments to the Plugin
	);
};

window.sync.updateBean = function(channel,id,state,successCallback,errorCallback)
{
	return cordova.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'updateBean', //Tell plugin, which action must be performed
    [channel,id,state] //Passing a list of arguments to the Plugin
	);
};

window.sync.deleteBean = function(channel,id,successCallback,errorCallback)
{
	return cordova.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'deleteBean', //Tell plugin, which action must be performed
    [channel,id] //Passing a list of arguments to the Plugin
	);
};

window.sync.arrayInsert = function(channel,id,arrayProperty,values,successCallback,errorCallback)
{
	return cordova.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'arrayInsert', //Tell plugin, which action must be performed
    [channel,id,arrayProperty,values] //Passing a list of arguments to the Plugin
	);
};

window.sync.clearArray = function(channel,id,arrayProperty,successCallback,errorCallback)
{
	return cordova.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'clearArray', //Tell plugin, which action must be performed
    [channel,id,arrayProperty] //Passing a list of arguments to the Plugin
	);
};

window.sync.arrayValue = function(channel,id,valueUri,successCallback,errorCallback)
{
	return cordova.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'arrayValue', //Tell plugin, which action must be performed
    [channel,id,valueUri] //Passing a list of arguments to the Plugin
	);
};

window.sync.arrayUpdate = function(channel,id,valueUri,value,successCallback,errorCallback)
{
	return cordova.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'arrayUpdate', //Tell plugin, which action must be performed
    [channel,id,valueUri,value] //Passing a list of arguments to the Plugin
	);
};

window.sync.arrayLength = function(channel,id,arrayProperty,successCallback,errorCallback)
{
	return cordova.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'arrayLength', //Tell plugin, which action must be performed
    [channel,id,arrayProperty] //Passing a list of arguments to the Plugin
	);
};