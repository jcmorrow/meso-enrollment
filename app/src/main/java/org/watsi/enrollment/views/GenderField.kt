package org.watsi.enrollment.views

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.view_gender_field.view.gender_button_female
import kotlinx.android.synthetic.main.view_gender_field.view.gender_button_male
import kotlinx.android.synthetic.main.view_gender_field.view.gender_error_message
import org.watsi.enrollment.R
import org.watsi.enrollment.domain.entities.Gender

class GenderField @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private lateinit var onGenderChange: (gender: Gender) -> Unit

    init {
        LayoutInflater.from(context).inflate(R.layout.view_gender_field, this, true)
        gender_button_female.setOnTouchListener { _, _ ->
            this.requestFocus()
            onGenderChange(Gender.F)
            gender_button_female.setSelected()
            gender_button_male.setUnselected(R.drawable.ic_member_placeholder_male)
            true
        }

        gender_button_male.setOnTouchListener { _, _ ->
            this.requestFocus()
            onGenderChange(Gender.M)
            gender_button_male.setSelected()
            gender_button_female.setUnselected(R.drawable.ic_member_placeholder_female)
            true
        }
    }

    fun setOnGenderChange(onGenderChange: (gender: Gender) -> Unit) {
        this.onGenderChange = onGenderChange
    }

    fun setError(errorMessage: String?) {
        gender_error_message.error = errorMessage
    }
}
