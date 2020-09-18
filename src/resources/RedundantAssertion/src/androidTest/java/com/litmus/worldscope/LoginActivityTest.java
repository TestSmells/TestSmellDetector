package com.litmus.worldscope;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest extends TestCase{

    private LoginActivity facebookLoginActivity;


    @Rule
    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Before
    public void setUp() {
        facebookLoginActivity = activityRule.getActivity();
    }

    @Test
    public void testTrue() {
        assertEquals(true, true);
    }

    @Test
    public void testRedirectToMainActivity() {
        Instrumentation.ActivityMonitor am = InstrumentationRegistry.getInstrumentation().addMonitor(MainActivity.class.getName(), null, true);
        facebookLoginActivity.redirectToMainActivity();
        assertEquals(1, am.getHits());
    }
}