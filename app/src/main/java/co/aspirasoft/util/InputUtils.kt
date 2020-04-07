package co.aspirasoft.util

import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Matcher
import java.util.regex.Pattern

object InputUtils {

    /**
     * Returns `true` if this [TextInputEditText] contains some valid non-whitespace characters.
     *
     * If [isErrorEnabled] is `true`, an appropriate error message is generated.
     */
    fun TextInputEditText.isNotBlank(isErrorEnabled: Boolean = false): Boolean {
        return if (this.text.isNullOrBlank()) {
            if (isErrorEnabled) this.showError("${this.hint} is required.")
            false
        } else true
    }

    /**
     * Shows the [error] message in an appropriate format.
     */
    fun TextInputEditText.showError(error: String) {
        try {
            val wrapper = this.parent.parent as TextInputLayout
            wrapper.error = error
            wrapper.isErrorEnabled = true
        } catch (ex: Exception) {
            Toast.makeText(this.context, error, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Method is used for checking valid email address format.
     *
     * @return boolean true for valid false for invalid
     */
    fun String.isEmail(): Boolean {
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(this)
        return matcher.matches()
    }

}