package wing.tree.bionda.extension

import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.timepicker.MaterialTimePicker
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.minute

fun AppCompatActivity.showMaterialTimePicker(
    calendar: Calendar,
    onPositiveButtonClick: (hour: Int, minute: Int) -> Unit
) {
    showMaterialTimePicker(
        hour = calendar.hourOfDay,
        minute = calendar.minute,
        onPositiveButtonClick = onPositiveButtonClick
    )
}

fun AppCompatActivity.showMaterialTimePicker(
    hour: Int,
    minute: Int,
    onPositiveButtonClick: (hour: Int, minute: Int) -> Unit
) {
    val materialTimePicker = MaterialTimePicker.Builder()
        .setHour(hour)
        .setMinute(minute)
        .build()

    materialTimePicker.also {
        it.addOnPositiveButtonClickListener { _ ->
            onPositiveButtonClick(
                it.hour,
                it.minute
            )
        }

        it.show(supportFragmentManager, it.tag)
    }
}
