package models;

import play.db.jpa.Model;
import java.io.Serializable;
import javax.persistence.Entity;

@Entity
public class Contact extends Model implements Serializable 
{

       public String getWebId() {
		return webId;
	}

	public void setWebId(String webId) {
		this.webId = webId;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getLocalisation() {
		return localisation;
	}

	public void setLocalisation(String localisation) {
		this.localisation = localisation;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getNom() {
		return nom;
	}
	public String webId;
       public String nom;
       public String prenom;
       public String localisation;
       
       public Contact(String id) {
               super();
               this.webId = id;
       }

       public Contact(String id, String nom, String prenom, String localisation) 
       {
               super();
               this.webId = id;
               this.nom = nom;
               this.prenom = prenom;
               this.localisation = localisation;
       }
       
       public static Contact getById(String id) {
               return find("webId", id).first();
       }

       
}