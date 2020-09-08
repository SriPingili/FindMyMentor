package sp.android.findmymentor.play.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import sp.android.findmymentor.play.models.Mentee
import sp.android.findmymentor.play.models.Mentor
import sp.android.findmymentor.play.repository.MainRepository
import sp.android.findmymentor.play.util.Event

class LoginViewModel(private val repository: MainRepository) : ViewModel() {
    var isLoggedInUserMentor = false
    var loggedInMentor: Mentor? = null
    var loggedInMentee: Mentee? = null
    val updateProfileStatus: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val registerResult: MutableLiveData<Event<Task<AuthResult>>> = MutableLiveData()
    val loggedInUserIsMentor: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val loginResult: MutableLiveData<Event<Task<AuthResult>>> = MutableLiveData()

    fun login(email: String, password: String) = viewModelScope.launch {
        repository.login(email, password).addOnCompleteListener {
            loginResult.postValue(Event(it))
        }
    }

    fun routeToUsersTimeline() {
        repository.getChildUserReference()?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                /*do nothing*/
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val map = snapshot.value as HashMap<String, String>?

                map?.let {
                    if (it.containsKey("mentor")) {
                        loggedInMentor = snapshot.getValue(Mentor::class.java)
                        isLoggedInUserMentor = true
                        loggedInUserIsMentor.postValue(Event(true))
                    } else {
                        loggedInMentee = snapshot.getValue(Mentee::class.java)
                        isLoggedInUserMentor = false
                        loggedInUserIsMentor.postValue(Event(false))
                    }
                }
            }
        })
    }

    fun registerUser(email: String, password: String) = viewModelScope.launch {
        repository.register(email, password).addOnCompleteListener {
            registerResult.postValue(Event(it))
        }
    }

    fun createUser(mentee: Mentee? = null, mentor: Mentor? = null) {
        if (mentee != null) {
            isLoggedInUserMentor = false
            loggedInMentee = mentee
            repository.getChildUserReference()?.setValue(mentee)
        } else if (mentor != null) {
            isLoggedInUserMentor = true
            loggedInMentor = mentor
            repository.getChildUserReference()?.setValue(mentor)
        }
    }


    fun updateProfile(map: HashMap<String, Any>, mentee: Mentee?, mentor: Mentor? = null) {
        repository.getChildUserReference()?.updateChildren(map) { error, _ ->
            if (error == null) {
                if (mentee != null) {
                    loggedInMentee = mentee
                }
                if (mentor != null) {
                    loggedInMentor = mentor
                }
                updateProfileStatus.postValue(Event(true))
            } else {
                updateProfileStatus.postValue(Event(false))
            }
        }
    }

    fun getLoggedInUserName(): String? {
        return when {
            loggedInMentee != null -> loggedInMentee?.full_name
            loggedInMentor != null -> loggedInMentor?.full_name
            else -> null
        }
    }

    fun getLoggedInEmailAddress(): String? {
        return when {
            loggedInMentee != null -> loggedInMentee?.email_address
            loggedInMentor != null -> loggedInMentor?.email_address
            else -> null
        }
    }

    fun performPasswordReset(email: String) {
        repository.getFirebaseAuthInstance().sendPasswordResetEmail(email)
    }

    fun logout() = repository.logout()
}