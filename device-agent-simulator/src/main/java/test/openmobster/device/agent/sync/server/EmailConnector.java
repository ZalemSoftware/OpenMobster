/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync.server;

import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;



import org.openmobster.cloud.api.ExecutionContext;
import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.synchronizer.server.SyncContext;

/**
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="emailConnector", 
			   mobileBeanClass="test.openmobster.device.agent.sync.server.Email")
public class EmailConnector implements Channel
{
	private static Logger log = Logger.getLogger(EmailConnector.class);
	
	private static Map<String, List<Email>> emailRepo = new HashMap<String, List<Email>>();
	
	private static Map<String, List<String>> addHistory = new HashMap<String, List<String>>();
	private static Map<String, List<String>> updateHistory = new HashMap<String, List<String>>();
	private static Map<String, List<String>> deleteHistory = new HashMap<String, List<String>>();
	
	public EmailConnector()
	{
		
	}
	//---Out of Band/Backgroud operations that modify the data in the repository that is connected via this
	//Connector implementation--------------------------------------------------------------------------------------------------
	public static void initialize(String device)
	{
		if(!emailRepo.isEmpty())
		{
			return;
		}
		
		List<Email> emails = new ArrayList<Email>();
		
		for(int i=0; i<5; i++)
		{
			Email email = new Email(
					"uid://"+i,
					"from://"+i,
					"to://"+i,
					"subject://"+i,
					"message://"+i
			);
			emails.add(email);
		}
		
		emailRepo.put(device, emails);
		addHistory.put(device, new ArrayList<String>());
		updateHistory.put(device, new ArrayList<String>());
		deleteHistory.put(device, new ArrayList<String>());
	}
	
	public static void close()
	{
		emailRepo = new HashMap<String, List<Email>>();
	}
	
	public static void add(String device, Email email)
	{
		emailRepo.get(device).add(email);
		addHistory.get(device).add(email.getUid());
	}
	
	public static void update(String device, Email email)
	{
		Email emailToUpdate = find(email);
		if(emailToUpdate != null)
		{			
			emailToUpdate.save(email);
			updateHistory.get(device).add(email.getUid());
		}
	}
	
	public static void delete(String device, Email email)
	{
		Email emailToDel = find(email);
		if(emailToDel != null)
		{			
			emailRepo.get(device).remove(emailToDel);
			deleteHistory.get(device).add(email.getUid());
		}		
	}
	
	private static Email find(Email email)
	{
		Email found = null;
		return found;
	}
	//-----MobileObjectConnector implementation---------------------------------------------------------------------------------------
	public String create(MobileBean object) 
	{
		String deviceId = this.getSyncContext().getDeviceId();
		
		Email email = (Email)object;
		
		if(emailRepo.get(deviceId)!=null)
		{
			email.setUid("uid://"+emailRepo.get(deviceId).size());
			emailRepo.get(deviceId).add(email);
		}
		else
		{
			email.setUid("uid://1");
		}
		
		return email.getUid();
	}
	
	public MobileBean read(String id) 
	{
		Email email = null;
		
		List<? extends MobileBean> emails = this.readAll();
		if(emails != null)
		{
			for(MobileBean curr: emails)
			{
				Email courEmail = (Email)curr;
				if(courEmail.getUid().equals(id))
				{
					return curr;
				}
			}
		}
		return email;
	}

	public List<? extends MobileBean> readAll() 
	{
		String deviceId = this.getSyncContext().getDeviceId();
		return emailRepo.get(deviceId);
	}
	
	public List<? extends MobileBean> bootup() 
	{		
		return this.readAll();
	}
	
	public void update(MobileBean object) 
	{		
		Email email = (Email)this.read(((Email)object).getUid());
		email.save((Email)object);
	}

	public void delete(MobileBean object) 
	{	
		String deviceId = this.getSyncContext().getDeviceId();
		Email email = (Email)this.read(((Email)object).getUid());
		emailRepo.get(deviceId).remove(email);
	}	
		
	public String[] scanForNew(Device device, Date lastScanTimestamp) 
	{
		String[] returnValue = null;
		
		List<String> ids = addHistory.get(device.getIdentifier());
		if(ids != null)
		{
			returnValue = ids.toArray(new String[0]);
			addHistory.get(device.getIdentifier()).clear();
			
			if(returnValue.length > 0)
			{
				log.info("Pushing New Email--------------------------------------------------");
				for(String newId:returnValue)
				{
					log.info("Email UID: "+newId);
				}
				log.info("-------------------------------------------------------------------");
			}
		}
		
		return returnValue;
	}

	public String[] scanForUpdates(Device device, Date lastScanTimestamp) 
	{
		String[] returnValue = null;
				
		List<String> ids = updateHistory.get(device.getIdentifier());
		if(ids != null)
		{
			returnValue = ids.toArray(new String[0]);
			updateHistory.get(device.getIdentifier()).clear();
		}
		
		return returnValue;
	}	
	
	public String[] scanForDeletions(Device device, Date lastScanTimestamp) 
	{
		String[] returnValue = null;
				
		List<String> ids = deleteHistory.get(device.getIdentifier());
		
		if(ids != null)
		{
			returnValue = ids.toArray(new String[0]);
			deleteHistory.get(device.getIdentifier()).clear();
		}
		
		return returnValue;
	}
	//-------------------------------------------------------------------------------------------------------
	private SyncContext getSyncContext()
	{
		return (SyncContext)ExecutionContext.getInstance().getSyncContext();
	}
}
