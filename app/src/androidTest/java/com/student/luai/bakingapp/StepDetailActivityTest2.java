package com.student.luai.bakingapp;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class StepDetailActivityTest2 {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void stepDetailActivityTest2() {
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.rv_recipe_list), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(3, click()));

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.rv_steps_list),
                        withParent(allOf(withId(R.id.fragment_recipe_detail),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        recyclerView2.perform(actionOnItemAtPosition(3, click()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.tv_step_details_instructions_title), withText("Step 3"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        textView.check(matches(withText("Step 3")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tv_step_details_instructions), withText("3. Fill a large roasting pan with a few inches of hot water and place it on the bottom rack of the oven."),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("3. Fill a large roasting pan with a few inches of hot water and place it on the bottom rack of the oven.")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
