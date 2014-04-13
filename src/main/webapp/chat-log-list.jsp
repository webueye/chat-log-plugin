<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	request.setAttribute("ctx", request.getContextPath()+"/plugins/chatlog");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  	<head>
	    <title>Chat Log List</title>
	    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	    <meta name="pageID" content="chat-log-service"/>
  	</head>
  
  	<body>
        <form id="submitForm" action="chat-log" method="post">
		    <div class="jive-contentBoxHeader">ChatLog Search</div>
		    <div class="jive-contentBox">
				Sender：<input type="text" name="sender" value="${chatLog.sender}" size="10"/>
				Receiver：<input type="text" name="receiver" value="${chatLog.receiver}" size="10"/>
			    Content：<input type="text" name="content" value="${chatLog.content}" size="10"/>
			    Send Date：<input type="text" name="createDate" value="${chatLog.createDate}" size="13"/>
	            <input type="submit" value="查询"/>
		    </div>
	    
		    <div class="jive-table">
		        <table cellpadding="0" cellspacing="0" border="0" width="100%">
		        <thead>
		            <tr>
		                <th>Sender</th>
		                <th>Receiver</th>
		                <th>Content</th>
		                <th>SendDate</th>
		                <th>Delete</th>
		            </tr>
		        </thead>
		        <tbody>
		            <c:forEach var="chatLog" items="${page.dateList}">
			            <tr class="jive-even">
			                <td>${chatLog.sender}</td>
			                <td>${chatLog.receiver}</td>
			                <td>${chatLog.content}</td>
			                <td>${chatLog.createDate}</td>
			                <td>
			                	<a href="${ctx}/chat-log?action=d&id=${chatLog.id}">
			                    	<img title="Delete this message" src="${ctx}/img/delete.gif">
			                    </a>
			                </td>
			            </tr>
		            </c:forEach>
		         </tbody>
		        </table>
		        
		        <c:if test="${page.totalPages > 1}">
					<div class="search" style="margin: 1px; padding: 5px;">
						
						<span style="float:right;margin:5px;">
							<c:if test="${page.totalPages > 1}">
								<c:if test="${page.pageNum == 1}">
									[<span>First</span> |
									<span>Previous</span> |
									<a href="${ctx}/chat-log?pageNum=${page.pageNum+1}${param.param}" onclick='return paginationHandle("${page.pageNum+1}");'>Next</a> |
									<a href="${ctx}/chat-log?pageNum=${page.totalPages}${param.param}" onclick='return paginationHandle("${page.totalPages}");'>Last</a>]
								</c:if>
								<c:if test="${page.pageNum > 1 and page.pageNum < page.totalPages}">
									[<a href="${ctx}/chat-log?pageNum=1${param.param}" onclick='return paginationHandle("${a}1");'>First</a> |
									<a href="${ctx}/chat-log?pageNum=${page.pageNum-1}${param.param}" onclick='return paginationHandle("${page.pageNum-1}");'>Previous</a> |
									<a href="${ctx}/chat-log?pageNum=${page.pageNum+1}${param.param}" onclick='return paginationHandle("${page.pageNum+1}");'>Next</a> |
									<a href="${ctx}/chat-log?pageNum=${page.totalPages}${param.param}" onclick='return paginationHandle("${page.totalPages}");'>Last</a>]
								</c:if>
								<c:if test="${page.pageNum == page.totalPages}">
									[<a href="${ctx}/chat-log?pageNum=1${param.param}" onclick='return paginationHandle("${a}1");'>First</a> |
									<a href="${ctx}/chat-log?pageNum=${page.pageNum-1}${param.param}" onclick='return paginationHandle("${page.pageNum-1}");'>Previous</a> |
									<span>Next</span> |
									<span>Last</span>]
								</c:if>	
							</c:if>
							<c:if test="${page.totalPages == 1}">
								[<span>First</span> |
								<span>Previous</span> |
								<span>Next</span> |
								<span>Last</span>]
							</c:if>
						
						</span>
						
					</div>
					
					<input id="pageSize" name="pageSize" value="${page.pageSize}" type="hidden" />
					<input id="pageNum" name="pageNum" value="${page.pageNum}" type="hidden" />
					
				</c:if>
		        
		    </div>
        </form>
	    
	    <script type="text/javascript">
	    function paginationHandle(pageNum, pageSize){
	    	if(!isNaN(pageNum)){
				document.getElementById("pageNum").value = pageNum;
			}
			if(!isNaN(pageSize)){
				document.getElementById("pageSize").value = pageSize;
			}
			document.getElementById("submitForm").submit();
			
	    }
	    </script>
	    
  	</body>
</html>