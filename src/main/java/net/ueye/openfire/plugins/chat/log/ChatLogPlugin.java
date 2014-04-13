package net.ueye.openfire.plugins.chat.log;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import net.ueye.openfire.plugins.chat.log.dao.ChatLogDao;
import net.ueye.openfire.plugins.chat.log.dao.ChatLogDaoImpl;
import net.ueye.openfire.plugins.chat.log.entity.ChatLog;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

/**
 * @author rubys@vip.qq.com
 * @since 2014-4-12
 */
public class ChatLogPlugin implements PacketInterceptor, Plugin {

	private static final Logger logger = LoggerFactory.getLogger(ChatLogPlugin.class);

	// private static PluginManager pluginManager;

	private InterceptorManager interceptorManager;

	private ChatLogDao chatLogDao = new ChatLogDaoImpl();

	public ChatLogPlugin() {
		interceptorManager = InterceptorManager.getInstance();
	}

	@Override
	public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {
		this.doAction(packet, incoming, processed, session);
	}

	@Override
	public void destroyPlugin() {
		interceptorManager.removeInterceptor(this);
	}

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		interceptorManager.addInterceptor(this);
		// pluginManager = manager;
	}

	private void doAction(Packet packet, boolean incoming, boolean processed, Session session) {
		Packet copyPacket = packet.createCopy();
		if (packet instanceof Message) {
			Message message = (Message) copyPacket;

			if (message.getType() == Message.Type.chat) {
				if (processed || !incoming) {
					return;
				}
				ChatLog chatLog = this.get(copyPacket, incoming, session);
				chatLogDao.save(chatLog);
				logger.debug("Save chatLog[{}]", chatLog);
			} else if (message.getType() == Message.Type.groupchat) {
				List<?> els = message.getElement().elements("x");
				if (els != null && !els.isEmpty()) {
				} else {
				}
			} else {
			}
		} else if (packet instanceof IQ) {
			IQ iq = (IQ) copyPacket;
			if (iq.getType() == IQ.Type.set && iq.getChildElement() != null && "session".equals(iq.getChildElement().getName())) {
			}
		} else if (packet instanceof Presence) {
			Presence presence = (Presence) copyPacket;
			if (presence.getType() == Presence.Type.unavailable) {
			}
		}
	}

	private ChatLog get(Packet packet, boolean incoming, Session session) {
		Message message = (Message) packet;
		ChatLog chatLog = new ChatLog();

		JID jid = session.getAddress();
		if (incoming) {
			chatLog.setSender(jid.getNode());
			JID recipient = message.getTo();
			chatLog.setReceiver(recipient.getNode());
		}
		chatLog.setContent(message.getBody());
		chatLog.setCreateDate(new Timestamp(new Date().getTime()));
		chatLog.setDetail(message.toXML());
		chatLog.setLength(chatLog.getContent().length());
		chatLog.setState(0);
		chatLog.setSessionJID(jid.toString());

		return chatLog;
	}

}