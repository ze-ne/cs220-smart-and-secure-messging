package com.cs220.ssmessaging.frontend.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.Conversation
import com.cs220.ssmessaging.clientBackend.User
import com.cs220.ssmessaging.frontend.activities.ConversationActivity
import com.google.common.base.Strings.isNullOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_search.view.*

class SearchFragment : Fragment() {
    private lateinit var currentUser: User
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var foundUsers: MutableList<String>
    private lateinit var searchBar: EditText
    private lateinit var searchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = MyApplication.currentUser!!
        foundUsers = mutableListOf()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val searchView =
            inflater.inflate(R.layout.fragment_search, container, false)
        searchRecyclerView = searchView.findViewById(R.id.search_recycler_list)
        searchRecyclerView.layoutManager = LinearLayoutManager(context)

        searchBar = searchView.findViewById(R.id.search_text)
        searchButton = searchView.findViewById(R.id.search_button)

        searchButton.setOnClickListener {
            val text = searchBar.text.toString()
            if (!isNullOrEmpty(text)) {
                currentUser.getAllUsersWithSearchTerm(text) { list ->
                    foundUsers = list
                    searchAdapter.notifyDataSetChanged()
                }
                searchBar.text.clear()
            }
        }

        displayUsers()
        return searchView
    }

    private fun displayUsers() {
        searchAdapter = SearchAdapter(activity as Context)
        searchRecyclerView.adapter = searchAdapter
    }


    internal inner class SearchAdapter(
        context: Context
    ) :
        RecyclerView.Adapter<ViewHolder>() {
        private val layoutInflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_search, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val user = foundUsers[position]
            viewHolder.bind(user)

            viewHolder.itemView.search_block_button.setOnClickListener {
                val otherUser = user.substringBefore(":")
                if (otherUser == currentUser.userId){
                    Toast.makeText(
                        activity,
                        "You cannot block yourself",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    currentUser.addBlockedContactToDb(otherUser) {
                        for (frag in fragmentManager?.fragments!!) {
                            frag.onStart()
                        }
                    }
                    Toast.makeText(activity, "$otherUser has been blocked", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            viewHolder.itemView.search_add_conversation_button.setOnClickListener {
                val participantUsername = user.substringBefore(":")
                var stop = false
                if (participantUsername == currentUser.userId){
                    Toast.makeText(
                        activity,
                        "Conversations with yourself are not permitted.",
                        Toast.LENGTH_LONG
                    ).show()
                    stop = true
                } else {
                    for (c in currentUser.conversations) {
                        if (c.user1Id == participantUsername || c.user2Id == participantUsername) {
                            Toast.makeText(
                                activity,
                                "Unable to start conversation. You already have a conversation with $participantUsername",
                                Toast.LENGTH_LONG
                            ).show()
                            stop = true
                            break
                        }
                    }
                }
                if (currentUser.checkIfInBlockList(participantUsername) && !stop) {
                    Toast.makeText(
                        activity,
                        "Unable to start conversation. $participantUsername has been blocked",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (participantUsername.isNotEmpty() && !stop) {
                    FirebaseFirestore.getInstance().collection("users")
                        .whereEqualTo("canonicalId", participantUsername)
                        .get()
                        .addOnSuccessListener { documentReference ->
                            if (documentReference.size() == 1) {
                                val newConvo = Conversation(currentUser.userId, participantUsername)
                                // Check if you're in the other user's block list
                                val blockList: MutableList<String>? =
                                    documentReference.documents[0].get("block_list") as MutableList<String>?

                                // Only start new conversation if the other person's blockList does not exist or you are not in it
                                if (blockList == null || !blockList.contains(currentUser.userId) || currentUser.blockedContacts.contains(
                                        participantUsername
                                    )
                                ) {
                                    currentUser.startConversation(newConvo)
                                    val conversationIntent =
                                        Intent(activity, ConversationActivity::class.java)
                                    conversationIntent.putExtra(
                                        "receiver_name",
                                        participantUsername
                                    )
                                    startActivity(conversationIntent)
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "Unable to start conversation. You have been blocked by $participantUsername",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            } else {
                                Toast.makeText(
                                    activity,
                                    "User could not be found. Check the username and try again.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(activity, exception.message, Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

        override fun getItemCount() = foundUsers.size
    }

    internal inner class ViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(userInfo: String) {
            itemView.findViewById<TextView>(R.id.user_item_id).text = userInfo
        }
    }
}