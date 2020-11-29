package com.mstoyanov.musiclessons

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mstoyanov.musiclessons.model.Weekday

class ActivityMain : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
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
        viewPager.adapter = MyAdapter(supportFragmentManager)
        viewPager.addOnPageChangeListener(MyOnPageChangeListener())

        tabLayout = findViewById(R.id.tab_layout)
        tabLayout.getTabAt(1)!!.icon!!.alpha = 128
        tabLayout.addOnTabSelectedListener(MyOnTabSelectedListener())

        if (intent.getLongExtra("ADDED_STUDENT_ID", 0) > 0 ||
                intent.getLongExtra("UPDATED_STUDENT_ID", 0) > 0 ||
                intent.getLongExtra("DELETED_STUDENT_ID", 0) > 0 ||
                intent.getBooleanExtra("EXPORTED_STUDENTS", false)) {
            tabLayout.getTabAt(1)!!.select()
        } else if (intent.getSerializableExtra("WEEKDAY") != null) {
            // returning from ActivityAddLesson, ActivityLessonDetails or ActivityEditLesson after deleting a lesson:
            val weekday = intent.getSerializableExtra("WEEKDAY") as Weekday
            viewPager.currentItem = weekday.ordinal
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt("SELECTED_WEEKDAY_INDEX", selectedWeekdayIndex)
        savedInstanceState.putInt("SELECTED_SECTION_INDEX", selectedSectionIndex)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        selectedWeekdayIndex = savedInstanceState.getInt("SELECTED_WEEKDAY_INDEX")
        selectedSectionIndex = savedInstanceState.getInt("SELECTED_SECTION_INDEX")
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) super.onBackPressed()
        else viewPager.currentItem = viewPager.currentItem - 1
    }

    private class MyAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return if (position == sectionTitles.size - 1) FragmentStudents.create(position)
            else FragmentSchedule.create(position)
        }

        override fun getItemPosition(item: Any): Int {
            return (item as Fragment).arguments!!.getInt("POSITION")
        }

        override fun getCount(): Int {
            return sectionTitles.size
        }
    }

    private inner class MyOnTabSelectedListener : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            // synchronize the ViewPager with the TabLayout:
            if (tab.position == 0) {
                tabLayout.getTabAt(0)!!.icon!!.alpha = 255
                tabLayout.getTabAt(1)!!.icon!!.alpha = 127
                viewPager.currentItem = selectedWeekdayIndex
            } else if (tab.position == 1) {
                tabLayout.getTabAt(0)!!.icon!!.alpha = 127
                tabLayout.getTabAt(1)!!.icon!!.alpha = 255
                viewPager.currentItem = sectionTitles.size - 1
                selectedSectionIndex = sectionTitles.size - 1
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            // do nothing
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            if (tab.position == 0) viewPager.currentItem = 0
        }
    }

    private inner class MyOnPageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            // do nothing
        }

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

        override fun onPageScrollStateChanged(state: Int) {
            // do nothing
        }
    }
}
