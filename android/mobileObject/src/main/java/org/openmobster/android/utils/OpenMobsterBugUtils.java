package org.openmobster.android.utils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObject;
import org.openmobster.core.mobileCloud.android.storage.DefaultCRUD;
import org.openmobster.core.mobileCloud.android.storage.Record;

/**
 * Classe adicionada na versão 2.4-M3.1 do OpenMobster.<br>
 * <br>
 * Utilitário para os contornos/correções necessários para os bugs/problemas do OpenMobster.
 * 
 * @author Thiago Gesser
 */
public final class OpenMobsterBugUtils {
	
	private static final String OBJECT_HIERARCHY_SEPARATOR = "/";

	private static OpenMobsterBugUtils singleton;
	
	private Set<String> persistentChannels;
	
	private Map<String, String[]> channelsNullableFields;
	private Map<String, String[]> objectsNullableFields;
	private Map<String, NullableObject[]> nullableObjects;
	
	
	private OpenMobsterBugUtils() {
	}
	
	public static OpenMobsterBugUtils getInstance() {
		//Não há necessidade de synchronized.
		if (singleton == null) {
			singleton = new OpenMobsterBugUtils();
		}
		return singleton;
	}
	
	
	/*
	 * Métodos de configuração dos contornos. Devem ser chamados pela aplicação que usa o OpenMobster.
	 */
	
	/**
	 * Determina os canais persistentes que não devem ter seus dados locais excluídos pelos contornos de bugs do OpenMobster.<br>
	 * Mais detalhes podem ser vistos em {@link #isPersistentChannel(String)}.
	 * 
	 * @param newPersistentChannels canais que não devem ter os dados excluídos pelos bugs do OpenMobster.
	 */
	public void addPersistentChannels(String... newPersistentChannels) {
		if (persistentChannels == null) {
			persistentChannels = new HashSet<String>(newPersistentChannels.length);
		}
		
		for (String persistentChannel : newPersistentChannels) {
			persistentChannels.add(persistentChannel);
		}
	}
	
	/**
	 * Determina os campos anuláveis do canal, ou seja, que podem receber valoers nulos.<br>
	 * Esta definição é utilizada para contornar um comportamento problemático do OpenMobster em que ele não salva
	 * campos com valores nulos no banco de dados. Desta forma, seria impossível para a API de queries obter os dados de registros
	 * que tivessem algum campo nulo sendo utilizado na query.<br>
	 * A solução se baseia em definir os campos com valores nulos no registro logo antes dele ser salvo no banco. Estes
	 * campos não são considerados campos normais, eles apenas ficam salvos no banco com o valor nulo, não sendo enviados
	 * para o servidor. Se o registro for alterado e algum campo não for mais nulo, ele será plenamente atualizado
	 * com o valor, pois a atualização de registro do OpenMobster consiste em excluir o registro e adicioná-lo novamente
	 * com os novos valores. 
	 * 
	 * @param channel canal dono dos campos anuláveis.
	 * @param fields campos anuláveis
	 */
	public void addChannelNullableFields(String channel, String... fields) {
		if (channelsNullableFields == null) {
			channelsNullableFields = new HashMap<String, String[]>();
		}
		
		channelsNullableFields.put(channel, fields);
	}
	
	/**
	 * Determina os campos anuláveis do tipo de objeto interno, ou seja, que podem receber valoers nulos.<br>
	 * Mais informações sobre o contorno o qual este método se aplica podem ser visto em {@link #addChannelNullableFields(String, String...)}.
	 * 
	 * @param objectType tipo do objeto interno.
	 * @param fields campos anuláveis.
	 */
	public void addObjectNullableFields(String objectType, String... fields) {
		if (objectsNullableFields == null) {
			objectsNullableFields = new HashMap<String, String[]>();
		}
		
		objectsNullableFields.put(objectType, fields);
	}

	/**
	 * Determina os objetos internos anuláveis para determinado canal ou tipo de objeto interno. Com isto, os campos determinados
	 * em {@link #addObjectNullableFields(String, String...)} passam a poder ser anuláveis no canal ou objeto interno.<br>
	 * Mais informações sobre o contorno o qual este método se aplica podem ser visto em {@link #addChannelNullableFields(String, String...)}.
	 * 
	 * @param objectTypeOrChannel canal ou tipo de objeto interno.
	 * @param objs objetos internos.
	 */
	public void addNullableObjects(String objectTypeOrChannel, NullableObject[] objs) {
		if (nullableObjects == null) {
			nullableObjects = new HashMap<String, NullableObject[]>();
		}
		
		nullableObjects.put(objectTypeOrChannel, objs);
	}
	
	
	/*
	 * Métodos de aplicação dos contornos. São chamados pelo OpenMobster.
	 */
	
	/**
	 * Verifica se o canal é persistente e possui dados que não podem ser excluídos pelo OpenMobster.<br>
	 * Utilizado em locais do OpenMobster que tentam se recuperar de outros bugs e acabam forçando a limpeza de todos
	 * os dados locais de determinado canal.<br>
	 * Por enquanto, sabe-se que os bugs do OpenMobster que forçavam esta recuperação eram um erro de comunicação
	 * com o servidor e uma falha no salvamento do mapa de registros no banco de dados.
	 * 
	 * @param channel canal que será verificado se é persistente ou não.
	 * @return <code>true</code> se o canal é persistente e <code>false</code> caso contrário.
	 */
	public boolean isPersistentChannel(String channel) {
		return persistentChannels != null && persistentChannels.contains(channel);
	}
	
	/**
	 * Deve ser chamado pelo OpenMobster antes de inserir ou atualizar um registro no banco de dados para ativar o contorno
	 * do problema de não salvar campos com valores nulos.
	 * 
	 * @param channel canal do registro que está sendo inserido.
	 * @param mobileObject o mobileObject do registro que está sendo inserido. 
	 * @param record o registro que está sendo inserido.
	 */
	public void setNullValues(String channel, MobileObject mobileObject, Record record) {
		if (channelsNullableFields != null) {
			String[] fields = channelsNullableFields.get(channel);
			if (fields != null) {
				for (String field : fields) {
					if (mobileObject.hasField(field)) {
						continue;
					}
					
					//Se não possui o campo, adiciona um campo "fake" no Record com valor nulo para que ele seja visível para as queries.
					//Este campo fake será excluído automaticamente caso o campo real ganhe um valor.
					record.setValue(field, null);
				}
			}
		}
		
		if (nullableObjects != null) {
			NullableObject[] objects = nullableObjects.get(channel);
			if (objects != null) {
				addNullObjects(mobileObject, record, objects, OBJECT_HIERARCHY_SEPARATOR);
			}
		}
	}

	private void addNullObjects(MobileObject mobileObject, Record record, NullableObject[] objects, String path) {
		for (NullableObject obj : objects) {
			if (obj.isArray()) {
				setNullObjectArray(mobileObject, record, obj, path);
			} else {
				setNullObject(mobileObject, record, obj, path);
			}
		}
	}

	
	/*
	 * Métodos/classes auxiliares
	 */
	
	private void setNullObjectArray(MobileObject mobileObject, Record record, NullableObject obj, String path) {
		String[] fields = objectsNullableFields.get(obj.getType());
		NullableObject[] innerObjects = nullableObjects.get(obj.getType());
		boolean hasFields = fields != null && fields.length > 0;
		boolean hasInnerObjects = innerObjects != null && innerObjects.length > 0;
		if (!hasFields && !hasInnerObjects) {
			return;
		}
		
		String objFullname = path + obj.getName();
		int length = mobileObject.getArrayLength(objFullname);
		if (length == 0) {
			return;
		}
		
		for (int i = 0; i < length; i++) {
			Map<String, String> element = mobileObject.getArrayElement(objFullname, i);
			
			String elementPath = objFullname + "[" + i + "]";
			if (hasFields) {
				for (String field : fields) {
					field = OBJECT_HIERARCHY_SEPARATOR + field;
					if (element.containsKey(field)) {
						//Já tem o valor, n precisa colocar nulo.
						continue;
					}
					
					record.setValue(DefaultCRUD.convertToDatabaseFormat(elementPath + field), null);
				}
			}
			
			if (hasInnerObjects) {
				addNullObjects(mobileObject, record, innerObjects, elementPath + OBJECT_HIERARCHY_SEPARATOR);
			}
		}
	}
	
	private void setNullObject(MobileObject mobileObject, Record record, NullableObject obj, String path) {
		String[] fields = objectsNullableFields.get(obj.getType());
		NullableObject[] innerObjects = nullableObjects.get(obj.getType());
		boolean hasFields = fields != null && fields.length > 0;
		boolean hasInnerObjects = innerObjects != null && innerObjects.length > 0;
		if (!hasFields && !hasInnerObjects) {
			return;
		}
		
		//O objeto em si n tem valor no banco, então n precisa colocar nulo para "objFullname".
		String objPath = path + obj.getName() + OBJECT_HIERARCHY_SEPARATOR;
		
		if (hasFields) {
			for (String field : fields) {
				String fieldFullname = objPath + field;
				if (mobileObject.hasFieldOrArray(fieldFullname)) {
					continue;
				}
				
				record.setValue(DefaultCRUD.convertToDatabaseFormat(fieldFullname), null);
			}
		}
		
		if (hasInnerObjects) {
			addNullObjects(mobileObject, record, innerObjects, objPath);
		}
	}
	
	/**
	 * Representa uma propriedade para um tipo de objeto interno cujo seus campos podem ser anuláveis.
	 */
	public static final class NullableObject {
		private final String name;
		private final String type;
		private final boolean isArray;

		public NullableObject(String name, String type, boolean isArray) {
			this.name = name;
			this.type = type;
			this.isArray = isArray;
		}
		
		public String getName()	{
			return name;
		}
		
		public String getType()	{
			return type;
		}
		
		public boolean isArray() {
			return isArray;
		}
	}
}
