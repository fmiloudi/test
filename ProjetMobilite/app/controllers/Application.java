package controllers;

import java.util.HashMap;
import java.util.Map;

import play.*;
import play.cache.Cache;
import play.db.DB;
import play.libs.OAuth2;
import play.libs.WS.*;
import play.libs.WS;
import play.mvc.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import models.APIManager;
import models.Contact;


public class Application extends Controller {    
	static boolean connection=false;

     
public static APIManager linkedin = new APIManager (
	            "https://www.linkedin.com/uas/oauth2/authorization",
	            "https://www.linkedin.com/uas/oauth2/accessToken",
	            "776bn8svtj9a0q",
	            "JcJjofLw1w14DesR"
	    );
    public static void index() 
    {
    	if(connection==true)   
    {
    	String accessLink = Cache.get("accessLink_" + session.getId(), String.class);
    	Contact me= Cache.get("Me_" + session.getId(), Contact.class);
    	render(me,accessLink);
    }
    else
    {
    	render();
    }
    	
    }
    
    //Fonction du premier bouton : connexion
    public static void auth_linkedin() {
        if (OAuth2.isCodeResponse()) {
        	
            String accessToken = linkedin.fetchAccessToken(authURL_linkedin(), "grant_type", "authorization_code");
            Cache.set("accessLink_" + session.getId(), accessToken, "30mn");
            JsonObject obj = linkedin.getConnectionsLinkedIn("https://api.linkedin.com/v1/people/~:(id,first-name,last-name,picture-url,public-profile-url)", accessToken);
            
            String idUser	= obj.get("id").getAsString();
            String nomUser 	= obj.get("firstName").getAsString();
            String prenomUser = obj.get("lastName").getAsString();
   
            if(Cache.get("Me_") == null) {
            	Contact me = new Contact(idUser, nomUser, prenomUser, null);
                Cache.set("Me_" + session.getId(), me, "30mn");
            }
            connection=true;
            redirect("http://linkedin:9000");
      
        }
        else {
        	//Premiere etape, recuperation du verfication de code
	    	Map parametre = new HashMap <String, String> ();
	    	parametre.put("response_type", "code");
	    	parametre.put("state", "ABYUJGH15682gsr4ux565");
	    	linkedin.retrieveVerificationCode(authURL_linkedin(), parametre);
	    	
        }
    }
    

    static String authURL_linkedin() {
    	return play.mvc.Router.getFullUrl("Application.auth_linkedin");
    }
    
	  public static void logoutFrom_linkedin() {
			Cache.delete("accessLink_" + session.getId());
			Cache.delete("Me_" + session.getId());
			redirect("http://linkedin:9000");
			
		 }	
	  



}
