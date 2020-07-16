package r2.adam.bashful


import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.annotation.ColorInt
import androidx.core.animation.doOnEnd
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import r2.adam.bashful.pac.*


class Bashful @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), BashfulPosition {

    companion object {
        private const val ANIMATION_DURATION = 350L
        private const val ANIMATION_REPEAT_COUNT = 0
    }

    private var state = BashfulState.CLOSE

    private var radius = 0f
    private var startHeight = 0f
    private var hideLevel = 0f

    private val backgroundPaint: Paint = Paint()
    private val backgroundRect = RectF(0f, 0f, 100f, 100f)

    @ColorInt
    private var gradientColor1 = 0

    @ColorInt
    private var gradientColor2 = 0

    private var anotherIcon1: Bitmap? = null
    private var anotherIcon1Rect = RectF(0f, 0f, 0f, 0f)

    private var anotherIcon2: Bitmap? = null
    private var anotherIcon2Rect = RectF(0f, 0f, 0f, 0f)

    private var anotherIcon3: Bitmap? = null
    private var anotherIcon3Rect = RectF(0f, 0f, 0f, 0f)

    private val anotherIconPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

    private val line = Path()
    private val linePaint = Paint()

    private val lineA = Path()
    private val lineB = Path()
    private val linePaintX = Paint()


    private var iconHeight = 0f
    private var iconWidth = 0f

    private var heightOffset = 4f

    private var bashfulSelectListener: BashfulSelectListener? = null
    fun setBashfulSelectListener(bashfulSelectListener: BashfulSelectListener) {
        this.bashfulSelectListener = bashfulSelectListener
    }

    private var progressAnimator: ValueAnimator? = null
    private var progress = 0f
        set(value) {
            if (field != value) {
                field = value
                backgroundRect.bottom = startHeight + lerp(0f, startHeight * (heightOffset - 1), value)

                val newAlpha = (field * 255).toInt()

                linePaint.alpha = newAlpha
                anotherIconPaint.alpha = newAlpha

                setLinePosition(value)
                setIconPosition(anotherIcon1Rect, 3, value, radius, iconWidth)
                setIconPosition(anotherIcon2Rect, 5, value, radius, iconWidth)
                setIconPosition(anotherIcon3Rect, 7, value, radius, iconWidth)

                progressAnimator = null
                postInvalidateOnAnimation()
            }
        }

    init {
        setOnClickListener { toggle() }
        attrs?.let { init(context, attrs, defStyleAttr) }
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.Bashful, defStyleAttr, 0)


        var drawableResId: Int = R.drawable.ic_video
        try {
            gradientColor1 = typedArray.getColor(R.styleable.Bashful_bashGradientColorStart, 0)
            gradientColor2 = typedArray.getColor(R.styleable.Bashful_bashGradientColorEnd, 0)
            drawableResId = typedArray.getResourceId(R.styleable.Bashful_bashBottomIcon, 0)

            iconHeight = typedArray.getDimension(R.styleable.Bashful_bashIconHeight, 0f)
            iconWidth = typedArray.getDimension(R.styleable.Bashful_bashIconWidth, 0f)

            hideLevel = typedArray.getDimension(R.styleable.Bashful_bashHideLevel, 0f)

        } finally {
            typedArray.recycle()
        }


        translationX += hideLevel

        // закрашиваем холст белым цветом
        backgroundPaint.color = Color.WHITE
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.isAntiAlias = true


        val drawable: VectorDrawableCompat? = context.getVectorDrawable(drawableResId)

        anotherIcon1 = BitmapUtil.getBitmapFromDrawable(drawable)  // TODO change icon
        anotherIcon2 = BitmapUtil.getBitmapFromDrawable(drawable)  // TODO change icon
        anotherIcon3 = BitmapUtil.getBitmapFromDrawable(drawable)  // TODO change icon

        setLineStyle(linePaint)
        setLineStyle(linePaintX)

    }


    var anim: ViewPropertyAnimator? = null
    override fun toggle() {
        if (anim != null) return

        if (state == BashfulState.CLOSE) {
            anim = animate()
                .translationX(-hideLevel)
                .setDuration(ANIMATION_DURATION)
                .setUpdateListener {
                    setPosition(it.animatedValue as Float)
                }
                .doOnEnd {
                    open {
                        anim = null
                    }
                }
        } else if (state == BashfulState.OPEN) {
            close {
                anim = animate()
                    .translationX(hideLevel)
                    .setDuration(ANIMATION_DURATION)
                    .setUpdateListener {
                        setPosition(1 - (it.animatedValue as Float))
                    }
                    .doOnEnd {
                        anim = null
                    }
            }
        }

        anim?.start()
    }

    override fun open(endCallback: UnitCallback) {
        progressAnimator?.cancel()

        progressAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                progress = it.animatedValue as Float
            }

            doOnEnd {
                state = BashfulState.OPEN
                endCallback.invoke()
            }
            interpolator = FastOutSlowInInterpolator()
            repeatCount = ANIMATION_REPEAT_COUNT
            duration = ANIMATION_DURATION
            start()
        }
    }

    override fun close(endCallback: UnitCallback) {
        progressAnimator?.cancel()

        progressAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                progress = 1 - (it.animatedValue as Float)
            }

            doOnEnd {
                state = BashfulState.CLOSE
                endCallback.invoke()
            }
            interpolator = FastOutSlowInInterpolator()
            repeatCount = ANIMATION_REPEAT_COUNT
            duration = ANIMATION_DURATION
            start()
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        Log.i("TTT", "OnDraw")

        canvas?.drawRoundRect(backgroundRect, radius, radius, backgroundPaint)

        canvas?.drawPath(line, linePaint)

        canvas?.drawPath(lineA, linePaintX) // More TODO
        canvas?.drawPath(lineB, linePaintX) // More TODO

        anotherIcon1?.let { canvas?.drawBitmap(it, null, anotherIcon1Rect, anotherIconPaint) }
        anotherIcon2?.let { canvas?.drawBitmap(it, null, anotherIcon2Rect, anotherIconPaint) }
        anotherIcon3?.let { canvas?.drawBitmap(it, null, anotherIcon3Rect, anotherIconPaint) }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(w, (h * heightOffset).toInt())

        linePaint.alpha = 0
        setPosition(0f, w / 2f)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val width = w.toFloat()

        radius = width.mid()
        startHeight = width

        backgroundRect.right = width
        backgroundRect.bottom = startHeight
    }

    private fun setIconPosition(rect: RectF, position: Int, percentage: Float, radius: Float, iconWidth: Float) {

        val iconMedium = iconWidth.mid()
        val centerRect = position * radius

        val left = radius - iconWidth.mid()
        val right = radius + iconWidth.mid()

        val top = (centerRect - iconMedium) * percentage
        val bottom = (centerRect + iconMedium) * percentage

        if (top < radius) return

        rect.set(left, top, right, bottom)
    }

    private fun setLinePosition(percentage: Float) {
        line.reset()

        line.moveTo(radius - (40 * percentage), radius * 2)
        line.lineTo(radius + (40 * percentage), radius * 2)
    }

    private fun setPosition(percentage: Float, mRadius: Float = radius) {

        lineA.reset()
        lineB.reset()

        val tt = iconHeight - 30
        lineA.moveTo(mRadius - tt.mid(), mRadius - (tt.mid() * percentage))
        lineA.lineTo(mRadius + (tt.mid() * percentage), mRadius + tt.mid())

        lineB.moveTo(mRadius - tt.mid(), mRadius + (tt.mid() * percentage))
        lineB.lineTo(mRadius + (tt.mid() * percentage), mRadius - tt.mid())

        postInvalidateOnAnimation()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {

            when (event.y) {
                in (0 * radius..2 * radius) -> toggle()

                in (2 * radius..4 * radius) -> {
                    if (state == BashfulState.OPEN) bashfulSelectListener?.click1()
                    else super.onTouchEvent(event)
                }
                in (4 * radius..6 * radius) -> {
                    if (state == BashfulState.OPEN) bashfulSelectListener?.click2()
                    else super.onTouchEvent(event)
                }
                in (6 * radius..8 * radius) -> {
                    if (state == BashfulState.OPEN) bashfulSelectListener?.click3()
                    else super.onTouchEvent(event)
                }
            }
        }

        return true
    }

    override fun setBackgroundColor(color: Int) {

    }

    override fun setBackground(background: Drawable?) {

    }

    override fun setBackgroundResource(resid: Int) {

    }


    /**
     * @see
     * [Stackoverflow](https://stackoverflow.com/a/7608516)
     */
    private fun setLineStyle(paint: Paint) = paint.apply {
        color = Color.BLACK                         // set the color
        strokeWidth = 5f                            // set the size
        isDither = true                             // set the dither to true
        style = Paint.Style.STROKE                  // set to STOKE
        strokeJoin = Paint.Join.ROUND               // set the join to round you want
        strokeCap = Paint.Cap.ROUND                 // set the paint cap to round too
        pathEffect = CornerPathEffect(2.5f)  // set the path effect when they join.
        isAntiAlias = true
    }
}

enum class BashfulState {
    OPEN,
    CLOSE
}

interface BashfulPosition {

    fun open(endCallback: UnitCallback = {})

    fun close(endCallback: UnitCallback = {})

    fun toggle()
}

interface BashfulSelectListener {

    fun click1()
    fun click2()
    fun click3()
}