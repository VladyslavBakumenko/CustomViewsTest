package com.example.customviewstest

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.customviewstest.databinding.PartButtonsBinding

enum class BottomButtonAction {
    POSITIVE, NEGATIVE
}

typealias OnBottomButtonsActionListener = (BottomButtonAction) -> Unit

class BottomButtonsView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val binding: PartButtonsBinding

    private var listener: OnBottomButtonsActionListener? = null

    var isProgressMode: Boolean = false
    set(value) {
        field = value
        with(binding) {
            if (isProgressMode) {
                positiveButton.visibility = INVISIBLE
                negativeButton.visibility = INVISIBLE
                progress.visibility = VISIBLE
            } else {
                positiveButton.visibility = VISIBLE
                negativeButton.visibility = VISIBLE
                progress.visibility = INVISIBLE
            }
        }
    }


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.bottomButtonsStyle
    )

    constructor(context: Context) : this(context, null)

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.part_buttons, this, true)
        binding = PartButtonsBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
        initListeners()
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, desStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.BottomButtonsView,
            defStyleAttr,
            desStyleRes
        )
        with(binding) {
            val positiveButtonText =
                typedArray.getString(R.styleable.BottomButtonsView_bottomPositiveButtonText)
            setPositiveButtonText(positiveButtonText)

            val negativeButtonText =
                typedArray.getString(R.styleable.BottomButtonsView_bottomNegativeButtonText)
            setNegativeButtonText(negativeButtonText)


            val positiveButtonBgColor =
                typedArray.getColor(
                    R.styleable.BottomButtonsView_bottomPositiveBackgroundColor,
                    Color.BLACK
                )
            positiveButton.backgroundTintList = ColorStateList.valueOf(positiveButtonBgColor)

            val negativeButtonBgColor =
                typedArray.getColor(
                    R.styleable.BottomButtonsView_bottomNegativeBackgroundColor,
                    Color.WHITE
                )
            negativeButton.backgroundTintList = ColorStateList.valueOf(negativeButtonBgColor)

            isProgressMode =
                typedArray.getBoolean(R.styleable.BottomButtonsView_bottomProgressMode, false)
        }
        typedArray.recycle()
    }

    private fun initListeners() {
        binding.positiveButton.setOnClickListener {
            this.listener?.invoke(BottomButtonAction.POSITIVE)
        }
        binding.negativeButton.setOnClickListener {
            this.listener?.invoke(BottomButtonAction.NEGATIVE)
        }
    }

    fun setListener(listener: OnBottomButtonsActionListener) {
        this.listener = listener
    }

    fun setPositiveButtonText(text: String?) {
       binding.positiveButton.text = text ?: "Ok"
    }

    fun setNegativeButtonText(text: String?) {
        binding.negativeButton.text = text ?: "Cancel"
    }

    override fun onSaveInstanceState(): Parcelable? {
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
    }




}