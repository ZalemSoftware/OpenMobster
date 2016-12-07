package org.openmobster.core.mobileCloud.android.util;

/**
 * Classe adicionada na versão 2.4-M3.1 do OpenMobster.<br>
 * <br>
 * Utilitário em geral para o OpenMobster, servindo como ponte entre a aplicação e a biblioteca.
 * 
 * @author Thiago Gesser
 */
public final class OpenMobsterUtils {
	
	private static boolean debug;
	
	private OpenMobsterUtils() {
	}
	
	
	/**
	 * Define se a aplicação está em debug.<br>
	 * Isto é necessário porque a forma como a estrutura do OpenMobster está montada não permite a geração de seu BuildConfig.
	 * 
	 * @param d <code>true</code> se a aplicação está em debug e <code>false</code> caso contrário.
	 */
	public static void setDebug(boolean d) {
		debug = d;
	}
	
	/**
	 * Indica se a aplicação está em debug.
	 * 
	 * @return <code>true</code> se a aplicação está em debug e <code>false</code> caso contrário.
	 */
	public static boolean isDebug()	{
		return debug;
	}
}
