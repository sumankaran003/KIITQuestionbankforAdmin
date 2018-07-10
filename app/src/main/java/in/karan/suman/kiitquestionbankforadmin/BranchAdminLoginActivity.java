package in.karan.suman.kiitquestionbankforadmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BranchAdminLoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    Spinner branchField;
    EditText emailField,passwordField;
    Button login;
    String branch,email,password,branchOp;
    String branchOption[]={"Computer Science","Computer Application","Civil","Computer Science","Electrical","Electronics","Mechanical",
            "Humanities and Social Sciences","Applied Sciences","Management","Biotechnology","Rural Management",
            "Law","Fashion Technology","Film and Media Sciences","Medicine"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_admin_login);

        mAuth=FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");




        branchField= (Spinner) findViewById(R.id.branch);
        emailField= (EditText) findViewById(R.id.email);
        passwordField= (EditText) findViewById(R.id.password);
        login= (Button) findViewById(R.id.login);

        branchField=(Spinner)findViewById(R.id.branch);
        ArrayAdapter adapter1 = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,branchOption);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchField.setAdapter(adapter1);
        branchField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                branch =branchOption[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });




    }

    private void login() {

        email=emailField.getText().toString().trim();
        password=passwordField.getText().toString().trim();
        branchOp=branch;

        if(!TextUtils.isEmpty(branchOp) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        checkUserExist();


                    } else {
                        Toast.makeText(BranchAdminLoginActivity.this, "Error Login", Toast.LENGTH_LONG).show();
                    }
                }
            });





        }else{
            Toast.makeText(BranchAdminLoginActivity.this,"You have not filled the fields properly",Toast.LENGTH_LONG).show();
        }

    }


    private void checkUserExist() {

        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(user_id))
                {

                    Intent loginIntent = new Intent(BranchAdminLoginActivity.this,QuestionUploadForAdminActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    loginIntent.putExtra("branch",branchOp);
                    startActivity(loginIntent);
                }else{
                    Toast.makeText(BranchAdminLoginActivity.this,"You are not a valid administrator",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

}
