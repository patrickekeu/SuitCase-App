package com.example.suitcaseapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.suitcaseapp.Adapter.ToDoAdapter;
import com.example.suitcaseapp.Helper.MyButtonClickListener;
import com.example.suitcaseapp.Helper.MySwipeHelper;
import com.example.suitcaseapp.R;
import com.example.suitcaseapp.Upload;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class viewToDoActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference todoRef = db.collection("holiday_items");

    private ToDoAdapter todoAdapter;
    Button logoutBtn, newItem, sendToBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_to_do);

        mAuth = FirebaseAuth.getInstance();

        setUpToDoRecyclerView();

        // Initialize UI elements and set click listeners

        logoutBtn = findViewById(R.id.logout);
        newItem = findViewById(R.id.new_item);

        newItem.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.logout) {
            // Sign out the user and navigate to the LoginActivity
            mAuth.signOut();
            Intent intent = new Intent(viewToDoActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.new_item) {
            // Navigate to the HomeActivity to add a new item
            Intent intent = new Intent(viewToDoActivity.this, HomeActivity.class);
            startActivity(intent);

        }

    }


    // Method to create a dialog for sending items to contacts
    private void createDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        int index = user.getEmail().indexOf("@");
        builder.setTitle("Send to");
        builder.setMessage("Who do you want to send it to " + user.getEmail().substring(0, index) + "?");
        final EditText input = new EditText(this);
        builder.setPositiveButton("Send", null);
        builder.setNegativeButton("Cancel", null);
        AlertDialog ad = builder.create();
        ad.show();
    }

    // Method to set up the RecyclerView for ToDo items
    private void setUpToDoRecyclerView() {
        // Query to retrieve ToDo items for the current user, ordered by time added
        Query todoQuery = todoRef.whereEqualTo("email", mAuth.getCurrentUser().getEmail()).orderBy("timeAdded", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<Upload> new_Pictures = new FirestoreRecyclerOptions.Builder<Upload>()
                .setQuery(todoQuery, Upload.class).build();

        todoAdapter = new ToDoAdapter(new_Pictures, this);

        // Set up the RecyclerView with the adapter
        RecyclerView todoRecyclerView = findViewById(R.id.recyclerView);
        todoRecyclerView.setHasFixedSize(true);
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoRecyclerView.setAdapter(todoAdapter);

        // Set up swipe actions using MySwipeHelper
        MySwipeHelper swipeHelper = new MySwipeHelper(this, todoRecyclerView, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(viewToDoActivity.this,
                        "Delete",
                        30,
                        0,
                        Color.parseColor("#FF3C30"),
                        ItemTouchHelper.LEFT,
                        new MyButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                Toast.makeText(viewToDoActivity.this, "Delete clicked", Toast.LENGTH_SHORT).show();
                                //delete item
                                todoAdapter.deleteItem(pos);
                            }
                        }));
                buffer.add(new MyButton(viewToDoActivity.this,
                        "Update",
                        30,
                        R.drawable.ic_edit_white_24dp,
                        Color.parseColor("#FF9502"),
                        ItemTouchHelper.RIGHT,
                        new MyButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                Toast.makeText(viewToDoActivity.this, "Update clicked", Toast.LENGTH_SHORT).show();
                                //edit item
                                todoAdapter.editItem(pos);
                            }
                        }));
            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        todoAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        todoAdapter.stopListening();
    }
}
