//This is all HTML5 GUI code based on the Jo HTML5 Mobile App Framework: http://joapp.com/

//initialize jo
jo.load();

//Global values
var channel = "testapp_ticket_channel";
var appTitle = "HTML5 Sync App";
var homeScn;

//sets up the toolbar
var toolbar = new joToolbar("");
var nav = new joNavbar();
var stack = new joStackScroller();
var flexCol = new joFlexcol([nav,stack]);

var container = new joContainer([flexCol,toolbar]);
container.setStyle({position: "absolute", top: "0", left: "0", bottom: "0", right: "0"});

var scn = new joScreen(container);
nav.setStack(stack);