/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObject;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestQuery extends AbstractAPITest 
{
	public void runTest()
	{		
		try
		{
			//AND Equals query testing
			this.testEqualsANDQuery();
			
			//OR Equals query
			this.testEqualsORQuery();
			
			//AND NotEquals query
			this.testNotEqualsANDQuery();
			
			//OR NotEquals query
			this.testNotEqualsORQuery();
			
			//AND Contains query testing
			this.testContainsANDQuery();
			
			//OR Contains query
			this.testContainsORQuery();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}
	//--------------------------------------------------------------------------------------------------------------------------------
	private void testEqualsANDQuery() throws Exception
	{
		this.seedData();
		
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("from", "0/from/value");
		criteria.setAttribute("to", "0/to/value");
		
		MobileBean[] beans = MobileBean.queryByEqualsAll("myChannel", criteria);
		
		//Assert
		this.assertNotNull(beans, getInfo()+"://testEqualsANDQuery/MustNotBeNull");
		this.assertTrue(beans.length==1, getInfo()+"://testEqualsANDQuery/MustHaveOneBean");
		
		System.out.println("testEqualsANDQuery-----------------------------------------");
		for(MobileBean bean:beans)
		{				
			String from = bean.getValue("from");
			String to = bean.getValue("to");
			
			System.out.println("From="+from);
			System.out.println("To="+to);
			System.out.println("-----------------------------------------");
			assertEquals(from,"0/from/value",getInfo()+"://testEqualsANDQuery/From/DoesNotMatch");
			assertEquals(to,"0/to/value",getInfo()+"://testEqualsANDQuery/To/DoesNotMatch");
		}
	}
	
	private void testEqualsORQuery() throws Exception
	{
		this.seedData();
		
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("from", "0/from/value");
		criteria.setAttribute("to", "1/to/value");
		
		MobileBean[] beans = MobileBean.queryByEqualsAtleastOne("myChannel", criteria);
		
		//Assert
		this.assertNotNull(beans, getInfo()+"://testEqualsORQuery/MustNotBeNull");
		this.assertTrue(beans.length==2, getInfo()+"://testEqualsORQuery/MustHaveTwoBeans");
		
		System.out.println("testEqualsORQuery-----------------------------------------");
		for(MobileBean bean:beans)
		{
			String from = bean.getValue("from");
			String to = bean.getValue("to");
			
			System.out.println("From="+from);
			System.out.println("To="+to);
			System.out.println("-----------------------------------------");	
			
			this.assertTrue(from.equals("0/from/value") || from.equals("1/from/value"), 
			getInfo()+"://testEqualsORQuery/FromCheckFailure");
			
			this.assertTrue(to.equals("0/to/value") || to.equals("1/to/value"), 
					getInfo()+"://testEqualsORQuery/ToCheckFailure");
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------------
	private void testNotEqualsANDQuery() throws Exception
	{
		this.seedData();
		
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("from", "0/from/value");
		criteria.setAttribute("to", "1/to/value");
		
		MobileBean[] beans = MobileBean.queryByNotEqualsAll("myChannel", criteria);
		
		//Assert
		this.assertNotNull(beans, this.getInfo()+"://testNotEqualsANDQuery/MustNotBeNull");
		this.assertTrue(beans.length == 3, getInfo()+"://testNotEqualsANDQuery/MustHaveThreeBeans");
		
		for(MobileBean bean:beans)
		{
			String from = bean.getValue("from");
			String to = bean.getValue("to");
			
			System.out.println("From="+from);
			System.out.println("To="+to);
			System.out.println("-----------------------------------------");	
			
			this.assertTrue(!from.equals("0/from/value") && !from.equals("1/from/value"), 
			getInfo()+"://testNotEqualsANDQuery/FromCheckFailure");
			
			this.assertTrue(!to.equals("0/to/value") && !to.equals("1/to/value"), 
					getInfo()+"://testNotEqualsANDQuery/ToCheckFailure");
		}
	}
	
	private void testNotEqualsORQuery() throws Exception
	{
		this.seedData();
		
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("from", "0/from/value");
		criteria.setAttribute("to", "1/to/value");
		
		MobileBean[] beans = MobileBean.queryByNotEqualsAtleastOne("myChannel", criteria);
		
		//Assert
		this.assertNotNull(beans, getInfo()+"://testNotEqualsORQuery/MustNotBeNull");
		this.assertTrue(beans.length==5, getInfo()+"://testNotEqualsORQuery/MustHaveFiveBeans");
	}
	//----------------------------------------------------------------------------------------------------------------------------------
	private void testContainsANDQuery() throws Exception
	{
		this.seedData();
		
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("from", "from/value");
		criteria.setAttribute("to", "to/value");
		
		MobileBean[] beans = MobileBean.queryByContainsAll("myChannel", criteria);
		
		//Assert
		this.assertNotNull(beans, getInfo()+"://testContainsANDQuery/MustNotBeNull");
		this.assertTrue(beans.length==5, getInfo()+"://testContainsANDQuery/MustHave5Beans");
	}
	
	private void testContainsORQuery() throws Exception
	{
		this.seedData();
		
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("from", "from/value");
		criteria.setAttribute("to", "blahblah");
		
		MobileBean[] beans = MobileBean.queryByContainsAtleastOne("myChannel", criteria);
		
		//Assert
		this.assertNotNull(beans, getInfo()+"://testContainsORQuery/MustNotBeNull");
		this.assertTrue(beans.length==5, getInfo()+"://testContainsORQuery/MustHave5Beans");
	}
	//--------------------------------------------------------------------------------------------------------
	private void seedData()
	{
		MobileObjectDatabase database = MobileObjectDatabase.getInstance();
		database.deleteAll("myChannel");
		
		//Create and Store Mobile Objects
		for(int i=0; i<5; i++)
		{
			MobileObject mobileObject = new MobileObject();
			mobileObject.setStorageId("myChannel");
			String oid = database.create(mobileObject);
			
			mobileObject = database.read("myChannel", oid);
			
			mobileObject.setValue("from", i+"/from/value");
			
			mobileObject.setValue("to", i+"/to/value");
			
			database.update(mobileObject);
		}
	}
}
