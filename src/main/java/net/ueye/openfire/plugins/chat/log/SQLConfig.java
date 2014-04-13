package net.ueye.openfire.plugins.chat.log;

/**
 * @author rubys@vip.qq.com
 * @since 2014-4-12
 */
public interface SQLConfig {

	String ChatLogMaxId = "SELECT max(id) FROM ofChatLog";
	String ChatLogGetById = "SELECT * FROM ofChatLog where id = ?";
	String ChatLogFindAll = "SELECT * FROM ofChatLog";
	String ChatLogDelete = "UPDATE ofChatLog set state = 1 where id = ?";
	String ChatLogInsert = "INSERT INTO ofChatLog(sessionJID, sender, receiver, createDate, length, content, detail, state) VALUES(?,?,?,?,?,?,?,?)";
	String ChatLogCount = "SELECT count(1) FROM ofChatLog where state = 0";
	String ChatLogQuery = "SELECT * FROM ofChatLog where state = 0";
	String ChatLogLastReceiver = "SELECT distinct receiver FROM ofChatLog where state = 0 and sender = ?";
	String ChatLogContact = "SELECT distinct sessionJID FROM ofChatLog where state = 0";

}
