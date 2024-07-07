package com.example.chirpio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.chirpio.R;
import com.example.chirpio.databinding.FragmentFirstBinding;
import com.example.chirpio.databinding.FragmentFirstBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirstFragment extends Fragment {

    private FirebaseAuth auth=null;
    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        auth=FirebaseAuth.getInstance();
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        binding.btnlogin.setOnClickListener(view ->{
            String email=binding.etemail.getText().toString();
            String password=binding.etpassword.getText().toString();

            OnFailureListener failureListener=new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            };
            auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String uid=task.getResult().getUser().getUid();
                                Bundle data =new Bundle();
                                data.putString("uid",uid);
                                Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(view).navigate(R.id.action_firstFragment_to_postListFragment,data);
                            }
                            else{
                                Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(failureListener);
        });//btnlogin ends
        binding.btnsignup.setOnClickListener(view ->{
            Navigation.findNavController(view).navigate(R.id.action_firstFragment_to_secondFragment);
        });

        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}