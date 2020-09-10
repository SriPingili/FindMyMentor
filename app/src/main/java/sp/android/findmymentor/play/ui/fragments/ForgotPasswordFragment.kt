package sp.android.findmymentor.play.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.MainActivity
import sp.android.findmymentor.play.extensions.showSnackBar
import sp.android.findmymentor.play.ui.viewmodels.LoginViewModel
import sp.android.findmymentor.play.util.Constants
import sp.android.findmymentor.play.util.UserInputValidator


class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {
    lateinit var viewModel: LoginViewModel
    lateinit var userInputValidator: UserInputValidator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        userInputValidator = UserInputValidator(requireContext())

        resetPasswordButton.setOnClickListener {
            if (userInputValidator.isEmailValid(inputEmailAddress)) {
                viewModel.performPasswordReset(inputEmailAddress.text.toString().trim())
                resetPasswordButton.text = getString(R.string.email_sent)
                Constants.showSnackBar(requireContext(), getString(R.string.pwd_reset_link_sent), Snackbar.LENGTH_LONG)
                findNavController().navigate(R.id.action_global_to_loginFragment)
            }
        }
    }
}