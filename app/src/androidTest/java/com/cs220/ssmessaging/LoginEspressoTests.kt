package com.cs220.ssmessaging

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.cs220.ssmessaging.activities.ConversationsListActivity
import com.cs220.ssmessaging.activities.LoginActivity

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginEspressoTests {

    @Rule
    var mActivityRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)

    /*
    @Test
    fun usernameIsNotEmptyLogin() {
        onView(withId(R.id.username))
            .perform(typeText("myUsername"))

        onView(withId(R.id.buttonLogin))
            .check(matches(isDisplayed()))
            .perform(click())

        intended(hasComponent(ConversationsListActivity::class.java.name))
    }

    @Test
    fun loginIsNotEmptyLogin() {
        onView(withId(R.id.username))
            .perform(typeText("myUsername"))

        onView(withId(R.id.buttonRegister))
            .check(matches(isDisplayed()))
            .perform(click())

        intended(hasComponent(ConversationsListActivity::class.java.name))
    }
    */
}