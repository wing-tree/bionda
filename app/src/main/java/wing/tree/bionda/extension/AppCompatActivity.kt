package wing.tree.bionda.extension

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.timepicker.MaterialTimePicker

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
