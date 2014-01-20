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

     
public static APIManager LINKEDIN = new APIManager (
	            "https://www.linkedin.com/uas/oauth2/authorization",
	            "https://www.linkedin.com/uas/oauth2/accessToken",
	            "776bn8svtj9a0q",
	            "JcJjofLw1w14DesR"
	    );
    public static void index() 
    {
    	if(connection==true)   
    {
    	String accessToken_linkedin = Cache.get("accessToken_linkedin_" + session.getId(), String.class);
    	Contact me= Cache.get("Me_" + session.getId(), Contact.class);
    	render(me,accessToken_linkedin);
    }
    else
    {
    	Contact me= new Contact("test","test","test",null);
    	render(me);
    }
    	
    }
    
    //Fonction du premier bouton : connexion
    public static void auth_linkedin() {
        if (OAuth2.isCodeResponse()) {
        	
        	//Seconde etape, echange du code contre un access token
        	//utilisation de la methode de APIManager qui utiliser la methode post() au lieu de get()
            String accessToken = LINKEDIN.fetchAccessToken(authURL_linkedin(), "grant_type", "authorization_code");
            //Sauvegarde du accessToken dans le cache	
          
            Cache.set("accessToken_linkedin_" + session.getId(), accessToken, "30mn");
            JsonObject obj = LINKEDIN.getConnectionsLinkedIn("https://api.linkedin.com/v1/people/~:(id,first-name,last-name,picture-url,public-profile-url)", accessToken);
            
            String idUser			= obj.get("id").getAsString();
            String firstNameUser 	= obj.get("firstName").getAsString();
            String lastNameUser 	= obj.get("lastName").getAsString();
            String picture 			= "/public/images/nophoto.png";

            Cache.set("Me_" + session.getId(), firstNameUser, "30mn");
            if(obj.has("pictureUrl")) {
            	picture = obj.get("pictureUrl").getAsString();
            }
            
            String profil = obj.get("publicProfileUrl").getAsString();
            if(Cache.get("Me_") == null) {
            	Contact me = new Contact(idUser, firstNameUser, lastNameUser, null);
                Cache.set("Me_" + session.getId(), me, "30mn");
            }
            connection=true;
            redirect("http://test:9000");
      
        }
        else {
        	//Premiere etape, recuperation du verfication de code
	    	Map params_code = new HashMap <String, String> ();
	    	params_code.put("response_type", "code");
	    	params_code.put("state", "ABYUJGH15682gsr4ux565");
	    	
	    	LINKEDIN.retrieveVerificationCode(authURL_linkedin(), params_code);
	    	
        }
    }
    

    static String authURL_linkedin() {
    	return play.mvc.Router.getFullUrl("Application.auth_linkedin");
    }
    
	  public static void logoutFrom_linkedin() {
			Cache.delete("accessToken_linkedin_" + session.getId());
			Cache.delete("Me_" + session.getId());
			redirect("http://test:9000");
			
		 }	



}
