package com.moonpig.mvisample.productdetail

import com.moonpig.mvisample.domain.ProductDetailAction
import com.moonpig.mvisample.domain.ProductDetailResult
import com.moonpig.mvisample.domain.ProductDetailUseCase
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ProductDetailViewModelTest {

    private val productDetailUseCase: ProductDetailUseCase = mock()
    private val productDetailTracker: ProductDetailTracker = mock()

    @Test
    fun shouldEmitInitialIntentOnce() {
        whenever(productDetailUseCase.resultFrom(ProductDetailAction.LoadProductDetail)).thenReturn(Observable.just(ProductDetailResult.GetProductDetail.InFlight))
        whenever(productDetailUseCase.resultFrom(ProductDetailAction.AddProductToBasket(PRODUCT_ID, QUANTITY))).thenReturn(Observable.just(ProductDetailResult.AddProduct.Success))

        val viewModel = givenAProductDetailViewModel()
        val testObserver = viewModel.viewState().test()
        viewModel.bindIntents(Observable.merge(Observable.just(ProductDetailIntent.Initial),
                                               Observable.just(ProductDetailIntent.AddToBasket(PRODUCT_ID, QUANTITY)),
                                               Observable.just(ProductDetailIntent.Initial)))

        assertThat(testObserver.valueCount()).isEqualTo(2)
    }

    @Test
    fun shouldEmitInFlightState_whenGetProductInFlight() {
        whenever(productDetailUseCase.resultFrom(ProductDetailAction.LoadProductDetail)).thenReturn(Observable.just(ProductDetailResult.GetProductDetail.InFlight))
        val viewModel = givenAProductDetailViewModel()
        val testObserver = viewModel.viewState().test()
        viewModel.bindIntents(Observable.just(ProductDetailIntent.Initial))

        assertThat(testObserver.values()[0].getProductDetailInFlight).isFalse()
        assertThat(testObserver.values()[1].getProductDetailInFlight).isTrue()
    }

    @Test
    fun shouldEmitSuccessState_whenGetProductSuccess() {
        val productDetail = ProductDetailResult.GetProductDetail.Success(NAME, DESCRIPTION, PRICE)
        whenever(productDetailUseCase.resultFrom(ProductDetailAction.LoadProductDetail)).thenReturn(Observable.just(productDetail))
        val viewModel = givenAProductDetailViewModel()
        val testObserver = viewModel.viewState().test()

        viewModel.bindIntents(Observable.just(ProductDetailIntent.Initial))

        assertThat(testObserver.values()[1].productDetail.name).isEqualTo(NAME)
        assertThat(testObserver.values()[1].productDetail.description).isEqualTo(DESCRIPTION)
        assertThat(testObserver.values()[1].productDetail.price).isEqualTo(PRICE)
        assertThat(testObserver.values()[1].getProductDetailInFlight).isFalse()
    }

    @Test
    fun shouldEmitErrorState_whenGetProductError() {
        val exception = RuntimeException()
        val error = ProductDetailResult.GetProductDetail.Error(exception)
        whenever(productDetailUseCase.resultFrom(ProductDetailAction.LoadProductDetail)).thenReturn(Observable.just(error))
        val viewModel = givenAProductDetailViewModel()
        val testObserver = viewModel.viewState().test()

        viewModel.bindIntents(Observable.just(ProductDetailIntent.Initial))

        assertThat(testObserver.values()[1].getProductDetailError).isEqualTo(exception)
        assertThat(testObserver.values()[1].getProductDetailInFlight).isFalse()
    }

    @Test
    fun shouldEmitInFlightState_whenAddProductInFlight() {
        whenever(productDetailUseCase.resultFrom(ProductDetailAction.AddProductToBasket(PRODUCT_ID, QUANTITY))).thenReturn(Observable.just(ProductDetailResult.AddProduct.InFlight))
        val viewModel = givenAProductDetailViewModel()
        val testObserver = viewModel.viewState().test()
        viewModel.bindIntents(Observable.just(ProductDetailIntent.AddToBasket(PRODUCT_ID, QUANTITY)))

        assertThat(testObserver.values()[0].addToBasketInFlight).isFalse()
        assertThat(testObserver.values()[1].addToBasketInFlight).isTrue()
    }

    @Test
    fun shouldEmitSuccessState_whenAddProductSuccess() {
        whenever(productDetailUseCase.resultFrom(ProductDetailAction.AddProductToBasket(PRODUCT_ID, QUANTITY))).thenReturn(Observable.just(ProductDetailResult.AddProduct.InFlight, ProductDetailResult.AddProduct.Success))
        val viewModel = givenAProductDetailViewModel()
        val testObserver = viewModel.viewState().test()
        viewModel.bindIntents(Observable.just(ProductDetailIntent.AddToBasket(PRODUCT_ID, QUANTITY)))

        assertThat(testObserver.values()[0].addToBasketInFlight).isFalse()
        assertThat(testObserver.values()[1].addToBasketInFlight).isTrue()
        assertThat(testObserver.values()[2].addToBasketInFlight).isFalse()
    }

    @Test
    fun shouldEmitErrorState_whenAddProductError() {
        val exception = RuntimeException()
        val error = ProductDetailResult.AddProduct.Error(exception)
        whenever(productDetailUseCase.resultFrom(ProductDetailAction.AddProductToBasket(PRODUCT_ID, QUANTITY))).thenReturn(Observable.just(error))
        val viewModel = givenAProductDetailViewModel()
        val testObserver = viewModel.viewState().test()

        viewModel.bindIntents(Observable.just(ProductDetailIntent.AddToBasket(PRODUCT_ID, QUANTITY)))

        assertThat(testObserver.values()[0].addToBasketError).isNull()
        assertThat(testObserver.values()[1].addToBasketError).isEqualTo(exception)
        assertThat(testObserver.values()[1].addToBasketInFlight).isFalse()
    }

    private fun givenAProductDetailViewModel() = ProductDetailViewModel(productDetailUseCase, productDetailTracker)

    companion object {
        const val PRODUCT_ID = "122fkjkjfd"
        const val QUANTITY = 1
        const val DESCRIPTION = "description"
        const val NAME = "name"
        const val PRICE = 199
    }
}