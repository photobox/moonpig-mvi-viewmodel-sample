package com.moonpig.mvisample.productdetail

import android.util.Log
import com.moonpig.mvisample.mvibase.BaseTracker

class ProductDetailTracker : BaseTracker<ProductDetailScreenViewState, ProductDetailIntent> {
    override fun trackViewState(viewState: ProductDetailScreenViewState) {
        Log.d(ProductDetailTracker::class.java.simpleName, "ViewState: $viewState")
    }

    override fun trackIntent(intent: ProductDetailIntent) {
        Log.d(ProductDetailTracker::class.java.simpleName, "Intent: $intent")
    }
}
