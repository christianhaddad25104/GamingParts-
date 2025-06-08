package com.example.gamingparts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://mydata-86e0f-default-rtdb.firebaseio.com/");
    EditText UserNameInput, emailInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        UserNameInput = findViewById(R.id.UserNameInput);
        emailInput = findViewById(R.id.emailInput2);
        passwordInput = findViewById(R.id.passwordInput);
    }

    public void registerUser(View view) {
        String usernameTxt = UserNameInput.getText().toString();
        String emailTxt = emailInput.getText().toString();
        String passwordTxt = passwordInput.getText().toString();

        if (usernameTxt.isEmpty() || emailTxt.isEmpty() || passwordTxt.isEmpty()) {
            Toast.makeText(SignUp.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(usernameTxt)) {
                        Toast.makeText(SignUp.this, "UserName is already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        databaseReference.child("users").child(usernameTxt).child("email").setValue(emailTxt);
                        databaseReference.child("users").child(usernameTxt).child("Password").setValue(passwordTxt);
                        databaseReference.child("users").child(usernameTxt).child("isAdmin").setValue(false);
                        Toast.makeText(SignUp.this, "User registered successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
}
