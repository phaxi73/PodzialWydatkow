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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;

    private Button mLoginBtn;

    private ProgressDialog mLoginProgress;

    ////Firebase Autoryzacja
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Firebase Autoryzacja
        mAuth = FirebaseAuth.getInstance();

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Logowanie");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLoginProgress = new ProgressDialog(this);

        mLoginEmail = (TextInputLayout) findViewById(R.id.login_email);
        mLoginPassword = (TextInputLayout) findViewById(R.id.login_password);
        mLoginBtn = (Button) findViewById(R.id.login_btn);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mLoginEmail.getEditText().getText().toString();
                String password = mLoginPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                    mLoginProgress.setTitle("Logowanie");
                    mLoginProgress.setMessage("Proszę czekać...");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    loginUser(email, password);

                }
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){

                   Toast.makeText(LoginActivity.this, "Wprowadź brakujące dane", Toast.LENGTH_LONG).show();
                }

                /*/else if (TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                    Toast.makeText(LoginActivity.this, "Wprowadź poprawny adres Email", Toast.LENGTH_LONG).show();
                }

                else if (!TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){

                    Toast.makeText(LoginActivity.this, "Wprowadź hasło", Toast.LENGTH_LONG).show();
                }/*/
            }
        });

    }

    private void loginUser(final String email, final String password) {

    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {

            if(task.isSuccessful()){

                mLoginProgress.dismiss();

                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Stworzenie nowego zadania i zamkniecie starego, zeby nie dalo wracac sie do StartActivity po zalogowaniu
                startActivity(mainIntent);
                finish();

            } else {

                mLoginProgress.hide();
                Toast.makeText(LoginActivity.this, "Wprowadzono nieprawidłowy login lub hasło, spróbuj ponownie", Toast.LENGTH_LONG).show();
            }

        }
    });

    }

}
