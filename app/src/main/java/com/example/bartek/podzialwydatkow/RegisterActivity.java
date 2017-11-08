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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

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

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Firebase Autoryzacja
        mAuth = FirebaseAuth.getInstance();

        mRegProgess = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Rejestracja");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDisplayName = (TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mCreateBtn = (Button) findViewById(R.id.reg_create_btn);


        //PRZYCISK - REJESTRACJA
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(display_name) && display_name.length()>=3 && display_name.length()<=24 && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                    mRegProgess.setTitle("Rejestracja użytkownika");
                    mRegProgess.setMessage("Proszę czekać...");
                    mRegProgess.setCanceledOnTouchOutside(false);
                    mRegProgess.show();
                    register_user(display_name, email, password);

                }

                if(TextUtils.isEmpty(display_name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

                    Toast.makeText(RegisterActivity.this, "Wprowadź brakujące dane", Toast.LENGTH_LONG).show();

                } else if (display_name.length()<3 || display_name.length()>24){

                    Toast.makeText(RegisterActivity.this, "Nie można utworzyć konta:\nNazwa użytkownika musi składać się z 3-24 znaków\nHasło musi mieć min. 6 znaków", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    //METODA - REJESTRACJA
    private void register_user(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid); //Główny węzeł -> Users -> UID

                    //Utworzenie userMap
                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", display_name);
                    userMap.put("image", "default");
                    userMap.put("thumb_image", "default");

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {  //Przekazuje obiekt do bazy danych (userMap)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                mRegProgess.dismiss();

                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Stworzenie nowego zadania i zamkniecie starego, zeby nie dalo wracac sie do StartActivity po zalogowaniu
                                startActivity(mainIntent);
                                finish();
                            }

                        }
                    });

                } else {

                    mRegProgess.hide();
                    String error = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        error = "Hasło jest zbyt krótkie!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        error = "Niepoprawny adres Email!";
                    } catch (FirebaseAuthUserCollisionException e) {
                        error = "Konto o podanym adresie Email już istnieje!";
                    } catch (Exception e) {
                        error = "Nieznany błąd!";
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();

                }

            }
        });


    }
}
