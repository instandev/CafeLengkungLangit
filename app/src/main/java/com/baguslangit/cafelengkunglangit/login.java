package com.baguslangit.cafelengkunglangit;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class login extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    ViewLoadingDialog loadingDialog;

    TextView email1, pasword1, login;
    String email, pasword, id, namaid;




    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        loadingDialog = new ViewLoadingDialog(login.this);
        mAuth = FirebaseAuth.getInstance();
        email1 = (TextView) findViewById(R.id.nama);
        pasword1 = (TextView) findViewById(R.id.pasword);
        login = (TextView) findViewById(R.id.login);
        //email1.setText("ririnistiani97@gmail.com");
        //pasword1.setText("lengkungyurin789");


        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);


        mAuth = FirebaseAuth.getInstance();


        // User is signed in
        // User is signed out
        // ...
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if(user.getEmail().equals("admin@ruangawi.com")){
                        FirebaseAuth.getInstance().signOut();
                    }else {
                        // User is signed in
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                        toastMessage("Successfully signed in with: " + user.getEmail());
                        Intent intent = new Intent(login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    // User is signed out
                    if (checkInternet()) {
                        toastMessage("Successfully signed out.");
                        loadingDialog.dismiss();
                    } else {
                        Toast.makeText(login.this, "Tidak Ada Internet",Toast.LENGTH_LONG).show();
                        loadingDialog.dismiss();
                    }
                }
                // ...
            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.setPesan("Mohon Tunggu Sebentar");
                loadingDialog.show();
                email = email1.getText().toString();
                pasword = pasword1.getText().toString();
                if(!email.equals("") && !pasword.equals("")){
                    Login(email,pasword);
                }else{
                    toastMessage("You didn't fill in all the fields.");
                }

            }
        });



    }

    public void setJumlahTransaksi() {
        Map<String, Object> user = new HashMap<>();
        user.put("status-order", "print");
        user.put("strnama", "");
        db.collection(namaid)
                .document(id)
                .update(user)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d(TAG, "DocumentSnapshot added with ID: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void Login(String email, String pasword){
        mAuth.signInWithEmailAndPassword(email,pasword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    loadingDialog.dismiss();
                    //id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    //namaid = Objects.requireNonNull(mAuth.getCurrentUser().getEmail());
                    //setJumlahTransaksi();
                    Intent intent = new Intent(login.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Toast.makeText(login.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    if (checkInternet()) {
                        Toast.makeText(login.this, "Cek Internet",Toast.LENGTH_LONG).show();
                        loadingDialog.dismiss();
                    } else {
                        Toast.makeText(login.this, "Tidak Ada Internet",Toast.LENGTH_LONG).show();
                        loadingDialog.dismiss();
                    }
                    loadingDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            if(user.getEmail().equals("admin@ruangawi.com")){
                FirebaseAuth.getInstance().signOut();
            }else {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();}
        }else {
            if (checkInternet()) {
                Toast.makeText(this, "Login Dulu",Toast.LENGTH_LONG).show();
                loadingDialog.dismiss();
            } else {
                Toast.makeText(this, "Tidak Ada Internet",Toast.LENGTH_LONG).show();
                loadingDialog.dismiss();
            }
        }
    }

    public boolean checkInternet() {
        boolean connectStatus = true;
        ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
        connectStatus = networkInfo != null && networkInfo.isConnected();
        return connectStatus;
    }

    private void setupFloatingLabelError() {
        final TextInputLayout floatingUsernameLabel = (TextInputLayout) findViewById(R.id.username_text_input_layout);
        final TextInputLayout floatingUsernameLabel1 = (TextInputLayout) findViewById(R.id.username_text_input_layout1);
        Objects.requireNonNull(floatingUsernameLabel.getEditText()).addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 4) {
                    floatingUsernameLabel.setError(getString(Integer.parseInt("Email")));
                    floatingUsernameLabel.setErrorEnabled(true);
                } else {
                    floatingUsernameLabel.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Objects.requireNonNull(floatingUsernameLabel1.getEditText()).addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 4) {
                    floatingUsernameLabel1.setError(getString(Integer.parseInt("Jumlah Tiket")));
                    floatingUsernameLabel1.setErrorEnabled(true);
                } else {
                    floatingUsernameLabel1.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    //add a toast to show when successfully signed in
    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 2000);
    }

}