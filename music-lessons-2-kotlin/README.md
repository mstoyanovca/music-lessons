An update of the Music Lessons android app:
- removed the "Add Student" tab;
- removed the pdf export;
- added new DB schema, allowing unlimited number of different type phone numbers, instead of hard coded ones;
- replaced the spinners for time-picking with standard number pickers;
- added floating action buttons within CoordinatorLayout;
- added ConstraintLayouts with centered progress bars for all DB queries;
- the DAO layer was rewritten with the room persistence ORM library;
- added data migration during update;

Tested backup:
- adb shell bmgr fullbackup com.mstoyanov.musiclessons;
- run next command to find the "current" token("39a86b4b3092e3b4" in this case);
- adb shell dumpsys backup;
- adb shell bmgr restore 39a86b4b3092e3b4 com.mstoyanov.musiclessons2;
- uninstall from the device, install from IntelliJ: data restored;

Tested updating:
- tested updating from Music Lessons 1 to Music Lessons 2;
- updated from a signed with the same certificate and the same applicationId *.apk;
- the application was recognized as an update, data was preserved;