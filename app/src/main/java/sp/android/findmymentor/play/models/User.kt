package sp.android.findmymentor.play.models

import java.io.Serializable

data class Mentee(
        var full_name: String = "",
        var email_address: String = "",
        var location: String? = "",
        val aboutYourself: String? = "",
        val interests: MutableList<String> = mutableListOf()) : Serializable

data class Mentor(
        var full_name: String = "",
        var email_address: String = "",
        var location: String? = "",
        val aboutYourself: String? = "",
        val interests: MutableList<String> = mutableListOf(),
        val availability: Int = 0,
        val isMentor: Boolean = false) : Serializable