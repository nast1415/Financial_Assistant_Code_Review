package ru.spbau.mit.starlab.financialassistant;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.concurrent.CountDownLatch;

public class SignInTask extends AsyncTask<Void, Void, Boolean> {
    private SignInActivity activity;
    private final String mEmail;
    private final String mPassword;
    private boolean isAuthorize = true;
    private FirebaseError error = null;

    SignInTask(SignInActivity activity, String email, String password) {
        this.activity = activity;
        mEmail = email;
        mPassword = password;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Firebase ref = new Firebase("https://luminous-heat-4027.firebaseio.com");
        final CountDownLatch done = new CountDownLatch(1);
        ref.authWithPassword(mEmail, mPassword, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                done.countDown();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                error = firebaseError;
                isAuthorize = false;
                done.countDown();
            }
        });

        try {
            done.await();
        } catch (InterruptedException e) {
            return false;
        }
        return isAuthorize;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        activity.mAuthTask = null;
        activity.showProgress(false);

        if (success) {
            activity.finish();

            Intent myIntent = new Intent(activity, MainActivity.class);
            activity.startActivity(myIntent);
        } else {
            if (error == null) {
                Toast.makeText(activity.getApplicationContext(), R.string.message_error,
                        Toast.LENGTH_SHORT).show();
            } else {
                switch (error.getCode()) {
                    case FirebaseError.USER_DOES_NOT_EXIST:
                        activity.mEmailView.setError(activity.getString(
                                R.string.error_incorrect_authorization));
                        activity.mEmailView.requestFocus();
                        break;
                    case FirebaseError.INVALID_PASSWORD:
                        activity.mPasswordView.setError(activity.getString(
                                R.string.error_incorrect_password));
                        activity.mPasswordView.requestFocus();
                        break;
                    case FirebaseError.NETWORK_ERROR:
                        Toast.makeText(activity.getApplicationContext(), R.string.error_network,
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(activity.getApplicationContext(), R.string.message_error,
                                Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    protected void onCancelled() {
        activity.mAuthTask = null;
        activity.showProgress(false);
    }
}
