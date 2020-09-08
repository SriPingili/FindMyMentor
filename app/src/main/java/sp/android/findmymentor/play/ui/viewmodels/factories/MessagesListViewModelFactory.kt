package sp.android.findmymentor.play.ui.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sp.android.findmymentor.play.repository.MainRepository
import sp.android.findmymentor.play.ui.viewmodels.MessagesListViewModel

class MessagesListViewModelFactory(
        private val repository: MainRepository, val email: String, val userName: String
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MessagesListViewModel(repository, email, userName) as T
    }
}

