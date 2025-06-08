package com.example.gamingparts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://mydata-86e0f-default-rtdb.firebaseio.com/");

    EditText UserNameInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize inputs
        UserNameInput = findViewById(R.id.UserNameInput);
        passwordInput = findViewById(R.id.passwordInput);
    }

    public void loginbtn(View view) {
        String UserNameTxt = UserNameInput.getText().toString();
        String passwordTxt = passwordInput.getText().toString();

        if (UserNameTxt.isEmpty() || passwordTxt.isEmpty()) {
            Toast.makeText(Login.this, "Please enter your UserName or password", Toast.LENGTH_SHORT).show();
        } else {

            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(UserNameTxt)) {
                        String getPassword = snapshot.child(UserNameTxt).child("Password").getValue(String.class);
                        if (getPassword != null && getPassword.equals(passwordTxt)) {
                            SharedPreferences sharedPref = getSharedPreferences("UserData", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("username", UserNameTxt);
                            editor.apply();
                            Boolean isAdmin = snapshot.child(UserNameTxt).child("isAdmin").getValue(Boolean.class);
                            if (isAdmin) {
                                Toast.makeText(Login.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, AdminActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(Login.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, Sidebar.class);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(Login.this, "Wrong password", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(Login.this, "Wrong UserName", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    public void SignUpbtn(View view) {
        Intent intent = new Intent(Login.this, SignUp.class);
        startActivity(intent);
    }

}

