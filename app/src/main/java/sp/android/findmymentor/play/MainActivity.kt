package sp.android.findmymentor.play

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import sp.android.findmymentor.R
import sp.android.findmymentor.play.firebase.FirebaseSource
import sp.android.findmymentor.play.repository.MainRepository
import sp.android.findmymentor.play.ui.viewmodels.LoginViewModel
import sp.android.findmymentor.play.ui.viewmodels.factories.LoginViewModelFactory

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainRepository = MainRepository(FirebaseSource())

        viewModel = ViewModelProvider(this, LoginViewModelFactory(mainRepository)).get(LoginViewModel::class.java)

        val navController = findNavController(R.id.navHostFragmentId)


        val appBarConfiguration = AppBarConfiguration
                .Builder(
                        R.id.loginFragment,
                        R.id.menteeHomeFragment,
                        R.id.messagesListFragment)
                .build()


        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        //todo option for mentor to view mentee profile


        //todo fix notify dataset changed in messageslist fragment
        //todo ****validations
        //todo rethink actions (nav graph) for up button

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.navHostFragmentId)
        return navController.navigateUp()
    }
}