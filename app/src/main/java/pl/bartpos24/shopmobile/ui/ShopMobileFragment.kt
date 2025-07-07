package pl.bartpos24.shopmobile.ui

import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pl.bartpos24.shopmobile.MainActivity
import pl.bartpos24.shopmobile.ShopMobileApplication
import pl.bartpos24.shopmobile.viewmodels.ShopMobileViewModelFactory

open class ShopMobileFragment : Fragment {
    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)
    private val viewModelFactory: ShopMobileViewModelFactory by lazy {
        ShopMobileViewModelFactory((requireActivity().application as ShopMobileApplication).appComponent, this, arguments)
    }

//    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
//        parentFragmentManager
//        return viewModelFactory
//    }

    override fun setHasOptionsMenu(value: Boolean) {
        super.setHasOptionsMenu(value)
        (this.activity as MainActivity).setHasOptionsMenu(value)
    }

    override fun onStop() {
        (this.activity as MainActivity).setHasOptionsMenu(false)
        super.onStop()
    }
}

open class ShopMobileDialogFragment : DialogFragment {
    constructor() : super()
    private val viewModelFactory: ShopMobileViewModelFactory by lazy {
        ShopMobileViewModelFactory((requireActivity().application as ShopMobileApplication).appComponent, this, arguments)
    }

//    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
//        parentFragmentManager
//        return viewModelFactory
//    }
}