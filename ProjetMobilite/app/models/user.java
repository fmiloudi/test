package models;

import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;

@Entity
public class user extends Model  {
	

	public String email;
    public String password;
    
    public user(String email, String password) 
    {
    	this.email = email;
    	this.password = password;   
    }
    
    public static user connect(String email, String password) {
        return find("byEmailAndPassword", email, password).first();
    }

}
