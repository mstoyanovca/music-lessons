package music_lessons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Weekday(private var value: String) : Parcelable {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    fun displayValue(): String {
        return value
    }

}
