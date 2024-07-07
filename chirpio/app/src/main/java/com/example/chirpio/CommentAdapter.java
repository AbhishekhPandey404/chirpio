package com.example.chirpio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Comments> commentlist;
    private FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    public CommentAdapter(ArrayList<Comments>list){
        commentlist =list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View v=inflater.inflate(R.layout.recvu_row_commentlist,parent,false);
        MyViewHolder mvh=new MyViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder mvh=(MyViewHolder)holder;
        Comments c=commentlist.get(position);
        mvh.tv1.setText(c.getComment());
        mvh.tv2.setText(c.getDate());
    }
    @Override
    public int getItemCount() {
        return commentlist.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv1, tv2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.prev_cmt_thought);
            tv2 = itemView.findViewById(R.id.prev_cmt_date);
        }
    }
}