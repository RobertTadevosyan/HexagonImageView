package com.robert.hexagon.image.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class HexagonImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AppCompatImageView(context, attrs) {
    private val mDrawableRect = RectF()
    private val mBorderRect = RectF()
    private val mShaderMatrix = Matrix()
    private val mBitmapPaint = Paint()
    private val mBorderPaint = Paint()
    private val mBackgroundPaint = Paint()
    private var mBorderColor = DEFAULT_BORDER_COLOR
    private var mBorderWidth = DEFAULT_BORDER_WIDTH
    private var mHexagonBackgroundColor = DEFAULT_BACKGROUND_COLOR
    private var mImageAlpha = DEFAULT_IMAGE_ALPHA
    private var mBitmap: Bitmap? = null
    private var mBitmapCanvas: Canvas? = null
    private var mDrawableRadius = 0f
    private var mBorderRadius = 0f
    private var mColorFilter: ColorFilter? = null
    private var mInitialized = false
    private var mRebuildShader = false
    private var mDrawableDirty = false
    private var mBorderOverlay = false
    private var mDisableHexagonalTransformation = false
    private val path: Path = Path()

    var borderWidth: Int
        get() = mBorderWidth
        set(borderWidth) {
            if (borderWidth == mBorderWidth) {
                return
            }
            mBorderWidth = borderWidth
            mBorderPaint.strokeWidth = borderWidth.toFloat()
            updateDimensions()
            invalidate()
        }
    var isBorderOverlay: Boolean
        get() = mBorderOverlay
        set(borderOverlay) {
            if (borderOverlay == mBorderOverlay) {
                return
            }
            mBorderOverlay = borderOverlay
            updateDimensions()
            invalidate()
        }
    var isDisableHexagonalTransformation: Boolean
        get() = mDisableHexagonalTransformation
        set(disableHexagonalTransformation) {
            if (disableHexagonalTransformation == mDisableHexagonalTransformation) {
                return
            }
            mDisableHexagonalTransformation = disableHexagonalTransformation
            if (disableHexagonalTransformation) {
                mBitmap = null
                mBitmapCanvas = null
                mBitmapPaint.shader = null
            } else {
                initializeBitmap()
            }
            invalidate()
        }

    var borderColor: Int
        get() = mBorderColor
        set(borderColor) {
            if (borderColor == mBorderColor) {
                return
            }
            mBorderColor = borderColor
            mBorderPaint.color = borderColor
            invalidate()
        }
    var hexagonBackgroundColor: Int
        get() = mHexagonBackgroundColor
        set(hexagonBackgroundColor) {
            if (hexagonBackgroundColor == mHexagonBackgroundColor) {
                return
            }
            mHexagonBackgroundColor = hexagonBackgroundColor
            mBackgroundPaint.color = hexagonBackgroundColor
            invalidate()
        }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.HexagonImageView, defStyle, 0)
        mBorderWidth = a.getDimensionPixelSize(
            R.styleable.HexagonImageView_strokeWidth,
            DEFAULT_BORDER_WIDTH
        )
        mBorderColor =
            a.getColor(
                R.styleable.HexagonImageView_borderColor,
                DEFAULT_BORDER_COLOR
            )
        mBorderOverlay =
            a.getBoolean(
                R.styleable.HexagonImageView_borderOverlay,
                DEFAULT_BORDER_OVERLAY
            )
        mHexagonBackgroundColor = a.getColor(
            R.styleable.HexagonImageView_backgroundColor,
            DEFAULT_BACKGROUND_COLOR
        )
        a.recycle()
        initPaint()
    }

    override fun setScaleType(scaleType: ScaleType) {
        require(scaleType == SCALE_TYPE) {
            String.format(
                "ScaleType %s not supported.",
                scaleType
            )
        }
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        require(!adjustViewBounds) { "adjustViewBounds not supported." }
    }

    @SuppressLint("CanvasSize")
    override fun onDraw(canvas: Canvas) {
        if (mDisableHexagonalTransformation) {
            super.onDraw(canvas)
            return
        }
        if (mHexagonBackgroundColor != Color.TRANSPARENT) {
            canvas.drawPath(
                path,
                mBackgroundPaint
            )
        }
        if (mBitmap != null) {
            if (mDrawableDirty && mBitmapCanvas != null) {
                mDrawableDirty = false
                val drawable = drawable
                drawable.setBounds(0, 0, mBitmapCanvas!!.width, mBitmapCanvas!!.height)
                drawable.draw(mBitmapCanvas!!)
            }
            if (mRebuildShader) {
                mRebuildShader = false
                val bitmapShader =
                    BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                bitmapShader.setLocalMatrix(mShaderMatrix)
                mBitmapPaint.shader = bitmapShader
            }
            canvas.drawPath(
                path,
                mBitmapPaint
            )
        }
        if (mBorderWidth > 0) {
            canvas.drawPath(
                path,
                mBorderPaint
            )
        }
    }

    override fun invalidateDrawable(dr: Drawable) {
        mDrawableDirty = true
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateDimensions()
        invalidate()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        updateDimensions()
        invalidate()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        updateDimensions()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.UNSPECIFIED)
        val height = MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.UNSPECIFIED)
        val size = min(width, height).let { it + it / 80 }
        setMeasuredDimension(size, size)
        calculatePath((size / 2f).coerceAtMost(size / 2f))
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        initializeBitmap()
        invalidate()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeBitmap()
        invalidate()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        initializeBitmap()
        invalidate()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        initializeBitmap()
        invalidate()
    }

    override fun setImageAlpha(alpha: Int) {
        var alp = alpha
        alp = alp and 0xFF
        if (alp == mImageAlpha) {
            return
        }
        mImageAlpha = alp
        if (mInitialized) {
            mBitmapPaint.alpha = alp
            invalidate()
        }
    }

    override fun getImageAlpha(): Int {
        return mImageAlpha
    }

    override fun setColorFilter(cf: ColorFilter) {
        if (cf === mColorFilter) {
            return
        }
        mColorFilter = cf
        if (mInitialized) {
            mBitmapPaint.colorFilter = cf
            invalidate()
        }
    }

    override fun getColorFilter(): ColorFilter {
        return mColorFilter!!
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mDisableHexagonalTransformation) {
            super.onTouchEvent(event)
        } else inTouchableArea(event.x, event.y) && super.onTouchEvent(event)
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else try {
            val bitmap: Bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(
                    COLOR_DRAWABLE_DIMENSION,
                    COLOR_DRAWABLE_DIMENSION,
                    BITMAP_CONFIG
                )
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    BITMAP_CONFIG
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun initializeBitmap() {
        mBitmap = getBitmapFromDrawable(drawable)
        mBitmapCanvas = if (mBitmap != null && mBitmap!!.isMutable) {
            Canvas(mBitmap!!)
        } else {
            null
        }
        if (!mInitialized) {
            return
        }
        if (mBitmap != null) {
            updateShaderMatrix()
        } else {
            mBitmapPaint.shader = null
        }
    }

    private fun updateDimensions() {
        mBorderRect.set(calculateBounds())
        mBorderRadius =
            ((mBorderRect.height() - mBorderWidth) / 2.0f).coerceAtMost((mBorderRect.width() - mBorderWidth) / 2.0f)
        mDrawableRect.set(mBorderRect)
        if (!mBorderOverlay && mBorderWidth > 0) {
            mDrawableRect.inset(mBorderWidth - 1.0f, mBorderWidth - 1.0f)
        }
        mDrawableRadius = (mDrawableRect.height() / 2.0f).coerceAtMost(mDrawableRect.width() / 2.0f)
        updateShaderMatrix()
    }

    private fun calculateBounds(): RectF {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        val sideLength = availableWidth.coerceAtMost(availableHeight)
        val left = paddingLeft + (availableWidth - sideLength) / 2f
        val top = paddingTop + (availableHeight - sideLength) / 2f
        return RectF(left, top, left + sideLength, top + sideLength)
    }

    private fun updateShaderMatrix() {
        if (mBitmap == null) {
            return
        }
        val scale: Float
        var dx = 0f
        var dy = 0f
        mShaderMatrix.set(null)
        val bitmapHeight = mBitmap!!.height
        val bitmapWidth = mBitmap!!.width
        if (bitmapWidth * mDrawableRect.height() > mDrawableRect.width() * bitmapHeight) {
            scale = mDrawableRect.height() / bitmapHeight.toFloat()
            dx = (mDrawableRect.width() - bitmapWidth * scale) * 0.5f
        } else {
            scale = mDrawableRect.width() / bitmapWidth.toFloat()
            dy = (mDrawableRect.height() - bitmapHeight * scale) * 0.5f
        }
        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(
            (dx + 0.5f).toInt() + mDrawableRect.left,
            (dy + 0.5f).toInt() + mDrawableRect.top
        )
        mRebuildShader = true
    }

    private fun initPaint() {
        mInitialized = true
        super.setScaleType(SCALE_TYPE)
        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.isDither = true
        mBitmapPaint.isFilterBitmap = true
        mBitmapPaint.alpha = mImageAlpha
        mBitmapPaint.colorFilter = mColorFilter
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.isAntiAlias = true
        mBorderPaint.color = mBorderColor
        mBorderPaint.strokeWidth = mBorderWidth.toFloat()
        mBackgroundPaint.style = Paint.Style.FILL
        mBackgroundPaint.isAntiAlias = true
        mBackgroundPaint.color = mHexagonBackgroundColor
    }

    private fun calculatePath(radius: Float) {
        val borderWidthHalf = borderWidth / 2
        val halfRadius = radius / 2f
        val triangleHeight = (sqrt(3.0f) * halfRadius)
        val centerX = measuredWidth / 2f
        val centerY = measuredHeight / 2f
        path.reset()
        path.moveTo(centerX, centerY + radius - borderWidthHalf)
        path.lineTo(centerX - triangleHeight, centerY + halfRadius)
        path.lineTo(centerX - triangleHeight, centerY - halfRadius)
        path.lineTo(centerX, centerY - radius + borderWidthHalf)
        path.lineTo(centerX + triangleHeight, centerY - halfRadius)
        path.lineTo(centerX + triangleHeight, centerY + halfRadius)
        path.close()
        invalidate()
    }


    private fun inTouchableArea(x: Float, y: Float): Boolean {
        return if (mBorderRect.isEmpty) {
            true
        } else (x - mBorderRect.centerX()).toDouble()
            .pow(2.0) + (y - mBorderRect.centerY()).toDouble()
            .pow(2.0) <= mBorderRadius.toDouble().pow(2.0)
    }


    companion object {
        private val SCALE_TYPE = ScaleType.CENTER_CROP
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private const val COLOR_DRAWABLE_DIMENSION = 2
        private const val DEFAULT_BORDER_WIDTH = 0
        private const val DEFAULT_BORDER_COLOR = Color.BLACK
        private const val DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT
        private const val DEFAULT_IMAGE_ALPHA = 255
        private const val DEFAULT_BORDER_OVERLAY = false
    }
}