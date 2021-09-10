package com.example.firstprogrammingassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String databaseURL = "https://first-project-2fb84-default-rtdb.firebaseio.com/";
    private DatabaseReference database = FirebaseDatabase.getInstance(databaseURL).getReference();
    private Button button;
    private Button resetButton;
    private TextView count;

    ValueEventListener countListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Integer countInt = dataSnapshot.getValue(Integer.class);
            Log.w("MainAct#", "count:" + countInt);
            count.setText(countInt.toString());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        count = findViewById(R.id.count);
        button = findViewById(R.id.button);
        resetButton = findViewById(R.id.button_reset);
        button.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        database.child("button_count").addValueEventListener(countListener);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.button:
                updateButtonClick(false);
                break;
            case R.id.button_reset:
                updateButtonClick(true);
        }
    }

    private void updateButtonClick(boolean reset) {
        if (reset) {
            database.child("button_count").setValue(0);
            return;
        }
        int curr_count = Integer.parseInt((String)count.getText());
        database.child("button_count").setValue(curr_count + 1);
    }

}