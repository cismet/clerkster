/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import play.db.ebean.*;
import javax.persistence.*;
import play.data.format.Formats;
import play.data.validation.Constraints;

/**
 *
 * @author Gilles Baatz
 */
@Entity 
@Table(name="account")
public class User extends Model {

        	@Id
    @Constraints.Required
    @Formats.NonEmpty
    public String name;
                
     @Constraints.Required   
        public String password;      // hashed password
        
     @Constraints.Required
     public String apiPassword; // cleartext password
     
     public static Model.Finder<String,User> find = new Model.Finder<String,User>(String.class, User.class);
     
         /**
     * Retrieve a User from email.
     */
    public static User findByName(String name) {
        return find.where().eq("name", name).findUnique();
    }
}
