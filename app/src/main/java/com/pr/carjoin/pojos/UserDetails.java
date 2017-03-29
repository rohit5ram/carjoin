package com.pr.carjoin.pojos;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by vishnu on 29/3/17.
 */

public class UserDetails {
    private static final String LOG_LABEL = "pojos.UserDetails";

    public String id;
    public String name;
    public String email;
    public String photoUrl;

    public UserDetails(){}

    public UserDetails(FirebaseUser firebaseUser){
        this.name = firebaseUser.getDisplayName();
        this.email = firebaseUser.getEmail();
        this.photoUrl = String.valueOf(firebaseUser.getPhotoUrl());
    }
}
