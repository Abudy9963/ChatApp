package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GirisActivity extends AppCompatActivity {
    EditText kullanciAdiEditText;
    Button kayitOlButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);
        tanimla();
    }

    private void tanimla() {
        kullanciAdiEditText=findViewById(R.id.kullanciAdiEditText);
        kayitOlButton=findViewById(R.id.kayitOlButton);
        firebaseDatabase=FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference();

        kayitOlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName=kullanciAdiEditText.getText().toString();
                kullanciAdiEditText.setText("");
                ekle(userName);
            }
        });
    }
    private void ekle(final String userName) {
        reference.child("kullancilar").child(userName).child("kullancilar").setValue(userName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(GirisActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(GirisActivity.this,MainActivity.class);
                            intent.putExtra("kAdi",userName);
                            startActivity(intent);
                        }
                    }
                });


    }
}