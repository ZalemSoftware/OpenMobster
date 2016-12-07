var SyncPlugin = function(){};

SyncPlugin.prototype.test = function(input,successCallback,errorCallback)
{
	return PhoneGap.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'test', //Tell plugin, which action must be performed
    [input] //Passing a list of arguments to the Plugin
	);
};

SyncPlugin.prototype.readall = function(channel,successCallback,errorCallback)
{
	return PhoneGap.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'readall', //Tell plugin, which action must be performed
    [channel] //Passing a list of arguments to the Plugin
	);
};

SyncPlugin.prototype.updateBean = function(channel,oid,jsonUpdate,successCallback,errorCallback)
{
	return PhoneGap.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'updateBean', //Tell plugin, which action must be performed
    [channel,oid,jsonUpdate] //Passing a list of arguments to the Plugin
	);
};

SyncPlugin.prototype.addNewBean = function(channel,jsonAdd,successCallback,errorCallback)
{
	return PhoneGap.exec(
                        successCallback, //Success callback
                        errorCallback, //Failure callback
                        'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                        'addNewBean', //Tell plugin, which action must be performed
                        [channel,jsonAdd] //Passing a list of arguments to the Plugin
                        );
};

SyncPlugin.prototype.deleteBean = function(channel,oid,successCallback,errorCallback)
{
	return PhoneGap.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'deleteBean', //Tell plugin, which action must be performed
                         [channel,oid] //Passing a list of arguments to the Plugin
                         );
};

SyncPlugin.prototype.insertIntoArray = function(channel,oid,arrayProperty,value,successCallback,errorCallback)
{
	return PhoneGap.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'insertIntoArray', //Tell plugin, which action must be performed
                         [channel,oid,arrayProperty,value] //Passing a list of arguments to the Plugin
                         );
};

SyncPlugin.prototype.arrayLength = function(channel,oid,arrayProperty,successCallback,errorCallback)
{
	return PhoneGap.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'arrayLength', //Tell plugin, which action must be performed
                         [channel,oid,arrayProperty] //Passing a list of arguments to the Plugin
                         );
};

SyncPlugin.prototype.clearArray = function(channel,oid,fieldUri,successCallback,errorCallback)
{
	return PhoneGap.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'clearArray', //Tell plugin, which action must be performed
                         [channel,oid,fieldUri] //Passing a list of arguments to the Plugin
                         );
};

SyncPlugin.prototype.commit = function(successCallback,errorCallback)
{
	return PhoneGap.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'commit', //Tell plugin, which action must be performed
                         [] //Passing a list of arguments to the Plugin
                         );
};

SyncPlugin.prototype.value = function(channel,oid,fieldUri,successCallback,errorCallback)
{
	return PhoneGap.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'value', //Tell plugin, which action must be performed
                         [channel,oid,fieldUri] //Passing a list of arguments to the Plugin
                         );
};

SyncPlugin.prototype.queryByMatchAll = function(channel,criteria,successCallback,errorCallback)
{
	return PhoneGap.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'queryByMatchAll', //Tell plugin, which action must be performed
                         [channel,criteria] //Passing a list of arguments to the Plugin
                         );
};

SyncPlugin.prototype.queryByMatchOne = function(channel,criteria,successCallback,errorCallback)
{
	return PhoneGap.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'queryByMatchOne', //Tell plugin, which action must be performed
                         [channel,criteria] //Passing a list of arguments to the Plugin
                         );
};

SyncPlugin.prototype.queryByNotMatchAll = function(channel,criteria,successCallback,errorCallback)
{
	return PhoneGap.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'queryByNotMatchAll', //Tell plugin, which action must be performed
                         [channel,criteria] //Passing a list of arguments to the Plugin
                         );
};

SyncPlugin.prototype.queryByNotMatchOne = function(channel,criteria,successCallback,errorCallback)
{
	return PhoneGap.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'queryByNotMatchOne', //Tell plugin, which action must be performed
                         [channel,criteria] //Passing a list of arguments to the Plugin
                         );
};

SyncPlugin.prototype.queryByContainsAll = function(channel,criteria,successCallback,errorCallback)
{
	return PhoneGap.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'queryByContainsAll', //Tell plugin, which action must be performed
                         [channel,criteria] //Passing a list of arguments to the Plugin
                         );
};

SyncPlugin.prototype.queryByContainsOne = function(channel,criteria,successCallback,errorCallback)
{
	return PhoneGap.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'queryByContainsOne', //Tell plugin, which action must be performed
                         [channel,criteria] //Passing a list of arguments to the Plugin
                         );
};

PhoneGap.addConstructor(function(){
	PhoneGap.addPlugin("sync",new SyncPlugin());
});