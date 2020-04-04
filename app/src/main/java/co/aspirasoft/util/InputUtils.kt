package co.aspirasoft.util

import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

object InputUtils {

    /**
     * Returns `true` if this [TextInputEditText] contains some valid non-whitespace characters.
     *
     * If [isErrorEnabled] is `true`, an appropriate error message is generated.
     */
    fun TextInputEditText.isNotBlank(isErrorEnabled: Boolean = false): Boolean {
        return if (this.text.isNullOrBlank()) {
            if (isErrorEnabled) {
                try {
                    val wrapper = this.parent.parent as TextInputLayout
                    wrapper.error = "This field is required."
                    wrapper.isErrorEnabled = true
                } catch (ex: Exception) {
                    Toast.makeText(
                            this.context,
                            "${this.hint} is required.",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
            false
        } else true
    }

}