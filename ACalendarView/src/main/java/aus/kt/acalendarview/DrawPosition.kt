package aus.kt.acalendarview

import kotlin.properties.Delegates

class DrawPosition {

    enum class What{
        IMAGE, TEXT
    }

    enum class StartDraw{
        TOP_LEFT, UNDER_DATE
    }

    lateinit var what : What
    lateinit var startDraw : StartDraw
    lateinit var ymd : YMD
    lateinit var ij : IJ
    val isYMDInInitialized get() = ::ymd.isInitialized
    val isIJInInitialized get() = ::ij.isInitialized
    var resourceId by Delegates.notNull<Int>()
    lateinit var text : String
    var x by Delegates.notNull<Float>()
    var y by Delegates.notNull<Float>()
    var autoResize by Delegates.notNull<Boolean>()
    var textSize by Delegates.notNull<Int>()


    fun <T> drawImage(startDraw : StartDraw, ymdOrIJ : T, resourceId : Int, x : Float, y : Float, autoResize : Boolean) : DrawPosition {
        what = What.IMAGE
        this.startDraw = startDraw
        when(ymdOrIJ){
            is IJ -> this.ij = ymdOrIJ
            is YMD -> this.ymd = ymdOrIJ
        }
        this.resourceId = resourceId
        this.x = x
        this.y = y
        this.autoResize = autoResize
        return this
    }

    fun <T> drawText(startDraw : StartDraw, ymdOrIJ : T, text : String, x : Float, y : Float, textSize : Int = 12) : DrawPosition {
        what = What.TEXT
        this.startDraw = startDraw
        when(ymdOrIJ){
            is IJ -> this.ij = ymdOrIJ
            is YMD -> this.ymd = ymdOrIJ
        }
        this.text = text
        this.x = x
        this.y = y
        this.textSize = textSize
        return this
    }

}