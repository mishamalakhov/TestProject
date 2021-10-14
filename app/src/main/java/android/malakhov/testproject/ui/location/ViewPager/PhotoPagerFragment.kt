package android.malakhov.testproject.ui.location.ViewPager

import android.icu.number.NumberFormatter.with
import android.malakhov.testproject.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide.with
import com.squareup.picasso.Picasso
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

class PhotoPagerFragment(private val photoUri: String?) : Fragment() {
    private var mImageView: PhotoView? = null
    private var mAttacher: PhotoViewAttacher? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_photo_pager, container, false)
        mImageView = v.findViewById(R.id.photoView)
        mAttacher = PhotoViewAttacher(mImageView)
        mAttacher!!.update()
        mAttacher = PhotoViewAttacher(mImageView)
        mAttacher!!.update()

        Picasso.get().load(photoUri).fit().centerCrop().into(v as ImageView)
        return v
    }

    companion object {
        fun newInstance(uri: String?): Fragment {
            return PhotoPagerFragment(uri)
        }
    }
}