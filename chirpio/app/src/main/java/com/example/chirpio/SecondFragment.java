package com.example.chirpio;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.chirpio.databinding.FragmentSecondBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SecondFragment extends Fragment {
    private FirebaseAuth auth = null;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        binding.btnregister.setOnClickListener(view -> {
            String email = binding.etsignupEmail.getText().toString();
            if (email == null || email.length() == 0) {
                Toast.makeText(getActivity(), "Email must not be blank", Toast.LENGTH_SHORT).show();
                return;
            }

            String pass = binding.etsignupPassword.getText().toString();
            String confpass = binding.etsignupConfirmpassword.getText().toString();
            if (!(pass.equals(confpass))) {
                Toast.makeText(getActivity(), "Password does not match", Toast.LENGTH_SHORT).show();
                return;
            }
            String dispname = binding.etsignupDisplayname.getText().toString();
            OnCompleteListener<AuthResult> completeListener = task -> {
                if (task.isSuccessful()) {
                    // User creation success
                    sendDisplayNameToFirestore(dispname);
                    Navigation.findNavController(view).navigate(R.id.action_secondFragment_to_firstFragment);
                    Map<String,Object> data=new HashMap<>();
                    String uid=task.getResult().getUser().getUid();
                    firestore.collection("users").document(uid).set(data)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Log.i("FIRETAG", "onComplete: user date insert success ####################");
                                            }
                                        }
                                    });
                    Toast.makeText(getActivity(), "User creation done", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Some Unknown Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

            auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(completeListener);
        });

        return binding.getRoot();
    }

    private void sendDisplayNameToFirestore(String displayName) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

        auth.getCurrentUser().updateProfile(changeRequest)
                .addOnSuccessListener(aVoid -> {
                    // Update the Firestore document with the display name
                    String userId = auth.getCurrentUser().getUid();
                    firestore.collection("users").document(userId)
                            .update("username", displayName)
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(getActivity(), "Display name sent to Firestore", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Failed to send display name to Firestore", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to update display name in Firebase", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
