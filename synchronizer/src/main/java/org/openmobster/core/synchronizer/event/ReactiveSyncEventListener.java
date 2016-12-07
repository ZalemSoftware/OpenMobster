package org.openmobster.core.synchronizer.event;

import java.util.Collections;

import org.openmobster.cloud.api.ExecutionContext;
import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.common.event.Event;
import org.openmobster.core.common.event.EventListener;
import org.openmobster.core.services.MobileObjectMonitor;
import org.openmobster.core.synchronizer.server.Session;
import org.openmobster.core.synchronizer.server.SyncContext;
import org.openmobster.core.synchronizer.server.engine.ChangeLogEntry;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;

/**
 * Classe adicionada na versão 2.4-M3.1 do OpenMobster.<br>
 * <br>
 * Listener de eventos que gera sincronização de volta ao dispositivo de forma reativa aos eventos de criação e
 * atualização de {@link MobileBean}. Desta forma, o dispositivo já recebe o MobileBean com os dados atualizados do
 * backend como resposta da requisição de criação/atualização. Isto é essencial para cenários onde o backend altera
 * informações dos dados recebidos antes de persisti-los.<br>
 * Para ativar este comportamento, o {@link ChannelInfo} dos MobileBean deve configurar a propriedade {@link ChannelInfo#syncAfterCreate()}
 * para sincronizar beans criados e a propriedade {@link ChannelInfo#syncAfterUpdate()} para sincronizar beans atualizados.
 * 
 * @see ChannelInfo
 * @see ServerSyncEngine
 *
 * @author Thiago Gesser
 */
public final class ReactiveSyncEventListener implements EventListener {
	
	private ServerSyncEngine syncEngine;
	private MobileObjectMonitor monitor;
	
	public ServerSyncEngine getSyncEngine()	{
		return syncEngine;
	}

	public void setSyncEngine(ServerSyncEngine syncEngine) {
		this.syncEngine = syncEngine;
	}
	
	public MobileObjectMonitor getMonitor()	{
		return monitor;
	}
	
	public void setMonitor(MobileObjectMonitor monitor)	{
		this.monitor = monitor;
	}
	
	@Override
	public void onEvent(Event event) {
		//Verifica se é um evento de mobileBean.
		String recordId = (String) event.getAttribute("mobile-bean-id");
		if (recordId == null) {
			return;
		}
		
		//Obtém os dados do ambiente e o canal alvo do evento.
		SyncContext context = (SyncContext) ExecutionContext.getInstance().getSyncContext();
		Session session = context.getSession();
		String channelId = session.getChannel();
		Channel channel = monitor.lookup(channelId);
		if (channel == null) {
			return;
		}
		
		//Verifica se o channel definiu o sync reativo de seus objetos criados ou atualizados, de acordo com a operação.
		ChannelInfo channelInfo = channel.getClass().getAnnotation(ChannelInfo.class);
		String action = (String)event.getAttribute("action");
		if (action.equalsIgnoreCase("create")) {
			if (!channelInfo.syncAfterCreate()) {
				return;
			}
		} else if (action.equalsIgnoreCase("update")) {
			if (!channelInfo.syncAfterUpdate()) {
				return;
			}
		} else {
			return;
		}
		
		//Cria e adiciona a entrada no changeLog, para que o registro seja sincronizado logo na resposta da requisição de criação / atualização.
		ChangeLogEntry cle = new ChangeLogEntry();
		cle.setNodeId(channelId);
		cle.setOperation(ServerSyncEngine.OPERATION_UPDATE);
		cle.setRecordId(recordId);
		syncEngine.addChangeLogEntries(session.getDeviceId(), session.getApp(), Collections.singletonList(cle));
	}
}
