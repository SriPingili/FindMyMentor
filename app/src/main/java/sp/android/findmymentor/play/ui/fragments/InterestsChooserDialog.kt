package sp.android.findmymentor.play.ui.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import sp.android.findmymentor.R
import sp.android.findmymentor.play.application.CustomApplication

class InterestsChooserDialog() : DialogFragment() {

    companion object {
        val list = CustomApplication.context?.resources?.getStringArray(R.array.interests_choice)
        val checkedItems = list?.size?.let { BooleanArray(it) }
    }

    private var interestsListener: ((Int, Boolean) -> Unit)? = null

    fun setActivityLevelListener(listener: ((Int, Boolean) -> Unit)) {
        interestsListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle(getString(R.string.your_interests))
                .setMultiChoiceItems(list, checkedItems) { dialogInterface: DialogInterface, position: Int, status: Boolean ->
                    interestsListener?.let { interests ->
                        interests(position, status)
                    }
                }
                .setPositiveButton(android.R.string.ok, null)
                .create()
    }
}


