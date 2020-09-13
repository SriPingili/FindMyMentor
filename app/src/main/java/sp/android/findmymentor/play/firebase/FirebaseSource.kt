package sp.android.findmymentor.play.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import sp.android.findmymentor.play.util.Constants.Companion.CHATS
import sp.android.findmymentor.play.util.Constants.Companion.MESSAGES
import sp.android.findmymentor.play.util.Constants.Companion.USERS

class FirebaseSource {
    /*
    https://stackoverflow.com/questions/38304258/how-to-encrypt-user-data-in-firebase
    * */
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firebaseUsersReference: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference.child(USERS) }
    private val firebaseChatsReference: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference.child(CHATS) }
    private val firebaseMessagesReference: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference.child(MESSAGES) }

    fun getFirebaseAuthInstance() = firebaseAuth
    fun getFirebaseUsersDBReferenceInstance() = firebaseUsersReference
    fun getFirebaseChatsDBReferenceInstance() = firebaseChatsReference
    fun getFirebaseMessagesDBReferenceInstance() = firebaseMessagesReference

    fun login(email: String, password: String): Task<AuthResult> = firebaseAuth.signInWithEmailAndPassword(email, password)
    fun register(email: String, password: String) = firebaseAuth.createUserWithEmailAndPassword(email, password)
    fun logout() = firebaseAuth.signOut()
    fun currentUser() = firebaseAuth.currentUser
    fun getChildUserReference() = currentUser()?.uid?.let { firebaseUsersReference?.child(it) }
}