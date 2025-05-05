package com.example.smartroute.testutil;

import android.view.View;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import org.hamcrest.Matcher;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

public final class WaitFor {
    private WaitFor() {}
    public static ViewAction ms(long millis) {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() { return isRoot(); }
            @Override public String getDescription() { return "wait " + millis + " ms"; }
            @Override public void perform(UiController ui, View v) {
                ui.loopMainThreadForAtLeast(millis);
            }
        };
    }
}
