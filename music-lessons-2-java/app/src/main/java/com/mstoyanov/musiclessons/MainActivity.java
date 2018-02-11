package com.mstoyanov.musiclessons;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mstoyanov.musiclessons.model.Weekday;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static List<String> sectionTitles;
    private static int selectedSectionIndex;
    private static int selectedWeekdayIndex;
    private static ViewPager viewPager;
    private static TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sectionTitles = new ArrayList<>();
        sectionTitles.addAll(Arrays.asList(getResources().getStringArray(R.array.section_titles)));

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MyAdapter adapter = new MyAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.getTabAt(1).getIcon().setAlpha(128);
        tabLayout.addOnTabSelectedListener(new MyOnTabSelectedListener());

        if (getIntent().getLongExtra("ADDED_STUDENT_ID", 0) > 0 ||
                getIntent().getLongExtra("UPDATED_STUDENT_ID", 0) > 0 ||
                getIntent().getLongExtra("DELETED_STUDENT_ID", 0) > 0) {
            tabLayout.getTabAt(1).select();
        } else if (getIntent().getSerializableExtra("WEEKDAY") != null) {
            // returning from AddLesson or EditLesson:
            Weekday weekday = (Weekday) getIntent().getSerializableExtra("WEEKDAY");
            viewPager.setCurrentItem(weekday.ordinal());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("SELECTED_WEEKDAY_INDEX", selectedWeekdayIndex);
        savedInstanceState.putInt("SELECTED_SECTION_INDEX", selectedSectionIndex);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedWeekdayIndex = savedInstanceState.getInt("SELECTED_WEEKDAY_INDEX");
        selectedSectionIndex = savedInstanceState.getInt("SELECTED_SECTION_INDEX");
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private static class MyAdapter extends FragmentStatePagerAdapter {

        private MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == sectionTitles.size() - 1) {
                return StudentsFragment.create(position);
            } else {
                return ScheduleFragment.create(position);
            }
        }

        @Override
        public int getItemPosition(Object item) {
            return ((Fragment) item).getArguments().getInt("POSITION");
        }

        @Override
        public int getCount() {
            return sectionTitles.size();
        }
    }

    private static class MyOnTabSelectedListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            // synchronize the ViewPager with the TabLayout:
            if (tab.getPosition() == 0) {
                tabLayout.getTabAt(0).getIcon().setAlpha(255);
                tabLayout.getTabAt(1).getIcon().setAlpha(127);
                viewPager.setCurrentItem(selectedWeekdayIndex);
            } else if (tab.getPosition() == 1) {
                tabLayout.getTabAt(0).getIcon().setAlpha(127);
                tabLayout.getTabAt(1).getIcon().setAlpha(255);
                viewPager.setCurrentItem(sectionTitles.size() - 1);
                selectedSectionIndex = sectionTitles.size() - 1;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            // do nothing
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            if (tab.getPosition() == 0) viewPager.setCurrentItem(0);
        }
    }

    private static class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // do nothing
        }

        @Override
        public void onPageSelected(int position) {
            // synchronize the TabLayout with the ViewPager:
            selectedSectionIndex = position;
            if (position < sectionTitles.size() - 1) {
                selectedWeekdayIndex = position;
                if (!tabLayout.getTabAt(0).isSelected()) tabLayout.getTabAt(0).select();
            } else if (position == sectionTitles.size() - 1) {
                if (!tabLayout.getTabAt(1).isSelected()) tabLayout.getTabAt(1).select();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // do nothing
        }
    }
}