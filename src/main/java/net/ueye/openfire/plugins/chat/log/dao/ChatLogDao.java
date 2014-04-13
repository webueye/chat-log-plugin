package net.ueye.openfire.plugins.chat.log.dao;

import java.util.List;

import net.ueye.openfire.plugins.chat.log.Page;
import net.ueye.openfire.plugins.chat.log.entity.ChatLog;

/**
 * @author rubys@vip.qq.com
 * @since 2014-4-12
 */
public interface ChatLogDao {

	void save(ChatLog chatLog);

	int getCount();

	int getLastId();

	ChatLog get(Long id);

	List<ChatLog> query(ChatLog chatLog, Page page);

	List<ChatLog> findAll();

	List<String> findLastContact(ChatLog chatLog);

	List<String> findAllContact();

	void remove(Long id);

}