package com.example.behindu.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.behindu.R;
import com.example.behindu.fragments.CallbackFragment;
import com.example.behindu.fragments.LoginFragment;
import com.example.behindu.fragments.RegisterFragment;
import com.example.behindu.util.User;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements CallbackFragment {

    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);

       addFragment();
    }


    /*Add a new fragment*/

    public void addFragment(){
        LoginFragment fragment = new LoginFragment();
        fragment.setCallbackFragment(this);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragmentContainer,fragment);
        transaction.commit();
    }

    /*Replace between fragments*/

    public void replaceFragment(){
        fragment = new RegisterFragment();
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragmentContainer,fragment);
        transaction.commit();
    }

    @Override
    public void changeFragment() {
        replaceFragment();
    }

    public interface LogInActions{
        void LogInSuccessfully(User user);
        void LogInFailed();
    }
    public interface registerActions{
        void registerSucceed(boolean succeed);
    }
}


