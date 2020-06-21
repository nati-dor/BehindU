package com.example.behindu.view;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.developer.kalert.KAlertDialog;
import com.example.behindu.R;
import com.example.behindu.fragments.CallbackFragment;
import com.example.behindu.fragments.LoginFragment;
import com.example.behindu.fragments.RegisterFragment;
import com.example.behindu.model.User;
import com.example.behindu.util.SaveSharedPreference;

import org.json.JSONObject;

import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements CallbackFragment {

    private Fragment mFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);


        if(SaveSharedPreference.getUserName(MainActivity.this).length() == 0)
        {
            addFragment();
        }
        else
        {
            if(SaveSharedPreference.getIsFollower(MainActivity.this).equals("true")){
                moveToNewActivity(FollowerActivity.class);
            }
            else if(SaveSharedPreference.getIsFollower(MainActivity.this).equals("false")){
                moveToNewActivity(ChildActivity.class);
            }
        }


    }



    private void moveToNewActivity(Class userClass) {
        Intent i = new Intent(this, userClass);
        startActivity(i);
        this.finish();
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


