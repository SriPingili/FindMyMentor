package sp.android.findmymentor.play.ui.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sp.android.findmymentor.play.repository.MainRepository
import sp.android.findmymentor.play.ui.viewmodels.MessagingViewModel

class MessagingViewModelFactory(
        private val repository: MainRepository, val chatKeyValue: String
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MessagingViewModel(repository, chatKeyValue) as T
    }
}

