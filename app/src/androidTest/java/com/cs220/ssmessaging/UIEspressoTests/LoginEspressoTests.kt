package com.cs220.ssmessaging.UIEspressoTests

import androidx.test.rule.ActivityTestRule
import com.cs220.ssmessaging.frontend.activities.LoginActivity
import org.junit.Rule
import org.junit.runner.RunWith

class LoginEspressoTests {

    @Rule
    var mActivityRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)

    // NOTE: The following is an implementation of how we think our UI tests will run for this screen
    // They are currently commented out given that the tests must refer to specific ui elements that haven't been built yet
    // These tests will be modified and updated to match our iteration 1 implementation

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
    fun usernameIsNotEmptyRegistration() {
        onView(withId(R.id.username))
            .perform(typeText("myUsername"))

        onView(withId(R.id.buttonRegister))
            .check(matches(isDisplayed()))
            .perform(click())

        intended(hasComponent(ConversationsListActivity::class.java.name))
    }
    */
}