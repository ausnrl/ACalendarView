package aus.kt.acalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import aus.kt.acalendarview.ACalendarView
import aus.kt.acalendarview.DrawPosition
import aus.kt.acalendarview.IJ
import aus.kt.acalendarview.YMD

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ACalendarView>(R.id.calendarview).apply {
            setDate(2021, 1, 1)
            setVerticalLineVisibility(true)
            for(i in 0..10) {
                drawAdd(DrawPosition().drawImage(DrawPosition.StartDraw.UNDER_DATE, YMD(2021, 2, i), R.drawable.ic_sample,0f, 0f, true))
            }
            callback{i, j->
                Log.d("CAL", "year : ${getYear(i, j)} , month : ${getMonth(i, j)} , day : ${getDay(i, j)}")
                drawAdd(DrawPosition().drawText(DrawPosition.StartDraw.UNDER_DATE, IJ(i, j), "Hello", 0f, 0f, 30))
                drawAdd(DrawPosition().drawText(DrawPosition.StartDraw.UNDER_DATE, IJ(i, j), "World!!", 0f, 30f, 30))
            }
        }
    }
}