package Domain_Layer;

public class Administrator {
 private String email;
 private String password;

 public Administrator(String email, String password) {
     this.email = email;
     this.password = password;
 }

 public String getEmail() {
     return email;
 }

 public String getPassword() {
     return password;
 }
}
