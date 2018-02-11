package com.mstoyanov.musiclessons;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.Locale;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    private int section;
    private String weekday;
    private int selectedTab;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        if (getIntent().getStringExtra("WEEKDAY") != null) {
            weekday = getIntent().getStringExtra("WEEKDAY");
        }
        if (getIntent().getIntExtra("SELECTED_TAB", 0) != 0) {
            selectedTab = getIntent().getIntExtra("SELECTED_TAB", 0);
        }

        mViewPager = findViewById(R.id.pager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 6) {
                    section = position;  // "Students" tab
                    actionBar.setSelectedNavigationItem(1);
                } else if (position == 7) {
                    section = position;  // "Add Student" tab
                    actionBar.setSelectedNavigationItem(2);
                } else {
                    section = position;  // a schedule tab
                    actionBar.setSelectedNavigationItem(0);
                }
            }
        });

        for (int i = 0; i < 3; i++) {
            actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }

        actionBar.selectTab(actionBar.getTabAt(selectedTab));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("SECTION", section);
        savedInstanceState.putString("WEEKDAY", weekday);
        savedInstanceState.putInt("SELECTED_TAB", selectedTab);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        section = savedInstanceState.getInt("SECTION");
        weekday = savedInstanceState.getString("WEEKDAY");
        selectedTab = savedInstanceState.getInt("SELECTED_TAB");
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // if returning from an activity:
        if (weekday != null) {
            switch (weekday) {
                case "Monday":
                    mViewPager.setCurrentItem(0);
                    weekday = null;
                    return;
                case "Tuesday":
                    mViewPager.setCurrentItem(1);
                    weekday = null;
                    return;
                case "Wednesday":
                    mViewPager.setCurrentItem(2);
                    weekday = null;
                    return;
                case "Thursday":
                    mViewPager.setCurrentItem(3);
                    weekday = null;
                    return;
                case "Friday":
                    mViewPager.setCurrentItem(4);
                    weekday = null;
                    return;
                case "Saturday":
                    mViewPager.setCurrentItem(5);
                    weekday = null;
                    return;
            }
        }
        // swipe or tap within the MainActivity:
        if (tab.getPosition() == 0 && section == 5) {
            // swipe entry from the "Students" tab:
            mViewPager.setCurrentItem(5); // Saturday section
        } else if (tab.getPosition() == 0) {
            // "Schedule" tab has been tapped:
            mViewPager.setCurrentItem(0); // Monday section
        } else if (tab.getPosition() == 1) {
            // "Students" tab has been tapped:
            mViewPager.setCurrentItem(6); // Students section
        } else if (tab.getPosition() == 2) {
            // "Add Student" tab has been tapped:
            mViewPager.setCurrentItem(7); // Add Student section
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // do nothing
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // do nothing
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            Bundle args = new Bundle();

            switch (position) {
                case 0:
                    fragment = new ScheduleFragment();
                    args.putString("WEEKDAY", "Monday");
                    fragment.setArguments(args);
                    return fragment;
                case 1:
                    fragment = new ScheduleFragment();
                    args.putString("WEEKDAY", "Tuesday");
                    fragment.setArguments(args);
                    return fragment;
                case 2:
                    fragment = new ScheduleFragment();
                    args.putString("WEEKDAY", "Wednesday");
                    fragment.setArguments(args);
                    return fragment;
                case 3:
                    fragment = new ScheduleFragment();
                    args.putString("WEEKDAY", "Thursday");
                    fragment.setArguments(args);
                    return fragment;
                case 4:
                    fragment = new ScheduleFragment();
                    args.putString("WEEKDAY", "Friday");
                    fragment.setArguments(args);
                    return fragment;
                case 5:
                    fragment = new ScheduleFragment();
                    args.putString("WEEKDAY", "Saturday");
                    fragment.setArguments(args);
                    return fragment;
                case 6:
                    return fragment = new StudentsFragment();
                case 7:
                    return fragment = new AddStudentFragment();
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale locale = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section_0).toUpperCase(locale);
                case 1:
                    return getString(R.string.title_section_1).toUpperCase(locale);
                case 2:
                    return getString(R.string.title_section_2).toUpperCase(locale);
            }
            return null;
        }
    }
}