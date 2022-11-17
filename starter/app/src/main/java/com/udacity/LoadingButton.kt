package com.udacity

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnRepeat
import androidx.core.animation.doOnStart
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var buttonText: String = ""
    private var displayedText: String = ""
    private var circleColor: Int = 0
    private var buttonDownloadText : String = ""
    private var buttonTextColor: Int = Color.GRAY
    private var buttonBackgroundColor: Int = 0
    private var buttonDownloadingBackgroundColor: Int = 0
    private var stopAnimation = false
    private var widthSize = 0
    private var heightSize = 0
    private var progress: Int = 0
    private var loadingProgress = 0f
    private var valueAnimator = ValueAnimator()
    private var progressArc = RectF()

    init {
        Log.i("LoadingButton", "Inside init")
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_buttonBackgroundColor,
                Color.parseColor("#FF07C2AA"))
            buttonDownloadingBackgroundColor = getColor(R.styleable.LoadingButton_buttonDownloadingBackgroundColor,
                Color.parseColor("#FF004349"))

            circleColor = getColor(R.styleable.LoadingButton_circleColor, Color.parseColor("#FFF9A825"))

            buttonText = getString(R.styleable.LoadingButton_buttonText) ?: ""
            buttonDownloadText = getString(R.styleable.LoadingButton_buttonDownloadText) ?: ""

            buttonTextColor = getColor(R.styleable.LoadingButton_buttonTextColor, Color.parseColor("#FFFFFFFF"))
            displayedText = buttonText
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER

        textSize = 75.0f
        typeface = Typeface.create( "",Typeface.BOLD)

    }
    override fun onDraw(canvas: Canvas) {
        Log.i("LoadingButton", "Inside onDraw")
        super.onDraw(canvas)
        val rectArea = Rect()
        paint.color = buttonBackgroundColor
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
        Log.i("LoadingButton", "OnDraw: Button State: ${buttonState}")

        if (buttonState == ButtonState.Loading) {
            Log.i("LoadingButton", "Inside onDraw check buttonState is Loading")
            paint.color = buttonDownloadingBackgroundColor
            val progressRect = progress / 1000f * widthSize
            canvas.drawRect(0f, 0f, progressRect, heightSize.toFloat(), paint)

            val sweepAngle = progress / 1000f * 360f
            paint.color = circleColor

            canvas.drawArc(progressArc, 0f, sweepAngle, true, paint)
        }
        paint.color = buttonTextColor
        Log.i("LoadingButton", "DisplayedText: ${displayedText}, " +
                "displayedText.length: + ${displayedText.length}, " +
                "rectArea: ${rectArea.toString()}" )

        paint.getTextBounds(displayedText, 0, displayedText.length, rectArea)

        val centerbutton = measuredHeight.toFloat() / 2 - rectArea.centerY()
        canvas.drawText(displayedText, widthSize / 2f, centerbutton, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.i("LoadingButton", "Inside onMeasure")
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val width: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val height: Int = resolveSizeAndState(
            MeasureSpec.getSize(width),
            heightMeasureSpec,
            0
        )
        widthSize = width
        heightSize = height
        setMeasuredDimension(width, height)
        progressArc = RectF(widthSize - 200f, heightSize / 2 - 40f, widthSize.toFloat() - 150f, heightSize / 2 + 40f)

    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                Log.i("LoadingButton", "buttonState:inside when(new):${new}")
                valueAnimator = ValueAnimator.ofInt(0, 900).apply {
                    addUpdateListener {
                        progress = animatedValue as Int
                        Log.i("LoadingButton", "Button Loading - Progress:<${progress}>")
                        invalidate()
                    }
                    duration = 20000
                    doOnStart {
                        Log.i("LoadingButton", "Button Loading - Start of Processing")
                        isEnabled = false
                        displayedText = buttonDownloadText
                    }

                    doOnEnd {
                        Log.i("LoadingButton", "Button Loading - End of Processing")
                        displayedText = buttonText
                        isEnabled = true
                        progress = 0
                    }
                    Log.i("LoadingButton", "Button Loading - Starting Animation")
                    start()
                }
            }
            ButtonState.Clicked -> {
                Log.i("LoadingButton", "Button Clicked:${new}")
                displayedText = buttonDownloadText
                buttonState = ButtonState.Loading
                isEnabled = false
            }
            ButtonState.Completed -> {
                Log.i("LoadingButton", "Button Completed:${new}")
                isEnabled = true
                displayedText = buttonText
            }
        }
        invalidate()
    }


    fun downloadStart() {
        Log.i("LoadingButton", "Inside downloadstart()")
        buttonState = ButtonState.Loading
    }
    fun downloadCompleted() {
        Log.i("LoadingButton", "Inside downloadCompleted()")
        val fraction = valueAnimator.animatedFraction
        valueAnimator.setCurrentFraction(fraction+0.1f)
        valueAnimator.duration = 1000
        valueAnimator.start()
    }

    override fun performClick(): Boolean {
        Log.i("LoadingButton", "Inside performClick()")
        super.performClick()
        isClickable = true
        return true
    }

    fun animationComplete() {
        Log.i("LoadingButton", "Inside animationComplete")
        stopAnimation = true
        buttonState = ButtonState.Completed
        isEnabled = true
    }

    fun printLoadingButtonState() {
        Log.i("LoadingButton", "Called from outside: Button State: ${buttonState}")
    }

    fun cleanAnimationAndEverything() {
        Log.i("LoadingButton", "Inside cleanAnimationAndEverything()")
        valueAnimator.cancel()
        stopAnimation = true
        buttonState = ButtonState.Completed
        isEnabled = true
    }
}