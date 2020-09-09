package sp.android.findmymentor.play.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.android.synthetic.main.fragment_login.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.MainActivity
import sp.android.findmymentor.play.ui.viewmodels.LoginViewModel
import sp.android.findmymentor.play.util.Event
import sp.android.findmymentor.play.util.UserInputValidator
import java.util.regex.Pattern


class LoginFragment : Fragment(R.layout.fragment_login) {
    lateinit var viewModel: LoginViewModel
    lateinit var userInputValidator: UserInputValidator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        userInputValidator = UserInputValidator(requireContext())

        addObservers()

        menteeRegisterId.setOnClickListener {
            registerMentee()
        }

        mentorRegisterID.setOnClickListener {
            registerMentor()
        }

        sign_in_button_id.setOnClickListener {
            if (areUserInputsValid()) viewModel.login(input_email.text.toString(), input_password.text.toString())
        }

        forgotPasswordLabel.setOnClickListener {
            findNavController().navigate(
                    R.id.action_loginFragment_to_forgotPasswordFragment
            )
        }

        input_password.doOnTextChanged { _, _, _, _ ->
            passwordLayout.isPasswordVisibilityToggleEnabled = true
        }
    }

    private fun addObservers() {

        val loginObserver = Observer<Event<Task<AuthResult>>> { event ->
            event.getContentIfNotHandled()?.let {
                if (it.isSuccessful) {
                    viewModel.routeToUsersTimeline()
                } else {
                    Toast.makeText(requireContext(), "Login failed!!", Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.loginResult.observe(viewLifecycleOwner, loginObserver)

        val loggedInUserObserver = Observer<Event<Boolean>> { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    val bundle = Bundle().apply {
                        putSerializable("mentorArg", viewModel.loggedInMentor)
                        putString("title", "Hello ${viewModel.loggedInMentor?.full_name}, your inbox")
                    }

                    findNavController().navigate(
                            R.id.action_loginFragment_to_messagesListFragment,
                            bundle
                    )
                } else {
                    val bundle = Bundle().apply {
                        putSerializable("menteeArg", viewModel.loggedInMentee)
                        putString("title", "Hello ${viewModel.loggedInMentee?.full_name}")
                    }

                    findNavController().navigate(
                            R.id.action_loginFragment_to_menteeHomeFragment,
                            bundle
                    )
                }
            }
        }

        viewModel.loggedInUserIsMentor.observe(viewLifecycleOwner, loggedInUserObserver)
    }


    private fun registerMentor() {
        val bundle = Bundle().apply {
            putBoolean("isMentor", true)
            putString("title", "Register your profile")
        }
        findNavController().navigate(
                R.id.action_loginFragment_to_userProfileFormFragment,
                bundle
        )
    }

    private fun registerMentee() {
        val bundle = Bundle().apply {
            putBoolean("isMentor", false)
            putString("title", "Register your profile")
        }
        findNavController().navigate(
                R.id.action_loginFragment_to_userProfileFormFragment,
                bundle
        )
    }

    private fun areUserInputsValid(): Boolean {
        var areInputsValid = true

        if (!userInputValidator.isEmailValid(input_email)) {
            areInputsValid = false
        }

        if (!userInputValidator.isTextValid(input_password, Pattern.compile("[a-zA-Z0-9]+"), getString(R.string.invalid_password))) {
            areInputsValid = false
            passwordLayout.isPasswordVisibilityToggleEnabled = false
        }

        return areInputsValid
    }
}