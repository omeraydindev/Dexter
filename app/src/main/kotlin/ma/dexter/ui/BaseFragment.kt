package ma.dexter.ui

import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    fun drawable(@DrawableRes drawableRes: Int) =
        ContextCompat.getDrawable(requireContext(), drawableRes)

    override fun onResume() {
        super.onResume()

        setHasOptionsMenu(isVisible)
    }

}
