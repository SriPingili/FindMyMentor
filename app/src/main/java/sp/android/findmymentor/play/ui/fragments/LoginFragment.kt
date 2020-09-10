package sp.android.findmymentor.play.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import kotlinx.android.synthetic.main.fragment_login.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.MainActivity
import sp.android.findmymentor.play.ui.viewmodels.LoginViewModel
import sp.android.findmymentor.play.util.Constants
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
        setListeners()
    }

    private fun addObservers() {
        val loginObserver = Observer<Event<Task<AuthResult>>> { event ->
            event.getContentIfNotHandled()?.let {
                if (it.isSuccessful) {
                    viewModel.routeToUsersTimeline()
                } else {
                    Constants.showSnackBar(requireContext(), getString(R.string.login_failed), Snackbar.LENGTH_LONG)
                }
            }
        }
        viewModel.loginResult.observe(viewLifecycleOwner, loginObserver)

        val loggedInUserObserver = Observer<Event<Boolean>> { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    val bundle = Bundle().apply {
                        putString(Constants.TITLE_ARG_KEY, String.format(getString(R.string.welcome_mentor), viewModel.loggedInMentor?.full_name))
                    }

                    findNavController().navigate(
                            R.id.action_global_to_messagesListFragment,
                            bundle
                    )
                } else {
                    val bundle = Bundle().apply {
                        putString(Constants.TITLE_ARG_KEY, String.format(getString(R.string.welcome_mentee), viewModel.loggedInMentee?.full_name))
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

    private fun setListeners() {

        menteeRegisterTextView.setOnClickListener {
            registerMentee()
        }

        mentorRegisterTextView.setOnClickListener {
            registerMentor()
        }

        loginButton.setOnClickListener {
            if (areUserInputsValid()) viewModel.login(inputEmail.text.toString(), inputPassword.text.toString())
        }

        forgotPasswordTextView.setOnClickListener {
            findNavController().navigate(
                    R.id.action_loginFragment_to_forgotPasswordFragment
            )
        }

        inputPassword.doOnTextChanged { _, _, _, _ ->
            passwordLayout.isPasswordVisibilityToggleEnabled = true
        }

        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


    private fun registerMentor() {
        val bundle = Bundle().apply {
            putString(Constants.TITLE_ARG_KEY, getString(R.string.register_profile))
        }
        findNavController().navigate(
                R.id.action_global_to_userProfileFormFragment,
                bundle
        )
    }

    private fun registerMentee() {
        val bundle = Bundle().apply {
            putString(Constants.TITLE_ARG_KEY, getString(R.string.register_profile))
        }
        findNavController().navigate(
                R.id.action_global_to_userProfileFormFragment,
                bundle
        )
    }

    private fun areUserInputsValid(): Boolean {
        var areInputsValid = true

        if (!userInputValidator.isEmailValid(inputEmail)) {
            areInputsValid = false
        }

        if (!userInputValidator.isTextValid(inputPassword, Pattern.compile(Constants.ONLY_LETTERS_NUMBERS), getString(R.string.invalid_password))) {
            areInputsValid = false
            passwordLayout.isPasswordVisibilityToggleEnabled = false
        }

        return areInputsValid
    }
}