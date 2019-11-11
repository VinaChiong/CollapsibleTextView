package me.vinachiong.collapsibletextview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.ColorRes
import org.jetbrains.anko.sp


/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
class CollapsibleTextView : View {
    private val measureWidthArray = FloatArray(1)
    private val mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val metrics = Paint.FontMetrics()
    @ColorRes
    private var textColorRes: Int = android.R.color.black
    private var textSize: Float = Utils.dp2px(14f)
    var text: String = ""
        set(value) {
            field = value
            setContents(arrayListOf(value))
        }
    private val contents: MutableList<String> = mutableListOf()

    var isSingleLine = true
        set(value) {
            field = value
            invalidate()
        }
    var minLines: Int = 5
        set(value) {
            field = value
            invalidate()
        }

    private var collapse: Boolean = true
        private set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    /**
     * 设置Content
     * @param data content内容
     */
    fun setContents(data: List<String>) {
        contents.clear()
        contents.addAll(data)
        invalidate()
    }

    /**
     * 添加单行内容
     * @param text 单行内容
     */
    fun addContents(text: String) {
        contents.add(text)
        invalidate()
    }

    fun toggle(): Boolean {
        collapse = !collapse
        return collapse
    }


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        if (null != attrs) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CollapsibleTextView)
            var index: Int // 复用变量
            (0..typedArray.indexCount).forEach {
                index = typedArray.getIndex(it)
                when (typedArray.getIndex(it)) {
                    R.styleable.CollapsibleTextView_textColor -> {
                        textColorRes = typedArray.getInteger(index, android.R.color.black)
                    }
                    R.styleable.CollapsibleTextView_textSize -> {
                        textSize = typedArray.getDimension(index, sp(14f).toFloat())
                    }
                    R.styleable.CollapsibleTextView_android_text -> {
                        text = typedArray.getString(index) ?: ""
                    }
                }
            }
            typedArray.recycle()
        }

        mTextPaint.textSize = textSize
        mTextPaint.color = context.compatColor(textColorRes)

        mTextPaint.getFontMetrics(metrics)
    }

    private fun getContentByCollapseStatus(): List<String> {
        return if (collapse && contents.size >= minLines) {
            contents.slice(0 until minLines)
        } else {
            contents
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //        val parentWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val parentWidthSize = MeasureSpec.getSize(widthMeasureSpec)
//        val parentHeightMode = MeasureSpec.getMode(heightMeasureSpec)
//        val parentHeightSize = MeasureSpec.getSize(heightMeasureSpec)


        // 高度计算的原则 = padding + margin + contentHeight
        // 宽度不处理，直接取与父类的Width
        val measuredWidth: Int = parentWidthSize
        val contentHeight = if (isSingleLine) {
            measureHeightForSingleLineText()
        } else {
            measureHeightForBreakText()
        }

        val specMode = MeasureSpec.getMode(heightMeasureSpec)
        val specSize = MeasureSpec.getSize(heightMeasureSpec)
        val contentHeightWithPadding = paddingTop + contentHeight + paddingBottom

        val measuredHeight = when (specMode) {
            MeasureSpec.EXACTLY -> Math.min(specSize, (contentHeightWithPadding).toInt())
            else -> (contentHeightWithPadding).toInt()
        }

        // 高度根据是否要展开来确定
        Log.d(TAG, "contentHeight = $contentHeight, measuredHeight = $measuredHeight")
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    /**
     * 单行模式下，按[contents]条数、是否[collapse]、[mTextPaint]的字体大小，
     * 计算内容高度，不包含padding
     */
    private fun measureHeightForSingleLineText(): Float =
        getContentByCollapseStatus().size * mTextPaint.fontSpacing + metrics.descent


    /**
     * 换行模式下，按[contents]条数、是否[collapse]、[mTextPaint]的字体大小，
     * 计算内容高度，不包含padding
     */
    private fun measureHeightForBreakText(): Float {
        var contentHeight = 0f
        val validWidth = measuredWidth.toFloat() - paddingLeft - paddingEnd
        getContentByCollapseStatus().forEach { content ->
            val length = content.length
            var start = 0
            var count: Int
            var yOffset = 0f

            while (start < length) {
                yOffset += mTextPaint.fontSpacing
                count = mTextPaint.breakText(content, start, length, true, validWidth, measureWidthArray)
                start += count
            }
            contentHeight += yOffset
        }
        contentHeight += metrics.descent

        return contentHeight
    }

    override fun onDraw(canvas: Canvas) {
        if (isSingleLine) {
            // 单行，超长截断
            singleLineDrawText(canvas)
        } else {
            // 换行，高度按内容
            drawWithBreakText(canvas)
        }
    }

    /**
     * 以单行模式，绘制文本，内容超长则截断尾部省略
     * @param canvas Canvas
     */
    private fun singleLineDrawText(canvas: Canvas) {
        // 第n条内容的Y偏移，起始需要加上paddingTop
        var contentOffsetY = paddingTop.toFloat()
        val validWidth = width.toFloat() - paddingLeft - paddingEnd
        val startX = paddingLeft.toFloat()
        getContentByCollapseStatus().forEach { content ->
            // 当前行draw完后，补上占用的偏移
            contentOffsetY += mTextPaint.fontSpacing
            val ellipsisContent = TextUtils.ellipsize(content, mTextPaint, validWidth, TextUtils.TruncateAt.END)
            canvas.drawText(ellipsisContent, 0, ellipsisContent.length, startX,
                            contentOffsetY,  // 第n条内容在当前View的Y偏移，作为起始值
                            mTextPaint)
            contentOffsetY += metrics.leading
        }
    }

    /**
     * 以换行模式，绘制文本
     * @param canvas Canvas
     */
    private fun drawWithBreakText(canvas: Canvas) {
        // 第n条内容的Y偏移，起始需要加上paddingTop
        var contentOffsetY = paddingTop.toFloat()
        val validWidth = width.toFloat() - paddingLeft - paddingEnd
        val startX = paddingLeft.toFloat()
        getContentByCollapseStatus().forEach { content ->
            val length = content.length
            var start = 0
            var count: Int
            var yOffset = 0f
            Log.d(TAG, "draw content='$content' len=$length")

            while (start < length) {
                yOffset += mTextPaint.fontSpacing

                count = mTextPaint.breakText(content, start, length, true, validWidth, measureWidthArray)
                canvas.drawText(content, start, start + count, startX,
                                yOffset + contentOffsetY,  // 第n条内容在当前View的Y偏移，作为起始值
                                mTextPaint)
                start += count
            }
            // 当前行draw完后，补上占用的偏移
            contentOffsetY += yOffset
        }
    }

    companion object {
        private const val TAG = "CollapsibleTextView"
    }
}