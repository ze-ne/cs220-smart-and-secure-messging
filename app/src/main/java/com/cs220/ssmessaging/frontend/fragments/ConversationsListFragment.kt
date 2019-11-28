package com.cs220.ssmessaging.frontend.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs220.ssmessaging.R
import android.content.Intent
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.widget.SearchView
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.clientBackend.Conversation
import com.cs220.ssmessaging.clientBackend.User
import com.cs220.ssmessaging.frontend.activities.ConversationActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_conversations_list.*
import kotlinx.android.synthetic.main.item_conversation.view.*
import java.lang.Exception


class ConversationsListFragment : Fragment() {
    private lateinit var conversationsRecyclerView: RecyclerView
    private lateinit var conversationsListAdapter: ConversationsListAdapter
    private lateinit var newConversationButton: FloatingActionButton
    private lateinit var currentUser: User
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = MyApplication.currentUser!!
        addConversationListener()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val conversationsView =
            inflater.inflate(R.layout.fragment_conversations_list, container, false)
        conversationsRecyclerView = conversationsView.findViewById(R.id.conversations_recycler_list)
        conversationsRecyclerView.layoutManager = LinearLayoutManager(context)

        val activity = activity as Context
        conversationsListAdapter = ConversationsListAdapter(activity, currentUser.conversations)
        conversationsRecyclerView.adapter = conversationsListAdapter

        newConversationButton = conversationsView.findViewById(R.id.new_conversation_button)
        newConversationButton.setOnClickListener {
            val addConversationDialog = AddConversationDialog()
            addConversationDialog.show(fragmentManager!!, "AddConversationDialog")
        }
        return conversationsView
    }

    private fun addConversation(dc: DocumentChange) {
        val document = dc.document
        val users = document.get("users") as List<String>
        val conversation = Conversation(users[0], users[1], ArrayList())

        try {
            val otherUser =
                if (users[0] == currentUser.userId)
                    users[1]
                else
                    users[0]
            // From Task documentation: If multiple listeners are added, they will be called in the order in which they were added.
            currentUser.getUserPublicKey(otherUser).addOnSuccessListener {
                Log.i("Key Exchange", "Key Exchange passed and now adding conversation")
                currentUser.addConversation(conversation)
                Log.i("Conversations", currentUser.conversations.toString())
                Log.i("Conversations", currentUser.conversations.size.toString())
                displayConversations()
            }
        } catch (e: Exception) {
            Log.e("Key Exchange", e.toString())
        }
    }

    private fun deleteConversation(dc: DocumentChange) {
        println("================== DELET CNVO")
        val document = dc.document
        val convoId = document.get("canonicalId") as String
        val bool = currentUser.deleteConversation(convoId)
        println("BOOL == " + bool)
        println("== CONVO LENGTH " + currentUser.conversations.size)
        println("== CONVO LIST " + currentUser.conversations)
        displayConversations()

    }

    private fun addConversationListener() {
        db.collection("conversations").whereArrayContains("users", currentUser.userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                //if (snapshot != null) { //&& !snapshot.isEmpty()) {
                for (dc: DocumentChange in snapshot!!.documentChanges) {

                    when (dc.type) {
                        DocumentChange.Type.ADDED -> addConversation(dc)
                        DocumentChange.Type.REMOVED -> deleteConversation(dc)
                        else -> println("====== conversation neither added nor removed =======")
                    }
                }
                //}
            }
    }

    private fun displayConversations() {
        if(activity != null) {
            conversationsListAdapter =
                ConversationsListAdapter(activity as Context, currentUser.conversations)
            conversations_recycler_list.adapter = conversationsListAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.conversations_list_fragment_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                conversationsListAdapter.filter.filter(newText)
                return false
            }
        })
    }

    internal inner class ConversationsListAdapter(
        context: Context,
        private val conversationsList: MutableList<Conversation>
    ) :
        RecyclerView.Adapter<ViewHolder>(), Filterable {
        private var conversationsListFull = mutableListOf<Conversation>()

        init {
            for (conversation in conversationsList) {
                conversationsListFull.add(conversation)
            }
        }

        private val layoutInflater = LayoutInflater.from(context)

        private fun getOtherUserId(conversation: Conversation): String {
            if (currentUser.userId == conversation.user1Id) {
                return conversation.user2Id
            }
            return conversation.user1Id
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_conversation, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val conversation = conversationsList[position]
            val otherUser = getOtherUserId(conversation)

            viewHolder.bind(otherUser)

            viewHolder.itemView.conversation_participant.setOnClickListener {
                val conversationIntent = Intent(context, ConversationActivity::class.java)
                conversationIntent.putExtra("receiver_name", getOtherUserId(conversation))
                startActivity(conversationIntent)
            }

            viewHolder.itemView.delete_conversation_button.setOnClickListener {
                currentUser.deleteConversationFromDb(conversation.convoId)
            }
            viewHolder.itemView.block_button.setOnClickListener {
                currentUser.addBlockedContactToDb(otherUser) {
                    fragmentManager?.findFragmentById(R.id.home_tab_pager)?.onStart()
                }
            }
        }

        override fun getItemCount() = conversationsList.size

        override fun getFilter(): Filter {
            return conversationsFilter
        }

        private val conversationsFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = ArrayList<Conversation>()

                if (constraint == null || constraint.isEmpty()) {
                    filteredList.addAll(conversationsListFull)
                } else {
                    val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }

                    for (item in conversationsListFull) {
                        if (item.convoId.toLowerCase().contains(filterPattern)) {
                            filteredList.add(item)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList

                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                conversationsList.clear()
                conversationsList.addAll(results.values as List<Conversation>)
                notifyDataSetChanged()
            }
        }
    }

    internal inner class ViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(fullname: String) {
            itemView.findViewById<TextView>(R.id.conversation_participant).text = fullname
        }
    }
}