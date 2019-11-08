package com.cs220.ssmessaging.UIEspressoTests

import androidx.test.rule.ActivityTestRule
import com.cs220.ssmessaging.frontend.fragments.ConversationsListFragment
import org.junit.Rule

class ConversationsListEspressoTests {

    /*@Rule
    var mActivityRule: ActivityTestRule<ConversationsListFragment> = ActivityTestRule(
        ConversationsListFragment::class.java)*/

    // NOTE: The following is an implementation of how we think our UI tests will run for this screen
    // They are currently commented out given that the tests must refer to specific ui elements that haven't been built yet
    // These tests will be modified and updated to match our iteration 1 implementation

    /*
    @Test
    fun openSettings() {
        Espresso.onView(withId(R.id.settingsButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())

        Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
    }

    @Test
    fun openContacts() {
        Espresso.onView(withId(R.id.contactsButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())

        Intents.intended(IntentMatchers.hasComponent(ContactsFragment::class.java.name))
    }

    @Test
    fun openConversation() {
        Espresso.onView(withId(R.id.scroll_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));

        Intents.intended(IntentMatchers.hasComponent(ConversationActivity::class.java.name))
    }
    */
}