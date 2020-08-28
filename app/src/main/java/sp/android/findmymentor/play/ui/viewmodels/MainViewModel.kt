package sp.android.findmymentor.play.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import sp.android.findmymentor.play.repository.MainRepository

class MainViewModel(private val repository: MainRepository) : ViewModel() {
    val USERS = "Users"
    var firebaseAuth: FirebaseAuth? = null
    var firebaseDatabaseReference: DatabaseReference? = null
    val loginResult: MutableLiveData<Task<AuthResult>> = MutableLiveData()
    val registerResult: MutableLiveData<Task<AuthResult>> = MutableLiveData()

    init {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabaseReference = FirebaseDatabase.getInstance().reference.child(USERS)
    }


    fun login(email: String, password: String) = viewModelScope.launch {
        repository.login(email, password).addOnCompleteListener {
            loginResult.postValue(it)
        }
    }

    fun registerUser(email: String, password: String) = viewModelScope.launch {
        repository.register(email, password).addOnCompleteListener {
            registerResult.postValue(it)
        }
    }

    fun logout() = repository.logout()

    fun currentUser() = repository.currentUser()
}