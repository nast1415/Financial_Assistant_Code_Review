package ru.spbau.mit.starlab.financialassistant;

import android.os.AsyncTask;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class SignUpTask extends AsyncTask<Void, Void, Boolean> {
    private SignUpActivity activity;
    private final String mEmail;
    private final String mPassword;
    private boolean isRegister = true;
    private FirebaseError error = null;

    SignUpTask(SignUpActivity activity, String email, String password) {
        this.activity = activity;
        this.mEmail = email;
        this.mPassword = password;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Firebase ref = new Firebase("https://luminous-heat-4027.firebaseio.com");
        final CountDownLatch done = new CountDownLatch(1);
        ref.createUser(mEmail, mPassword,
                new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        done.countDown();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        error = firebaseError;
                        isRegister = false;
                        done.countDown();
                    }
                });

        try {
            done.await();
        } catch (InterruptedException e) {
            return false;
        }
        return isRegister;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        activity.mAuthTask = null;
        activity.showProgress(false);

        if (success) {
            activity.finish();
        } else {
            if (error == null) {
                Toast.makeText(activity.getApplicationContext(),
                        R.string.message_error, Toast.LENGTH_SHORT).show();
            } else {
                switch (error.getCode()) {
                    case FirebaseError.EMAIL_TAKEN:
                        activity.mEmailView.setError(activity.getString(
                                R.string.error_incorrect_registration));
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
            activity.mEmailView.requestFocus();
        }
    }

    @Override
    protected void onCancelled() {
        activity.mAuthTask = null;
        activity.showProgress(false);
    }
}
