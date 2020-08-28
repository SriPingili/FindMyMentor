package sp.android.findmymentor.play.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class FirebaseSource {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun login(email: String, password: String): Task<AuthResult> = firebaseAuth.signInWithEmailAndPassword(email, password)

    fun register(email: String, password: String) = firebaseAuth.createUserWithEmailAndPassword(email,password)

    fun logout() = firebaseAuth.signOut()

    fun currentUser() = firebaseAuth.currentUser
}