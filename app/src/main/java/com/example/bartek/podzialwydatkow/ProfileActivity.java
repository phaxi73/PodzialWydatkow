package com.example.bartek.podzialwydatkow;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mDisplayName;
    private Button mProfileInviteBtn;
    private Button mCancelBtn;                                  //Przycisk do odrzucania zaproszenia

    private android.support.v7.widget.Toolbar mToolbar;

    private DatabaseReference mUsersDatabase;                   //Mam user_id, więc potrzebuje referencji do bazy danych, żeby móc uzyskać resztę danych
                                                                        //Uzyć obu referencji w onCreate(Bundle savedInstanceState)
    private DatabaseReference mFriendReqDatabase;               //Referencja do zrobienia zapraszania do znajomych
    private DatabaseReference mFriendDatabase;                  //Referencja do zrobienia znajomych

    private FirebaseUser mCurrentUser;                          //Żeby móc wyciągać user id z autha, użuć FirebaseAuth w onCreate (Bundle savedInstanceState)

    private ProgressDialog mProgressDialog;

    private String mCurrent_state;                               //Status znajomosci

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profil użytkownika");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String user_id = getIntent().getStringExtra("user_id");         //Pobiera user_id z UsersActivity (z populateViewHolder)

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);               //Definiowanie obiektów
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");                     //"Users", "Friend_req", "Friends"
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");                           //To nazwy węzłów (kluczy) w bazie danych
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUsersDatabase.keepSynced(true);                                                                        //Umożliwia
        mFriendReqDatabase.keepSynced(true);                                                                    //Odczyt
        mFriendDatabase.keepSynced(true);                                                                       //Offline

        mProfileImage = findViewById(R.id.profile_image);                                              //ImageView do wyświetlania obrazu profilowego
        mDisplayName = findViewById(R.id.profile_display_name);                                         //TextView do wyświetlania nazwy użytkownika
        mProfileInviteBtn = findViewById(R.id.profile_invite_btn);                                        //Przycisk do zapraszania do znajomych (i inne funkcje później)
        mCancelBtn = findViewById(R.id.profile_cancel_btn);                                               //Przycisk do odrzucania zaproszenia

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Wczytywanie profilu");
        mProgressDialog.setMessage("Proszę czekać...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        mCurrent_state = "not_friends";


        mUsersDatabase.addValueEventListener(new ValueEventListener() {                                    //Odbieram dane przy użyciu data snapshota
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mDisplayName.setText(display_name);                                                        //Wyswietlanie odebranej nazwy użytkownika

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.account_icon_orange_wide).into(mProfileImage);   //Wyswietlanie obrazu- Placeholder, żeby wyswietlalo najpierw domyslny obraz,

                //---------------------- ZNAJOMI / ODBIERANIE ZAPROSZENIA ---------------------------------------------------------------------------
                //----Wykrywa, czy zalogowany użytkownik jest zaproszony przez danego innego użytkownika i zmienia odpowiednio przycisk----
                //mFriendReqDatabase- w tym obiekcie (zdefiniowany na górze (=Friend_req, to jest nazwa węzła w bazie danych)),
                //child(mCurrentUser.getUid)- pobieram swoje id (aktualnie zalogowany użytkownik)
                mFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {              //SingleValueEvent działa tylko raz i dla jednej wartości
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){                                           //Jeżeli aktualny obiekt () ma ID osoby, na której profilu jestem, to:

                            String req_type = dataSnapshot.child(user_id)                             //Wchodzę do childa użytkownika, który wysłał zaproszenie, uzyskuje jego wartosć (typ requesta)
                                    .child("request_type").getValue().toString();

                            if(req_type.equals("received")){                                                               //Uzyskaną wartość wykorzystuje tutaj i w "else if"

                                mCurrent_state = "req_received";                                      //Zmiana statusu z powrotem na "not_friends", z "req_sent"
                                mProfileInviteBtn.setText("Zaakceptuj zaproszenie");                  //Zmiana tekstu na przycisku, po anulowaniu wysłania zaproszenia

                                //TYLKO TUTAJ POWINNO SIĘ DAĆ ODRZUCIĆ ZAPROSZENIE (GDY UŻYTKOWNIK JE JUŻ DOSTANIE)
                                mCancelBtn.setVisibility(View.VISIBLE);                              //Przycisk jest widzialny
                                mCancelBtn.setEnabled(true);                                         //Przycisk jest włączony

                            } else if(req_type.equals("sent")){

                                mCurrent_state = "req_sent";
                                mProfileInviteBtn.setText("Anuluj zaproszenie");

                                mCancelBtn.setVisibility(View.INVISIBLE);                             //Przycisk staje się niewidzialny
                                mCancelBtn.setEnabled(false);                                         //Przycisk jest wyłączony

                            }

                            mProgressDialog.dismiss();

                        } else {

                            //Jak zaakceptuje zaproszenie to:
                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {    //Wchodzę do child, który zawiera ID aktualnego użytkownika
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)){                                      //Sprawdzam, czy profil użytkownika, na którym aktualnie jestem istnieje,
                                                                                                             //Jeżeli istnieje, to znaczy, że ten użytkownik jest już znajomym

                                        mCurrent_state = "friends";                                          //Zmiana statusu z na "friends"
                                        mProfileInviteBtn.setText("Usuń ze znajomych");                      //Zmiana tekstu na przycisku

                                        mCancelBtn.setVisibility(View.INVISIBLE);                             //Przycisk staje się niewidzialny
                                        mCancelBtn.setEnabled(false);                                         //Przycisk jest wyłączony

                                    }

                                    mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    mProgressDialog.dismiss();

                                }
                            });

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                //Co w przypadku errora
            }
        });

        mProfileInviteBtn.setOnClickListener(new View.OnClickListener() {                                   //Przycisk do zapraszania
            @Override
            public void onClick(View view) {

                mProfileInviteBtn.setEnabled(false);                                                        //Po tapnięciu na przycisk, wyłącza się on

                // ------------------ WYSYŁANIE ZAPROSZENIA - aktualny stan: "not_friends" -----------------------------------------------------------------------------------

                if(mCurrent_state.equals("not_friends")){

                    /*
                    Map requestMap = new HashMap();
                    requestMap.put(mCurrentUser.getUid() + "/" + user_id + "request_type", "sent");
                    requestMap.put(user_id + "/" + mCurrentUser.getUid() + "request_type", "received");

                    mFriendDatabase.updateChildren(requestMap);

                    */

                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                mFriendReqDatabase.child(user_id).child(mCurrentUser.getUid()).child("request_type")
                                    .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mCurrent_state = "req_sent";                                         //Zmiana statusu na "req_sent", z "not_friends", a cała cały if działa tylko dla "not_friends"
                                        mProfileInviteBtn.setText("Anuluj zaproszenie");                     //Zmiana tekstu na przycisku, po wysłaniu zaproszenia

                                        mCancelBtn.setVisibility(View.INVISIBLE);                             //Przycisk staje się niewidzialny
                                        mCancelBtn.setEnabled(false);                                         //Przycisk jest wyłączony

                                        Toast.makeText(ProfileActivity.this, "Wysłano zaproszenie do znajomych\n", Toast.LENGTH_SHORT).show();

                                    }
                                });


                            } else {

                                Toast.makeText(ProfileActivity.this, "Wysyłanie zaproszenia nie powiodło się", Toast.LENGTH_SHORT).show();

                            }

                            mProfileInviteBtn.setEnabled(true);                                  //Po wysłaniu zaproszenia, znowu włączam przycisk, nawet przy nieudanej próbie

                        }
                    });



                }

                // ------------------ ANULOWANIE WYSŁANEGO ZAPROSZENIA - aktualny stan: "req_sent" ----------------------------------------------------------------------------

                if(mCurrent_state.equals("req_sent")){

                    //Usuwam zaproszenie z current user (remove value usuwa wartość z klucza, a pusty klucz znika)
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                                mFriendReqDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mProfileInviteBtn.setEnabled(true);                                  //Po anulowaniu wysłania zaproszenia, znowu włączam przycisk
                                        mCurrent_state = "not_friends";                                      //Zmiana statusu z powrotem na "not_friends", z "req_sent"
                                        mProfileInviteBtn.setText("Zaproś do znajomych");                    //Zmiana tekstu na przycisku, po anulowaniu wysłania zaproszenia

                                        mCancelBtn.setVisibility(View.INVISIBLE);                             //Przycisk staje się niewidzialny
                                        mCancelBtn.setEnabled(false);                                         //Przycisk jest wyłączony

                                    }
                                });

                        }
                    });

                }


                // ----------------- OTRZYMANE ZAPROSZENIE - aktualny stan: "req_received"  - DODAWANIE DO ZNAJOMYCH W BAZIE ------------------------------------------------------------------

                if(mCurrent_state.equals("req_received")){

                    final String currentDate = DateFormat.getDateInstance().format(new Date());

                    //              ID (atualny użytkownik)     ID (inny użytkownik)
                   mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).child("acceptdate").setValue(currentDate)
                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {

                                   mFriendDatabase.child(user_id).child(mCurrentUser.getUid()).child("acceptdate").setValue(currentDate)
                                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                                               @Override
                                               public void onSuccess(Void aVoid) {

                                                   mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                       @Override
                                                       public void onSuccess(Void aVoid) {

                                                           mFriendReqDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                               @Override
                                                               public void onSuccess(Void aVoid) {

                                                                   mProfileInviteBtn.setEnabled(true);                                  //Po anulowaniu wysłania zaproszenia, znowu włączam przycisk
                                                                   mCurrent_state = "friends";                                          //Zmiana statusu z powrotem na "friends", z "req_received"
                                                                   mProfileInviteBtn.setText("Usuń ze znajomych");                      //Zmiana tekstu na przycisku, po przyjęciu zaproszenia

                                                                   mCancelBtn.setVisibility(View.INVISIBLE);                             //Przycisk staje się niewidzialny
                                                                   mCancelBtn.setEnabled(false);                                         //Przycisk jest wyłączony

                                                               }
                                                           });

                                                       }
                                                   });

                                               }
                                           });

                               }
                           });

                }

                // -------------------- USUWANIE ZE ZNAJOMYCH -----------------------------------------------------------------------------------------------------

                if(mCurrent_state.equals("friends")){

                    //Usuwam zaproszenie z current user (remove value usuwa wartość z klucza, a pusty klucz znika)
                    mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfileInviteBtn.setEnabled(true);                                  //Po anulowaniu wysłania zaproszenia, znowu włączam przycisk
                                    mCurrent_state = "not_friends";                                      //Zmiana statusu z powrotem na "not_friends", z "req_sent"
                                    mProfileInviteBtn.setText("Zaproś do znajomych");                    //Zmiana tekstu na przycisku, po anulowaniu wysłania zaproszenia

                                    mCancelBtn.setVisibility(View.INVISIBLE);                             //Przycisk staje się niewidzialny
                                    mCancelBtn.setEnabled(false);                                         //Przycisk jest wyłączony

                                }
                            });

                        }
                    });
                }

            }
        });

        // -------------------------- ODRZUCANIE ZAPROSZENIA DO ZNAJOMYCH ---------------------------------------------------------------------------------------

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mCurrent_state.equals("req_received")){

                    //Usuwam zaproszenie z current user (remove value usuwa wartość z klucza, a pusty klucz znika)
                    mFriendReqDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfileInviteBtn.setEnabled(true);                                  //Po anulowaniu wysłania zaproszenia, znowu włączam przycisk
                                    mCurrent_state = "not_friends";                                      //Zmiana statusu z powrotem na "not_friends", z "req_sent"
                                    mProfileInviteBtn.setText("Zaproś do znajomych");                    //Zmiana tekstu na przycisku, po anulowaniu wysłania zaproszenia

                                    mCancelBtn.setVisibility(View.INVISIBLE);                             //Przycisk staje się niewidzialny
                                    mCancelBtn.setEnabled(false);                                         //Przycisk jest wyłączony

                                }
                            });

                        }
                    });

                }

            }
        });

    }
}
