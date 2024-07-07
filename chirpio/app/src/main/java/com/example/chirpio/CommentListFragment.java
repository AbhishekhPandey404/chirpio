package com.example.chirpio;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import com.example.chirpio.databinding.FragmentCommentListBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentListFragment extends Fragment {
    private String id;

    private FirebaseFirestore firestore = null;
    private CollectionReference post_collection = null;
    private FragmentCommentListBinding binding = null;
    private ArrayList<Comments> c_list = new ArrayList<>();
    private ListenerRegistration listenerRegistration;
    private CommentAdapter adapter = null;
    private RecyclerView.LayoutManager manager = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CommentListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommentListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommentListFragment newInstance(String param1, String param2) {
        CommentListFragment fragment = new CommentListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCommentListBinding.inflate(inflater, container, false);
        adapter = new CommentAdapter(c_list);
        manager = new LinearLayoutManager(getActivity());

        Bundle data = getArguments();
        id = data.getString("id");
        Log.i("FIRETAG", "onCreateView: TID " + id);

        binding.recvuCommentList.setAdapter(adapter);
        binding.recvuCommentList.setLayoutManager(manager);
        firestore = FirebaseFirestore.getInstance();

        binding.btncmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                Navigation.findNavController(requireView()).navigate(R.id.action_commentListFragment_to_newCommentFragment, bundle);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("FIRE", "onResume: commentlist ########## ");
        Log.i("FIRE", "========================" + id);

        listenerRegistration = firestore.collection("ThoughtMaster")
                .document(id)
                .collection("comments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.i("FIRE", "onEvent: ERROR " + error.getMessage());
                            return;
                        }
                        c_list.clear();
                        for (DocumentSnapshot ds : value.getDocuments()) {
                            //Log.i("FIRE", "onEvent: comment : " + ds.getString("ctext"));
                            String text = ds.getString("ctext");
                            String dt = ds.getString("cdate");
                            Comments comment = new Comments(dt, text);
                            c_list.add(comment);
                            Log.i("Comment list", "####### ONE RECORD ADDED ######");
                        }//for ends
                        adapter.notifyDataSetChanged();
                    }
                });
    }

//    private void navigateToNewCommentFragment() {
//        Bundle bundle = new Bundle();
//        bundle.putString("id", id);
//        Navigation.findNavController(requireView()).navigate(R.id.action_commentListFragment_to_newCommentFragment, bundle);
//    }
}
