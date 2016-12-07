package <appCreator.android.main.groupId>.system;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckConnection
{
	private static CheckConnection checkConnection;
	
	private CheckConnection()
	{
		
	}
	
	public static CheckConnection getInstance()
	{
		if(checkConnection == null)
		{
			synchronized(CheckConnection.class)
			{
				if(checkConnection == null)
				{
					checkConnection = new CheckConnection();
				}
			}
		}
		return checkConnection;
	}
	public boolean isUp(Context ctx)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
	    if (networkInfo != null && networkInfo.isConnectedOrConnecting()) 
	    {
	        return true;
	    }
		return false;
	}
}
