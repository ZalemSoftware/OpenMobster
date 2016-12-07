/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.cloud.api.sync;

import java.util.List;
import java.util.Date;

import org.openmobster.core.security.device.Device;


/**
 * A Channel serves as a gateway for integrating on-device model/data objects with
 * server side backend storage systems such as relational databases, content repositories, or 
 * Enterprise systems like
 * CRMs, ERPs etc
 * 
 * @author openmobster@gmail.com
 */
public interface Channel 
{
	/**
	 * Reads all the Mobile Beans managed by this connector from the corresponding backend storage system
	 * 
	 * @return a list of mobile beans
	 */
	public List<? extends MobileBean> readAll();
	
	/**
	 * Reads the Mobile Bean uniquely identified by the specified id from the backend storage system
	 * 
	 * @param id must uniquely identify the Mobile Bean instance that needs to be read
	 * @return the Mobile Bean instance uniquely identified by the specified id
	 */
	public MobileBean read(String id);
	
	/**
	 * Reads a subset of all Mobile Beans from the backend storage that are enough to 
	 * get a service functional on the device. This saves against loading up lots of unnecessary beans on the 
	 * storage constrained device. How many/which beans to return are totally at the discretion of the service being
	 * mobilized and could even contain all the beans on the server
	 * 
	 * @return a list of mobile beans
	 */
	public List<? extends MobileBean> bootup();
	
	/**
	 * Creates a new instance of the specified Mobile Bean within the backend storage system
	 * 
	 * @param mobileBean the Mobile Bean that must be created
	 * @return unique identifier of the newly created instance
	 */
	public String create(MobileBean mobileBean);
	
	/**
	 * Updates an existing instance of the specified Mobile Bean within the backend storage system
	 * 
	 * @param mobileBean - the Mobile Bean that must be updated
	 */
	public void update(MobileBean mobileBean);
	
	/**
	 * Permanently deletes the specified MobileBean from the backend storage system
	 * 
	 * @param mobileBean - the Mobile Bean that must be deleted
	 */
	public void delete(MobileBean mobileBean);
	
	/**
	 * Scan for any Mobile Bean modifications that need to be synchronized with the specified device
	 * 
	 * @param device Device for which the data changes apply
	 * @param lastScanTimestamp timestamp when the last scan was done
	 * @return an array of Strings which represent the unique ids of the modified Mobile Beans
	 */
	public String[] scanForUpdates(Device device, Date lastScanTimestamp);
	
	/**
	 * Scan for any Mobile Bean creations that need to be synchronized with the specified device
	 * 
	 * @param device Device for which the data applies
	 * @param lastScanTimestamp timestamp when the last scan was done
	 * @return an array of Strings which represent the unique ids of the new Mobile Beans
	 */
	public String[] scanForNew(Device device, Date lastScanTimestamp);
	
	/**
	 * Scan for any Mobile Bean deletions that need to be synchronized with the specified device
	 * 
	 * @param device Device for which the data applies
	 * @param lastScanTimestamp timestamp when the last scan was done
	 * @return an array of Strings which represent the unique ids of the deleted Mobile Beans
	 */
	public String[] scanForDeletions(Device device, Date lastScanTimestamp);
}
