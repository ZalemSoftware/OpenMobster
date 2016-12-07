package org.openmobster.dev.tools.appcreator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

/**
 * @author openmobster@gmail.com
 *
 */
public class AppCreator
{
	public AppCreator()
	{
		
	}
	
	public void start() throws Exception
	{
		System.out.println("OpenMobster Mobile Cloud Platform - App Creator");
		
		this.collectDeveloperInfo();
	}
	
	public static void main(String[] args) throws Exception
	{	
		AppCreator appCreator = new AppCreator();
		appCreator.start();
	}
	
	private Properties loadConfiguration() throws Exception
	{
		Properties config = new Properties();
		InputStream is = null;
		try
		{
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream("configuration.properties");
			config.load(is);
			return config;
		}
		finally
		{
			if(is != null)
			{
				is.close();
			}
		}
	}
	
	private void collectDeveloperInfo() throws Exception
	{
		//Read in the configuration
		Properties config = this.loadConfiguration();
		Map<String, String> userValues = new HashMap<String, String>();
		userValues.put("version.android.api.value", config.getProperty("version.android.api"));
		
		String projectName = "MyApp"; //user with default
		String groupId = "com.myapp"; //user with default
		String projectUrl = "http://www.myapp.com"; //user with default
		String openMobsterVersion = config.getProperty("openmobster.version"); //user with default
		String projectVersion = "1.0"; //user with default
		
		boolean sampleApp = true; //by default
		
		
		projectName = this.askUser("Project Name", projectName);
		String[] split = projectName.split(" ");
		projectName = "";
		for(String token: split)
		{
			projectName += token;
		}
		
		groupId = this.askUser("Group Id", groupId);
		projectVersion = this.askUser("Project Version", projectVersion);
		projectUrl = this.askUser("Project Url", projectUrl);
		//openMobsterVersion = this.askUser("Version of the OpenMobster Platform", openMobsterVersion);
		
		//Decide if sample workspace or sketelon (hello world) workspace
		String sampleCode = this.askUser("Generate Sample Code", "yes/no [default:yes]");
		if(sampleCode.trim().length() == 0 || sampleCode.contains("yes"))
		{
			sampleApp = true;
		}
		else
		{
			sampleApp = false;
		}
		
		//Ask user to select the mobile platforms of interest
		List<String> supportedPlatforms = new ArrayList<String>();
		String platforms = config.getProperty("platforms");
		String[] platformTokens = platforms.split(",");
		for(String token:platformTokens)
		{
			//String selection = this.askUser("Support Mobile Platform: "+token, "yes");
			String selection = "yes";
			if(selection != null && selection.trim().equalsIgnoreCase("yes"))
			{
				supportedPlatforms.add(token.toLowerCase());
				
				if(token.equalsIgnoreCase("android"))
				{
					//String androidApi = this.askUser("Android API Version", config.getProperty("version.android.api"));
					String androidApi = config.getProperty("version.android.api");
					userValues.put("version.android.api.value", androidApi);
				}
			}
		}
		
		String rimosAppGroupId = groupId+".rimos.app"; //derived 
		String rimosAppName = projectName; //derived
		String cloudAppName = projectName+"-cloud"; //derived
		String mobletGroupId = groupId + ".moblet";
		String mobletName = projectName;
		String mobletArtifactId = mobletName.toLowerCase();
		String androidAppGroupId = groupId+".android.app"; //derived
		String androidMainGroupId=groupId;
		String androidAppName = projectName; //derived
		int androidAppVersionCode = 1; //derived
		try
		{
			androidAppVersionCode = Integer.parseInt(projectVersion.substring(0, 1)); //derived
		}
		catch(Exception e)
		{
			androidAppVersionCode = 1;
		}
		
		
		userValues.put("project.name", projectName);
		userValues.put("appCreator.project.name", projectName+"-parent");
		userValues.put("appCreator.project.groupId", groupId);
		userValues.put("appCreator.project.version", projectVersion);
		userValues.put("appCreator.project.url", projectUrl);
		userValues.put("appCreator.openmobster.version", openMobsterVersion);
		userValues.put("appCreator.project.artifactId", projectName.toLowerCase());
		
		userValues.put("appCreator.rimos.app.groupId", rimosAppGroupId);
		userValues.put("appCreator.rimos.app.name", rimosAppName);
		userValues.put("appCreator.rimos.app.artifactId", rimosAppName.toLowerCase());		
		
		userValues.put("appCreator.cloud.app.groupId", groupId+".cloud.app");
		userValues.put("appCreator.cloud.app.name", cloudAppName);
		userValues.put("appCreator.cloud.app.artifactId", cloudAppName.toLowerCase());
		
		userValues.put("appCreator.moblet.groupId", mobletGroupId);
		userValues.put("appCreator.moblet.name", mobletName);
		userValues.put("appCreator.moblet.artifactId", mobletArtifactId);
		
		userValues.put("appCreator.android.main.groupId", androidMainGroupId);
		userValues.put("appCreator.android.app.groupId", androidAppGroupId);
		userValues.put("appCreator.android.app.name", androidAppName);
		userValues.put("appCreator.android.app.artifactId", androidAppName.toLowerCase());
		userValues.put("appCreator.android.app.versionCode", ""+androidAppVersionCode);
		
		userValues.put("openmobster.version.value", openMobsterVersion);
		
		File projectDir = null;
		if(sampleApp)
		{
			projectDir = this.generateWorkspace(supportedPlatforms,userValues);
		}
		else
		{
			SkeletonWorkspace skeleton = new SkeletonWorkspace();
			projectDir = skeleton.generateWorkspace(supportedPlatforms,userValues);
		}
		
		//Show output
		System.out.println("----------------------------------------------------");
		System.out.println("Project Name: "+projectName);
		System.out.println("Project Version: "+projectVersion);
		System.out.println("OpenMobster Platform: "+openMobsterVersion);		
		System.out.println("Created at: "+projectDir.getAbsolutePath());
	}
	
	private String askUser(String message, String defaultValue) throws Exception
	{
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		System.out.print(message+"["+defaultValue+"]: ");
		
		String userInput = bf.readLine();
		if(userInput != null && userInput.trim().length()>0)
		{
			return userInput;
		}
		
		return defaultValue;		
	}
	
	private File generateWorkspace(List<String> supportedPlatforms,Map<String, String> userValues) throws Exception
	{
		String projectName = userValues.get("project.name");
		
		//Start the project tree
		File projectDir = new File("workspace"+File.separator+projectName.toLowerCase());
		if(!projectDir.exists())
		{
			projectDir.mkdirs();
		}
		else
		{
			int counter = 2;
			while(projectDir.exists())
			{
				projectDir = new File("workspace"+File.separator+projectName.toLowerCase()+"_"+(counter++)+"_0");				
			}
			projectDir.mkdirs();
		}
		
		//Create the app-rimos tree
		if(supportedPlatforms.contains("blackberry"))
		{
			File command = new File(projectDir, "app-rimos/src/main/java/com/offlineApp/rimos/app/command"); 
			File screen = new File(projectDir, "app-rimos/src/main/java/com/offlineApp/rimos/app/screen");
			File appIcon = new File(projectDir, "app-rimos/src/main/resources/moblet-app/icon");
			File assemble = new File(projectDir, "app-rimos/src/assemble");
			command.mkdirs();
			screen.mkdirs();
			appIcon.mkdirs();
			assemble.mkdirs();
		}
		
		//Create the cloud tree
		
		File src_bootstrap = new File(projectDir, "cloud/src/main/java/org/crud/cloud/crm/bootstrap");
		File src_hibernate = new File(projectDir, "cloud/src/main/java/org/crud/cloud/crm/hibernate");
		File srcRes = new File(projectDir, "cloud/src/main/resources/META-INF");
		File test = new File(projectDir, "cloud/src/test/java/org/crud/cloud/crm");
		File testRes = new File(projectDir, "cloud/src/test/resources/META-INF");
		src_bootstrap.mkdirs();
		src_hibernate.mkdirs();
		srcRes.mkdirs();
		test.mkdirs();
		testRes.mkdirs();

		//Create the moblet tree
		/*File moblet = new File(projectDir, "moblet/src/assemble");
		File moblet_res = new File(projectDir, "moblet/src/main/resources/META-INF");
		moblet.mkdirs();
		moblet_res.mkdirs();*/
		
		//Create the app-android tree
		if(supportedPlatforms.contains("android"))
		{
			
			String packageName=userValues.get("appCreator.android.main.groupId");
			packageName=packageName.replace('.','/');	
			
			File android_command = new File(projectDir, "app-android/src/"+packageName+"/command");
			File android_screen = new File(projectDir, "app-android/src/"+packageName+"/screen");
			File android_system = new File(projectDir, "app-android/src/"+packageName+"/system");
			
			File drawable_hdpi = new File(projectDir, "app-android/res/drawable-hdpi");
			File drawable_ldpi = new File(projectDir, "app-android/res/drawable-ldpi");
			File drawable_mdpi = new File(projectDir, "app-android/res/drawable-mdpi");
			
			File layout = new File(projectDir, "app-android/res/layout");
			File values = new File(projectDir, "app-android/res/values");
			File libs = new File(projectDir, "app-android/libs");
			
			android_command.mkdirs();
			android_screen.mkdirs();
			android_system.mkdirs();
			
			drawable_hdpi.mkdirs();
			drawable_ldpi.mkdirs();
			drawable_mdpi.mkdirs();
			layout.mkdirs();
			values.mkdirs();
			libs.mkdirs();
		}
		
		//Copy project-level files
		
		this.generateProject(supportedPlatforms,projectDir, userValues);
		
		if(supportedPlatforms.contains("blackberry"))
		{
			this.generateRimOsApp(new File(projectDir, "app-rimos"), userValues);
		}
		
		this.generateCloudApp(new File(projectDir, "cloud"), userValues);
		//this.generateMoblet(supportedPlatforms,new File(projectDir, "moblet"), userValues);
		
		if(supportedPlatforms.contains("android"))
		{
			this.generateAndroidOsApp(new File(projectDir,"app-android"), userValues);
		}
		
		return projectDir;
	}
	
	private String generateProjectProperties(Map<String,String> userValues) throws Exception
	{
		String propertyXml = this.readTemplateResource("/template/properties.xml");
		
		propertyXml = propertyXml.replaceAll("<openmobster.version.value>", userValues.get("openmobster.version.value"));
		propertyXml = propertyXml.replaceAll("<version.android.api.value>", userValues.get("version.android.api.value"));
		
		return propertyXml;
	}
	
	private String generateProjectModules(List<String> supportedPlatforms) throws Exception
	{
		/*if(supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/modules.all.xml");
		}
		else if(supportedPlatforms.contains("android") && !supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/modules.android.xml");
		}
		else if(!supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/modules.blackberry.xml");
		}
		
		return null;*/
		return "";
	}
	
	private String generateSystemDependencies(List<String> supportedPlatforms) throws Exception
	{
		if(supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/dependencies.all.xml");
		}
		else if(supportedPlatforms.contains("android") && !supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/dependencies.android.xml");
		}
		else if(!supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/dependencies.blackberry.xml");
		}
		
		return null;
	}
	
	private void generateProject(List<String> supportedPlatforms,File directory,Map<String, String> userValues) throws Exception
	{
		/*String parentPom = this.readTemplateResource("/template/pom.xml");
		
		parentPom = parentPom.replaceAll("<appCreator.project.name>", 
		userValues.get("appCreator.project.name"));
		
		parentPom = parentPom.replaceAll("<appCreator.project.groupId>", 
				userValues.get("appCreator.project.groupId"));
		
		parentPom = parentPom.replaceAll("<appCreator.project.url>", 
				userValues.get("appCreator.project.url"));
		
		parentPom = parentPom.replaceAll("<appCreator.openmobster.version>", 
				userValues.get("appCreator.openmobster.version"));
		
		parentPom = parentPom.replaceAll("<appCreator.project.artifactId>", 
				userValues.get("appCreator.project.artifactId"));
		
		parentPom = parentPom.replaceAll("<appCreator.project.version>", 
				userValues.get("appCreator.project.version"));
		
		String propertyXml = this.generateProjectProperties(userValues);
		parentPom = parentPom.replaceAll("<appCreator.properties>", propertyXml);
		
		String moduleFragment = this.generateProjectModules(supportedPlatforms);
		if(moduleFragment != null)
		{
			parentPom = parentPom.replaceAll("<appCreator.modules>", moduleFragment);
		}
		
		String dependencyFragment = this.generateSystemDependencies(supportedPlatforms);
		if(dependencyFragment != null)
		{
			parentPom = parentPom.replaceAll("<appCreator.platform.dependencies>", dependencyFragment);
		}
		
		File parentPomFile = new File(directory, "pom.xml");
		this.generateFile(parentPomFile, parentPom);
		
		//.classpath for Eclipse 
		this.generateFile(new File(directory, ".classpath"), 
		this.readTemplateResource("/template/.classpath"));*/
		
		//README.txt file to help developer
		this.generateFile(new File(directory, "README.txt"), 
		this.readTemplateResource("/template/README.txt"));
	}
	
	private String generateMobletPom(List<String> supportedPlatforms) throws Exception
	{
		if(supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/moblet/pom.all.xml");
		}
		else if(supportedPlatforms.contains("android") && !supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/moblet/pom.android.xml");
		}
		else if(!supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/moblet/pom.blackberry.xml");
		}
		
		return null;
	}
	
	private String generateMobletApps(List<String> supportedPlatforms) throws Exception
	{
		if(supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/moblet/src/main/resources/META-INF/moblet-apps.all.xml");
		}
		else if(supportedPlatforms.contains("android") && !supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/moblet/src/main/resources/META-INF/moblet-apps.android.xml");
		}
		else if(!supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/moblet/src/main/resources/META-INF/moblet-apps.blackberry.xml");
		}
		
		return null;
	}
	
	private void generateMoblet(List<String> supportedPlatforms,File directory,Map<String, String> userValues) throws Exception
	{
		String pom = this.generateMobletPom(supportedPlatforms);
		
		pom = pom.replaceAll("<appCreator.project.version>", 
				userValues.get("appCreator.project.version"));
		
		pom = pom.replaceAll("<appCreator.project.groupId>", 
				userValues.get("appCreator.project.groupId"));
		
		pom = pom.replaceAll("<appCreator.project.artifactId>", 
				userValues.get("appCreator.project.artifactId"));
		
		pom = pom.replaceAll("<appCreator.moblet.groupId>", 
				userValues.get("appCreator.moblet.groupId"));
		
		pom = pom.replaceAll("<appCreator.moblet.name>", 
				userValues.get("appCreator.moblet.name"));
		
		pom = pom.replaceAll("<appCreator.moblet.artifactId>", 
				userValues.get("appCreator.moblet.artifactId"));
		
		pom = pom.replaceAll("<appCreator.cloud.app.groupId>", 
				userValues.get("appCreator.cloud.app.groupId"));
		
		pom = pom.replaceAll("<appCreator.cloud.app.artifactId>", 
				userValues.get("appCreator.cloud.app.artifactId"));
		
		pom = pom.replaceAll("<appCreator.rimos.app.groupId>", 
				userValues.get("appCreator.rimos.app.groupId"));
		
		pom = pom.replaceAll("<appCreator.rimos.app.artifactId>", 
				userValues.get("appCreator.rimos.app.artifactId"));
		
		pom = pom.replaceAll("<appCreator.android.app.groupId>", 
				userValues.get("appCreator.android.app.groupId"));
		
		pom = pom.replaceAll("<appCreator.android.app.artifactId>", 
				userValues.get("appCreator.android.app.artifactId"));
		
		File pomFile = new File(directory, "pom.xml");
		this.generateFile(pomFile, pom);
		
		//src/resources/META-INF/moblet-apps.xml
		String mobletApps = this.generateMobletApps(supportedPlatforms);
		mobletApps = mobletApps.replaceAll("<appCreator.rimos.app.name>", 
				userValues.get("appCreator.rimos.app.name"));
		
		mobletApps = mobletApps.replaceAll("<appCreator.android.app.name>", 
				userValues.get("appCreator.android.app.name"));
		
		mobletApps = mobletApps.replaceAll("<appCreator.android.app.artifactId>", 
				userValues.get("appCreator.android.app.artifactId"));
		
		mobletApps = mobletApps.replaceAll("<appCreator.project.version>", 
				userValues.get("appCreator.project.version"));
		
		File mobletAppsFile = new File(directory, "src/main/resources/META-INF/moblet-apps.xml");
		this.generateFile(mobletAppsFile, mobletApps);
		
		//src/assemble/moblet.xml
		this.generateFile(new File(directory, "src/assemble/moblet.xml"), 
				this.readTemplateResource("/template/moblet/src/assemble/moblet.xml"));
	}
	
	private void generateRimOsApp(File directory, Map<String, String> userValues) throws Exception
	{
		//app pom
		String pom = this.readTemplateResource("/template/app-rimos/pom.xml");
		
		pom = pom.replaceAll("<appCreator.project.version>", 
				userValues.get("appCreator.project.version"));
		
		pom = pom.replaceAll("<appCreator.project.groupId>", 
				userValues.get("appCreator.project.groupId"));
		
		pom = pom.replaceAll("<appCreator.project.artifactId>", 
				userValues.get("appCreator.project.artifactId"));
		
		pom = pom.replaceAll("<appCreator.rimos.app.groupId>", 
				userValues.get("appCreator.rimos.app.groupId"));
		
		pom = pom.replaceAll("<appCreator.rimos.app.name>", 
				userValues.get("appCreator.rimos.app.name"));
		
		pom = pom.replaceAll("<appCreator.rimos.app.artifactId>", 
				userValues.get("appCreator.rimos.app.artifactId"));
		
		
		File pomFile = new File(directory, "pom.xml");
		this.generateFile(pomFile, pom);
		
		//app rapc
		String appRapc = this.readTemplateResource("/template/app-rimos/app.rapc");
		
		appRapc = appRapc.replaceAll("<appCreator.rimos.app.name>", 
				userValues.get("appCreator.rimos.app.name"));
		
		appRapc = appRapc.replaceAll("<appCreator.rimos.app.artifactId>", 
				userValues.get("appCreator.rimos.app.artifactId"));
		
		File rapcFile = new File(directory, "app.rapc");
		this.generateFile(rapcFile, appRapc);
		
		//app.alx
		String appAlx = this.readTemplateResource("/template/app-rimos/app.alx");
		
		appAlx = appAlx.replaceAll("<appCreator.rimos.app.name>", 
				userValues.get("appCreator.rimos.app.name"));
		
		File alxFile = new File(directory, "app.alx");
		this.generateFile(alxFile, appAlx);
		
		//devcloud.rapc
		this.generateFile(new File(directory, "devcloud.rapc"), 
		this.readTemplateResource("/template/app-rimos/devcloud.rapc"));
		
		//activation.properties
		this.generateFile(new File(directory, "activation.properties"), 
		this.readTemplateResource("/template/app-rimos/activation.properties"));
		
		
		//moblet-app
		this.generateFile(new File(directory, "src/main/resources/moblet-app/icon/icon.png"), 
		this.readTemplateBinaryResource("/template/app-rimos/src/main/resources/moblet-app/icon/icon.png"));
		
		this.generateFile(new File(directory, "src/main/resources/moblet-app/localize.properties"), 
		this.readTemplateResource("/template/app-rimos/src/main/resources/moblet-app/localize.properties"));
		this.generateFile(new File(directory, "src/main/resources/moblet-app/localize_en_GB.properties"), 
		this.readTemplateResource("/template/app-rimos/src/main/resources/moblet-app/localize_en_GB.properties"));
		this.generateFile(new File(directory, "src/main/resources/moblet-app/moblet-app.xml"), 
		this.readTemplateResource("/template/app-rimos/src/main/resources/moblet-app/moblet-app.xml"));
		
		//src/main/java
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/rimos/app/command/DemoDetails.java"),
		this.readTemplateBinaryResource("/template/app-rimos/src/main/java/com/offlineApp/rimos/app/command/DemoDetails.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/rimos/app/command/DemoMobileRPC.java"),
		this.readTemplateBinaryResource("/template/app-rimos/src/main/java/com/offlineApp/rimos/app/command/DemoMobileRPC.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/rimos/app/command/PushTrigger.java"),
		this.readTemplateBinaryResource("/template/app-rimos/src/main/java/com/offlineApp/rimos/app/command/PushTrigger.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/rimos/app/command/ResetChannel.java"),
		this.readTemplateBinaryResource("/template/app-rimos/src/main/java/com/offlineApp/rimos/app/command/ResetChannel.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/rimos/app/command/PushHandler.java"),
				this.readTemplateBinaryResource("/template/app-rimos/src/main/java/com/offlineApp/rimos/app/command/PushHandler.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/rimos/app/screen/HomeScreen.java"),
		this.readTemplateBinaryResource("/template/app-rimos/src/main/java/com/offlineApp/rimos/app/screen/HomeScreen.java"));
		
		//Add assembly
		String bin_xml = this.readTemplateResource("/template/app-rimos/src/assemble/bin.xml");
		
		bin_xml = bin_xml.replaceAll("<appCreator.rimos.app.groupId>", 
				userValues.get("appCreator.rimos.app.groupId"));
		
		bin_xml = bin_xml.replaceAll("<appCreator.rimos.app.artifactId>", 
				userValues.get("appCreator.rimos.app.artifactId"));
		
		File binXmlFile = new File(directory, "src/assemble/bin.xml");
		this.generateFile(binXmlFile, bin_xml);
	}
	
	private void generateAndroidOsApp(File directory, Map<String, String> userValues) throws Exception
	{
		
		// .project for Eclipse

		String dot_project = this.readTemplateResource("/template/app-android/.project");
		dot_project = dot_project.replaceAll("<appCreator.project.artifactId>",userValues.get("appCreator.project.artifactId"));
		File dot_project_file = new File(directory, ".project");
		this.generateFile(dot_project_file, dot_project);

		// .classpath for Eclipse
		this.generateFile(new File(directory, "/.classpath"),
		this.readTemplateResource("/template/app-android/.classpath"));

		// proguard-project.txt
		this.generateFile(new File(directory, "/proguard-project.txt"),
		this.readTemplateResource("/template/app-android/proguard-project.txt"));

		// project.properties
		this.generateFile(new File(directory, "/project.properties"),
		this.readTemplateResource("/template/app-android/project.properties"));
		
		//libs
		this.generateFile(new File(directory, "/libs/device-sdk-2.4-SNAPSHOT-full.jar"),
		this.readTemplateBinaryResource("/template/app-android/libs/device-sdk-2.4-SNAPSHOT-full.jar"));
		
		//openmobster-app.xml
		String openmobsterAppXml = this.readTemplateResource("/template/app-android/src/main/resources/openmobster-app.xml");
		openmobsterAppXml = openmobsterAppXml.replaceAll("<appCreator.android.main.groupId>", userValues.get("appCreator.android.main.groupId"));
		this.generateFile(new File(new File(directory,"src"), "openmobster-app.xml"),
		openmobsterAppXml);
		
		//Android Manifest
		String androidManifest = this.readTemplateResource("/template/app-android/AndroidManifest.xml");
		androidManifest = androidManifest.replaceAll("<appCreator.android.main.groupId>", 
				userValues.get("appCreator.android.main.groupId"));
		androidManifest = androidManifest.replaceAll("<appCreator.android.app.groupId>", 
				userValues.get("appCreator.android.app.groupId"));
		androidManifest = androidManifest.replaceAll("<appCreator.project.version>", 
				userValues.get("appCreator.project.version"));
		androidManifest = androidManifest.replaceAll("<appCreator.android.app.versionCode>", 
				userValues.get("appCreator.android.app.versionCode"));
		
		File androidManifestFile = new File(directory, "AndroidManifest.xml");
		this.generateFile(androidManifestFile, androidManifest);
		
		//Adding files verbatim
//		this.generateFile(new File(directory, "local.properties"),
//		this.readTemplateBinaryResource("/template/app-android/local.properties"));
//		
//		this.generateFile(new File(directory, "default.properties"),
//		this.readTemplateBinaryResource("/template/app-android/default.properties"));
		
		//res folder
		this.generateFile(new File(directory, "res/drawable-hdpi/icon.png"),
		this.readTemplateBinaryResource("/template/app-android/res/drawable-hdpi/icon.png"));
		
		this.generateFile(new File(directory, "res/drawable-hdpi/push.png"),
		this.readTemplateBinaryResource("/template/app-android/res/drawable-hdpi/push.png"));
		
		this.generateFile(new File(directory, "res/drawable-ldpi/icon.png"),
		this.readTemplateBinaryResource("/template/app-android/res/drawable-ldpi/icon.png"));
		
		this.generateFile(new File(directory, "res/drawable-ldpi/push.png"),
		this.readTemplateBinaryResource("/template/app-android/res/drawable-ldpi/push.png"));
		
		this.generateFile(new File(directory, "res/drawable-mdpi/icon.png"),
		this.readTemplateBinaryResource("/template/app-android/res/drawable-mdpi/icon.png"));
		
		this.generateFile(new File(directory, "res/drawable-mdpi/push.png"),
		this.readTemplateBinaryResource("/template/app-android/res/drawable-mdpi/push.png"));
		
		this.generateFile(new File(directory, "res/layout/home.xml"),
		this.readTemplateBinaryResource("/template/app-android/res/layout/home.xml"));
		this.generateFile(new File(directory, "res/layout/appactivation.xml"),
		this.readTemplateBinaryResource("/template/app-android/res/layout/appactivation.xml"));
		this.generateFile(new File(directory, "res/layout/new_ticket.xml"),
		this.readTemplateBinaryResource("/template/app-android/res/layout/new_ticket.xml"));
		this.generateFile(new File(directory, "res/layout/ticket_row.xml"),
		this.readTemplateBinaryResource("/template/app-android/res/layout/ticket_row.xml"));
		this.generateFile(new File(directory, "res/layout/update_ticket.xml"),
		this.readTemplateBinaryResource("/template/app-android/res/layout/update_ticket.xml"));
		
		this.generateFile(new File(directory, "res/values/strings.xml"),
		this.readTemplateBinaryResource("/template/app-android/res/values/strings.xml"));
		
		//src/main/java
		
		
		String packageName=userValues.get("appCreator.android.main.groupId");
		packageName="src/"+packageName.replace('.','/');
		packageName=new File(directory,packageName).toString();
		String asyncLoadSpinners = this.readTemplateResource("/template/app-android/src/main/java/org/crud/android/command/AsyncLoadSpinners.java");
		asyncLoadSpinners = asyncLoadSpinners.replaceAll("<appCreator.android.main.groupId>",userValues.get("appCreator.android.main.groupId"));
		File asyncLoadSpinnersFile = new File(new File(packageName,"command"),"AsyncLoadSpinners.java");
		this.generateFile(asyncLoadSpinnersFile, asyncLoadSpinners);
				
		String createTicket = this.readTemplateResource("/template/app-android/src/main/java/org/crud/android/command/CreateTicket.java");
		createTicket = createTicket.replaceAll("<appCreator.android.main.groupId>",userValues.get("appCreator.android.main.groupId"));
		File createTicketFile = new File(new File(packageName,"command"),"CreateTicket.java");
		this.generateFile(createTicketFile, createTicket);
				
		String deleteTicket = this.readTemplateResource("/template/app-android/src/main/java/org/crud/android/command/DeleteTicket.java");
		deleteTicket = deleteTicket.replaceAll("<appCreator.android.main.groupId>",userValues.get("appCreator.android.main.groupId"));
		File deleteTicketFile = new File(new File(packageName,"command"),"DeleteTicket.java");
		this.generateFile(deleteTicketFile, deleteTicket);
				
		String demoPush = this.readTemplateResource("/template/app-android/src/main/java/org/crud/android/command/DemoPush.java");
		demoPush = demoPush.replaceAll("<appCreator.android.main.groupId>",userValues.get("appCreator.android.main.groupId"));
		File demoPushFile = new File(new File(packageName,"command"),"DemoPush.java");
		this.generateFile(demoPushFile, demoPush);
				
		String plainPush = this.readTemplateResource("/template/app-android/src/main/java/org/crud/android/command/PlainPush.java");
		plainPush = plainPush.replaceAll("<appCreator.android.main.groupId>",userValues.get("appCreator.android.main.groupId"));
		File plainPushFile = new File(new File(packageName,"command"),"PlainPush.java");
		this.generateFile(plainPushFile, plainPush);

		String resetChannel = this.readTemplateResource("/template/app-android/src/main/java/org/crud/android/command/ResetChannel.java");
		resetChannel = resetChannel.replaceAll("<appCreator.android.main.groupId>",userValues.get("appCreator.android.main.groupId"));
		File resetChannelFile = new File(new File(packageName,"command"),"ResetChannel.java");
		this.generateFile(resetChannelFile, resetChannel);
		
		String updateTicket = this.readTemplateResource("/template/app-android/src/main/java/org/crud/android/command/UpdateTicket.java");
		updateTicket = updateTicket.replaceAll("<appCreator.android.main.groupId>",userValues.get("appCreator.android.main.groupId"));
		File updateTicketFile = new File(new File(packageName,"command"),"UpdateTicket.java");
		this.generateFile(updateTicketFile, updateTicket);
		
		String homeScreen = this.readTemplateResource("/template/app-android/src/main/java/org/crud/android/screen/HomeScreen.java");
		homeScreen = homeScreen.replaceAll("<appCreator.android.main.groupId>",userValues.get("appCreator.android.main.groupId"));
		File homeScreenFile = new File(new File(packageName,"screen"),"HomeScreen.java");
		this.generateFile(homeScreenFile, homeScreen);
				
		String newTicketScreen = this.readTemplateResource("/template/app-android/src/main/java/org/crud/android/screen/NewTicketScreen.java");
		newTicketScreen = newTicketScreen.replaceAll("<appCreator.android.main.groupId>",userValues.get("appCreator.android.main.groupId"));
		File newTicketScreenFile = new File(new File(packageName,"screen"),"NewTicketScreen.java");
		this.generateFile(newTicketScreenFile, newTicketScreen);
		
		String updateTicketScreen = this.readTemplateResource("/template/app-android/src/main/java/org/crud/android/screen/UpdateTicketScreen.java");
		updateTicketScreen = updateTicketScreen.replaceAll("<appCreator.android.main.groupId>",userValues.get("appCreator.android.main.groupId"));
		File updateTicketScreenFile = new File(new File(packageName,"screen"),"UpdateTicketScreen.java");
		this.generateFile(updateTicketScreenFile, updateTicketScreen);
				
		String activationRequest = this.readTemplateResource("/template/app-android/src/main/java/org/crud/android/system/ActivationRequest.java");
		activationRequest = activationRequest.replaceAll("<appCreator.android.main.groupId>",userValues.get("appCreator.android.main.groupId"));
		File activationRequestFile = new File(new File(packageName,"system"),"ActivationRequest.java");
		this.generateFile(activationRequestFile, activationRequest);

		String checkConnection = this.readTemplateResource("/template/app-android/src/main/java/org/crud/android/system/CheckConnection.java");
		checkConnection = checkConnection.replaceAll("<appCreator.android.main.groupId>",userValues.get("appCreator.android.main.groupId"));
		File checkConnectionFile = new File(new File(packageName,"system"),"CheckConnection.java");
		this.generateFile(checkConnectionFile,checkConnection);
		
	}
	
	private void generateCloudApp(File directory, Map<String, String> userValues) throws Exception
	{
		String pom = this.readTemplateResource("/template/cloud/pom.xml");
		
		pom = pom.replaceAll("<appCreator.project.version>", 
				userValues.get("appCreator.project.version"));
		
		pom = pom.replaceAll("<appCreator.project.groupId>", 
				userValues.get("appCreator.project.groupId"));
		
		pom = pom.replaceAll("<appCreator.project.artifactId>", 
				userValues.get("appCreator.project.artifactId"));
		
		pom = pom.replaceAll("<appCreator.cloud.app.groupId>", 
				userValues.get("appCreator.cloud.app.groupId"));
		
		pom = pom.replaceAll("<appCreator.cloud.app.name>", 
				userValues.get("appCreator.cloud.app.name"));
		
		pom = pom.replaceAll("<appCreator.cloud.app.artifactId>", 
				userValues.get("appCreator.cloud.app.artifactId"));
		
		String propertyXml = this.generateProjectProperties(userValues);
		pom = pom.replaceAll("<appCreator.properties>", 
		propertyXml);
	
		File pomFile = new File(directory, "pom.xml");
		this.generateFile(pomFile, pom);
		
		//DemoMobileBeanService
		
		
		this.generateFile(new File(directory, "src/main/java/org/crud/cloud/crm/bootstrap/AsyncLoadSpinners.java"), 
		this.readTemplateResource("/template/cloud/src/main/java/org/crud/cloud/crm/bootstrap/AsyncLoadSpinners.java"));
		this.generateFile(new File(directory, "src/main/java/org/crud/cloud/crm/bootstrap/BootstrapData.java"), 
		this.readTemplateResource("/template/cloud/src/main/java/org/crud/cloud/crm/bootstrap/BootstrapData.java"));
		this.generateFile(new File(directory, "src/main/java/org/crud/cloud/crm/bootstrap/StartPush.java"), 
		this.readTemplateResource("/template/cloud/src/main/java/org/crud/cloud/crm/bootstrap/StartPush.java"));
		this.generateFile(new File(directory, "src/main/java/org/crud/cloud/crm/hibernate/TicketDS.java"), 
		this.readTemplateResource("/template/cloud/src/main/java/org/crud/cloud/crm/hibernate/TicketDS.java"));
		
		this.generateFile(new File(directory, "src/main/java/org/crud/cloud/crm/Ticket.java"), 
		this.readTemplateResource("/template/cloud/src/main/java/org/crud/cloud/crm/Ticket.java"));
		this.generateFile(new File(directory, "src/main/java/org/crud/cloud/crm/TicketChannel.java"), 
		this.readTemplateResource("/template/cloud/src/main/java/org/crud/cloud/crm/TicketChannel.java"));
		
		//openmobster-config.xml
		this.generateFile(new File(directory, "src/main/resources/META-INF/openmobster-config.xml"), 
		this.readTemplateResource("/template/cloud/src/main/resources/META-INF/openmobster-config.xml"));
		this.generateFile(new File(directory, "src/main/resources/crm.hbm.xml"), 
		this.readTemplateResource("/template/cloud/src/main/resources/crm.hbm.xml"));
		this.generateFile(new File(directory, "src/main/resources/crud.cfg.xml"), 
		this.readTemplateResource("/template/cloud/src/main/resources/crud.cfg.xml"));
			
		
		//TestDemoRPC
		this.generateFile(new File(directory, "src/test/java/org/crud/cloud/crm/TestTicketChannel.java"), 
		this.readTemplateResource("/template/cloud/src/test/java/org/crud/cloud/crm/TestTicketChannel.java"));
		
		//openmobster-config.xml
		this.generateFile(new File(directory, "src/test/resources/META-INF/openmobster-config.xml"), 
		this.readTemplateResource("/template/cloud/src/test/resources/META-INF/openmobster-config.xml"));
		
		//log4j.properties
		this.generateFile(new File(directory, "src/test/resources/log4j.properties"), 
		this.readTemplateResource("/template/cloud/src/test/resources/log4j.properties"));
	}
	//-----------------------------------------------------------------------------------------------------------------------
	private void generateFile(File file, String content) throws Exception
	{
		FileOutputStream fos = new FileOutputStream(file);
		try
		{
			fos.write(content.getBytes());
			fos.flush();
		}
		finally
		{
			if(fos != null)
			{
				fos.close();
			}
		}
	}
	
	private void generateFile(File file, byte[] content) throws Exception
	{
		FileOutputStream fos = new FileOutputStream(file);
		try
		{
			fos.write(content);
			fos.flush();
		}
		finally
		{
			if(fos != null)
			{
				fos.close();
			}
		}
	}
	
	private String readTemplateResource(String resourceLocation) throws Exception
	{	
		InputStream resourceStream = AppCreator.class.getResourceAsStream(resourceLocation);
		if(resourceStream != null)
		{
			return new String(IOUtilities.readBytes(resourceStream));
		}
		
		return null;
	}
	
	private byte[] readTemplateBinaryResource(String resourceLocation) throws Exception
	{	
		InputStream resourceStream = AppCreator.class.getResourceAsStream(resourceLocation);
		if(resourceStream != null)
		{
			return IOUtilities.readBytes(resourceStream);
		}
		
		return null;
	}
}
