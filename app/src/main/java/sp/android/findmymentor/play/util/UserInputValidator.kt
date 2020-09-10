package sp.android.findmymentor.play.util

import android.content.Context
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.EditText
import sp.android.findmymentor.R
import java.util.regex.Pattern

class UserInputValidator(private val context: Context) {
    fun isTextValid(editText: EditText, inputPattern: Pattern, errorMessage: String?): Boolean {
        val matcher = inputPattern.matcher(editText.text.toString())
        if (matcher.matches()) {
            editText.error = null
            editText.background = context.resources.getDrawable(R.drawable.default_shape, context.theme)
            return true
        } else {
            editText.error = errorMessage
            editText.startAnimation(shakeTextBoxAnimation())
            editText.background = context.resources.getDrawable(R.drawable.error_input, context.theme)
            return false
        }
    }

    /**
     * helper method to validate email
     *
     *
     * reference:  https://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address as reference
     *
     * @param editText - email input edit text
     * @return - true if email is valid
     */
    fun isEmailValid(editText: EditText): Boolean {
        val VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(Constants.EMAIL_REGEX, Pattern.CASE_INSENSITIVE)
        val matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(editText.text.toString())
        if (!matcher.matches()) {
            editText.error = context.getString(R.string.check_your_email_adrress)
            editText.startAnimation(shakeTextBoxAnimation())
            editText.background = context.resources.getDrawable(R.drawable.error_input, context.theme)
            return false
        } else {
            editText.error = null
            editText.background = context.resources.getDrawable(R.drawable.default_shape, context.theme)
        }
        return true
    }

    /*
    * This method gets called when the user enters the wrong arguments in the text
    * box i.e, when the validation fails. This animation is to shake the textbox to alert
    * the user of the error in input.
    * */
    fun shakeTextBoxAnimation(): TranslateAnimation {
        val shake = TranslateAnimation(ANIMATION_START_X_AXIS.toFloat(), ANIMATION_END_X_AXIS.toFloat(), ANIMATION_START_Y_AXIS.toFloat(), ANIMATION_END_Y_AXIS.toFloat())
        shake.duration = ANIMATION_DURATION.toLong()
        shake.interpolator = CycleInterpolator(ANIMATION_CYCLES_COUNT.toFloat())
        return shake
    }

    companion object {
        private const val ANIMATION_START_X_AXIS = 0
        private const val ANIMATION_END_X_AXIS = 10
        private const val ANIMATION_START_Y_AXIS = 0
        private const val ANIMATION_END_Y_AXIS = 0
        private const val ANIMATION_DURATION = 200
        private const val ANIMATION_CYCLES_COUNT = 7
    }
}