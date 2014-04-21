package net.ueye.openfire.plugins.chat.log;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import net.ueye.openfire.plugins.chat.log.dao.ChatLogDao;
import net.ueye.openfire.plugins.chat.log.dao.ChatLogDaoImpl;
import net.ueye.openfire.plugins.chat.log.entity.ChatLog;

import org.dom4j.Element;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

/**
 * @author rubys@vip.qq.com
 * @since 2014-4-12
 */
public class ChatLogPlugin implements PacketInterceptor, Plugin {

	private static final Logger logger = LoggerFactory.getLogger(ChatLogPlugin.class);

	private static final String propKey = "fromUserID";

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
	}

	private void doAction(Packet packet, boolean incoming, boolean processed, Session session) {
		Packet copyPacket = packet.createCopy();

		if (packet instanceof Message) {
			Message message = (Message) copyPacket;

			if (message.getType() == Message.Type.chat || message.getType() == Message.Type.groupchat) {
				if (session == null) {
					return;
				}
				if (processed) {
					return;
				}
				ChatLog chatLog = this.get(copyPacket, incoming, session);

				String fromUserID = getFromUser(message, propKey);

				if (fromUserID != null && fromUserID.equals(chatLog.getReceiver())) {
					return;
				}

				chatLogDao.save(chatLog);
				logger.debug("Save chatLog[{}]", chatLog);
			}
		}
	}

	private ChatLog get(Packet packet, boolean incoming, Session session) {
		Message message = (Message) packet;
		ChatLog chatLog = new ChatLog();

		if (session != null) {
			chatLog.setSessionJID(session.getAddress().toString());
		}

		chatLog.setSender(message.getFrom().getNode());
		chatLog.setReceiver(message.getTo().getNode());
		chatLog.setContent(message.getBody());
		chatLog.setCreateDate(new Timestamp(new Date().getTime()));
		chatLog.setDetail(message.toXML());
		chatLog.setLength(chatLog.getContent().length());
		chatLog.setState(0);

		return chatLog;
	}

	@SuppressWarnings("unchecked")
	private String getFromUser(Message message, String propKey) {
		Element propElem = message.getElement().element("properties");
		if (propElem == null) {
			return null;
		}
		List<Element> props = propElem.elements();
		if (props == null || props.size() == 0) {
			return null;
		}
		for (Element prop : props) {
			if (propKey.equalsIgnoreCase(prop.element("name").getTextTrim())) {
				return prop.element("value").getTextTrim();
			}
		}
		return null;
	}

}