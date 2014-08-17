<%@page import="com.liferay.portal.kernel.json.JSONObject"%>
<%@page import="com.liferay.portal.kernel.json.JSONFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.json.JSONArray"%>
<%@page import="java.net.URLEncoder"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<portlet:defineObjects />

<c:choose>
	<c:when test="${twitterAuthorisationURL != null}" >
		<a href="${twitterAuthorisationURL}">Authorise Twitter to provide my mentions</a>
	</c:when>
	<c:otherwise>
		
		<table>
		<tr><th>Time</th><th>User</th><th>Tweet</th></tr>
		
		<%
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray((String)renderRequest.getAttribute("mentions"));
		
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			
			JSONObject userJsonObj = jsonObj.getJSONObject("user");
			String screenName = userJsonObj.getString("screen_name");
			String createdAt = jsonObj.getString("created_at");
			String text = jsonObj.getString("text");
			
			out.write("<tr>");
			out.write("<td>" + createdAt + "</td>");
			out.write("<td>" + screenName + "</td>");
			out.write("<td>" + text + "</td>");
			out.write("</tr>");
		}
		%>
		
		</table>
	
	</c:otherwise>
</c:choose>
