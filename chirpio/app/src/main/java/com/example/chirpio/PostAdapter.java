package com.example.chirpio;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Posts> postlist;
    private FirebaseAuth auth;
    private static FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseFirestore thoughtMasterFirestore = FirebaseFirestore.getInstance();
    private static Set<String> likedPostIds = new HashSet<>();

    public PostAdapter(ArrayList<Posts> list) {
        postlist = list;
        auth = FirebaseAuth.getInstance();
        likedPostIds = new HashSet<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.recvu_row_postlist, parent, false);
        MyViewHolder mvh = new MyViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder mvh = (MyViewHolder) holder;
        Posts p = postlist.get(position);
        mvh.tv1.setText(p.getName());
        mvh.tv2.setText(p.getDate());
        mvh.tv3.setText(p.getPost());
        mvh.tv4.setText(String.valueOf(p.getLike_count()));

        mvh.tv6.setText(p.getId()); // thought id
        mvh.tv7.setText(p.getUser_id()); // user id

        // Disable the like button if the post is already liked
        if (likedPostIds.contains(p.getId())) {
            mvh.btnlikecount.setEnabled(false);
        } else {
            mvh.btnlikecount.setEnabled(true);
        }
    }

    @Override
    public int getItemCount() {
        return postlist.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv1, tv2, tv3, tv4, tv6, tv7;
        public Button btnlikecount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.tv_recvu_username);
            tv2 = itemView.findViewById(R.id.tv_recvu_thought);
            tv3 = itemView.findViewById(R.id.tv_recvu_date);
            tv4 = itemView.findViewById(R.id.tv_postlistlikecount);
            tv6 = itemView.findViewById(R.id.tvid); // thought id
            tv7 = itemView.findViewById(R.id.tv_post_id); // user id
            btnlikecount = itemView.findViewById(R.id.btnpostlistlikecount);

            btnlikecount.setOnClickListener(view -> {
                String id = tv6.getText().toString();
                String uid = tv7.getText().toString();
                if (!likedPostIds.contains(id)) {
                    DocumentReference docref = firestore.collection("ThoughtMaster").document(tv6.getText().toString());
                    Log.i("post adapter thought master", "MyViewHolder: " + tv6.getText().toString());
                    docref.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot ds = task.getResult();
                            if (ds != null && ds.exists()) {
                                Long likes = ds.getLong("likecount");
                                if (likes != null) {
                                    long updatedLikes = likes + 1;
                                    docref.update("likecount", updatedLikes)
                                            .addOnSuccessListener(aVoid -> {
                                                likedPostIds.add(id);
                                                btnlikecount.setEnabled(false);
                                            })
                                            .addOnFailureListener(e -> Log.i("FIRETAG", "Failed to update like count", e));
                                } else {
                                    Log.i("FIRETAG", "Likes count is null");
                                }
                            } else {
                                Log.i("FIRETAG", "Document doesn't exist");
                            }
                        } else {
                            Log.i("FIRETAG", "Failed to get document", task.getException());
                        }
                    });
                }
            });

            itemView.setOnClickListener(view -> {
                Bundle data = new Bundle();
                data.putString("id", tv6.getText().toString());
                Navigation.findNavController(view).navigate(R.id.action_postListFragment_to_commentListFragment, data);
            });
        }
    }
}
