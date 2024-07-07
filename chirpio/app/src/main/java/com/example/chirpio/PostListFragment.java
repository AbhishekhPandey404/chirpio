package com.example.chirpio;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chirpio.PostAdapter;
import com.example.chirpio.Posts;
import com.example.chirpio.databinding.FragmentPostListBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class PostListFragment extends Fragment {
    private FirebaseFirestore firestore = null;
    private CollectionReference post_collection = null;
    private FragmentPostListBinding binding = null;
    private ArrayList<Posts> list = new ArrayList<>();
    private ListenerRegistration listenerRegistration;
    private PostAdapter adapter = null;
    private RecyclerView.LayoutManager layoutManager = null;
    private boolean isLayoutManagerSet = false;

    public PostListFragment() {
        // Required empty public constructor
    }

    public static PostListFragment newInstance() {
        return new PostListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPostListBinding.inflate(inflater, container, false);
        adapter = new PostAdapter(list);
        binding.recvuPostList.setAdapter(adapter);
        binding.btnewpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.action_postListFragment_to_newPostFragment, null);
            }
        });

        // Check if the LayoutManager is already set
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recvuPostList.setLayoutManager(layoutManager);  /*This thing fixed the blank post_list_fragment*/

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("PostListFragment", "onResume: ######");
        post_collection = firestore.collection("ThoughtMaster");

        listenerRegistration = post_collection.orderBy("username")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.i("FIRE_BASE_TAG", "###### ERROR:" + error.getMessage());
                        // Handle error
                        return;
                    }
                    List<DocumentSnapshot> docs = value.getDocuments();
                    Log.i("FIRETASK", "#########  onResume: ######## " + docs.size());
                    if (docs.size() > 0) {
                        list.clear();
                    }

                    for (DocumentSnapshot doc : docs) {
                        String id = doc.getId();
                        Log.i("FOR LOOP", "THOUGHT ID "+id);
                        String name = doc.getString("username");
                        String date = doc.getString("dt");
                        String post = doc.getString("post");
                        long likeCount = doc.getLong("likecount");

                        Posts p = new Posts(name, post, date, likeCount);
                        p.setId(id);
                        list.add(p);
                    }//for ends
                    adapter.notifyDataSetChanged();
                });
    }
    @Override
    public void onPause() {
        super.onPause();
        listenerRegistration.remove();
    }
}
