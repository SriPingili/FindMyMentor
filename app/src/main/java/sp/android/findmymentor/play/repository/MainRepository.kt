package sp.android.findmymentor.play.repository

import sp.android.findmymentor.play.firebase.FirebaseSource

class MainRepository(private val firebaseSource: FirebaseSource) {
    fun getFirebaseAuthInstance() = firebaseSource.getFirebaseAuthInstance()
    fun getFirebaseUsersDBReference() = firebaseSource.getFirebaseUsersDBReferenceInstance()
    fun getFirebaseChatsDBReference() = firebaseSource.getFirebaseChatsDBReferenceInstance()
    fun getFirebaseMessagesDBReference() = firebaseSource.getFirebaseMessagesDBReferenceInstance()
    fun getChildUserReference() = firebaseSource.getChildUserReference()

    fun login(email: String, password: String) = firebaseSource.login(email, password)
    fun register(email: String, password: String) = firebaseSource.register(email, password)
    fun currentUser() = firebaseSource.currentUser()
    fun logout() = firebaseSource.logout()
}