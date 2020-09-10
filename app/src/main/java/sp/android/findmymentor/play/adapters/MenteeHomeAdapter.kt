package sp.android.findmymentor.play.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramotion.foldingcell.FoldingCell
import kotlinx.android.synthetic.main.cell_folded_layout.view.*
import kotlinx.android.synthetic.main.cell_unfolded_layout.view.*
import sp.android.findmymentor.R
import sp.android.findmymentor.play.models.Mentor
import sp.android.findmymentor.play.util.Constants
import java.util.HashSet

/*
Adapter class for the recycler view
* */
class MenteeHomeAdapter : RecyclerView.Adapter<MenteeHomeAdapter.MentorViewHolder>() {
    private var unfoldedIndexes = HashSet<Int>()
    private var onItemClickListener: ((View, Int) -> Unit)? = null
    private var onRequestMentorClickListener: ((Mentor) -> Unit)? = null
    private var onCommonGroupsClickListener: ((Mentor) -> Unit)? = null
    private var loggedInUserEmail: String = ""
    var chatKeys = mutableSetOf<String>()

    inner class MentorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Mentor>() {
        override fun areItemsTheSame(oldItem: Mentor, newItem: Mentor): Boolean {
            return oldItem.email_address == newItem.email_address
        }

        override fun areContentsTheSame(oldItem: Mentor, newItem: Mentor): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): MentorViewHolder {
        return MentorViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.cell,
                        parent,
                        false
                )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MenteeHomeAdapter.MentorViewHolder, position: Int) {
        val mentor = differ.currentList[position]

        holder.itemView.apply {
            val cell = this as FoldingCell

            //folded scenario
            roundedTextViewFolded.text = mentor.full_name.subSequence(0, 1)
            availabilityTextViewFolded.text = "${mentor.availability}/${mentor.totalSpots}"
            userNameTextViewFolded.text = mentor.full_name
            userRoleTextViewFolded.text = mentor.role
            organizationNameTextViewFolded.text = mentor.organization
            locationTextViewFolded.text = mentor.location

            //unfolded scenario
            roundedTextViewUnfolded.text = mentor.full_name.subSequence(0, 1)
            userNameTextViewUnfolded.text = mentor.full_name
            userRoleTextViewUnfolded.text = mentor.role
            aboutUserLabelTextView.text = String.format(context.getString(R.string.description), mentor.full_name.substringBefore(' '))
            aboutUserDescriptionTextView.text = mentor.aboutYourself
            availabilityTextViewUnfolded.text = mentor.availability.toString()
            organizationNameTextViewUnfolded.text = mentor.organization
            locationTextViewUnfolded.text = mentor.location

            if (mentor.availability < 1) {
                availabilityStatusTVUnfolded.text = context.getString(R.string.unavailable)
            } else {
                availabilityStatusTVUnfolded.text = context.getText(R.string.available)
            }

            requestUserButton.setOnClickListener {
                requestUser(requestUserButton, availabilityStatusTVUnfolded)
                onRequestMentorClickListener?.let {
                    it(mentor)
                }
            }

            val chatKey = Constants.getKey(loggedInUserEmail, mentor.email_address)

            if (chatKeys.contains(chatKey)) {
                requestUser(requestUserButton, availabilityStatusTVUnfolded)
            }

            commonInterestsImageView.setOnClickListener {

                onCommonGroupsClickListener?.let {
                    it(mentor)
                }
            }
            // for existing cell set valid valid state(without animation todo revisit this logic
            /*if (unfoldedIndexes.contains(position)) {
                cell.unfold(true)
            } else {
                cell.fold(true)
            }*/

            setOnClickListener {
                onItemClickListener?.let {
                    it(this, position)
                }
            }
        }
    }

    private fun requestUser(requestUserButton: TextView, availabilityStatusTVUnfolded: TextView) {
        availabilityStatusTVUnfolded.text = availabilityStatusTVUnfolded.context.getString(R.string.requested)
        requestUserButton.text = requestUserButton.context.getString(R.string.go_to_messages)
        requestUserButton.alpha = 0.5f
        //TODO make it true and take to messages on click
        requestUserButton.isEnabled = false
    }

    // simple methods for register cell state changes
    fun registerToggle(position: Int) {
        if (unfoldedIndexes.contains(position)) registerFold(position) else registerUnfold(position)
    }

    fun registerFold(position: Int) {
        unfoldedIndexes.remove(position)
    }

    fun registerUnfold(position: Int) {
        unfoldedIndexes.add(position)
    }

    fun setLoggedInUserEmail(email: String) {
        loggedInUserEmail = email
    }

    /*
    on click listener for the recycler view row item
    * */
    fun setOnItemClickListener(listener: (View, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnRequestMentorClickListener(listener: (Mentor) -> Unit) {
        onRequestMentorClickListener = listener
    }

    fun setCommonGroupsClickListener(listener: (Mentor) -> Unit) {
        onCommonGroupsClickListener = listener
    }

    /*
    submits the hacker news response to the differ util, also saves
    the response to fullList (used by search)
     */
    fun submitList(stores: List<Mentor>) {
        differ.submitList(stores)
    }
}