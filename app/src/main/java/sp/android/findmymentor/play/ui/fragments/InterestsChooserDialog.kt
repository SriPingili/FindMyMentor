package sp.android.findmymentor.play.ui.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import sp.android.findmymentor.R

class InterestsChooserDialog : DialogFragment() {
    companion object {
        val checkedItems = booleanArrayOf(false, false, false, false, false)
    }

    private var interestsListener: ((Int, Boolean) -> Unit)? = null

    fun setActivityLevelListener(listener: ((Int, Boolean) -> Unit)) {
        interestsListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val list = context?.resources?.getStringArray(R.array.interests_choice)

        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle("Choose your Interests")
                .setMultiChoiceItems(list, checkedItems) { dialogInterface: DialogInterface, position: Int, status: Boolean ->
                    interestsListener?.let { interests ->
                        interests(position, status)
                    }
                }
                .create()
    }
}