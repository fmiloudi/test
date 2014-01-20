package models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import play.libs.OAuth2;
import play.libs.WS;
import play.mvc.Scope;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: alanzirek
 * Date: 12/09/13
 * Time: 14:37
 */
public class APIManager extends OAuth2 {

	public APIManager(String authorizationURL, String accessTokenURL, String clientid, String secret) {
		super(authorizationURL, accessTokenURL, clientid, secret);
	}

	public String fetchAccessToken(String callbackURL) {
		return fetchAccessToken(callbackURL, new HashMap<String, Object>());
	}

	public String fetchAccessToken(String callbackURL, String parameterName, String parameterValue) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(parameterName, parameterValue);
		return fetchAccessToken(callbackURL, parameters);
	}
	public static JsonArray getConnections(String url, HashMap<String, String> params, String jsonDataFieldName) {
		  return WS.url(url)
		    .setParameters(params)
		    .setHeader("x-li-format", "json")                           // pour Linkedin
		    .get()
		    .getJson()
		    .getAsJsonObject()
		    .get(jsonDataFieldName)
		    .getAsJsonArray();
	}

	public String fetchAccessToken(String callbackURL, Map<String, Object> params) {
		String accessCode = Scope.Params.current().get("code");
		params.put("client_id", clientid);
		params.put("client_secret", secret);
		params.put("redirect_uri", callbackURL);
		params.put("code", accessCode);
		WS.HttpResponse response = WS.url(accessTokenURL).params(params).post();
		return response.getJson().getAsJsonObject().get("access_token").getAsString();
	}

	public String getClientid() {
		return this.clientid;
	}

	public String getSecret() {
		return this.secret;
	}

	public String getAccessTokenURL() {
		return this.accessTokenURL;
	}

	public JsonObject getConnectionsLinkedIn(String string, String accessToken) {
		return WS.url(string)
				.setParameter("oauth2_access_token", accessToken)
				.setHeader("x-li-format", "json")                           // pour Linkedin
				.get()
				.getJson()
				.getAsJsonObject();
	
	}


}
