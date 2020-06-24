package com.example.behindu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.developer.kalert.KAlertDialog;
import com.example.behindu.R;
import com.example.behindu.model.Child;
import com.example.behindu.model.Follower;
import com.example.behindu.model.User;
import com.example.behindu.view.ChildActivity;
import com.example.behindu.view.FollowerActivity;
import com.example.behindu.view.MainActivity;
import com.example.behindu.viewmodel.MainActivityViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterFragment extends Fragment {

    private MainActivityViewModel viewModel = new MainActivityViewModel();
    private TextInputLayout mFirstNameError;
    private TextInputLayout mLastNameError;
    private TextInputLayout mEmailError;
    private TextInputLayout mPasswordError;
    private TextInputLayout mPhoneNumberError;
    private int mFollowerPhoneNum;
    private KAlertDialog mDialog;

    public RegisterFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registration,container,false);

        mFirstNameError = view.findViewById(R.id.first_name_layout_registration);
        mLastNameError = view.findViewById(R.id.last_name_layout_registration);
        mEmailError = view.findViewById(R.id.email_layout_registration);
        mPasswordError = view.findViewById(R.id.password_layout_registration);
        mPhoneNumberError = view.findViewById(R.id.phone_number_layout_registration);


        final EditText firstNameEt = view.findViewById(R.id.firstNameInput_register);
        final EditText lastNameEt = view.findViewById(R.id.lastNameInput_register);
        final EditText emailEt = view.findViewById(R.id.emailInput_register);
        final EditText passwordEt = view.findViewById(R.id.passwordInput_register);
        final EditText mFollowerPhoneNumEt = view.findViewById(R.id.follower_phoneNum_Et);
        final CheckBox followerCb = view.findViewById(R.id.follower_Cb);


        Button registerBtn = view.findViewById(R.id.createUserBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String firstName = firstNameEt.getText().toString().trim();
                String lastName = lastNameEt.getText().toString().trim();
                String email = emailEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();


                if(firstName.isEmpty()) {
                    mFirstNameError.setError(getString(R.string.first_name_error));
                    return;
                }

                if(lastName.isEmpty()) {
                    mLastNameError.setError(getString(R.string.last_name_error));
                    return;
                }

                if(email.isEmpty() || !email.contains("@")) {
                    mEmailError.setError(getString(R.string.email_login_error));
                    return;
                }


                if(password.isEmpty() ||  password.length() < 6) {
                    mPasswordError.setError(getString(R.string.enter_password_error) + "\n" + getString(R.string.password_instructions));
                    return;
                }

                if(mFollowerPhoneNumEt.getText().toString().isEmpty() ) {
                    mPhoneNumberError.setError(getString(R.string.phone_number_error));
                   // mDialog.cancel();
                    return;
                }
                else{
                    mFollowerPhoneNum = Integer.parseInt(mFollowerPhoneNumEt.getText().toString().trim());
                }

                setProgressBar();

                if(followerCb.isChecked()){
                    mDialog.show();
                    Follower follower = new Follower(firstName,lastName,email,mFollowerPhoneNum,true,password,null,"DDDDDD");
                    signUpUser(follower);

                }
                else {
                    mDialog.show();
                    Child child = new Child(firstName,lastName,email,mFollowerPhoneNum,false,password,null,null,null,false,0);
                    signUpUser(child);
                }
            }
        });
        return view;
    }


     private void signUpUser(final User user){

         viewModel.signUpUser(user ,new MainActivity.registerActions() {
             @Override
             public void registerSucceed(boolean succeed) {
                 if(succeed) {
                     mDialog.cancel();
                     registrationsSucceed(user);
                 }
                 else{
                     Snackbar.make(getView(),"Registration Failed", BaseTransientBottomBar.LENGTH_LONG);
                 }
             }
         });
     }

     public void setProgressBar(){
         mDialog = new KAlertDialog(getContext(), KAlertDialog.PROGRESS_TYPE);
         mDialog.getProgressHelper().setSpinSpeed(150);
         mDialog.setCancelable(false);
         mDialog.show();
     }

     public void registrationsSucceed(final User user){
         new KAlertDialog(getContext(), KAlertDialog.SUCCESS_TYPE)
                 .setTitleText(getString(R.string.registration_succeed))
                 .setConfirmText(getString(R.string.ok_confirmation))
                 .confirmButtonColor(R.color.colorPrimaryDark)
                 .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                     @Override
                     public void onClick(KAlertDialog kAlertDialog) {
                         if(user.isFollower())
                             moveToNewActivity(FollowerActivity.class);
                         else{
                             moveToNewActivity(ChildActivity.class);
                         }
                     }
                 })
                 .show();
     }

    private void moveToNewActivity (Class userClass) {
        Intent i = new Intent(getActivity(), userClass);
        startActivity(i);
        getActivity().overridePendingTransition(0, 0);
        getActivity().finish();
    }

}
