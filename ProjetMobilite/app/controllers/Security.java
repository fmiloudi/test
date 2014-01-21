package controllers;
 
import models.*;
 
public class Security extends Secure.Security {
 
	static boolean authentify(String email, String password) {
		
		if(user.connect(email, password)==null)
		{
			user member= new user(email, password);
			return member.connect(email, password) == null;
		}
		else
		{

			return user.connect(email, password) != null;
		}
		
		
	
	}
	
}
