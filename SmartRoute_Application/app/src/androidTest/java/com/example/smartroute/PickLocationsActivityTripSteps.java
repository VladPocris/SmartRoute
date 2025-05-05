package com.example.smartroute;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static com.example.smartroute.testutil.WaitFor.ms;   // tiny helper
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

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
public class PickLocationsActivityTripSteps {

    @Rule
    public ActivityScenarioRule<HomePageActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(HomePageActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void pickLocationsActivityTripSteps() {

        ViewInteraction start = onView(allOf(withId(R.id.startButton),
                withText("Start Your Journey")));
        start.perform(click());
        onView(isRoot()).perform(ms(5000));

        ViewInteraction startField = onView(withId(R.id.startLocation));
        startField.perform(scrollTo(), click());
        onView(isRoot()).perform(ms(5000));

        onView(withId(com.google.android.libraries.places.R.id.places_autocomplete_search_bar))
                .perform(replaceText("Dublin"), closeSoftKeyboard());
        onView(isRoot()).perform(ms(5000));

        onView(withId(com.google.android.libraries.places.R.id.places_autocomplete_list))
                .perform(actionOnItemAtPosition(0, click()));
        onView(isRoot()).perform(ms(5000));

        onView(withId(R.id.endLocation)).perform(scrollTo(), click());
        onView(isRoot()).perform(ms(5000));

        onView(withId(com.google.android.libraries.places.R.id.places_autocomplete_search_bar))
                .perform(replaceText("Dubai"), closeSoftKeyboard());
        onView(isRoot()).perform(ms(5000));

        onView(withId(com.google.android.libraries.places.R.id.places_autocomplete_list))
                .perform(actionOnItemAtPosition(0, click()));
        onView(isRoot()).perform(ms(5000));

        onView(allOf(withId(R.id.addButton), withContentDescription("TODO")))
                .perform(scrollTo(), click());
        onView(isRoot()).perform(ms(5000));

        onView(withId(R.id.endLocation)).perform(scrollTo(), click());
        onView(isRoot()).perform(ms(5000));

        onView(withId(com.google.android.libraries.places.R.id.places_autocomplete_search_bar))
                .perform(replaceText("Paris"), closeSoftKeyboard());
        onView(isRoot()).perform(ms(5000));

        onView(withId(com.google.android.libraries.places.R.id.places_autocomplete_list))
                .perform(actionOnItemAtPosition(0, click()));
        onView(isRoot()).perform(ms(5000));

        // --- first heavy optimise ---
        onView(allOf(withId(R.id.btnOptimize), withText("Plan The Trip")))
                .perform(scrollTo(), click());
        onView(isRoot()).perform(ms(6000));            // 6‑sec wait

        onView(allOf(withId(R.id.btnEditTrip), withText("Edit Trip")))
                .perform(click());
        onView(isRoot()).perform(ms(5000));

        // assertions …
        onView(allOf(withId(R.id.startLocation), withText("Dublin ,Ireland")))
                .check(matches(isDisplayed()));

        onView(allOf(withText("Dubai ,United Arab Emirates"),
                withParent(withId(R.id.locationContainer))))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.endLocation), withText("Paris ,France")))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.removeButton), withContentDescription("500 km")))
                .perform(scrollTo(), click());
        onView(isRoot()).perform(ms(5000));

        // --- second heavy optimise ---
        onView(allOf(withId(R.id.btnOptimize), withText("Plan The Trip")))
                .perform(scrollTo(), click());
        onView(isRoot()).perform(ms(6000));            // 6‑sec wait

        // more assertions …
        onView(allOf(withId(R.id.tvFromTo),
                withText("Dublin ,Ireland → Paris ,France")))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.tvDistance), withText("1064.5 km")))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.tvDuration), withText("805 min")))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.btnShowTripDetails), withText("SHOW DETAILS")))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.btnEditTrip), withText("EDIT TRIP")))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.btnShowTripDetails), withText("Show Details")))
                .perform(click());
        onView(isRoot()).perform(ms(5000));

        // detail assertions …
        onView(allOf(withId(R.id.tvFromTo),
                withText("Dublin, Ireland → Paris, France")))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.btnFinishTrip), withText("Finish Trip")))
                .perform(scrollTo(), click());
        onView(isRoot()).perform(ms(5000));

        onView(withContentDescription("Google Map")).check(matches(isDisplayed()));
    }

    /* helper for recorder-style childAtPosition() */
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {
        return new TypeSafeMatcher<View>() {
            @Override public void describeTo(Description d) {
                d.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(d);
            }
            @Override public boolean matchesSafely(View v) {
                ViewParent p = v.getParent();
                return p instanceof ViewGroup && parentMatcher.matches(p)
                        && v.equals(((ViewGroup) p).getChildAt(position));
            }
        };
    }
}
