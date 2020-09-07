package sp.android.findmymentor.play

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.firebase.FirebaseSource
import sp.android.findmymentor.play.repository.MainRepository
import sp.android.findmymentor.play.ui.viewmodels.MainViewModel
import sp.android.findmymentor.play.ui.viewmodels.MainViewModelFactory

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainRepository = MainRepository(FirebaseSource())

        viewModel = ViewModelProvider(this, MainViewModelFactory(mainRepository)).get(MainViewModel::class.java)

        val navController = findNavController(R.id.navHostFragmentId)


        val appBarConfiguration = AppBarConfiguration
                .Builder(
                        R.id.loginFragment,
                        R.id.menteeHomeFragment,
                        R.id.messagesListFragment)
                .build()


        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)


        //todo refactor code (view models)
        //todo option for mentor to view mentee profile
        //todo fix messgaes list fragment bug (still exists)//gets called multiple times
        //add save button to options
        //todo animations for fragments @nav graph
        //todo forgot password
        //totdo fix notify dataset changed in messageslist fragment
        //fix availability status
        //****validations

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.navHostFragmentId)
        return navController.navigateUp()
    }
}