package com.example.kangw.evolut;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kangw.evolut.fragment.FriendListFragment;
import com.example.kangw.evolut.fragment.FriendProfile;
import com.example.kangw.evolut.fragment.HomepageFragment;
import com.example.kangw.evolut.fragment.TransactionHistoryFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FriendListFragment.OnFragmentInteractionListener,
        HomepageFragment.OnFragmentInteractionListener,
        TransactionHistoryFragment.OnFragmentInteractionListener,
        FriendProfile.OnFragmentInteractionListener {

    ImageView mProfilePic;
    TextView mUserName;
    TextView mUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), NewTransactionActivity.class);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Need to handle resume previous fragment, else it will always return to homepage fragment by default
        Fragment fragment = new HomepageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "homepage").commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String token = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("FCM");
        dref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.default_notification_channel_name));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        //Need to handle resume previous fragment, else it will always return to homepage fragment by default
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        try {
            //Initialize name,email and profile picture at navigation header
            mUserName = (TextView) findViewById(R.id.textViewProfileName);
            mUserName.setText(user.getDisplayName());
            mUserEmail = (TextView) findViewById(R.id.textViewProfileEmail);
            mUserEmail.setText(user.getEmail());
            String profilePic = user.getPhotoUrl().toString();
            mProfilePic = (ImageView) findViewById(R.id.imageViewProfilePicture);
            BitmapDownloaderTask task = new BitmapDownloaderTask(mProfilePic);
            task.execute(profilePic);
        } catch (NullPointerException e) {

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (id == R.id.nav_home) {
            Fragment fragment = new HomepageFragment();
            ft.replace(R.id.frame_container, fragment, "homepage").commit();
        } else if (id == R.id.nav_topUp) {
            Intent i = new Intent(getApplicationContext(), TopUpActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_friends) {
            FriendListFragment fragment = new FriendListFragment();
            ft.replace(R.id.frame_container, fragment, "FriendList").commit();
        } else if (id == R.id.nav_setting) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_transaction) {
            TransactionHistoryFragment fragment = new TransactionHistoryFragment();
            ft.replace(R.id.frame_container, fragment, "TransactionHistory").commit();
        } else if (id == R.id.nav_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent i = new Intent(MainActivity.this, LauncherActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                showSnackbar(R.string.sign_out_failed);
                            }
                        }
                    });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(findViewById(R.id.frame_container), errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
