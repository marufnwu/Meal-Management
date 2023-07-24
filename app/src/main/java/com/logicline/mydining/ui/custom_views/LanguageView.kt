package com.logicline.mydining.ui.custom_views

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.logicline.mydining.R
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LangUtils
import com.logicline.mydining.utils.LanguageSelectorDialog

class LanguageView(context: Context, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatTextView(context, attrs) {
    init {
        this.setText(LangUtils.getFullLanguage(context, LangUtils.getLanguage(context)))
        this.textSize = 14f
        this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.translate, 0, 0, 0);
        this.setOnClickListener {
            LanguageSelectorDialog.Builder(context)
                .build()
                .show()
        }
    }
}