package ma.dexter.ui.fragment

import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    override fun onResume() {
        super.onResume()

        setHasOptionsMenu(isVisible)
    }

}
