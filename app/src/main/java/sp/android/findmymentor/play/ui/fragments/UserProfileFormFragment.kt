package sp.android.findmymentor.play.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_user_profile_form.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.MainActivity
import sp.android.findmymentor.play.application.CustomApplication
import sp.android.findmymentor.play.models.Mentee
import sp.android.findmymentor.play.models.Mentor
import sp.android.findmymentor.play.ui.viewmodels.LoginViewModel
import sp.android.findmymentor.play.util.Constants
import sp.android.findmymentor.play.util.UserInputValidator
import java.util.ArrayList
import java.util.HashMap
import java.util.regex.Pattern

/*
* This Fragment is responsible for handling profile registration and profile
* updation for both Mentors and Mentees
* */
class UserProfileFormFragment : Fragment(R.layout.fragment_user_profile_form) {
    private var listOfInterests: MutableList<String> = mutableListOf()
    private var location: String = ""
    private lateinit var viewModel: LoginViewModel
    private lateinit var userInputValidator: UserInputValidator
    private val countries = CustomApplication.context?.resources?.getStringArray(R.array.countries_array)
    private val args: UserProfileFormFragmentArgs by navArgs()
    private val FRAGMENT_TAG = "USER_PROFILE_FORM_FRAGMENT"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = (activity as MainActivity).viewModel
        userInputValidator = UserInputValidator(requireContext())
        initializeUI()
        addObservers()
        prepareEditProfileIfNeeded()
    }

    private fun initializeUI() {
        if (args.isMentor) {
            availableSpotsLayout.visibility = View.VISIBLE
            totalSpotsLayout.visibility = View.VISIBLE
        } else {
            availableSpotsLayout.visibility = View.GONE
            totalSpotsLayout.visibility = View.GONE
        }

        //setup spinner
        val adapter = countries?.let {
            ArrayAdapter(requireContext(),
                    R.layout.spinner_item, it)
        }

        locationSpinner.adapter = adapter

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                location = adapter?.getItem(position).toString()
            }
        }

        locationSpinner.onItemSelectedListener = listener

        //setup interests dialog
        chooseInterests.setOnClickListener {
            InterestsChooserDialog().apply {
                setActivityLevelListener { pos, bool ->
                    updateInterests(pos, bool)
                }
            }.show(parentFragmentManager, FRAGMENT_TAG)
        }

        inputPassword.doOnTextChanged { _, _, _, _ ->
            passwordLayout.isPasswordVisibilityToggleEnabled = true
        }
    }

    private fun addObservers() {
        viewModel.registerResult.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                if (it.isSuccessful) {
                    if (args.isMentor) {
                        if (!inputFullName.text.toString().isNullOrEmpty()) {
                            val mentor = Mentor(inputFullName.text.toString(), inputEmail.text.toString(), location, inputAboutYourself.text.toString(), inputOrganization.text.toString(), inputRole.text.toString(), listOfInterests as ArrayList<String>, inputAvailableSpots.text.toString().toInt(), inputTotalSpots.text.toString().toInt(), true)
                            viewModel.createUser(null, mentor)

                            val bundle = Bundle().apply {
                                putString(Constants.TITLE_ARG_KEY, String.format(getString(R.string.welcome_mentor), viewModel.loggedInMentor?.full_name))
                            }

                            findNavController().navigate(R.id.action_global_to_messagesListFragment, bundle)
                        }
                    } else {
                        if (!inputFullName.text.toString().isNullOrEmpty()) {
                            val mentee = Mentee(inputFullName.text.toString(), inputEmail.text.toString(), location, inputAboutYourself.text.toString(), inputOrganization.text.toString(), inputRole.text.toString(), listOfInterests as ArrayList<String>)
                            viewModel.createUser(mentee)

                            val bundle = Bundle().apply {
                                putString(Constants.TITLE_ARG_KEY, String.format(getString(R.string.welcome_mentee), viewModel.loggedInMentee?.full_name))
                            }

                            findNavController().navigate(R.id.action_userProfileFormFragment_to_menteeHomeFragment, bundle)
                        }
                    }
                }
            }
        })

        viewModel.updateProfileStatus.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                if (it) {
                    if (viewModel.isLoggedInUserMentor) {
                        val bundle = Bundle().apply {
                            putString(Constants.TITLE_ARG_KEY, String.format(getString(R.string.welcome_mentor), viewModel.loggedInMentor?.full_name))
                        }

                        findNavController().navigate(R.id.action_global_to_messagesListFragment, bundle)
                    } else {
                        val bundle = Bundle().apply {
                            putString(Constants.TITLE_ARG_KEY, String.format(getString(R.string.welcome_mentee), viewModel.loggedInMentee?.full_name))
                        }

                        findNavController().navigate(R.id.action_userProfileFormFragment_to_menteeHomeFragment, bundle)
                    }
                    Constants.showSnackBar(requireContext(), getString(R.string.profile_update_success), Snackbar.LENGTH_LONG)
                } else {
                    Constants.showSnackBar(requireContext(), getString(R.string.profile_update_failure), Snackbar.LENGTH_LONG)
                }
            }
        })
    }


    private fun prepareEditProfileIfNeeded() {
        if (viewModel.loggedInMentee != null || viewModel.loggedInMentor != null) {
            passwordLayout.visibility = View.GONE
            inputPassword.visibility = View.GONE

            val mentee = viewModel.loggedInMentee
            val mentor = viewModel.loggedInMentor

            val name = mentee?.full_name ?: mentor?.full_name
            inputFullName.setText(name)
            inputEmail.isEnabled = false

            val email = mentee?.email_address ?: mentor?.email_address
            inputEmail.setText(email)

            val locationSelected = mentee?.location ?: mentor?.location
            countries?.indexOf(locationSelected)?.let { locationSpinner.setSelection(it) }

            val aboutYourself = mentee?.aboutYourself ?: mentor?.aboutYourself
            inputAboutYourself.setText(aboutYourself)

            val organization = mentee?.organization ?: mentor?.organization
            inputOrganization.setText(organization)

            val role = mentee?.role ?: mentor?.role
            inputRole.setText(role)

            //interests
            val interests = InterestsChooserDialog.list
            val selectedInterests = mentee?.interests ?: mentor?.interests
            listOfInterests.clear()
            if (selectedInterests != null) {
                listOfInterests.addAll(selectedInterests)
            }

            for (interest in interests!!) {
                selectedInterests?.let {
                    InterestsChooserDialog.checkedItems?.set(interests.indexOf(interest), it.contains(interest))
                }
            }

            val availability = mentor?.availability
            inputAvailableSpots.setText(availability.toString())

            val totalSpots = mentor?.totalSpots
            inputTotalSpots.setText(totalSpots.toString())
        }
    }

    private fun submitUpdates() {
        if (viewModel.isLoggedInUserMentor) {
            val mentor = Mentor(inputFullName.text.toString(), inputEmail.text.toString(), locationSpinner.selectedItem.toString(), inputAboutYourself.text.toString(), inputOrganization.text.toString(), inputRole.text.toString(), listOfInterests, inputAvailableSpots.text.toString().toInt(), inputTotalSpots.text.toString().toInt(), true)
            val hashMap = HashMap<String, Any>()
            hashMap[Constants.FULL_NAME_KEY] = mentor.full_name
            hashMap[Constants.EMAIL_ADDRESS_KEY] = mentor.email_address
            hashMap[Constants.AVAILABILITY_KEY] = mentor.availability
            hashMap[Constants.TOTAL_SPOTS_KEY] = mentor.totalSpots
            hashMap[Constants.ABOUT_YOURSELF_KEY] = mentor.aboutYourself ?: ""
            hashMap[Constants.ORGANIZATION_KEY] = mentor.organization ?: ""
            hashMap[Constants.ROLE_KEY] = mentor.role
            hashMap[Constants.INTERESTS__KEY] = listOfInterests
            hashMap[Constants.LOCATION__KEY] = mentor.location ?: ""
            hashMap[Constants.MENTOR__KEY] = true
            viewModel.updateProfile(hashMap, null, mentor)
        } else {
            val mentee = Mentee(inputFullName.text.toString(), inputEmail.text.toString(), locationSpinner.selectedItem.toString(), inputAboutYourself.text.toString(), inputOrganization.text.toString(), inputRole.text.toString(), listOfInterests)
            val hashMap = HashMap<String, Any>()
            hashMap[Constants.FULL_NAME_KEY] = mentee.full_name
            hashMap[Constants.EMAIL_ADDRESS_KEY] = mentee.email_address
            hashMap[Constants.ABOUT_YOURSELF_KEY] = mentee.aboutYourself ?: ""
            hashMap[Constants.ORGANIZATION_KEY] = mentee.organization ?: ""
            hashMap[Constants.ROLE_KEY] = mentee.role
            hashMap[Constants.INTERESTS__KEY] = listOfInterests
            hashMap[Constants.LOCATION__KEY] = mentee.location ?: ""
            viewModel.updateProfile(hashMap, mentee)
        }
    }

    private fun areUserInputsValid(): Boolean {
        var areInputsValid = true

        if (!userInputValidator.isTextValid(inputFullName, Pattern.compile(Constants.ONLY_LETTERS), getString(R.string.only_letters_allowed))) {
            areInputsValid = false
        }
        if (!userInputValidator.isEmailValid(inputEmail)) {
            areInputsValid = false
        }
        if (!userInputValidator.isTextValid(inputPassword, Pattern.compile(Constants.ONLY_LETTERS_NUMBERS), getString(R.string.invalid_password))) {
            passwordLayout.isPasswordVisibilityToggleEnabled = false
            areInputsValid = false
        }

        val defaultSelection = countries?.get(0)
        if (location.equals(defaultSelection)) {
            setSpinnerError(locationSpinner)
            areInputsValid = false
        }

        if (!userInputValidator.isTextValid(inputOrganization, Pattern.compile(Constants.ONLY_LETTERS), getString(R.string.only_letters_allowed))) {
            areInputsValid = false
        }

        if (!userInputValidator.isTextValid(inputRole, Pattern.compile(Constants.ONLY_LETTERS), getString(R.string.only_letters_allowed))) {
            areInputsValid = false
        }

        if (!userInputValidator.isTextValid(inputAboutYourself, Pattern.compile(Constants.ONLY_LETTERS_NUMBERS_NEW_LINES), getString(R.string.letters_and_numbers_allowed))) {
            areInputsValid = false
        }

        if (availableSpotsLayout.visibility == View.VISIBLE) {
            if (!userInputValidator.isTextValid(inputAvailableSpots, Pattern.compile(Constants.ONLY_NUMBERS), getString(R.string.only_numbers_allowed))) {
                areInputsValid = false
            }
        }

        if (totalSpotsLayout.visibility == View.VISIBLE) {
            if (!userInputValidator.isTextValid(inputTotalSpots, Pattern.compile(Constants.ONLY_NUMBERS), getString(R.string.only_numbers_allowed))) {
                areInputsValid = false
            }
        }

        return areInputsValid
    }

    private fun updateInterests(pos: Int, bool: Boolean) {
        val interest = resources.getStringArray(R.array.interests_choice)?.get(pos).toString()
        if (bool) {
            InterestsChooserDialog.checkedItems?.set(pos, true)
            if (!listOfInterests.contains(interest))
                listOfInterests.add(interest)
        } else {
            InterestsChooserDialog.checkedItems?.set(pos, false)
            listOfInterests.remove(interest)
        }
    }

    private fun setSpinnerError(spinner: Spinner) {
        val selectedView = spinner.selectedView
        if (selectedView != null && selectedView is TextView) {
            spinner.requestFocus()
            selectedView.setTextColor(Color.RED) //text color in which you want your error message to be displayed
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.save_profile_changes).isVisible = true
        menu.findItem(R.id.edit_profile).isVisible = false

        if (viewModel.loggedInMentor == null && viewModel.loggedInMentee == null) {
            menu.findItem(R.id.logout).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save_profile_changes) {
            if (viewModel.loggedInMentee == null && viewModel.loggedInMentor == null) {
                if (areUserInputsValid()) viewModel.registerUser(inputEmail.text.toString(), inputPassword.text.toString())
            } else {
                submitUpdates()
            }

            return true
        }

        if (item.itemId == R.id.logout) {
            viewModel.logout()

            findNavController().navigate(
                    R.id.action_global_to_loginFragment
            )

            return true
        }

        return false
    }
}