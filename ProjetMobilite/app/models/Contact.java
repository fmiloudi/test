package models;

import play.db.jpa.Model;
import java.io.Serializable;
import javax.persistence.Entity;

@Entity
public class Contact extends Model implements Serializable 
{
	/**
	 * Constructeur
	 * 
	 * @param webId
	 */
	
	   public Contact(String webId) {
           super();
           this.webId = webId;
   }
	/**
	 * Constructeur
	 * 
	 * @param webId
	 * @param nom
	 * @param prenom
	 * @param localisation
	 * @param profile
	 * @param picture
	 * @param userName
	 */
	
	   public String webId;
       public String nom;
       public String prenom;
       public String localisation;
       public String profile;
       public String picture;
       public String userName;
    

       public Contact(String id, String nom, String prenom, String localisation, String profile, String picture,String userName) 
       {
               super();
               this.webId = id;
               this.nom = nom;
               this.prenom = prenom;
               this.localisation = localisation;
               this.picture=picture;
               this.profile=profile;
               this.userName = userName;
               
       }
       
   	/**
   	 * @param id
   	 * @return
   	 */
       public static Contact getById(String id) {
               return find("webId", id).first();
       }

       
}