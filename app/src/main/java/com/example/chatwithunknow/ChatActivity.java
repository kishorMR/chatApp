package com.example.chatwithunknow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    Button send;
    ListView chatList;
    EditText textBox;
    private DatabaseReference databaseReference;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        send = findViewById(R.id.btnSend);
        chatList = findViewById(R.id.listViewChat);
        textBox = findViewById(R.id.etMessage);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        String timeStamp = getIntent().getStringExtra("timeStamp");
        String name = getIntent().getStringExtra("name");

        adapter = new ArrayAdapter<>(ChatActivity.this, android.R.layout.simple_list_item_1);
        chatList.setAdapter(adapter);

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("users").child(timeStamp).child("chat");

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    adapter.clear();
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String data = childSnapshot.getValue().toString();
                        String name1 = childSnapshot.getKey();
                        String Cha1 = name1.substring(13) + " : " + data;
                        adapter.add(Cha1);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String chat = textBox.getText().toString();

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                if(childSnapshot.getChildrenCount() >= 2 && Objects.equals(timeStamp, childSnapshot.getKey())){
                                    databaseReference.child(timeStamp).child("chat").child(System.currentTimeMillis() + name).setValue(chat);
                                }else {
                                    Toast.makeText(ChatActivity.this, "Still finding for Stranger", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("If you exit all your Chat will disappear");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Finish the activity and exit the app
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(Objects.requireNonNull(getIntent().getStringExtra("timeStamp")));
                databaseReference.removeValue();
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Dismiss the dialog, and don't exit the app
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}