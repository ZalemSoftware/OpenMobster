/**
 * 
 */
package org.openmobster.core.mobileObject.xml;

import org.apache.log4j.Logger;
import org.openmobster.core.mobileObject.MockPOJO;

/**
 * @author openmobster
 *
 */
public class TestObjectDetection extends TestSerialization
{
	private static Logger log =  Logger.getLogger(TestObjectDetection.class);
	
	public void setUp() throws Exception
	{
		super.setUp();
	}
	
	public void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	public void testObjectDetection()
	{
		MockPOJO pojo1 = this.createPOJOWithStrings("top-level", false);
		MockPOJO pojo2 = this.createPOJOWithStrings("top-level", false);
		
		
		String deviceXml1 = this.serializer.serialize(pojo1);
		String deviceXml2 = this.serializer.serialize(pojo2);
		
		
		log.info("POJO1-------------------------------------------------");		
		log.info(deviceXml1);
		log.info("------------------------------------------------------");	
		
		log.info("POJO2-------------------------------------------------");		
		log.info(deviceXml2);
		log.info("------------------------------------------------------");	
		
		this.assertEquals(deviceXml1, deviceXml2);
	}
}
