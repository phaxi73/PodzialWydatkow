package com.example.bartek.podzialwydatkow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;

    //Proges
    private ProgressDialog mRegProgess;

    //Firebase Autoryzacja
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Firebase Autoryzacja
        mAuth = FirebaseAuth.getInstance();

        mRegProgess = new ProgressDialog(this);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Rejestracja");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDisplayName = (TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mCreateBtn = (Button) findViewById(R.id.reg_create_btn);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(display_name) && display_name.length()>2 && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                    mRegProgess.setTitle("Rejestracja użytkownika");
                    mRegProgess.setMessage("Proszę czekać...");
                    mRegProgess.setCanceledOnTouchOutside(false);
                    mRegProgess.show();
                    register_user(display_name, email, password);

                }

                if(TextUtils.isEmpty(display_name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

                    Toast.makeText(RegisterActivity.this, "Wprowadź brakujące dane", Toast.LENGTH_LONG).show();

                } else if (display_name.length()<3){

                    Toast.makeText(RegisterActivity.this, "Nie można utworzyć konta, sprawdź poprawność wprowadzonych danych", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void register_user(String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    mRegProgess.dismiss();

                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Stworzenie nowego zadania i zamkniecie starego, zeby nie dalo wracac sie do StartActivity po zalogowaniu
                    startActivity(mainIntent);
                    finish();

                } else {

                    mRegProgess.hide();
                    Toast.makeText(RegisterActivity.this, "Nie można utworzyć konta, sprawdź poprawność wprowadzonych danych", Toast.LENGTH_LONG).show();

                }

            }
        });


    }
}
