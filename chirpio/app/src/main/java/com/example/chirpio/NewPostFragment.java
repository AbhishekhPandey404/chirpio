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

import com.example.chirpio.databinding.FragmentNewPostBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewPostFragment extends Fragment {
    private FirebaseFirestore firestore = null;
    private FirebaseAuth auth;
    private FragmentNewPostBinding binding = null;
    private FirebaseFirestore thoughtMasterFirestore = FirebaseFirestore.getInstance();

    public NewPostFragment() {
        // Required empty public constructor
    }

    public static NewPostFragment newInstance() {
        return new NewPostFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewPostBinding.inflate(inflater, container, false);
        binding.btnPost.setOnClickListener(view -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                String username = currentUser.getDisplayName();
                String postText = binding.edtpost.getText().toString().trim();
                if (postText.isEmpty()) {
                    Toast.makeText(getActivity(), "Post cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

                    Map<String, Object> document = new HashMap<>();
                    document.put("likecount", 0);
                    document.put("dt", currentDate);
                    document.put("username", username);
                    document.put("post", postText);

                    firestore.collection("users").document(userId).collection("thoughts").document().set(document)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentReference thoughtRef = firestore.collection("users").document(userId).collection("thoughts").document();
                                    String thought_Id=thoughtRef.getId();
                                    thoughtMasterFirestore.collection("ThoughtMaster").document(thought_Id).set(document)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.i("NewPostFragment", "userId: " + userId);
                                                Log.i("NewPostFragment", "id: " + thought_Id);
                                                Toast.makeText(getActivity(), "Thought Posted", Toast.LENGTH_SHORT).show();
                                                Navigation.findNavController(view).navigate(R.id.action_newPostFragment_to_postListFragment);
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getActivity(), "Failed to post thought: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Toast.makeText(getActivity(), "Failed to post thought: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(getActivity(), "User is not logged in", Toast.LENGTH_SHORT).show();
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
