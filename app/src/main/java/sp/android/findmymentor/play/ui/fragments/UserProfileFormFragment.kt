package sp.android.findmymentor.play.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_user_profile_form.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.MainActivity
import sp.android.findmymentor.play.application.CustomApplication
import sp.android.findmymentor.play.models.Mentee
import sp.android.findmymentor.play.models.Mentor
import sp.android.findmymentor.play.ui.viewmodels.LoginViewModel
import java.util.ArrayList
import java.util.HashMap

class UserProfileFormFragment : Fragment(R.layout.fragment_user_profile_form) {
    val args: UserProfileFormFragmentArgs by navArgs()
    var listOfGroups: MutableList<String> = mutableListOf()
    var location: String? = ""
    lateinit var viewModel: LoginViewModel

    // Initialize a new array with elements
    val countries = CustomApplication.context?.resources?.getStringArray(R.array.countries_array)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = (activity as MainActivity).viewModel
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

        chooseInterests.setOnClickListener {
            InterestsChooserDialog().apply {
                setActivityLevelListener { pos, bool ->
                    updateInterests(pos, bool)
                }
            }.show(parentFragmentManager, "BMR_FRAGMENT_TAG")
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
                viewModel.registerUser(input_email.text.toString(), input_password.text.toString())
            } else {
                submitUpdates()
            }

            return true
        }

        if (item.itemId == R.id.logout) {
            viewModel.isLoggedInUserMentor = false
            viewModel.loggedInMentee = null
            viewModel.loggedInMentor = null

            viewModel.logout()

            findNavController().navigate(
                    R.id.action_global_to_loginFragment
            )

            return true
        }

        return false
    }

    private fun updateInterests(pos: Int, bool: Boolean) {
        val interest = resources.getStringArray(R.array.interests_choice)?.get(pos).toString()
        if (bool) {
            InterestsChooserDialog.checkedItems?.set(pos, true)
            if (!listOfGroups.contains(interest))
                listOfGroups.add(interest)
        } else {
            InterestsChooserDialog.checkedItems?.set(pos, false)
            listOfGroups.remove(interest)
        }
    }

    private fun addObservers() {
        viewModel.registerResult.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                if (it.isSuccessful) {
                    if (args.isMentor) {
                        if (!input_fullname.text.toString().isNullOrEmpty()) {
                            val mentor = Mentor(input_fullname.text.toString(), input_email.text.toString(), location, input_about_yourself.text.toString(), input_organization.text.toString(), input_role.text.toString(), listOfGroups as ArrayList<String>, input_available_spots.text.toString().toInt(), input_total_spots.text.toString().toInt(), true)
                            viewModel.createUser(null, mentor)

                            val bundle = Bundle().apply {
                                putSerializable(
                                        "mentorArg",
                                        viewModel.loggedInMentor
                                )
                                putString("title", "Hello ${viewModel.loggedInMentor?.full_name}, your inbox")
                            }
                            findNavController().navigate(R.id.action_userProfileFormFragment_to_messagesListFragment, bundle)
                        }
                    } else {
                        if (!input_fullname.text.toString().isNullOrEmpty()) {
//                            viewModel.getUsersFromFirebase()
//                            viewModel.getChatKeysFromFirebase()

                            val mentee = Mentee(input_fullname.text.toString(), input_email.text.toString(), location, input_about_yourself.text.toString(), input_organization.text.toString(), input_role.text.toString(), listOfGroups as ArrayList<String>)
                            viewModel.createUser(mentee)

                            val bundle = Bundle().apply {
                                putSerializable(
                                        "menteeArg",
                                        viewModel.loggedInMentee
                                )
                                putString("title", "Hello ${viewModel.loggedInMentee?.full_name}")
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
                            putSerializable(
                                    "mentorArg",
                                    viewModel.loggedInMentor
                            )
                            putString("title", "Hello ${viewModel.loggedInMentor?.full_name}, your inbox")
                        }
                        findNavController().navigate(R.id.action_userProfileFormFragment_to_messagesListFragment, bundle)
                    } else {
                        val bundle = Bundle().apply {
                            putSerializable(
                                    "menteeArg",
                                    viewModel.loggedInMentee
                            )
                            putString("title", "Hello ${viewModel.loggedInMentee?.full_name}")
                        }
                        findNavController().navigate(R.id.action_userProfileFormFragment_to_menteeHomeFragment, bundle)
                    }
                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "failure", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun prepareEditProfileIfNeeded() {
        if (viewModel.loggedInMentee != null || viewModel.loggedInMentor != null) {
            passwordLayout.visibility = View.GONE
            input_password.visibility = View.GONE

            val mentee = viewModel.loggedInMentee
            val mentor = viewModel.loggedInMentor

            val name = mentee?.full_name ?: mentor?.full_name
            input_fullname.setText(name)

            val email = mentee?.email_address ?: mentor?.email_address
            input_email.setText(email)

            val locationSelected = mentee?.location ?: mentor?.location
            countries?.indexOf(locationSelected)?.let { locationSpinner.setSelection(it) }

            val aboutYourself = mentee?.aboutYourself ?: mentor?.aboutYourself
            input_about_yourself.setText(aboutYourself)

            val organization = mentee?.organization ?: mentor?.organization
            input_organization.setText(organization)

            val role = mentee?.role ?: mentor?.role
            input_role.setText(role)

            //interests
            val interests = InterestsChooserDialog.list
            val selectedInterests = mentee?.interests ?: mentor?.interests
            listOfGroups.clear()
            if (selectedInterests != null) {
                listOfGroups.addAll(selectedInterests)
            }

            for (interest in interests!!) {
                selectedInterests?.let {
                    InterestsChooserDialog.checkedItems?.set(interests.indexOf(interest), it.contains(interest))
                }
            }

            val availability = mentor?.availability
            input_available_spots.setText(availability.toString())

            val totalSpots = mentor?.totalSpots
            input_total_spots.setText(totalSpots.toString())
        }
    }

    private fun submitUpdates() {
        if (args.isMentor) {
            val mentor = Mentor(input_fullname.text.toString(), input_email.text.toString(), locationSpinner.selectedItem.toString(), input_about_yourself.text.toString(), input_organization.text.toString(), input_role.text.toString(), listOfGroups, input_available_spots.text.toString().toInt(), input_total_spots.text.toString().toInt(), true)
            val hashMap = HashMap<String, Any>()
            hashMap["full_name"] = mentor.full_name
            hashMap["email_address"] = mentor.email_address
            hashMap["availability"] = mentor.availability
            hashMap["totalSpots"] = mentor.totalSpots
            hashMap["aboutYourself"] = mentor.aboutYourself ?: ""
            hashMap["organization"] = mentor.organization ?: ""
            hashMap["role"] = mentor.role ?: ""
            hashMap["interests"] = listOfGroups
            hashMap["location"] = mentor.location ?: ""
            hashMap["mentor"] = true
            viewModel.updateProfile(hashMap, null, mentor)
        } else {
            val mentee = Mentee(input_fullname.text.toString(), input_email.text.toString(), locationSpinner.selectedItem.toString(), input_about_yourself.text.toString(), input_organization.text.toString(), input_role.text.toString(), listOfGroups)
            val hashMap = HashMap<String, Any>()
            hashMap["full_name"] = mentee.full_name
            hashMap["email_address"] = mentee.email_address
            hashMap["aboutYourself"] = mentee.aboutYourself ?: ""
            hashMap["organization"] = mentee.organization ?: ""
            hashMap["role"] = mentee.role ?: ""
            hashMap["interests"] = listOfGroups
            hashMap["location"] = mentee.location ?: ""
            viewModel.updateProfile(hashMap, mentee)
        }
    }
}