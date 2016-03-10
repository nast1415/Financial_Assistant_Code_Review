package ru.spbau.mit.starlab.financialassistant;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;


public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    public void testActivityExists() {
        LoginActivity activity = getActivity();
        assertNotNull(activity);
    }

    public void testTransitionToSignIn() {
        LoginActivity activity = getActivity();
        Button button = (Button) activity.findViewById(R.id.btnSignIn);
        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(SignInActivity.class.getName(), null, false);
        TouchUtils.clickView(this, button);
        Activity currentActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5);
        assertNotNull(currentActivity);
        assertEquals(SignInActivity.class, currentActivity.getClass());
    }

    public void testTransitionToSignUp() {
        LoginActivity activity = getActivity();
        Button button = (Button) activity.findViewById(R.id.btnSignUp);
        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(SignUpActivity.class.getName(), null, false);
        TouchUtils.clickView(this, button);
        Activity currentActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5);
        assertNotNull(currentActivity);
        assertEquals(SignUpActivity.class, currentActivity.getClass());
    }
}