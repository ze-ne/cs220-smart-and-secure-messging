package com.cs220.ssmessaging.frontend.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Button
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

class BlockedListFragment : Fragment() {
    private lateinit var currentUser: User
    private lateinit var blockListRecyclerView: RecyclerView
    private lateinit var blockListAdapter: BlockListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = MyApplication.currentUser!!
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val blockListView =
            inflater.inflate(R.layout.fragment_blocked_list, container, false)
        blockListRecyclerView = blockListView.findViewById(R.id.block_recycler_list)
        blockListRecyclerView.layoutManager = LinearLayoutManager(context)

        val activity = activity as Context
        blockListAdapter = BlockListAdapter(activity, currentUser.blockedContacts)
        blockListRecyclerView.adapter = blockListAdapter

        return blockListView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.block_list_fragment_menu, menu)
        val searchItem = menu.findItem(R.id.block_action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                blockListAdapter.filter.filter(newText)
                return false
            }
        })
    }

    internal inner class BlockListAdapter(
        context: Context,
        private val blockList: MutableList<String>
    ) :
        RecyclerView.Adapter<ViewHolder>(), Filterable {
        private var blockListFull = mutableListOf<String>()

        init {
            for (userId in blockList) {
                blockListFull.add(userId)
            }
        }

        private val layoutInflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_blocked, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val user = blockList[position]

            viewHolder.bind(user)

            val btn = viewHolder.itemView.findViewById<Button>(R.id.unblock_button)
            btn.setOnClickListener{
                currentUser.deleteBlockedContact(user)
            }
        }

        override fun getItemCount() = blockList.size

        override fun getFilter(): Filter {
            return blockListFilter
        }

        private val blockListFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = ArrayList<String>()

                if (constraint == null || constraint.isEmpty()) {
                    filteredList.addAll(blockListFull)
                } else {
                    val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }

                    for (item in blockListFull) {
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
                blockList.clear()
                blockList.addAll(results.values as List<String>)
                notifyDataSetChanged()
            }
        }
    }

    internal inner class ViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(fullname: String) {
            itemView.findViewById<TextView>(R.id.blocked_participant).text = fullname
        }
    }

}