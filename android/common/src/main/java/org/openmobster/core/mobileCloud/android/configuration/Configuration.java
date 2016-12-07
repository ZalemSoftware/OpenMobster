/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.configuration;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.storage.Record;
import java.util.BitSet;

/**
 * 
 * @author openmobster@gmail.com
 */
public class Configuration
{	
	private static Configuration singleton;
	
	private String deviceId;
	private String serverId;
	private String serverIp;
	private String plainServerPort;
	private String secureServerPort;
	private String httpPort;
	private boolean isSSLActive;
	private int maxPacketSize;
	private String authenticationHash;
	private String authenticationNonce;
	private String email;
	private boolean isActive;
	private boolean isSSLCertStored;
	private List<String> myChannels;
	
	private long cometPollInterval;
	private String cometMode;
	
	private Configuration()
	{							
	}
	
	public synchronized void start(Context context) 
	{
		try
		{						
			this.load(context);												
		}
		catch(Exception dbe)
		{
			throw new SystemException(this.getClass().getName(), "start", new Object[]{
				"Exception="+dbe.getMessage()
			});
		}
	}

	public void stop() 
	{	
		this.deviceId = null;
		this.serverId = null;
		this.serverIp = null;
		this.httpPort = null;
		this.plainServerPort = null;
		this.secureServerPort = null;
		this.isSSLActive = false;
		this.maxPacketSize = 0;
		this.authenticationHash = null;
		this.authenticationNonce = null;
		this.email = null;
		this.isActive = false;
		this.isSSLCertStored = false;
		this.myChannels = null;
		this.cometPollInterval = 0;
		this.cometMode = null;
	}
	
	public static Configuration getInstance(Context context)
	{
		if(Configuration.singleton == null)
		{
			synchronized(Configuration.class)
			{
				if(Configuration.singleton == null)
				{
					Configuration.singleton = new Configuration();
					Configuration.singleton.start(context);
				}
			}
		}
		
		Configuration.singleton.load(context); //this makes sure its the most current state
		
		return Configuration.singleton;
	}
	
	public static void stopSingleton()
	{
		Configuration.singleton = null;
	}
	//-------------------------------------------------------------------------------------------------------------------
	public String getDeviceId()
	{
		return this.deviceId;
	}
	
	public int getMaxPacketSize()
	{
		return this.maxPacketSize;
	}
	
	public String getAuthenticationHash()
	{
		String nonce = this.getAuthenticationNonce();
		if(nonce == null || nonce.trim().length()==0)
		{
			//This should be the provisioned username, password based hash
			nonce = this.authenticationHash;
		}
		return nonce;
	}
	
	public String getAuthenticationNonce()
	{
		return this.authenticationNonce;
	}
	
	public String getServerId()
	{
		return this.serverId;
	}
	
	public String getServerIp()
	{
		return this.serverIp;
	}
	
	public String getServerPort()
	{
		if(this.isSSLActivated())
		{
			return this.secureServerPort;
		}
		else
		{
			return this.plainServerPort;
		}
	}
	
	public String getPlainServerPort()
	{
		return this.plainServerPort;
	}
	
	public String getSecureServerPort()
	{
		return this.secureServerPort;
	}
	
	public boolean isSSLActivated()
	{
		return this.isSSLActive;
	}
	
	public String getEmail() 
	{
		return email;
	}
	
	public boolean isActive() 
	{
		return isActive;
	}
	
	public boolean isSSLCertStored() 
	{
		return isSSLCertStored;
	}
	
	public List<String> getMyChannels()
	{
		return this.myChannels;
	}
	
	public long getCometPollInterval() 
	{
		return cometPollInterval;
	}
	
	public String getCometMode() 
	{
		return cometMode;
	}
	
	public boolean isInPushMode()
	{
		if(this.cometMode == null || this.cometMode.trim().length() == 0 || this.cometMode.equalsIgnoreCase("push"))
		{
			return true;
		}
		return false;
	}
	
	public String getHttpPort()
	{		
		return this.httpPort;
	}
	
	public String decidePort()
	{
		if(this.isSSLActivated())
		{
			return this.getSecureServerPort();
		}
		else
		{
			return this.getPlainServerPort();
		}
	}
	//----------------------------------------------------------------------------------------------------------------
	public synchronized void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}
					
	public synchronized void setMaxPacketSize(int maxPacketSize)
	{
		this.maxPacketSize = maxPacketSize;
	}
			
	public synchronized void setAuthenticationHash(String authenticationHash)
	{
		this.authenticationHash = authenticationHash;
	}
			
	public synchronized void setAuthenticationNonce(String authenticationNonce)
	{
		this.authenticationNonce = authenticationNonce;
	}
			
	public synchronized void setServerId(String serverId)
	{
		this.serverId = serverId;
	}
			
	public synchronized void setServerIp(String serverIp)
	{
		this.serverIp = serverIp;
	}
			
	public synchronized void setPlainServerPort(String plainServerPort)
	{
		this.plainServerPort = plainServerPort;
	}
			
	public synchronized void setSecureServerPort(String secureServerPort)
	{
		this.secureServerPort = secureServerPort;
	}
			
	public synchronized void activateSSL()
	{
		this.isSSLActive = true;
	}
	
	public synchronized void deActivateSSL()
	{
		this.isSSLActive = false;
	}	
		
	public synchronized void setEmail(String email) 
	{
		this.email = email;
	}

	public synchronized void setActive(boolean isActive) 
	{
		this.isActive = isActive;
	}
	
	public synchronized void setSSLCertStored(boolean isSSLCertStored) 
	{
		this.isSSLCertStored = isSSLCertStored;
	}
					
	public synchronized boolean addMyChannel(String channel)
	{
		if(this.myChannels == null)
		{
			this.myChannels = new ArrayList<String>();
		}
		
		if(!this.myChannels.contains(channel))
		{
			this.myChannels.add(channel);
			return true;
		}
		
		return false;
	}
	
	public synchronized void clearMyChannels()
	{
		if(this.myChannels == null)
		{
			this.myChannels = new ArrayList<String>();
		}
		
		this.myChannels.clear();
	}
		
	public synchronized void setCometPollInterval(long cometPollInterval) 
	{
		this.cometPollInterval = cometPollInterval;
	}
		
	

	public synchronized void setCometMode(String cometMode) 
	{
		this.cometMode = cometMode;
	}
	
	public synchronized void setHttpPort(String httpPort)
	{
		this.httpPort = httpPort;
	}
	//---------------------------------------------------------------------------------------------------------------------
	public synchronized void save(Context context)
	{
		try
		{			
			Database database = Database.getInstance(context);
			
			Set<Record> all = database.selectAll(Database.provisioning_table);
			if(all == null || all.isEmpty())
			{
				//insert
				Record provisioningRecord = new Record();
				this.prepareRecord(provisioningRecord);
				
				database.insert(Database.provisioning_table, 
				provisioningRecord);
			}
			else
			{
				//update
				Record provisioningRecord = all.iterator().next();
				this.prepareRecord(provisioningRecord);
				
				database.update(Database.provisioning_table, 
				provisioningRecord);
			}
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "save", new Object[]{
				"Configuration Save Exception="+e.getMessage()
			});
		}
	}
	
	private void load(Context context)
	{
		if (!Registry.isActive()) {
			return;
		}
		
		try
		{
			Database database = Database.getInstance(context);
			
			Set<Record> all = database.selectAll(Database.provisioning_table);
			if(all != null && !all.isEmpty())
			{
				Record provisioningRecord = all.iterator().next();
				
				this.prepareConfiguration(provisioningRecord);
			}
			
			//This is default out-of-the-box server configuration
			if(this.plainServerPort == null || this.plainServerPort.trim().length() == 0)
			{
				this.plainServerPort = "1502"; //non-ssl port for the cloud server
			}
			if(this.secureServerPort == null || this.secureServerPort.trim().length() == 0)
			{
				this.secureServerPort = "1500"; //ssl port for the cloud server
			}
			
			if(this.httpPort == null || this.httpPort.trim().length() == 0)
			{
				this.httpPort = "80"; //http port by default
			}
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "load", new Object[]{
				"Configuration Load Exception="+e.getMessage()
			});
		}
	}
	
	private void prepareRecord(Record provisioningRecord)
	{
		if(this.deviceId != null)
		{
			provisioningRecord.setValue("deviceId", this.deviceId);
		}
		
		if(this.serverId != null)
		{
			provisioningRecord.setValue("serverId", this.serverId);
		}
		
		if(this.serverIp != null)
		{
			provisioningRecord.setValue("serverIp", this.serverIp);
		}
		
		if(this.plainServerPort != null)
		{
			provisioningRecord.setValue("plainServerPort", this.plainServerPort);
		}
		
		if(this.secureServerPort != null)
		{
			provisioningRecord.setValue("secureServerPort", this.secureServerPort);
		}
		
		if(this.authenticationHash != null)
		{
			provisioningRecord.setValue("authenticationHash", this.authenticationHash);
		}
		else
		{
			provisioningRecord.removeValue("authenticationHash");
		}
		
		if(this.authenticationNonce != null)
		{
			provisioningRecord.setValue("authenticationNonce", this.authenticationNonce);
		}
		else
		{
			provisioningRecord.removeValue("authenticationNonce");
		}
		
		if(this.email != null)
		{
			provisioningRecord.setValue("email", this.email);
		}
		else
		{
			provisioningRecord.removeValue("email");
		}
		
		if(this.cometMode != null)
		{
			provisioningRecord.setValue("cometMode", this.cometMode);
		}
		
		provisioningRecord.setValue("cometPollInterval", ""+this.cometPollInterval);
		
		if(this.httpPort != null)
		{
			provisioningRecord.setValue("httpPort", this.httpPort);
		}
				
		provisioningRecord.setValue("isSSLActive", 
		""+this.isSSLActive);
		
		provisioningRecord.setValue("maxPacketSize", 
		""+this.maxPacketSize);
		
		provisioningRecord.setValue("isActive", ""+this.isActive);
		
		provisioningRecord.setValue("isSSLCertStored", ""+this.isSSLCertStored);
		
		this.serializeChannels(provisioningRecord);
		
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Salva o valor dos bootedChannels no registro.
		 */
		if (bootedChannels != null) {
			long[] bootedChannelsValues = bootedChannels.toLongArray();
			if (bootedChannelsValues.length > 0) {
				if (bootedChannelsValues.length > 1) {
					throw new UnsupportedOperationException("There are so many channels that the BitSet used to store which were booted has grown to two long values. If this number of channels is realy needed (more than 64), the code below that converts a BitSet to a String and vice versa needs to be adjusted.");
				}
				
				String bootedChannelsString = String.valueOf(bootedChannelsValues[0]);
				provisioningRecord.setValue(BOOTUP_CHANNELS_PROPERTY, bootedChannelsString);
			}
		}
	}
	
	private void serializeChannels(Record record)
	{
		if(this.myChannels == null || this.myChannels.isEmpty())
		{
			return;
		}
		
		int channelCount = this.myChannels.size();
		record.setValue("myChannels:size", ""+channelCount);
		int i = 0;
		for(String channel: this.myChannels)
		{
			record.setValue("myChannels["+(i++)+"]", channel);
		}
	}
	
	private void prepareConfiguration(Record record)
	{				
		this.deviceId = record.getValue("deviceId");
		this.serverId = record.getValue("serverId");
		this.serverIp = record.getValue("serverIp");
		this.plainServerPort = record.getValue("plainServerPort");
		this.secureServerPort = record.getValue("secureServerPort");
		this.authenticationHash = record.getValue("authenticationHash");
		this.authenticationNonce = record.getValue("authenticationNonce");
		this.email = record.getValue("email");
		this.cometMode = record.getValue("cometMode");
		this.httpPort = record.getValue("httpPort");
		
		String cometPollIntervalStr = record.getValue("cometPollInterval");
		if(cometPollIntervalStr != null && cometPollIntervalStr.trim().length()>0)
		{
			this.cometPollInterval = Long.parseLong(cometPollIntervalStr);			
		}
		
		String maxPacketSizeStr = record.getValue("maxPacketSize");
		if(maxPacketSizeStr != null && maxPacketSizeStr.trim().length()>0)
		{
			this.maxPacketSize = Integer.parseInt(maxPacketSizeStr);
		}
				
		String sslStatus = record.getValue("isSSLActive");
		if(sslStatus != null && sslStatus.trim().length()>0)
		{
			this.isSSLActive = Boolean.parseBoolean(sslStatus);
		}
						
		String isActiveStr = record.getValue("isActive");
		if(isActiveStr != null && isActiveStr.trim().length()>0)
		{
			this.isActive = Boolean.parseBoolean(isActiveStr);
		}
		
		String isSSLCertStoredStr = record.getValue("isSSLCertStored");
		if(isSSLCertStoredStr != null && isSSLCertStoredStr.trim().length()>0)
		{
			isSSLCertStored = Boolean.parseBoolean(isSSLCertStoredStr);
		}
		
		this.prepareChannels(record);
		
		
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Obtém o valor dos bootedChannels do registro.
		 */
		String bootedChannelsString = record.getValue(BOOTUP_CHANNELS_PROPERTY);
		if (bootedChannelsString != null) {
			long[] bootedChannelsValues = new long[] {Long.parseLong(bootedChannelsString)};
			bootedChannels = BitSet.valueOf(bootedChannelsValues);
		}
	}
	
	private void prepareChannels(Record record)
	{
		String cour = record.getValue("myChannels:size");
		if(cour != null && cour.trim().length()>0)
		{
			int channelCount = Integer.parseInt(cour);
			for(int i=0; i<channelCount; i++)
			{
				String channel = record.getValue("myChannels["+i+"]");
				this.addMyChannel(channel);
			}
		}
	}
	
	
	/*
	 * Estrutura adicionada na versão 2.4-M3.1
	 * Armazena os canais que foram bootados em um BitSet de acordo com seu índice na lista de canais (myChannels).
	 * Os índices dos canais são fixos pois não há como removê-los, apenas adicionar novos. 
	 */
	
	private static final String BOOTUP_CHANNELS_PROPERTY = "bootedChannels";
	private boolean booting;
	private BitSet bootedChannels;
	
	/**
	 * Define que o <code>channel</code> já foi inicializado, tendo todos registros vindos do método <code>bootup</code>
	 * do próprio channel do lado servidor.
	 * 
	 * @param channel o channel que foi inicializado.
	 */
	public synchronized final void setBooted(String channel) {
		setBooted(channel, true);
	}
	
	/**
	 * Define se o <code>channel</code> já foi inicializado, tendo todos registros vindos do método <code>bootup</code>
	 * do próprio channel do lado servidor.
	 * 
	 * @param channel o channel.
	 * @param booted <code>true</code> se foi inicializado e <code>false</code> caso contrário.
	 */
	public synchronized final void setBooted(String channel, boolean booted) {
		if (bootedChannels == null) {
			if (!booted) {
				return;
			}
			bootedChannels = new BitSet();
		}
		
		int channelIndex = getChannelIndex(channel);
		if (channelIndex == -1) {
			throw new IllegalArgumentException("Unknown channel: " + channel);
		}
		bootedChannels.set(channelIndex, booted);
	}
	
	/**
	 * Verifica se o <code>channel</code> já foi inicializado com todos os registros vindos do método <code>bootup</code>
	 * do próprio channel.
	 * 
	 * @param channel o channel que será verificado.
	 * @return <code>true</code> se o channel já foi inicializado e <code>false</code> caso contrário.
	 */
	public final boolean isBooted(String channel) { 
		if (booting || bootedChannels == null) {
			return false;
		}
		
		int channelIndex = getChannelIndex(channel);
		if (channelIndex == -1) {
			return false;
		}
		
		return bootedChannels.get(channelIndex);
	}

	/**
	 * Define se os canais estão em processo de inicialização, tornando-os não inicializados até que este processo termine.
	 *   
	 * @param booting <code>true</code> para definir que os canais estão em processo de inicialização e <code>false</code> caso contrário.
	 */
	public final void setBooting(boolean booting) {
		this.booting = booting;
	}
	
	private int getChannelIndex(String channel) {
		int ret = -1;
		if (myChannels != null) {
			ret = myChannels.indexOf(channel);
		}
		return ret;
	}
	
	
	/*
	 * Estrutura adicionada na versão 2.4-M3.1
	 * Permite o armazenamento na memória de um token de autenticação, o qual será enviado para o servidor em cada requisição. 
	 */
	
	private String authenticationToken;
	
	public String getAuthenticationToken() {
		return authenticationToken;
	}
	
	public void setAuthenticationToken(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}
	
	
	/*
	 * Método adicionada na versão 2.4-M3.1.
	 * Disponibiliza uma verificação de se o dispositivo está ativado na nuvem a partir do e-mail, pois o atributo "active"
	 * vai estar true quando o dispsitivo estiver sendo usado apenas em modo offline. 
	 */
	
	/**
	 * Indica se o dispositivo está ativado na nuvem, estando apto ao envio/recebimento de dados (online).
	 *  
	 * @return <code>true</code> se o dispositivo está ativado na nuvem e <code>false</code> caso contrário.
	 */
	public boolean isDeviceActivated() {
		return getEmail() != null && getEmail().length() > 0;
	}
}
