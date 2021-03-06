package com.moonpig.mvisample.screentests.productdetail

import android.content.Intent
import com.moonpig.mvisample.domain.entities.ProductDetail
import com.moonpig.mvisample.domain.productdetail.RepositoryState
import com.moonpig.mvisample.productdetail.ProductDetailActivity
import com.moonpig.mvisample.screentests.ScreenTestApplication
import com.moonpig.mvisample.screentests.di.DaggerTestApplicationComponent
import com.moonpig.mvisample.screentests.di.MockProductDetailDataModule
import com.nhaarman.mockito_kotlin.given
import io.reactivex.Observable
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ProductDetailTests {

    @get:Rule
    val testRule = ProductDetailTestRule()

    private fun givenAProductDetailActivity() {
        ScreenTestApplication.overriddenApplicationComponent =
                DaggerTestApplicationComponent.builder()
                        .build()

        val intent = ProductDetailActivity.intentForProduct(ScreenTestApplication.instance, productId)
        testRule.launchActivity(intent)
    }

    @Test
    fun shouldBeShowingLoadingIndicator() {
        given(MockProductDetailDataModule.productDetailRepository.getProductDetails(productId))
                .willReturn(Observable.just(RepositoryState.GetProductDetail.InFlight))
        givenAProductDetailActivity()

        ProductDetailRobot()
                .isLoading()
    }

    @Test
    fun shouldNotBeShowingLoadingIndicatorAfterFetchingFinished() {
        given(MockProductDetailDataModule.productDetailRepository.getProductDetails(productId))
                .willReturn(Observable.just(
                        RepositoryState.GetProductDetail.InFlight,
                        RepositoryState.GetProductDetail.Success(ProductDetail(
                                "name",
                                "desc",
                                0,
                                "imageUrl")
                        ))
                )
        givenAProductDetailActivity()

        ProductDetailRobot()
                .isNotLoading()
    }

    @Test
    fun shouldShowProductDetails_whenFetchingSucceeds() {
        given(MockProductDetailDataModule.productDetailRepository.getProductDetails(productId))
                .willReturn(Observable.just(
                        RepositoryState.GetProductDetail.InFlight,
                        RepositoryState.GetProductDetail.Success(ProductDetail(
                                "name",
                                "desc",
                                0,
                                "imageUrl")
                        ))
                )
        givenAProductDetailActivity()

        ProductDetailRobot()
                .nameDisplayed("name")
                .descriptionDisplayed("desc")
                .priceDisplayed("£0")
    }

    @Test
    fun shouldNotBeShowingLoadingIndicator_whenLoadingFails() {
        given(MockProductDetailDataModule.productDetailRepository.getProductDetails(productId))
                .willReturn(Observable.just(
                        RepositoryState.GetProductDetail.InFlight,
                        RepositoryState.GetProductDetail.Error(IOException("Product not found"))
                ))
        givenAProductDetailActivity()

        ProductDetailRobot()
                .isNotLoading()
    }

    @Test
    fun shouldNotShowErrorMessage_whenLoading() {
        given(MockProductDetailDataModule.productDetailRepository.getProductDetails(productId))
                .willReturn(Observable.just(RepositoryState.GetProductDetail.InFlight))
        givenAProductDetailActivity()

        ProductDetailRobot()
                .errorMessageNotDisplayed()
    }

    @Test
    fun shouldShowErrorMessage_whenLoadingFails() {
        given(MockProductDetailDataModule.productDetailRepository.getProductDetails(productId))
                .willReturn(Observable.just(
                        RepositoryState.GetProductDetail.InFlight,
                        RepositoryState.GetProductDetail.Error(IOException("Product not found"))
                ))
        givenAProductDetailActivity()

        ProductDetailRobot()
                .errorMessageDisplayed()
    }

    companion object {
        const val productId = "productId"
    }
}