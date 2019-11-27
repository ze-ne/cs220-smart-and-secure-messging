package com.cs220.ssmessaging.frontend.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.User
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {
    private lateinit var currentUser: User
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var allUsers: MutableList<String>
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = MyApplication.currentUser!!
        setHasOptionsMenu(true)

        // TODO: get all users to list of userIds gotten from database
        allUsers = mutableListOf()
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
        displayUsers()
        return searchView
    }

    private fun displayUsers(){
        searchAdapter = SearchAdapter(activity as Context, allUsers)
        searchRecyclerView.adapter = searchAdapter
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_fragment_menu, menu)
        val searchItem = menu.findItem(R.id.user_search)
        val searchView = searchItem.actionView as SearchView

        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchAdapter.filter.filter(newText)
                return false
            }
        })
    }

    internal inner class SearchAdapter(
        context: Context,
        private val usersList: MutableList<String>
    ) :
        RecyclerView.Adapter<ViewHolder>(), Filterable {
        private var usersListFull = mutableListOf<String>()

        init {
            for (userId in usersList) {
                usersListFull.add(userId)
            }
        }

        private val layoutInflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_search, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val user = usersList[position]
            viewHolder.bind(user)
        }

        override fun getItemCount() = usersList.size

        override fun getFilter(): Filter {
            return usersListFilter
        }

        private val usersListFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = ArrayList<String>()

                if (constraint == null || constraint.isEmpty()) {
                    filteredList.addAll(usersListFull)
                } else {
                    val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }

                    for (item in usersListFull) {
                        if (item.toLowerCase().contains(filterPattern)) {
                            filteredList.add(item)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList

                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                usersList.clear()
                usersList.addAll(results.values as List<String>)
                notifyDataSetChanged()
            }
        }
    }

    internal inner class ViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(username: String) {
            itemView.findViewById<TextView>(R.id.user_item_id).text = username
        }
    }
}