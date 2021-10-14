package android.malakhov.testproject.ui.location.RecyclerAdapter

import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.malakhov.testproject.R
import android.malakhov.testproject.ui.location.LocationFragment
import android.malakhov.testproject.ui.location.ViewPager.PhotoSearchFragment
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.gridlayout.widget.GridLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class MyViewHolder(itemView: View, var inflater: LayoutInflater, var type: Int,
                   var frag: LocationFragment
) :
    RecyclerView.ViewHolder(itemView) {

    var btn: ImageView? = null  //plus-button fot adding photos
    var grid: GridLayout? = null    //package-Grid
    var locationName: EditText? = null  // Edit text with Location name
    var packageName: EditText? = null  // Edit text with photos-package name
    var deleteBtn: TextView? = null  // Edit text with photos-package name
    var swipeDelete: CardView? = null   //When we swipe this button appears for delete
    var scroll: HorizontalScrollView? = null  //ScrollView

    init {
        //If the element isn't header so we initialize its views
        if (type != 0) {
            btn = itemView.findViewById(R.id.smallPlus)
            grid = itemView.findViewById(R.id.grid)
            packageName = itemView.findViewById(R.id.packageName)
            deleteBtn = itemView.findViewById(R.id.deleteBtn)
            swipeDelete = itemView.findViewById(R.id.swipeDelete)
            scroll = itemView.findViewById(R.id.scroll)

            //Set width of view equals to Display width
            val display: Display? = frag.activity?.windowManager?.getDefaultDisplay()
            val iv = itemView.findViewById<CardView>(R.id.cardView)
            val width = display?.width // ((display.getWidth()*20)/100)
            val params = iv.layoutParams
            params.width = width!!
            iv.layoutParams = params
        }
        //the element is header and we initialize his views
        else locationName = itemView.findViewById(R.id.locationName)
    }

    fun onBind(packId: String?) {
        var i = 0   //number of image
        val pack = frag.location.packagesList[packId]


        //If element isn't header so we bind photos
        if (type != 0) {
            grid?.removeAllViews()

            //bind all images from package
            for (photo in pack?.uriList!!) {
                val view = inflater.inflate(R.layout.photo_layout, null, false)//layout of one photo
                val imageView: ImageView = view.findViewById(R.id.photo)//imageView int photo layout

                //Long click on photo and then delete button appear
                imageView.setOnLongClickListener {
                    deleteBtn?.visibility = View.VISIBLE
                    onBind(packId)
                    true
                }

                //here a checkbox = circle(ImageView) + cross(ImageView)
                val uncheckedBox = view.findViewById<ImageView>(R.id.empty_check)//circle of checkbox for one image
                val cross = view.findViewById<ImageView>(R.id.cross)//cross in check box for one image

                //if deleteBtn is visible so circle checkboxes become visible too
                if (deleteBtn?.visibility == View.VISIBLE) {
                    uncheckedBox.visibility = View.VISIBLE

                    //when we click on image then its check box become checked or unchecked
                    //(cross appear or disappear)
                    imageView.setOnClickListener{
                        if(deleteBtn?.visibility == View.VISIBLE)
                            if (cross.visibility == View.INVISIBLE) cross.visibility = View.VISIBLE
                            else cross.visibility = View.INVISIBLE
                    }
                } else {
                    cross.visibility = View.INVISIBLE

                    imageView.setOnClickListener{
                        val image = it as ImageView
                        val imgList = ArrayList(pack.uriList.values)
                        val pos = imgList.indexOf(pack.uriList[image.contentDescription])
                        val nextFrag = PhotoSearchFragment(imgList, pos)
                        frag.activity?.supportFragmentManager?.beginTransaction()
                            ?.add(R.id.parentContainer, nextFrag, "findThisFragment")
                            ?.addToBackStack(null)
                            ?.commit()
                    }
                }

                //Load photo into ImageView
                Picasso.get().load(photo.value).fit().centerCrop().into(imageView)

                //Save id of image(id = key of uriList-hashMap) in image as a content description, so
                // then I can delete this uri from pack and from DB
                imageView.contentDescription = photo.key
                //Add image to package(package = GridLayout)
                grid?.addView(view)
                i++
            }

            //deleteBtn onClick - delete uri from uriList-hashmap in package and then call deletePhotosFromDB-func
            //to delete photos from DB
            deleteBtn?.setOnClickListener{
                val images = grid?.children?.toMutableList()
                val list = ArrayList<String>()

                images?.forEach {
                    val cross = it.findViewById<ImageView>(R.id.cross)
                    val image = it.findViewById<ImageView>(R.id.photo)

                    if (cross.visibility == View.VISIBLE){
                        grid!!.removeView(it)
                        pack.uriList.remove(image.contentDescription)
                        frag.location.packagesList[pack.id] = pack
                        list.add(image.contentDescription.toString())
                    }
                }
                deleteBtn!!.visibility = View.GONE
                frag.adapter?.notifyDataSetChanged()
                frag.deletePhotosFromDB(pack, list)
            }
        }
    }
}