package com.mstoyanov.musiclessons

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActivityAddStudentTest {
    @Rule
    @JvmField
    val testRule = ActivityTestRule(ActivityAddStudent::class.java)

    @Test
    fun changeText_sameActivity() {
        onView(withId(R.id.first_name)).check(matches(isDisplayed()))

        onView(withId(R.id.first_name)).perform(clearText(), typeText("John"))
        onView(withId(R.id.delete)).perform(click())
        onView(withId(R.id.action_insert)).perform(click())

        onView(withId(R.id.students_list)).check(matches(isDisplayed()))
    }
}