package watersupplier.main.Pojo;

import java.io.Serializable;

/**
 * Created by Aman Gupta on 27/1/17.
 */

public class SignUp_POJO {
   public String User_id = "";
    public String Password = "";
    public String Shop_Name = "";
    public String Phone = "";
    public String Email = "";

    public SignUp_POJO(String User_id,
            String Password,
            String Shop_Name,
            String Phone,
            String Email){
        this.User_id = User_id;
        this.Password = Password;
        this.Shop_Name = Shop_Name;
        this.Phone = Phone;
        this.Email = Email;
    }
}
