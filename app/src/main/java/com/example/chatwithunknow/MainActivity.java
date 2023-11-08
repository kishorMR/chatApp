package com.example.chatwithunknow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button moveToAnotherActivityButton;
    private EditText etUsename;

    long currentTimeMillis = System.currentTimeMillis();
    String uniqueId = UUID.randomUUID().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        moveToAnotherActivityButton = findViewById(R.id.moveToAnotherActivityButton);
        etUsename = findViewById(R.id.usernameEditText);

        moveToAnotherActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = etUsename.getText().toString();

                // Create a reference to the Firebase Database
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot childdata : snapshot.getChildren()){
                                if(childdata.getChildrenCount() < 2){
                                    String currentTimestanmp = childdata.getKey();

                                    // Create a reference to the specific user node under the current timestamp
                                    DatabaseReference userRef = databaseRef.child(currentTimestanmp).child(uniqueId);
                                    // Set the username under the user node
                                    userRef.setValue(username);
                                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                                    intent.putExtra("timeStamp", currentTimestanmp);
                                    intent.putExtra("name",username);
                                    startActivity(intent);

                                }else{
                                    // Create a reference to the specific user node under the current timestamp
                                    DatabaseReference userRef = databaseRef.child(String.valueOf(currentTimeMillis)).child(uniqueId);
                                    // Set the username under the user node
                                    userRef.setValue(username);
                                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                                    intent.putExtra("timeStamp", currentTimeMillis);
                                    intent.putExtra("name",username);
                                    startActivity(intent);
                                }

                            }

                        }else {
                            // Create a reference t64+6o the specific user node under the current timestamp
                            DatabaseReference userRef = databaseRef.child(String.valueOf(currentTimeMillis)).child(uniqueId);
                            // Set the username under the user node
                            userRef.setValue(username);
                            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                            intent.putExtra("timeStamp", currentTimeMillis);
                            intent.putExtra("name",username);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }
}