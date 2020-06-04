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
import com.example.behindu.model.User;


public class MainActivity extends AppCompatActivity implements CallbackFragment {

    private Fragment mFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;

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
        mFragmentManager = getSupportFragmentManager();
        mTransaction = mFragmentManager.beginTransaction();
        mTransaction.add(R.id.fragmentContainer,fragment);
        mTransaction.commit();
    }

    /*Replace between fragments*/

    public void replaceFragment(){
        mFragment = new RegisterFragment();
        mFragmentManager = getSupportFragmentManager();
        mTransaction = mFragmentManager.beginTransaction();
        mTransaction.addToBackStack(null);
        mTransaction.replace(R.id.fragmentContainer,mFragment);
        mTransaction.commit();
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


