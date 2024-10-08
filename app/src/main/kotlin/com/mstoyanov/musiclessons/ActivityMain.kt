package com.mstoyanov.musiclessons

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.mstoyanov.musiclessons.global.Functions.serializable
import com.mstoyanov.musiclessons.model.Weekday

class ActivityMain : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    companion object {
        lateinit var sectionTitles: List<String>
        private var selectedSectionIndex: Int = 0
        private var selectedWeekdayIndex: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sectionTitles = listOf(*resources.getStringArray(R.array.section_titles))

        setSupportActionBar(findViewById(R.id.toolbar))

        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = FragmentStateAdapterImpl(supportFragmentManager)
        viewPager.registerOnPageChangeCallback(OnPageChangeCallbackImpl())

        tabLayout = findViewById(R.id.tab_layout)
        tabLayout.getTabAt(1)!!.icon!!.alpha = 128
        tabLayout.addOnTabSelectedListener(OnTabSelectedListenerImpl())

        if (intent.getLongExtra(resources.getString(R.string.added_student_id), 0) > 0 ||
            intent.getLongExtra(resources.getString(R.string.updated_student_id), 0) > 0 ||
            intent.getLongExtra(resources.getString(R.string.deleted_student_id), 0) > 0 ||
            intent.getBooleanExtra(resources.getString(R.string.export_students), false)
        ) {
            tabLayout.getTabAt(1)!!.select()
        } else if (intent.serializable<Weekday>("WEEKDAY") != null) {
            // returning from ActivityAddLesson, ActivityLessonDetails or ActivityEditLesson after deleting a lesson:
            val weekday = intent.serializable<Weekday>("WEEKDAY")!!
            viewPager.currentItem = weekday.ordinal
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("SELECTED_SECTION_INDEX", selectedSectionIndex)
        outState.putInt("SELECTED_WEEKDAY_INDEX", selectedWeekdayIndex)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        selectedSectionIndex = savedInstanceState.getInt("SELECTED_SECTION_INDEX")
        selectedWeekdayIndex = savedInstanceState.getInt("SELECTED_WEEKDAY_INDEX")
    }

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewPager.currentItem > 0) viewPager.currentItem -= 1
        }
    }

    private inner class FragmentStateAdapterImpl(fm: FragmentManager) : FragmentStateAdapter(fm, lifecycle) {
        override fun createFragment(position: Int): Fragment {
            return if (position == sectionTitles.size - 1)
                FragmentStudents.create(position)
            else
                FragmentSchedule.create(position)
        }

        override fun getItemCount(): Int = sectionTitles.size
    }

    private inner class OnTabSelectedListenerImpl : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            // synchronize the ViewPager with the TabLayout:
            if (tab.position == 0) {
                tabLayout.getTabAt(0)!!.icon!!.alpha = 255
                tabLayout.getTabAt(1)!!.icon!!.alpha = 127
                selectedSectionIndex = selectedWeekdayIndex
                viewPager.currentItem = selectedWeekdayIndex
            } else if (tab.position == 1) {
                tabLayout.getTabAt(0)!!.icon!!.alpha = 127
                tabLayout.getTabAt(1)!!.icon!!.alpha = 255
                selectedSectionIndex = sectionTitles.size - 1
                viewPager.currentItem = sectionTitles.size - 1
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            // do nothing
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            if (tab.position == 0) {
                selectedSectionIndex = 0
                selectedWeekdayIndex = 0
                viewPager.currentItem = 0
            }
        }
    }

    private inner class OnPageChangeCallbackImpl : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            // synchronize the TabLayout with the ViewPager:
            selectedSectionIndex = position
            if (position < sectionTitles.size - 1) {
                selectedWeekdayIndex = position
                if (!tabLayout.getTabAt(0)!!.isSelected) tabLayout.getTabAt(0)!!.select()
            } else if (position == sectionTitles.size - 1) {
                if (!tabLayout.getTabAt(1)!!.isSelected) tabLayout.getTabAt(1)!!.select()
            }
        }
    }
}
