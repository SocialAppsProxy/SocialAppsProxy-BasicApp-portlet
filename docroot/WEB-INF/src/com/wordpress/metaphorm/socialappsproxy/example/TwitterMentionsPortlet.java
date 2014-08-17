package com.wordpress.metaphorm.socialappsproxy.example;

import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Portlet implementation class TwitterMentionsPortlet
 */
public class TwitterMentionsPortlet extends MVCPortlet {
 
	@Override
	public void doView(RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {

		HttpClient httpClient = new HttpClient();
		
		// Configure the Apache Commons client to use the Social Apps Proxy as its HTTP proxy
		httpClient.getHostConfiguration().setProxy(renderRequest.getServerName(), renderRequest.getServerPort());

		// Twitter's OAuth service only supports HTTPS.
		// Ensure you tick the "use HTTPS" checkbox for the Twitter OAuth provider 
		// as configured in "Social Apps Proxy Manager" found in Liferay Portal control panel.
		GetMethod getMethod = new GetMethod("http://api.twitter.com/1.1/statuses/mentions_timeline.json");
		
		// Set the Auth authorisation oauth_callback URL to the current URL
		getMethod.addRequestHeader(new Header("oauth_callback", renderResponse.createRenderURL().toString()));
		
		// Set the userToken as provided by the Social Apps Proxy
		getMethod.addRequestHeader(new Header("userToken", (String)renderRequest.getAttribute("userToken")));		 
		
		int status = httpClient.executeMethod(getMethod);
		String body = getMethod.getResponseBodyAsString();
		
		Pattern p = Pattern.compile("<authorisation_needed>\\s*<url>([^<]*?)</url>\\s*</authorisation_needed>");
		Matcher m = p.matcher(body);
		
		// If OAuth authorisation is required from the user...
		if (m.find())
			// ... make the OAuth provider's authorisation URL available to the JSP so a link can be rendered ...
			renderRequest.setAttribute("twitterAuthorisationURL", decodeXMLEntities(m.group(1)));
		else
			// ... otherwise else prepare the OAuth resource for rendering via the JSP
			renderRequest.setAttribute("mentions", body);
		
		super.doView(renderRequest, renderResponse);
	}
	
	private String decodeXMLEntities(String xmlStr) {
		return xmlStr.replaceAll("&nbsp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
	}
}
