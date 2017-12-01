package com.example.kangw.evolut;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.Scopes;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.root)
    View mRootView;

    private static final int RC_SIGN_IN = 100;
    private static final String GOOGLE_TOS_URL = "https://www.google.com/policies/terms/";
    private static final String GOOGLE_PRIVACY_POLICY_URL = "https://www.google.com/policies/privacy/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Intent i = new Intent(this,HomeActivity.class);
            startActivity(i);
            finish();
            return;
        } else {
            signIn();
        }
    }

    public void signIn() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(AuthUI.getDefaultTheme())
                        .setLogo(AuthUI.NO_LOGO)
                        .setAvailableProviders(
                                Arrays.asList(
                                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build()))
                        .setTosUrl(GOOGLE_TOS_URL)
                        .setPrivacyPolicyUrl(GOOGLE_PRIVACY_POLICY_URL)
                        .setIsSmartLockEnabled(false,true)
                        .setAllowNewEmailAccounts(true)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Intent i = new Intent(this,HomeActivity.class);
            startActivity(i);
            return;
        }
    }

    @MainThread
    private List<String> getGooglePermissions() {
        List<String> result = new ArrayList<>();
            result.add("https://www.googleapis.com/auth/youtube.readonly");
            result.add(Scopes.DRIVE_FILE);
        return result;
    }
}
