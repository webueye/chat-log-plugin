package net.ueye.openfire.plugins.chat.log.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ueye.openfire.plugins.chat.log.Page;
import net.ueye.openfire.plugins.chat.log.dao.ChatLogDao;
import net.ueye.openfire.plugins.chat.log.dao.ChatLogDaoImpl;
import net.ueye.openfire.plugins.chat.log.entity.ChatLog;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.admin.AuthCheckFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ChatLogServlet extends HttpServlet {

	private Logger logger = LoggerFactory.getLogger(ChatLogServlet.class);

	private ChatLogDao chatLogDao;

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		chatLogDao = new ChatLogDaoImpl();

		AuthCheckFilter.addExclude("chatlog");
		AuthCheckFilter.addExclude("chatlog/chat-log");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if ("d".equals(request.getParameter("action")) && StringUtils.isNotEmpty(request.getParameter("id"))) {
			chatLogDao.remove(Long.parseLong(request.getParameter("id")));
			response.sendRedirect("/plugins/chatlog/chat-log");
		} else {
			doPost(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");

		ChatLog chatLog = getChatLog(request);

		Page page = getPage(request);
		List<ChatLog> chatLogs = chatLogDao.query(chatLog, page);
		logger.debug("Find chatLog size[{}]", chatLogs.size());

		if ("json".equalsIgnoreCase(request.getParameter("type"))) {
			StringWriter writer = new StringWriter();
			mapper.writeValue(writer, chatLogs);
			response.setContentType("text/json");

			PrintWriter out = response.getWriter();
			out.println(writer.toString());
			out.flush();
		} else {
			request.setAttribute("chatLog", chatLog);
			request.setAttribute("page", page);
			request.getRequestDispatcher("/plugins/chatlog/chat-log-list.jsp").forward(request, response);
		}
	}

	private ChatLog getChatLog(HttpServletRequest request) {
		ChatLog chatLog = new ChatLog();
		if (StringUtils.isNotEmpty(request.getParameter("sender"))) {
			chatLog.setSender(request.getParameter("sender"));
		}
		if (StringUtils.isNotEmpty(request.getParameter("receiver"))) {
			chatLog.setReceiver(request.getParameter("receiver"));
		}
		if (StringUtils.isNotEmpty(request.getParameter("content"))) {
			chatLog.setContent(request.getParameter("content"));
		}
		if (StringUtils.isNotEmpty(request.getParameter("createDate"))) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				chatLog.setCreateDate(new Timestamp(df.parse(request.getParameter("createDate")).getTime()));
			} catch (ParseException e) {
				logger.error("Format date error", e);
			}
		}
		return chatLog;
	}

	private Page getPage(HttpServletRequest request) {
		Page page = new Page();
		if (StringUtils.isNotEmpty(request.getParameter("pageSize"))) {
			page.setPageSize(Integer.valueOf(request.getParameter("pageSize")));
		}
		if (StringUtils.isNotEmpty(request.getParameter("pageNum"))) {
			page.setPageNum(Integer.valueOf(request.getParameter("pageNum")));
		}
		return page;
	}

	@Override
	public void destroy() {
		super.destroy();
		// Release the excluded URL
		AuthCheckFilter.removeExclude("chatlog/chat-log");
		AuthCheckFilter.removeExclude("chatlog");
	}

}