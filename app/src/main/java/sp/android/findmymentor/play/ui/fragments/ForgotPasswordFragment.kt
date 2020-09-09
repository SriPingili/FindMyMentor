package sp.android.findmymentor.play.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.fragment_login.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.MainActivity
import sp.android.findmymentor.play.ui.viewmodels.LoginViewModel
import sp.android.findmymentor.play.util.UserInputValidator
import java.util.regex.Pattern


class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {
    lateinit var viewModel: LoginViewModel
    lateinit var userInputValidator: UserInputValidator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        userInputValidator = UserInputValidator(requireContext())

        resetPasswordButton.setOnClickListener {
            if (userInputValidator.isEmailValid(emailAddressInput)) {
                viewModel.performPasswordReset(emailAddressInput.text.toString().trim())
                resetPasswordButton.text = "Email Sent"
                showSnackBar()
                findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
            }
        }
    }

    private fun showSnackBar() {
        val parentLayout: View? = activity?.findViewById(android.R.id.content)

        parentLayout?.let {
            Snackbar.make(it, "Password reset link sent. Please check your email and login", Snackbar.LENGTH_LONG)
                    .show()
        }
    }
}