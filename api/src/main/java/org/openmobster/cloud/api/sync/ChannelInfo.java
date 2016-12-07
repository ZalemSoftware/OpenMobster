/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.cloud.api.sync;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotation which carries configuration information about a Mobile Bean Channel
 * <p>
 * A Channel serves as a gateway for integrating on-device data objects with
 * server side backend storage systems such as relational databases, content repositories, or Enterprise systems like
 * CRMs, ERPs etc 
 *  
 * @author openmobster@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ChannelInfo 
{
	/**
	 * A unique identifier that allows the mobile system to route data object traffic to 
	 * the proper channel
	 * 
	 * @return a unique channel identifier
	 */
	String uri();
	
	/**
	 * The fully qualified class name of the Mobile Bean that will be processed by this Channel
	 * 
	 * @return fully qualified class name of the Mobile Bean
	 */
	String mobileBeanClass();
	
	/**
	 * Specifies how often this channel should be queries for updates to be pushed as notifications
	 * (comet, mobile push, etc). Default Value is : 20000ms
	 * 
	 * @return an updateCheckInterval
	 */
	long updateCheckInterval() default 20000;
	
	
	/*
	 * Métodos adicionados na versão 2.4-M3.1
	 */
	
	/**
	 * Especifica se os beans deste canal devem ser sincronizados logo após serem criados por um dispositivo, retornando
	 * dados atualizados do backend na resposta da requisição de criação.<br>
	 * O valor padrão é <code>false</code>.
	 * 
	 * @return <code>true</code> ativar a sincronização após a criação e <code>false</code> caso contrário.
	 */
	boolean syncAfterCreate() default false;
	
	/**
	 * Especifica se os beans deste canal devem ser sincronizados logo após serem atualizados por um dispositivo,
	 * retornando dados atualizados do backend na resposta da requisição de atualização.<br>
	 * O valor padrão é <code>false</code>.
	 * 
	 * @return <code>true</code> ativar a sincronização após a atualização e <code>false</code> caso contrário.
	 */
	boolean syncAfterUpdate() default false;
}
