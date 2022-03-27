package com.test.test_karim.feature.main

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.test.test_karim.R
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest : TestCase() {
    @Test
    fun add() {
        assertEquals(10, 10)
    }

    @Rule
    @JvmField
    var activityRule = ActivityScenario.launch(MainActivity::class.java)

    @Test
    @Throws(Exception::class)
    fun recyclerDisplayed() {
        activityRule.moveToState(Lifecycle.State.CREATED)
        onView(withId(R.id.recycler_user)).check(matches(isDisplayed()))
    }

    @Test
    fun test_DetailOnclick() {
        activityRule.moveToState(Lifecycle.State.CREATED)
        // Click list item #LIST_ITEM_IN_TEST
        onView(withId(R.id.view_bt)).perform(ViewActions.click())
    }
}
