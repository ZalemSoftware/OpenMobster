package <appCreator.android.main.groupId>.system;

public class ActivationRequest{

	private String serverIP;
	private Integer portNo;
	private String emailId;
	private String password;
	
	public ActivationRequest(String serverIP, Integer portNo, String emailId,
			String password)
	{
		super();
		this.serverIP = serverIP;
		this.portNo = portNo;
		this.emailId = emailId;
		this.password = password;
	}

	public ActivationRequest()
	{
		super();
	}

	public String getServerIP()
	{
		return serverIP;
	}

	public void setServerIP(String serverIP)
	{
		this.serverIP = serverIP;
	}

	public Integer getPortNo()
	{
		return portNo;
	}

	public void setPortNo(Integer portNo)
	{
		this.portNo = portNo;
	}

	public String getEmailId()
	{
		return emailId;
	}

	public void setEmailId(String emailId)
	{
		this.emailId = emailId;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}	
}
