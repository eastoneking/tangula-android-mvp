package com.tangula.android.mvp.testapp.presenter.view.recyclerview;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.tangula.android.mvp.testapp.R;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.view.View;

import junit.framework.AssertionFailedError;

@RunWith(AndroidJUnit4.class)
public class RecyclerViewTestActivityTest {

    @Rule
    public ActivityTestRule<RecyclerViewTestActivity> rule = new ActivityTestRule(RecyclerViewTestActivity.class); // defined the activity for test.


    @Test
    public void test() {

        Espresso.onView(withId(R.id.vw_act_recycler_view_test_recycler))  // find the view
                .check(ViewAssertions.matches(isDisplayed())) // check view is display on the current activity.
                .check(new ViewAssertion() {
                    @Override
                    public void check(View view, NoMatchingViewException noViewFoundException) {
                        RecyclerView rvw = (RecyclerView)view;
                        assertEquals(3, rvw.getAdapter().getItemCount());
                    }
                }).perform(RecyclerViewActions.actionOnItemAtPosition(0, scrollTo()))
                .perform(ViewActions.swipeDown())
                .check(new ViewAssertion() {
                    @Override
                    public void check(View view, NoMatchingViewException noViewFoundException) {
                        RecyclerView rvw = (RecyclerView)view;
                        if(rvw.getAdapter().getItemCount()!=6){
                            throw new AssertionFailedError("recycler view items count not was 6, but it was "+rvw.getAdapter().getItemCount());
                        }
                    }
                }).perform(RecyclerViewActions.actionOnItemAtPosition(0, scrollTo()))
                .perform(ViewActions.swipeDown())
                .check(new ViewAssertion() {
                    @Override
                    public void check(View view, NoMatchingViewException noViewFoundException) {
                        RecyclerView rvw = (RecyclerView)view;
                        if(rvw.getAdapter().getItemCount()!=8){
                            throw new AssertionFailedError("recycler view items count not was 8, but it was "+rvw.getAdapter().getItemCount());
                        }
                    }
                })
        ;
    }

}

