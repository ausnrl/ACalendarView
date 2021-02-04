package aus.kt.acalendarview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import java.util.*
import kotlin.math.abs
import kotlin.properties.Delegates


class ACalendarView : View {
    constructor(context: Context) : super(context) {
        setWillNotDraw(false)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setWillNotDraw(false)
        setAtrribute(attrs!!)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setWillNotDraw(false)
        setAtrribute(attrs!!)
    }

    private val LOG = "ACalendarView"
    private lateinit var mCallback: (positionI: Int, positionJ: Int) -> Unit
    private lateinit var info: () -> Boolean
    private var infoFlag = false
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var lineFirstEnd by Delegates.notNull<Int>() // horiLine 에서 계산 text에서 씀
    private var heightCount by Delegates.notNull<Int>() // setDate에서 값 생성
    private var textSize by Delegates.notNull<Int>() // 날짜 글자크기
    private var firstDay by Delegates.notNull<Int>() // 시작일 요일 위치
    private var lastDay by Delegates.notNull<Int>() // 시작일과 마지막일 총 일수
    private var startDay by Delegates.notNull<Int>() // 시작일
    private var startMonth by Delegates.notNull<Int>() // 해당월
    private var startYear by Delegates.notNull<Int>() // 해당연
    private var endDay by Delegates.notNull<Int>() // 시작일이 1일이 아닐경우 나오는 다음달의 마지막날
    private var endMonth by Delegates.notNull<Int>() // 시작일이 1일이 아닐경우 나오는 달
    private var endYear by Delegates.notNull<Int>() // 시작일이 1일이 아닐경우 나오는 연도
    private var monthLastDay by Delegates.notNull<Int>() // 이달의 마지막 날

    private var touchX by Delegates.notNull<Float>() // touch action down x 좌표
    private var touchY by Delegates.notNull<Float>() // touch action down y 좌표

    private lateinit var startX: IntArray // line x좌표 시작
    private lateinit var startY: IntArray // line y좌표 시작

    private lateinit var endX: IntArray // line x 좌표 끝
    private lateinit var endY: IntArray // line y 좌표 끝

    private lateinit var dateX: IntArray // 날짜 숫자 바로 왼쪽 - 스티커 라인 잡기위해
    private lateinit var dateY: IntArray // 날짜 숫자 바로 아래 - 스티커 라인 잡기위해

    private lateinit var touchXY: Array<IntArray> // 터치한 위치 날짜
    private lateinit var touchXYMonth: Array<IntArray> // 터치한 위치 월
    private lateinit var touchXYYear: Array<IntArray> // 터치한 위치 년도

    private lateinit var listDrawPosition: MutableList<DrawPosition> // drawAdd

    // Default Set
    private var dayOfWeektextSize = 50 // 요일 글자크기
    private var dayOfMonthtextSize = 45 // 날짜 글자크기

    private var autoTextSize = true

    private var dayOfMonthTextColor = Color.GRAY // 날짜 색상
    private var dayOfMonthSunTextColor = Color.RED // 날짜 일요일 색상
    private var dayOfMonthSatTextColor = Color.BLUE // 날짜 토요일 색상

    private var dayOfWeekTextColor = Color.GRAY // 요일 색상
    private var dayOfWeekSunTextColor = Color.RED // 요일 일요일 색상
    private var dayOfWeekSatTextColor = Color.BLUE // 요일 토요일 색상

    private var verticalLineColor = Color.GRAY // 세로 선 색상
    private var horizontalLineColor = Color.GRAY // 가로 선 색상
    private var lineColor = Color.GRAY // 전체 선 색상 - 가로, 세로 색상값을 따로 주지 않을 경우

    private var verticalLineVisible = false // 세로선 보임, 안보임
    private var horizontalLineVisible = true // 가로선 보임, 안보임
    private var dateCneter = false // 날짜 위치 정중앙
    private var lastMonthVisible = false // 지난달 날짜 보이게 할건지 여부

    fun setAutoTextSize(auto: Boolean) {
        autoTextSize = auto
    }

    fun setDayOfWeekTextSize(size: Int) {
        dayOfWeektextSize = size
    }

    fun setDayOfMonthTextSize(size: Int) {
        dayOfMonthtextSize = size
    }

    fun setLineColor(lineColor_: Int) {
        lineColor = lineColor_
    }

    fun setVerticalLineColor(vertLineColor_: Int) {
        verticalLineColor = vertLineColor_
    }

    fun setHorizontalLineColor(horiLineColor_: Int) {
        horizontalLineColor = horiLineColor_
    }

    fun setDayOfMonthTextColor(dateTextColor_: Int) {
        dayOfMonthTextColor = dateTextColor_
    }

    fun setDayOfMonthSunTextColor(dateSunTextColor_: Int) {
        dayOfMonthSunTextColor = dateSunTextColor_
    }

    fun setDayOfMonthSatTextColor(dateSatTextColor_: Int) {
        dayOfMonthSatTextColor = dateSatTextColor_
    }

    fun setHorizontalLineVisibility(horizontalLineVisible_: Boolean) {
        horizontalLineVisible = horizontalLineVisible_
    }

    fun setVerticalLineVisibility(verticalLineVisible_: Boolean) {
        verticalLineVisible = verticalLineVisible_
    }

    fun setDayOfWeekTextColor(dayTextColor_: Int) {
        dayOfWeekTextColor = dayTextColor_
    }

    fun setDayOfWeekSunTextColor(daySunTextColor_: Int) {
        dayOfWeekSunTextColor = daySunTextColor_
    }

    fun setDayOfWeekSatTextColor(daySatTextColor_: Int) {
        dayOfWeekSatTextColor = daySatTextColor_
    }

    fun setDateCenter(dateCneter_: Boolean) {
        dateCneter = dateCneter_
    }

    fun setLastMonthVisible(lastMonthVisible_: Boolean) {
        lastMonthVisible = lastMonthVisible_
    }

    fun getYear(i: Int, j: Int) = touchXYYear[i][j]
    fun getMonth(i: Int, j: Int) = touchXYMonth[i][j]
    fun getDay(i: Int, j: Int) = touchXY[i][j]

    fun drawAdd(drawPosition: DrawPosition) {
        listDrawPosition.add(drawPosition)
    }

    fun callback(touchCallback: (positionI: Int, positionJ: Int) -> Unit) {
        mCallback = touchCallback
    }

    private fun setAtrribute(attrs: AttributeSet) {
        setAutoTextSize(attrs.getAttributeBooleanValue(null, "autoTextSize", true))

        setDayOfMonthTextColor(attrs.getAttributeIntValue(null, "dateTextColor", Color.GRAY))
        setDayOfMonthSunTextColor(attrs.getAttributeIntValue(null, "dateSunTextColor", Color.RED))
        setDayOfMonthSatTextColor(attrs.getAttributeIntValue(null, "dateSatTextColor", Color.BLUE))

        setDayOfWeekTextColor(attrs.getAttributeIntValue(null, "dayTextColor", Color.GRAY))
        setDayOfWeekSunTextColor(attrs.getAttributeIntValue(null, "daySunTextColor", Color.RED))
        setDayOfWeekSatTextColor(attrs.getAttributeIntValue(null, "daySatTextColor", Color.BLUE))

        setHorizontalLineColor(attrs.getAttributeIntValue(null, "horiLineColor", Color.GRAY))
        setVerticalLineColor(attrs.getAttributeIntValue(null, "vertLineColor", Color.GRAY))
        setLineColor(attrs.getAttributeIntValue(null, "lineColor", Color.GRAY))

        setVerticalLineVisibility(attrs.getAttributeBooleanValue(null, "verticalLineVisible", false))
        setHorizontalLineVisibility(attrs.getAttributeBooleanValue(null, "horizontalLineVisible", true))
        setDateCenter(attrs.getAttributeBooleanValue(null, "dateCenter", false))
        setLastMonthVisible(attrs.getAttributeBooleanValue(null, "lastMonthVisible", false))

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w: Int
        val h: Int
        if (MeasureSpec.getSize(widthMeasureSpec) < MeasureSpec.getSize(heightMeasureSpec)) {
            w = specSize(widthMeasureSpec, widthMeasureSpec)
            h = specSize(heightMeasureSpec, widthMeasureSpec)
        } else {
            w = specSize(widthMeasureSpec, heightMeasureSpec)
            h = specSize(heightMeasureSpec, heightMeasureSpec)
        }
        if (autoTextSize) {
            textSize = h / (heightCount + 3) / 2 / 2
        }
        setMeasuredDimension(w, h)

//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun specSize(measureSpec: Int, wrap: Int): Int {
        var specSize = 0
        when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.UNSPECIFIED -> specSize = measureSpec
            MeasureSpec.AT_MOST -> {
                specSize = MeasureSpec.getSize(wrap)
            }
            MeasureSpec.EXACTLY -> specSize = MeasureSpec.getSize(measureSpec)
        }
        return specSize
    }

    private fun setInit() {
        startX = IntArray(7)
        endX = IntArray(7)
        startY = IntArray(heightCount)
        endY = IntArray(heightCount)
        dateX = IntArray(7)
        dateY = IntArray(heightCount)
        touchXY = Array(7) { IntArray(heightCount) }
        touchXYMonth = Array(7) { IntArray(heightCount) }
        touchXYYear = Array(7) { IntArray(heightCount) }

        listDrawPosition = mutableListOf()

        info = {
            Log.i(LOG, "Reference size for this calendar ::: x Length : ${endX[0] - startX[0]}")
            Log.i(LOG, "Reference size for this calendar ::: y Length : ${endY[0] - startY[0]}")
            Log.i(LOG, "Reference size for this calendar ::: x Length by Date : ${endX[0] - dateX[0]}")
            Log.i(LOG, "Reference size for this calendar ::: y Length by Date : ${endY[0] - dateY[0]}")
            true
        }

    }

    fun setDate(year: Int, month: Int, startDayOfMonth: Int) {
        if (::listDrawPosition.isInitialized && listDrawPosition.size > 0)
            listDrawPosition.clear()
        var day = startDayOfMonth
        val calendar = Calendar.getInstance()
        val thisDay = calendar[Calendar.DATE]
//        if (day <= thisDay) {
        // 이번달
        if (day >= 28) {
            val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            if (lastDay <= day) { // 30 <= 31
                day = lastDay
            }
        }
        startYear = year
        startMonth = month
        startDay = day
        if (day == 1) {
            // 같은 달
            endYear = year
            endMonth = month
            calendar[endYear, endMonth] = 1
            endDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        } else {
            // 다음달
            if (month > 10) {
                endYear = year + 1
                endMonth = 0
                endDay = day - 1
            } else {
                endYear = year
                endMonth = month + 1
                endDay = day - 1
            }
        }
        calendar[startYear, startMonth] = startDay
        val startMillis = calendar.timeInMillis
        firstDay = calendar[Calendar.DAY_OF_WEEK]
        monthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar[endYear, endMonth] = endDay
        val endMillis = calendar.timeInMillis
        val millis = endMillis - startMillis
        lastDay = (millis / (1000 * 60 * 60 * 24)).toInt() + 1 // 시작일로 부터 마지막일까지의 총 일수
        val totalDay = firstDay + lastDay
        heightCount = if (totalDay % 7 > 1) {
            totalDay / 7 + 1
        } else if (totalDay == 29) {
            4
        } else {
            5
        }
        setInit()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.x
                touchY = event.y
            }
            MotionEvent.ACTION_UP -> {
                var i = 0
                while (i < 7) {
                    var j = 0
                    while (j < heightCount) {
                        if (getTouch(i, j, touchX, touchY) && getTouch(i, j, event.x, event.y)) {
                            if (::mCallback.isInitialized) {
                                mCallback.invoke(i, j)
                                invalidate()
                            } else {
                                Log.e(LOG, "mCallback is Not init")
                            }
                            break
                        }
                        j++
                    }
                    i++
                }
            }
        }
        return true
    }

    private fun getTouch(i: Int, j: Int, x: Float, y: Float): Boolean {
        if (x <= endX[i] && y <= endY[j]) {
            if (i - 1 >= 0 || j - 1 >= 0) {
                if (x >= startX[i] && y >= startY[j]) {
                    if (touchXY[i][j] != 0) {
                        return true
                    }
                }
            } else {
                if (touchXY[i][j] != 0) {
                    return true
                }
            }
        }
        return false
    }

    override fun onDraw(canvas_: Canvas) {

        canvas_.apply {
            verticalLine()
            horizonLine()
            dayOfWeek()
            dayOfMonth()
            addDraw()


//            testDraw()
        }
    }

    // 세로 사이즈 / 주 - y 좌표
    fun Canvas.horizonLine() {
        paint.apply { color = if (horizontalLineColor != verticalLineColor) horizontalLineColor else lineColor } // line color

        lineFirstEnd = height / (heightCount + 1) / 2 // 요일 윗 줄
        val lineFirstStart = 0
        drawLine(0f, lineFirstStart.toFloat(), width.toFloat(), lineFirstStart + 1.toFloat(), paint)

        // 요일 아랫줄부터 주 단위로..
        val lineEnd = (height - lineFirstEnd) / heightCount
        var lineStart = lineFirstEnd
        // heightCount 만큼 반복
        for (i in 0 until heightCount) {
            startY[i] = lineStart
            val end = lineStart + lineEnd
            endY[i] = end
            if (horizontalLineVisible) {
                drawLine(0f, lineStart - 1.toFloat(), width.toFloat(), lineStart.toFloat(), paint)
            }
            // 날짜 세로 영역  lineStart - 1 ~ end
            lineStart = end
        }
        drawLine(0f, height - 1.toFloat(), width.toFloat(), height.toFloat(), paint) // 마지막 라인
        // 요일 세로 영역 lineFirstStart + 1 ~ lineStart - 1
    }

    // 가로 사이즈 / 7 - x 좌표
    private fun Canvas.verticalLine() {
        paint.apply { color = if (horizontalLineColor != verticalLineColor) verticalLineColor else lineColor } // line color

        // 달력 세로줄 start
        var lineStart = 0
        val lineEnd = width / 7
        for (i in 0..5) {
            startX[i] = lineStart
            val end = lineStart + lineEnd
            endX[i] = end
            if (verticalLineVisible) {
                drawLine(end - 1.toFloat(), 0f, end.toFloat(), height.toFloat(), paint)
            }
            lineStart = end
        }
        endX[6] = width
        startX[6] = endX[6] - lineEnd

    }

    private fun Canvas.dayOfWeek() { // 요일
        if (!autoTextSize)
            textSize = dayOfWeektextSize
        val y = lineFirstEnd / 2 + textSize / 2
        for (i in 0..6) {
            val x = endX[i] - (endX[i] - startX[i]) / 2
            var day = ""
            var textColor = dayOfWeekTextColor
            when (i + 1) {
                Calendar.SUNDAY -> {
                    textColor = dayOfMonthSunTextColor
                    day = "SUN"
                }
                Calendar.MONDAY -> day = "MON"
                Calendar.TUESDAY -> day = "TUE"
                Calendar.WEDNESDAY -> day = "WED"
                Calendar.THURSDAY -> day = "THU"
                Calendar.FRIDAY -> day = "FRI"
                Calendar.SATURDAY -> {
                    textColor = dayOfMonthSatTextColor
                    day = "SAT"
                }
            }
            paint.apply {
                textAlign = Paint.Align.CENTER
                textSize = this@ACalendarView.textSize.toFloat()
                color = textColor
            }
            drawText(day, x.toFloat(), y.toFloat(), paint)
        }
    }

    private fun Canvas.dayOfMonth() {
        if (!autoTextSize)
            textSize = dayOfMonthtextSize
        paint.apply {
            if (dateCneter) {
                textAlign = Paint.Align.CENTER
                textSize = this@ACalendarView.textSize.toFloat()
            } else {
                textAlign = Paint.Align.LEFT
                textSize = this@ACalendarView.textSize * 4 / 5.toFloat()
            }
        }
        var fDay = 0
        for (j in 0 until heightCount) {
            var y = if (dateCneter) { // 날짜 text 정렬
                startY[j] + (endY[j] - startY[j]) / 2
            } else {
                startY[j] + (endY[j] - startY[j]) / 7 + (paint.textSize / 2).toInt()
            }
            dateY[j] = y
            for (i in 0..6) {
                paint.color = dayOfMonthTextColor
                if (i == 6) { // 토요일
                    paint.color = dayOfMonthSatTextColor
                } else if (i == 0) { // 일요일
                    paint.color = dayOfMonthSunTextColor
                }
                var x = if (dateCneter) {  // 날짜 text 정렬
                    startX[i] + (endX[i] - startX[i]) / 2
                } else {
                    startX[i] + (endX[i] - startX[i]) / 6
                }
                dateX[i] = x
                if (firstDay <= i + 1 + j * 7) { // 시작일 칸부터 시작
                    if (lastDay + firstDay > i + 1 + j * 7) { // 시작일부터 마지막 일까지 터치 좌표 잡기위해 분기
                        var d = 0
                        if (monthLastDay >= startDay + (i + 1 + j * 7 - firstDay)) { // 시작일부터 해당월 말일까지
                            d = startDay + (i + 1 + j * 7 - firstDay)
                            touchXYMonth[i][j] = startMonth + 1 // 터치한날짜의 년도
                            touchXYYear[i][j] = startYear  // 터치한날짜의 월
                        } else { // 해당월 말일 이후(시작일이 1일이 아닐 경우에만)
                            if (fDay == 0) {
                                fDay = i + 1 + j * 7 - firstDay - 1
                            }
                            d = i + 1 + j * 7 - firstDay - fDay
                            touchXYMonth[i][j] = endMonth + 1  // 터치한날짜의 월
                            touchXYYear[i][j] = endYear // 터치한날짜의 년도
                        }
                        touchXY[i][j] = d
                        if (::listDrawPosition.isInitialized) {
                            for (drawPosition in listDrawPosition) {
                                if (drawPosition.isYMDInInitialized) {
                                    if (drawPosition.ymd.y == touchXYYear[i][j] && drawPosition.ymd.m == touchXYMonth[i][j] && drawPosition.ymd.d == touchXY[i][j]) {
                                        drawPosition.ij = IJ(i, j)
                                    }
                                }
                            }
                        }
                        if (d == startDay && touchXYMonth[i][j] == startMonth + 1) { // 시작일에 달 표시가 필요한 경우
                            drawText((startMonth + 1).toString() + "/" + d.toString(), x.toFloat(), y.toFloat(), paint)
                        } else if (d == 1 && touchXYMonth[i][j] == endMonth + 1) { // 시작일이 1일이 아닐경우 해당월 다음달에 달 표시가 필요한 경우
                            drawText((endMonth + 1).toString() + "/" + d.toString(), x.toFloat(), y.toFloat(), paint)
                        } else { // 달표시 필요없는 날짜
                            drawText(d.toString(), x.toFloat(), y.toFloat(), paint)
                        }
                    } else {
                        // 마지막 일 이후 날짜가 채워지지않은 빈칸
                        break
                    }
                } else {
                    if (lastMonthVisible) { // 시작일 이전 날짜(시작일에 따라 지난달 또는 시작일 이전일)
                        paint.color = Color.LTGRAY
                        drawText(lastMonth(i).toString(), x.toFloat(), y.toFloat(), paint)
                    }
                }
            }
        }
        if (!infoFlag)
            infoFlag = info.invoke()
    }

    private fun lastMonth(position: Int): Int {
        val calendar = Calendar.getInstance()
        var year = 0
        var month = 0

        if (startMonth < 1) {
            year = startYear - 1
            month = 12
        } else {
            year = startYear
            month = startMonth - 1
        }
        calendar[year, month] = 1
        val lastMax = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val lastD = lastMax - (firstDay - 1 - startDay) + position
        return if (lastMax < lastD) lastD - lastMax else lastD
    }

    fun Canvas.testDraw() {
        // drawTest..
    }

    private fun Canvas.addDraw() {
        if (::listDrawPosition.isInitialized) {
            for (drawPosition in listDrawPosition) {
                if (drawPosition.isIJInInitialized) {
                    var startDrawX = 0
                    var startDrawY = 0
                    when (drawPosition.startDraw) {
                        DrawPosition.StartDraw.TOP_LEFT -> {
                            startDrawX = startX[drawPosition.ij.i]
                            startDrawY = startY[drawPosition.ij.j]
                        }
                        DrawPosition.StartDraw.UNDER_DATE -> {
                            startDrawX = dateX[drawPosition.ij.i]
                            startDrawY = dateY[drawPosition.ij.j]
                        }
                    }

                    when (drawPosition.what) {
                        DrawPosition.What.IMAGE -> {
                            val bitmap = getBitmap(drawPosition.resourceId, drawPosition.autoResize, drawPosition.startDraw)
                            drawBitmap(bitmap, startDrawX + drawPosition.x, startDrawY + drawPosition.y, paint)
                            bitmap.recycle()
                        }
                        DrawPosition.What.TEXT -> {
                            paint.textSize = drawPosition.textSize.toFloat()
                            val bounds = Rect()
                            paint.getTextBounds(drawPosition.text, 0, drawPosition.text.length, bounds)
                            drawText(drawPosition.text, startDrawX + drawPosition.x, startDrawY + bounds.height() + drawPosition.y, paint)
                        }
                    }
                }
            }
        }
    }

    private fun getBitmap(resource: Int, autoResize: Boolean, startDraw : DrawPosition.StartDraw): Bitmap { // 이미지 사이즈 조절해야함
        var bitmap: Bitmap
        if (autoResize) {
            var drawable: Drawable = ContextCompat.getDrawable(context, resource)!!

            var x = endX[0] - startX[0]
            var y = endY[0] - dateY[0]
            if(startDraw == DrawPosition.StartDraw.UNDER_DATE){
                x = endX[0] - dateX[0]
                y = endY[0] - dateY[0]
            }

            val dx = drawable.intrinsicWidth
            val dy = drawable.intrinsicHeight

            var a: Int
            var b: Int
            val density = resources.displayMetrics.density

            if (abs(x - dx) <= abs(y - dy)) {
                a = y * dx / dy
                b = y
            } else {
                a = x
                b = x * dy / dx
            }

            bitmap = Bitmap.createBitmap((a / density).toInt(), (b / density).toInt(), Bitmap.Config.ARGB_8888);
            val can = Canvas(bitmap);
            drawable.setBounds(0, 0, can.width, can.height);
            drawable.draw(can);
        } else {
            bitmap = BitmapFactory.decodeResource(resources, resource)

            Log.d(LOG, "width : ${bitmap.width} , height : ${bitmap.height}")
        }

        return bitmap;
    }

}