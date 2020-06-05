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
    private TextInputLayout firstNameError;
    private TextInputLayout lastNameError;
    private TextInputLayout emailError;
    private TextInputLayout passwordError;
    private TextInputLayout phoneNumberError;
    private int followerPhoneNum;
    private KAlertDialog pDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registration,container,false);

        firstNameError = view.findViewById(R.id.first_name_layout_registration);
        lastNameError = view.findViewById(R.id.last_name_layout_registration);
        emailError = view.findViewById(R.id.email_layout_registration);
        passwordError = view.findViewById(R.id.password_layout_registration);
        phoneNumberError = view.findViewById(R.id.phone_number_layout_registration);


        final EditText firstNameEt = view.findViewById(R.id.firstNameInput_register);
        final EditText lastNameEt = view.findViewById(R.id.lastNameInput_register);
        final EditText emailEt = view.findViewById(R.id.emailInput_register);
        final EditText passwordEt = view.findViewById(R.id.passwordInput_register);
        final EditText followerPhoneNumEt = view.findViewById(R.id.follower_phoneNum_Et);
        final CheckBox followerCb = view.findViewById(R.id.follower_Cb);


        Button registerBtn = view.findViewById(R.id.createUserBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                setProgressBar();
                Log.d("onClickRegisterBtn","Arrive");
                String firstName = firstNameEt.getText().toString().trim();
                String lastName = lastNameEt.getText().toString().trim();
                String email = emailEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();


                if(firstName.isEmpty()) {
                    firstNameError.setError(getString(R.string.first_name_error));
                    pDialog.cancel();
                    return;
                }

                if(lastName.isEmpty()) {
                    lastNameError.setError(getString(R.string.last_name_error));
                    pDialog.cancel();
                    return;
                }

                if(email.isEmpty() || !email.contains("@")) {
                    emailError.setError(getString(R.string.email_login_error));
                    pDialog.cancel();
                    return;
                }


                if(password.isEmpty() ||  password.length() < 6) {
                    passwordError.setError(getString(R.string.enter_password_error) + "\n" + getString(R.string.password_instructions));
                    pDialog.cancel();
                    return;
                }

                if(followerPhoneNumEt.getText().toString().isEmpty() ) {
                    phoneNumberError.setError(getString(R.string.phone_number_error));
                    pDialog.cancel();
                    return;
                }
                else{
                    followerPhoneNum = Integer.parseInt(followerPhoneNumEt.getText().toString().trim());
                }

                if(followerCb.isChecked()){
                    pDialog.show();
                    Follower follower = new Follower(firstName,lastName,email,followerPhoneNum,true,password,null,"DDDDDD");
                    signUpUser(follower);
                }
                else {
                    pDialog.show();
                    Child child = new Child(firstName,lastName,email,followerPhoneNum,false,password,null,null,null,false);
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
                     pDialog.cancel();
                     registrationsSucceed(user);
                 }
                 else{
                     Snackbar.make(getView(),"Registration Failed", BaseTransientBottomBar.LENGTH_LONG);
                 }
             }
         });
     }

     public void setProgressBar(){
         pDialog = new KAlertDialog(getContext(), KAlertDialog.PROGRESS_TYPE);
         pDialog.getProgressHelper().setSpinSpeed(150);
         pDialog.setCancelable(false);
         pDialog.show();
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
