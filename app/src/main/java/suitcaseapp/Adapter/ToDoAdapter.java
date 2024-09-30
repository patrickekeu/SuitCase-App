package com.example.suitcaseapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.suitcaseapp.Activities.viewToDoActivity;
import com.example.suitcaseapp.R;
import com.example.suitcaseapp.Upload;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public class ToDoAdapter extends FirestoreRecyclerAdapter<Upload, ToDoAdapter.ToDoHolder> {

    private Context mContext;
    private List<Upload> mUploads;
    private DatabaseReference mDatabaseRef;
    private AdapterView.OnItemClickListener onItemClickListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
/*

    public ToDoAdapter(@NonNull FirestoreRecyclerOptions<ToDoItem> options, Context context) {
        super(options);
        mContext = context;
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Photo Uploads");
    }*/

    public ToDoAdapter(@NonNull FirestoreRecyclerOptions<Upload> uploads, Context context)
    {
        super(uploads);
        mContext = context;

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Photo Uploads");
    }

    @NonNull
    @Override
    public ToDoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.activity_to_do_item, parent, false);
        return new ToDoHolder(v);
    }




    @Override
    protected void onBindViewHolder(@NonNull ToDoHolder holder, int position, @NonNull Upload model) {
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDesc());
        holder.price.setText(model.getPrice());
        holder.checkBox.setChecked(model.isPurchased());
        //String imageUrl = model.getImage();

        // Handle checkbox click events
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the purchase status in the Firestore database
            updatePurchaseStatus(position, isChecked);
        });



            // Load image using Picasso or your preferred image loading library
            Glide.with(mContext)
                    .load(model.getImage())
                    .placeholder(R.drawable.default_pic)
                    .error(R.drawable.default_pic)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("ToDoAdapter", "Failed to load image", e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("ToDoAdapter", "Image loaded successfully");
                            return false;
                        }
                    })
                    .into(holder.photo);

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(model.getTimeAdded().getSeconds() * 1000);
        holder.dateAdded.setText(timeAgo);
        holder.sendToBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String txt_message = "Hey, I'm going on holiday! I need you to buy " + model.getTitle() + " that costs " + model.getPrice() + ". It is " + model.getDesc() + ". Thanks!";
                int index = user.getEmail().indexOf("@");
                builder.setTitle("Send to");
                builder.setMessage("Who do you want to send it to " + user.getEmail().substring(0, index) + "?");
                final EditText input = new EditText(mContext);

                builder.setView(input).setPositiveButton("Send", ((dialog, id) -> {
                    String sendTo = input.getText().toString();
                    if(!isNumeric(sendTo))
                    {
                        Toast.makeText(mContext, "Please enter a valid phone number without dashes or parentheses", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("sms:" + sendTo));
                    intent.putExtra("sms_body", txt_message);
                    mContext.startActivity(intent);

                })).setNegativeButton("Cancel", null);
                AlertDialog ad = builder.create();
                ad.show();
            }
        });
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void updatePurchaseStatus(int position, boolean isChecked) {
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
        String documentId = snapshot.getId();

        // Reference to the document in the "holiday_items" collection
        DocumentReference documentReference = db.collection("holiday_items").document(documentId);

        // Update the "purchased" field in the document
        documentReference
                .update("purchased", isChecked)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Successfully updated the purchase status
                        Log.d("ToDoAdapter", "Purchase status updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to update purchase status
                        Log.e("ToDoAdapter", "Failed to update purchase status", e);
                    }
                });
    }


    public void deleteItem(int position) {
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
        db.collection("holiday_items").document(snapshot.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Item deleted successfully

                        notifyItemRemoved(position);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle deletion failure
                        Log.e("ToDoAdapter", "Failed to delete item", e);
                    }
                });
    }

    public void editItem(int position) {
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
        String documentId = snapshot.getId();

        // Reference to the document in the "holiday_items" collection
        DocumentReference documentReference = db.collection("holiday_items").document(documentId);

        // Get the existing data to pre-fill the dialog
        String title = snapshot.getString("title");
        String desc = snapshot.getString("desc");
        String price = snapshot.getString("price");
        Timestamp timeAdded = snapshot.getTimestamp("timeAdded");

        // Create and show the dialog for editing
        showEditDialog(documentReference, title, desc, price, timeAdded);
    }

    private void showEditDialog(DocumentReference documentReference, String currentTitle, String currentDesc, String currentPrice, Timestamp currentTimeAdded) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("Edit Item");

        View view = LayoutInflater.from(mContext).inflate(R.layout.edit_item_dialog, null);
        builder.setView(view);

        EditText editTitle = view.findViewById(R.id.editTitle);
        EditText editDesc = view.findViewById(R.id.editDesc);
        EditText editPrice = view.findViewById(R.id.editPrice);

        // Pre-fill the dialog with existing data
        editTitle.setText(currentTitle);
        editDesc.setText(currentDesc);
        editPrice.setText(currentPrice);

        builder.setPositiveButton("Save", (dialog, which) -> {
            // Get the edited values
            String editedTitle = editTitle.getText().toString().trim();
            String editedDesc = editDesc.getText().toString().trim();
            String editedPrice = editPrice.getText().toString().trim();

            Timestamp editedTime = new Timestamp(new Date());


            // Update the document with edited values
            documentReference
                    .update("title", editedTitle, "desc", editedDesc, "price", editedPrice, "timeAdded", editedTime)
                    .addOnSuccessListener(aVoid -> {
                        // Successfully updated the item
                        Toast.makeText(mContext, "Item updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to update item
                        Log.e("ToDoAdapter", "Failed to update item", e);
                        Toast.makeText(mContext, "Failed to update item", Toast.LENGTH_SHORT).show();
                    });
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public class ToDoHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView title;
        TextView description;
        TextView dateAdded;
        TextView price;
        ImageView photo;

        Button sendToBtn;

        public ToDoHolder(@NonNull View itemView) {
            super(itemView);
            dateAdded = itemView.findViewById(R.id.dateAdded);
            title = itemView.findViewById(R.id.todoTitle);
            description = itemView.findViewById(R.id.todoDesc);
            price = itemView.findViewById(R.id.todoPrice);
            photo = itemView.findViewById(R.id.item_image);
            checkBox = itemView.findViewById(R.id.checkBox);
            sendToBtn = itemView.findViewById(R.id.send_to);


        }
    }
}

