package com.cs220.ssmessaging

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.cs220.ssmessaging.activities.ContactsActivity
import com.cs220.ssmessaging.activities.ConversationActivity
import com.cs220.ssmessaging.activities.ConversationsListActivity
import com.cs220.ssmessaging.activities.SettingsActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConversationsListEspressoTests {

    @Rule
    var mActivityRule: ActivityTestRule<ConversationsListActivity> = ActivityTestRule(ConversationsListActivity::class.java)

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

        Intents.intended(IntentMatchers.hasComponent(ContactsActivity::class.java.name))
    }

    @Test
    fun openConversation() {
        Espresso.onView(withId(R.id.scroll_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));

        Intents.intended(IntentMatchers.hasComponent(ConversationActivity::class.java.name))
    }
    */
}