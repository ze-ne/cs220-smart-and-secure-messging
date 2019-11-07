package com.cs220.ssmessaging.UIEspressoTests

import androidx.test.rule.ActivityTestRule
import com.cs220.ssmessaging.frontend.activities.ConversationActivity
import org.junit.Rule

class ConversationEspressoTests {

    @Rule
    var activityRule: ActivityTestRule<ConversationActivity> = ActivityTestRule(ConversationActivity::class.java)

    // NOTE: The following is an implementation of how we think our UI tests will run for this screen
    // They are currently commented out given that the tests must refer to specific ui elements that haven't been built yet
    // These tests will be modified and updated to match our iteration 1 implementation

    /*
    @Test
    fun sendNonEmptyTextMessage() {
        onView(withId(R.id.textEntry))
            .perform(typeText("Hi Tim"))

        onView(withId(R.id.sendButton))
            .check(matches(isDisplayed()))
            .perform(click())


        onView(ViewMatchers.withId(R.id.messages))
            .perform(RecyclerViewActions.scrollToPosition(0))

        val itemElementText = activityRule.activity.resources
            .getString(R.string.last)
        onView(withText(itemElementText)).check(matches(isDisplayed()))
    }

    @Test
    fun sendEmptyTextMessage() {
        onView(withId(R.id.textEntry))
            .perform(typeText(""))

        onView(withId(R.id.sendButton))
            .check(matches(isDisplayed()))
            .perform(click())
    }

    @Test
    fun addImageToMessage() {
        onView(withId(R.id.addImageButton))
            .check(matches(isDisplayed()))
            .perform(click())

        // TODO bring up image screen
    }

    @Test
    fun sendImageMessage() {
        onView(withId(R.id.addImageButton))
            .check(matches(isDisplayed()))
            .perform(click())

        // TODO bring up image screen
    }
    */
}