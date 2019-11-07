package com.cs220.ssmessaging.UIEspressoTests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.frontend.activities.LoginActivity
import com.cs220.ssmessaging.frontend.activities.SettingsActivity
import org.junit.Rule
import org.junit.Test

class SettingsEspressoTests {

    @Rule
    var mActivityRule: ActivityTestRule<SettingsActivity> = ActivityTestRule(SettingsActivity::class.java)

    // NOTE: The following is an implementation of how we think our UI tests will run for this screen
    // They are currently commented out given that the tests must refer to specific ui elements that haven't been built yet
    // These tests will be modified and updated to match our iteration 1 implementation

    /*
    @Test
    fun logout() {
        onView(withId(R.id.buttonLogout))
            .check(matches(isDisplayed()))
            .perform(click())

        intended(hasComponent(LoginActivity::class.java.name))
    }

    @Test
    fun changeFirstname() {
        onView(withId(R.id.changeFirstname))
            .perform(typeText("John"))

        onView(withId(R.id.buttonSubmitFirstname))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.firstname))
            .check(matches(withText("John")))
    }

    @Test
    fun changeLastname() {
        onView(withId(R.id.changeLastname))
            .perform(typeText("Smith"))

        onView(withId(R.id.buttonSubmitLastname))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.lastname))
            .check(matches(withText("Smith")))
    }
    */
}