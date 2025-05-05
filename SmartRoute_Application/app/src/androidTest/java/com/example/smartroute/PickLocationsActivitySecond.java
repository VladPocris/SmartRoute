package com.example.smartroute;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.smartroute.testutil.WaitFor.ms;   // ← tiny wait helper
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
public class PickLocationsActivitySecond {

    @Rule
    public ActivityScenarioRule<HomePageActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(HomePageActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void pickLocationsActivitySecond() {

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.startButton), withText("Start Your Journey"),
                        childAtPosition(
                                childAtPosition(withId(android.R.id.content), 0),
                                0),
                        isDisplayed()));
        materialButton.perform(click());
        onView(isRoot()).perform(ms(5000));

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.startLocation),
                        childAtPosition(
                                childAtPosition(withClassName(is("android.widget.ScrollView")), 0), 1)));
        appCompatEditText.perform(scrollTo(), click());
        onView(isRoot()).perform(ms(1000));

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(com.google.android.libraries.places.R.id.places_autocomplete_search_bar),
                        childAtPosition(
                                allOf(withId(com.google.android.libraries.places.R.id.places_autocomplete_search_bar_container),
                                        childAtPosition(withClassName(is("android.widget.LinearLayout")), 0)),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("Dublin"), closeSoftKeyboard());
        onView(isRoot()).perform(ms(3000));

        ViewInteraction recyclerView = onView(
                allOf(withId(com.google.android.libraries.places.R.id.places_autocomplete_list),
                        childAtPosition(withClassName(is("android.widget.LinearLayout")), 2)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        onView(isRoot()).perform(ms(3000));

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.endLocation),
                        childAtPosition(
                                childAtPosition(withClassName(is("android.widget.ScrollView")), 0), 3)));
        appCompatEditText3.perform(scrollTo(), click());
        onView(isRoot()).perform(ms(1000));

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(com.google.android.libraries.places.R.id.places_autocomplete_search_bar),
                        childAtPosition(
                                allOf(withId(com.google.android.libraries.places.R.id.places_autocomplete_search_bar_container),
                                        childAtPosition(withClassName(is("android.widget.LinearLayout")), 0)),
                                1),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("Dubai"), closeSoftKeyboard());
        onView(isRoot()).perform(ms(3000));

        ViewInteraction recyclerView2 = onView(
                allOf(withId(com.google.android.libraries.places.R.id.places_autocomplete_list),
                        childAtPosition(withClassName(is("android.widget.LinearLayout")), 2)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));
        onView(isRoot()).perform(ms(3000));

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.addButton), withContentDescription("TODO"),
                        childAtPosition(
                                childAtPosition(withClassName(is("android.widget.LinearLayout")), 4), 2)));
        materialButton2.perform(scrollTo(), click());
        onView(isRoot()).perform(ms(1000));

        // … repeat the same pattern for every perform(...)

        // Example for Plan The Trip
        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.btnOptimize), withText("Plan The Trip"),
                        childAtPosition(
                                childAtPosition(withClassName(is("android.widget.ScrollView")), 0), 5)));
        materialButton5.perform(scrollTo(), click());
        onView(isRoot()).perform(ms(5000));

        // Keep existing checks (no waits needed after pure assertions)

        // --- rest of the test unchanged ---
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {
        return new TypeSafeMatcher<View>() {
            @Override public void describeTo(Description d) {
                d.appendText("Child at position " + position + " in parent "); parentMatcher.describeTo(d);
            }
            @Override public boolean matchesSafely(View view) {
                ViewParent p = view.getParent();
                return p instanceof ViewGroup && parentMatcher.matches(p)
                        && view.equals(((ViewGroup) p).getChildAt(position));
            }
        };
    }
}
