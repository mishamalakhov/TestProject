package android.malakhov.testproject.ui.location.RecyclerAdapter

import android.malakhov.testproject.R
import android.malakhov.testproject.ui.location.LocationFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(var list: ArrayList<String>?, var frag: LocationFragment) :
    RecyclerView.Adapter<MyViewHolder>() {

    private var deleteBtnVisibility = false  //Visibility of delete button

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        //Set header if viewType == 0
        if (viewType == 0) {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_header, parent, false)
            return MyViewHolder(
                v,
                LayoutInflater.from(parent.context),
                viewType,
                frag
            )
        }
        //Set photoPackage View
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_element, parent, false)

        return MyViewHolder(v, LayoutInflater.from(parent.context), viewType, frag)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {



        //If position isn't 0(element isn't a header) so we call onBind
        //Position = position-1 because there is a header with index 0
        if (position != 0) {

            val pack = frag.location.packagesList[list!![position-1]]

            holder.swipeDelete?.setOnClickListener{
                frag.list.remove(pack?.id)
                holder.scroll?.scrollTo(holder.scroll!!.left, 0)
                frag.location.packagesList.remove(pack?.id)
                frag.adapter?.notifyDataSetChanged()
                frag.deletePackageFromDB(pack!!)
            }

            if (!deleteBtnVisibility)
                holder.deleteBtn?.visibility = View.GONE

            holder.onBind(list?.get(position - 1))
            holder.btn?.setOnClickListener(View.OnClickListener {
                frag.openGallery(list?.get(position-1))
            })

            //Load location to DB after write a name of any photos-package
            holder.packageName?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    pack?.name = s.toString()
                    frag.loadLocationToDB()
                }
            })
        }
        //The element is header and we load Location to DB after we wrote his name
        else {
            holder.locationName?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    frag.location.name = s.toString()
                    frag.loadLocationToDB()
                }
            })
        }
    }

    //ItemCount = itemCount + 1 because there is a header with index 0
    override fun getItemCount(): Int {
        return list?.size!! + 1
    }

    //Check for a header
    override fun getItemViewType(position: Int): Int {
        return position
    }

    //Set delete button visibility = invisible when we touch a recycler
    fun setDeleteBtnVisibility():Boolean{
        return false
    }
}