package sp.android.findmymentor.play.ui.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sp.android.findmymentor.play.models.Mentee
import sp.android.findmymentor.play.repository.MainRepository
import sp.android.findmymentor.play.ui.viewmodels.MenteeViewModel
import sp.android.findmymentor.play.ui.viewmodels.MessagesListViewModel

class MenteeViewModelFactory(
        private val repository: MainRepository, private val loggedInMentee: Mentee
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MenteeViewModel(repository, loggedInMentee) as T
    }
}

