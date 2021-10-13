package android.malakhov.testproject.ui.location.ViewPager

import android.malakhov.testproject.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import java.text.FieldPosition


class PhotoSearchFragment(val list: ArrayList<String>,val currPosition: Int) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_search, container, false)


        val mViewPager =
            view.findViewById<ViewPager>(R.id.photo_pager)
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        mViewPager.adapter = object :
            FragmentStatePagerAdapter(fragmentManager) {
            override fun getItem(position: Int): Fragment {
                val uri: String = list.get(position)
                return PhotoPagerFragment.newInstance(uri)
            }

            override fun getCount(): Int {
                return list.size
            }
        }
        mViewPager.currentItem = currPosition


        return view
    }
}