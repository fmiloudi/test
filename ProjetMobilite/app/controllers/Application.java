package controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import play.*;
import play.cache.Cache;
import play.db.DB;
import play.db.jpa.GenericModel.JPAQuery;
import play.libs.OAuth2;
import play.libs.WS.*;
import play.libs.WS;
import play.mvc.*;
import antlr.collections.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mysql.jdbc.Connection;

import models.APIManager;
import models.Contact;

@With(Secure.class)
public class Application extends Controller {    
	static boolean connection=false;
	static boolean contact=false;
     
public static APIManager linkedin = new APIManager (
	            "https://www.linkedin.com/uas/oauth2/authorization",
	            "https://www.linkedin.com/uas/oauth2/accessToken",
	            "776bn8svtj9a0q",
	            "JcJjofLw1w14DesR"
	    );
    public static void index() 
    {
    	String user = Security.connected();
    	
    	if(connection==true)   
		    {
    		 //System.out.println("connection");
		    	String accessLink = Cache.get("accessLink_" + session.getId(), String.class);
		    	Contact me= Cache.get("Me_" + session.getId(), Contact.class);
		    
		    	java.util.List<Contact> listeContacts;
		    	java.util.List<Contact> listeContactsUser = new ArrayList<Contact>() ;
		    	java.util.List<Contact> listeContactsReduit = new ArrayList<Contact>() ;
		    	
			   if(contact==true)
			   {
				   listeContacts = Contact.findAll();
				   boolean existe = false;
				   for(Contact c : listeContacts)
				   {
					   if(c.userName.equals(Security.connected()))
					   {
						   existe = true; 
						   
					   }			   
				   }

				   if (existe == true)
				   {
					   System.out.println("size:"+listeContacts.size());
					   for(int i=0;  i<listeContacts.size(); i++)
					   {
						   if (listeContacts.get(i).userName.equals(Security.connected()))
						   {
							   listeContactsReduit.add(listeContacts.get(i));					   
						   }
						   System.out.println(listeContacts.get(i).userName+"=?="+Security.connected());
					   }

					   render(me,accessLink,listeContactsReduit);
				   }
				   else
				   {
					   //recuprerContact();
					   System.out.println("false");
					   render(me,accessLink,listeContactsReduit);
					   
				   }
				  
			
			   }
			
				//render(me,accessLink);
			    
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
            JsonObject obj = linkedin.getConnectionsLinkedIn("https://api.linkedin.com/v1/people/~:(id,first-name,last-name,picture-url)", accessToken);
            
            String idUser	= obj.get("id").getAsString();
            String nomUser 	= obj.get("firstName").getAsString();
            String prenomUser = obj.get("lastName").getAsString();
   
            if(Cache.get("Me_") == null) {
            	Contact me = new Contact(idUser, nomUser, prenomUser, null, null,null,Security.connected());
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
	  
	  public static void recuprerContact()
	  {
		 	String accessToken = Cache.get("accessLink_" + session.getId(), String.class);
		 	
	    	//Récupération des contacts
	    	JsonObject monJsonObj = linkedin.getConnectionsLinkedIn("https://api.linkedin.com/v1/people/~/connections", accessToken);
	     
	    	//Recupération du nombre de contact
	    	//String test= monJsonObj.toString();
	    	Integer nbContact = (int)monJsonObj.get("_total").getAsDouble();
	    	String test = String.valueOf(nbContact);
	    	Cache.set("test_" + session.getId(), test, "30mn");
	    	HashMap<String,Double> coor = new HashMap<String,Double>();
	    	String id, nom, prenom, picture, profil;
	    	for (int i=0; i<nbContact; i++)
	    	{
	    		id = monJsonObj.get("values").getAsJsonArray().get(i).getAsJsonObject().get("id")
						.getAsString();
	    		nom = monJsonObj.get("values").getAsJsonArray().get(i).getAsJsonObject().get("firstName")
						.getAsString();
	    		prenom = monJsonObj.get("values").getAsJsonArray().get(i).getAsJsonObject().get("lastName")
						.getAsString();
	    		profil = monJsonObj.get("values").getAsJsonArray().get(i).getAsJsonObject()
	    				.get("siteStandardProfileRequest").getAsJsonObject().get("url")
						.getAsString();
	    		
	    		if(monJsonObj.get("values").getAsJsonArray().get(i).getAsJsonObject().has("pictureUrl")) {
	    			picture = monJsonObj.get("values").getAsJsonArray().get(i).getAsJsonObject().get("pictureUrl")
	    					.getAsString();
	    		} else {
	    			picture = "/public/images/nophoto.png";
	    		}
	    		
	    		Contact cont = new Contact(id, nom,prenom,"",profil,picture,Security.connected());
	    		cont._save();
	    	}
	    	contact=true;
	    	index();
	    	

	  }
	  
	  public static void deleteContactsLinkedIn() {
		  	Contact.deleteAll();
		    index();
		    
		}
	  



}
