package in.karan.suman.kiitquestionbankforadmin.Model;

/**
 * Created by Suman on 31-Dec-17.
 */

public class User {
    private String email,password,branchName;

    public User() {
    }

    public User(String email, String password, String branchName) {
        this.email = email;
        this.password = password;
        this.branchName = branchName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
