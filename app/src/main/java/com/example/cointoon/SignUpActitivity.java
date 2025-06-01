package com.example.cointoon;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.cointoon.databinding.ActivitySignUpActitivityBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActitivity extends AppCompatActivity {

    ActivitySignUpActitivityBinding binding;
    FirebaseAuth auth;
    DatabaseReference mDatabase;

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpActitivityBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // From google-services.json
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignUpButton();
        click();
        createAccount();
    }

    private  void googleSignUpButton(){
        binding.googleSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });


    }

    private void signInWithGoogle() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String name = user.getDisplayName();
                            String email = user.getEmail();
                            String uid = user.getUid();

                            // Save name and email to Realtime Database
                            User userObj = new User(name, email); // your existing User model
                            mDatabase.child(uid).setValue(userObj);
                        }

                        startActivity(new Intent(SignUpActitivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Firebase Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void click(){
        binding.loginPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActitivity.this , Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void createAccount(){

        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            registerUser();
            }
        });
    }

    private void registerUser() {
        String name = binding.userName.getText().toString().trim();
        String email = binding.userEmail.getText().toString().trim();
        String password = binding.userPassword.getText().toString().trim();

        // if user did not fill any data
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // creating user account using name , email and password
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = auth.getCurrentUser().getUid();
                        User user = new User(name, email); // custom user model class
                        Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                        mDatabase.child(uid).setValue(user).addOnCompleteListener(dbTask -> {
                            if (dbTask.isSuccessful()) {
                                Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActitivity.this, MainActivity.class));
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}