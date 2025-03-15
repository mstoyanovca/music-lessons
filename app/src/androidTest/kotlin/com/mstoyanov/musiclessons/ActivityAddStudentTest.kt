package com.mstoyanov.musiclessons

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ActivityAddStudentTest {
    @get:Rule
    var rule: ActivityScenarioRule<ActivityAddStudent> = ActivityScenarioRule(ActivityAddStudent::class.java)

    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        rule.scenario.close()
        Intents.release()
    }

    @Test
    fun openStudentListTest() {
        onView(withId(R.id.first_name)).check(matches(isDisplayed()))

        onView(withId(R.id.first_name)).perform(clearText(), typeText("John"))
        onView(withId(R.id.delete)).perform(click())
        onView(withId(R.id.action_insert)).perform(click())

        Intents.intended(hasComponent(ActivityMain::class.java.name))
        onView(withId(R.id.students_list)).check(matches(isDisplayed()))
    }
}
