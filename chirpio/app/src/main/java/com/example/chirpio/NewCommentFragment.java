package com.example.chirpio;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chirpio.databinding.FragmentFirstBinding;
import com.example.chirpio.databinding.FragmentNewCommentBinding;
import com.example.chirpio.databinding.FragmentNewPostBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewCommentFragment extends Fragment {
    private FirebaseFirestore firestore=null;
    private String id;
    private FirebaseAuth auth;
    FragmentNewCommentBinding binding = null;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public NewCommentFragment() {
        // Required empty public constructor
    }

    public static NewCommentFragment newInstance(String param1, String param2) {
        NewCommentFragment fragment = new NewCommentFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewCommentBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        Bundle data = getArguments();
        id = data.getString("id");
        Log.i("newcommentfragment", "onCreateView: TID " + id);

        binding.btnPostCmt.setOnClickListener(view -> {
            String postComment = binding.postCmt.getText().toString().trim();
            if (postComment.isEmpty()) {
                Toast.makeText(getActivity(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

                Map<String, Object> document = new HashMap<>();
                document.put("cdate", currentDate);
                document.put("ctext", postComment);

                firestore.collection("ThoughtMaster").document(id)
                        .collection("comments").document()
                        .set(document)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Comment Posted: ", Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(requireView()).popBackStack();
                                } else {
                                    Toast.makeText(getActivity(), "User is not logged in", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
