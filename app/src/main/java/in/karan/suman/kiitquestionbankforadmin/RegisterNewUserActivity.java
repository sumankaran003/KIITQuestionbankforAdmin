package in.karan.suman.kiitquestionbankforadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterNewUserActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;
    private EditText branchField,emailField,passwordField;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);

        branchField= (EditText) findViewById(R.id.branch1);
        emailField= (EditText) findViewById(R.id.email);
        passwordField= (EditText) findViewById(R.id.password);
        register= (Button) findViewById(R.id.register);

        mAuth=FirebaseAuth.getInstance();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        mProgress= new ProgressDialog(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });



    }


    private void startRegister() {

       final String branch,email,password;
        branch=branchField.getText().toString().trim();
        email=emailField.getText().toString().trim();
        password=passwordField.getText().toString().trim();


        if(!TextUtils.isEmpty(branch) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgress.setMessage("Signing Up ...");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        String user_id = mAuth.getCurrentUser().getUid();

                        //DatabaseReference mDatabaseReference = database.getReference(user_id);
                        DatabaseReference current_user_db=mDatabase.child(user_id);
                        current_user_db.child("branch").setValue(branch);

                       // FirebaseDatabase database = FirebaseDatabase.getInstance();
                       // DatabaseReference myRef = database.getReference("Users/"+user_id);
                       // myRef.child("branch").setValue("abcd");
                        //myRef.child("city").setValue(city);

                        mProgress.dismiss();
                        Toast.makeText(RegisterNewUserActivity.this,user_id,Toast.LENGTH_LONG).show();

                        Intent mainIntent = new Intent(RegisterNewUserActivity.this,AdminAfterLoginActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);


                    }


                }
            });
        }
    }
}
