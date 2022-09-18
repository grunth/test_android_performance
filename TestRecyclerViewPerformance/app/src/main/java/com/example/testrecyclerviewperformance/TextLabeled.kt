package com.example.testrecyclerviewperformance

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

class TextLabeled : LinearLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var hintView: TextView
    private var separator: View
    var textView: TextView

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_text_labeled, this, true)

        hintView = findViewById(R.id.hint)
        textView = findViewById(R.id.text)
        separator = findViewById(R.id.separator)

        val attr = context.theme.obtainStyledAttributes(attrs, R.styleable.TextLabeled, 0, 0)

    }
}