package sp.android.findmymentor.play.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_login.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.MainActivity
import sp.android.findmymentor.play.ui.viewmodels.MainViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {
    lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        viewModel.loginResult.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), "result = " + it.isSuccessful(), Toast.LENGTH_LONG).show();
        })

        sign_in_button_id.setOnClickListener {
            viewModel.login(input_email.text.toString(), input_password.text.toString())
        }
    }
}