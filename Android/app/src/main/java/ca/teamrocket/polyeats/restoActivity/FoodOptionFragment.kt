package ca.teamrocket.polyeats.restoActivity

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import ca.teamrocket.polyeats.R
import ca.teamrocket.polyeats.network.Backend
import ca.teamrocket.polyeats.network.models.FullMenuItem
import ca.teamrocket.polyeats.network.models.MenuItem
import ca.teamrocket.polyeats.network.models.Resto
import kotlinx.android.synthetic.main.fragment_foodoption_list.*
import kotlinx.android.synthetic.main.fragment_foodoption_list.view.*
import java.util.ArrayList

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [FoodOptionFragment.OnListFragmentInteractionListener] interface.
 */
class FoodOptionFragment : Fragment() {

    private var columnCount = 2
    private val items: MutableList<MenuItem> = ArrayList()
    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var resto: Resto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as RestoActivity).order = ArrayList()
        resto = activity?.intent?.getSerializableExtra("Resto") as Resto
        Backend.getMenuItemsForResto((activity as RestoActivity).requestQueue, resto.id.toString(), ::populateMenuItems)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkoutBtn.setOnClickListener {
            val fmi = FullMenuItem()
            fmi.id = "-1"
            fmi.price = "1.02$"
            fmi.options = ""
            fmi.name = "Delivery fees"
            fmi.specs = ""
            (activity as RestoActivity).order.add(fmi)
            (activity as RestoActivity).swapFrag()
        }
    }

    private fun populateMenuItems(listMenuItems:List<MenuItem>?) {
        if(listMenuItems==null) {
            Log.d("ERROR", "AUCUN ITEM DANS LE MENU")
            return
        }

        items.addAll(listMenuItems)
        food_list.adapter?.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_foodoption_list, container, false)

        // Set the adapter
        if (view is ConstraintLayout) {
            with(view.food_list) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = FoodOptionRecyclerViewAdapter(items, listener)
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: MenuItem?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            FoodOptionFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
