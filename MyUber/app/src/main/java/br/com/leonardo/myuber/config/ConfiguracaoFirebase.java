package br.com.leonardo.myuber.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {

    private static DatabaseReference databaseRef;
    private static FirebaseAuth auth;

    public static DatabaseReference getFirebaseDatabase(){

        if (databaseRef == null){
            databaseRef = FirebaseDatabase.getInstance().getReference();
        }
        return databaseRef;
    }

    public static FirebaseAuth getFirebaseAuth(){

        if (auth == null){
            auth = FirebaseAuth.getInstance();
        }

        return auth;
    }

}
