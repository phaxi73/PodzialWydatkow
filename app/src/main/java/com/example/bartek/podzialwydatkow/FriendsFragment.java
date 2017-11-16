package com.example.bartek.podzialwydatkow;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;
    private DatabaseReference mUsersDatabase;

    private DatabaseReference mFriendsDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;


    public FriendsFragment() {/* Required empty public constructor */}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));


        return mMainView;         // Inflate the layout for this fragment
    }

    public void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>

                (Friends.class,
                R.layout.users_single_layout,
                FriendsViewHolder.class,
                mFriendsDatabase)


        {

            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, final Friends friends, int position) {

                friendsViewHolder.setName(friends.getName());
                friendsViewHolder.setUserImage(friends.getThumb_image(), getContext());
                friendsViewHolder.setEmail(friends.getEmail());


                final String list_user_id= getRef(position).getKey();       //Do przekazania do dalszej klasy w razie potrzeby


                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userName = dataSnapshot.child("name").getValue().toString();
                        String setUserImage = dataSnapshot.child("thumb_image").getValue().toString();
                        String userEmail = dataSnapshot.child("email").getValue().toString();

                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setUserImage(setUserImage, getContext());
                        friendsViewHolder.setEmail(userEmail);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };

        mFriendsList.setAdapter((friendsRecyclerViewAdapter));


    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FriendsViewHolder(View itemView){
            super(itemView);

            mView = itemView;

        }

        public void setEmail(String email){

            TextView userNameView = mView.findViewById(R.id.user_single_email);
            userNameView.setText(email);

        }


        public void setName(String name){

            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setUserImage (String thumb_image, Context ctx){

            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.account_icon_orange).into(userImageView);

        }

    }


}














