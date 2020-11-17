package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    String userName ,otherName ;
    TextView chatUserName ;
    EditText chatEditText;
    ImageView backImage,sendImage;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    RecyclerView chatRecView;
    MesajAdapter mesajAdapter;
    List<MesajModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        tanimla();
        loadMesaj();
    }

    private void tanimla() {
        list=new ArrayList<MesajModel>();
        userName=getIntent().getExtras().getString("userName");
        otherName=getIntent().getExtras().getString("otherName");
        Log.v("**************",userName+otherName);
        chatUserName=findViewById(R.id.chatUserName);
        chatEditText=findViewById(R.id.chatEditText);
        backImage=findViewById(R.id.backImage);
        sendImage=findViewById(R.id.sendImage);
        chatUserName.setText(otherName);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChatActivity.this,MainActivity.class);
                intent.putExtra("kAdi",userName);
                startActivity(intent);
            }
        });
        firebaseDatabase=FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference();
        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mesaj=chatEditText.getText().toString();
                chatEditText.setText("");
                mesajGonder(mesaj);
            }


        });
        chatRecView=findViewById(R.id.chatRecView);
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(ChatActivity.this,1);
        chatRecView.setLayoutManager(layoutManager);
        mesajAdapter=new MesajAdapter(ChatActivity.this,list,ChatActivity.this,userName);
        chatRecView.setAdapter(mesajAdapter);


    }

    public void mesajGonder(String text) {
        final String key=reference.child("Mesajlar").child(userName).child(otherName).push().getKey();
        Log.v("key",key);
        final Map messageMap=new HashMap();
        messageMap.put("text",text);
        messageMap.put("from",userName);
        reference.child("Mesajlar").child(userName).child(otherName).child(key).setValue(messageMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            reference.child("Mesajlar").child(otherName).child(userName).child(key)
                                    .setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }

                    }
                });
    }

    private void loadMesaj() {
        reference.child("Mesajlar").child(userName).child(otherName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MesajModel mesajModel=dataSnapshot.getValue(MesajModel.class);

                list.add(mesajModel);
                mesajAdapter.notifyDataSetChanged();
                chatRecView.scrollToPosition(list.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}