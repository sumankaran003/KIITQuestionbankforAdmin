package in.karan.suman.kiitquestionbankforadmin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddBranchActivity extends AppCompatActivity {

    Button upload;
    Spinner yearField,typeField;
    EditText subjectField,qyearField,branchField;
    TextView textViewStatus;
    final static int PICK_PDF_CODE = 2342;
    private Uri mImageUri;
    ProgressBar progressBar;

    String spinType,spinYear;

    String year[] = {"1st","2nd","3rd","4th","5th","6th"};
    String type[] = {"Mid Sem","End Sem","Re Mid Sem","Practice"};

    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;

    private static final int GALLERY_REQUEST=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_branch);

        upload = (Button) findViewById(R.id.upload);
        yearField = (Spinner) findViewById(R.id.year);
        qyearField = (EditText) findViewById(R.id.qyear);
        typeField = (Spinner) findViewById(R.id.type);
        subjectField = (EditText) findViewById(R.id.subject);
        branchField = (EditText) findViewById(R.id.branch);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);





        mStorageReference = FirebaseStorage.getInstance().getReference();

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        ArrayAdapter adapter1 = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,year);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearField.setAdapter(adapter1);
        yearField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                spinYear =year[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        ArrayAdapter adapter2 = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,type);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeField.setAdapter(adapter2);
        typeField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                spinType =type[position];
                //Toast.makeText(MainActivity.this,"you selected : "+str,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(AddBranchActivity.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + getPackageName()));


                    startActivity(intent);
                    return;
                }

                //creating an intent for file chooser
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PDF_CODE);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                uploadFile(data.getData());
            }else{
                Toast.makeText(this, "No file chosen ...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFile(Uri data) {
        progressBar.setVisibility(View.VISIBLE);


        final String branch = branchField.getText().toString().trim();
        final String year = spinYear;
        final String subject = subjectField.getText().toString().trim();
        final String type = spinType;
        final String qyear = qyearField.getText().toString().trim();
        final String qname = branch+" "+year+" year "+subject+" "+type+" "+qyear;

        final String str=branch+"/"+year+"/"+subject+"/"+type+"/"+qname;


        final FirebaseDatabase database = FirebaseDatabase.getInstance();




       /* if(!TextUtils.isEmpty(branch) && !TextUtils.isEmpty(year) && !TextUtils.isEmpty(subject) && !TextUtils.isEmpty(type) &&
                !TextUtils.isEmpty(qyear) && !TextUtils.isEmpty(qname) && mImageUri !=null){



        }
*/

            StorageReference sRef = mStorageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + ".pdf");
            sRef.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.GONE);
                textViewStatus.setText("File Uploaded Successfully");

                Upload upload = new Upload(taskSnapshot.getDownloadUrl().toString());
                DatabaseReference mDatabaseReference = database.getReference(branch+"/"+year+"/"+subject+"/"+type+"/"+qname);
                mDatabaseReference.child("url").setValue(upload);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        textViewStatus.setText((int) progress + "% Uploading...");
                    }
                });

    }


}
