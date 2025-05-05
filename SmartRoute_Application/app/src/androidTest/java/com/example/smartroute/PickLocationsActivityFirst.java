package com.example.smartroute;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PickLocationsActivityFirst {

    @Rule
    public ActivityScenarioRule<HomePageActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(HomePageActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void pickLocationsActivity() {
        onView(withId(R.id.startButton)).perform(click());

        onView(withContentDescription("Google Map")).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.btnRetrieveTrip), withText("Find Trip")))
                .check(matches(isDisplayed()));

        onView(withId(R.id.startLocation2))
                .check(matches(withHint("Enter the trip Code")));

        onView(allOf(withId(R.id.btnOptimize), withText("Plan The Trip")))
                .check(matches(isDisplayed()));

        onView(withId(R.id.removeButton)).check(matches(isDisplayed()));
        onView(withId(R.id.addButton)).check(matches(isDisplayed()));

        onView(withId(R.id.endLocation))
                .check(matches(withHint("Enter Destination")));

        onView(withId(R.id.startLocation))
                .check(matches(withHint("Enter Start Location")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }
            @Override public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup
                        && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
