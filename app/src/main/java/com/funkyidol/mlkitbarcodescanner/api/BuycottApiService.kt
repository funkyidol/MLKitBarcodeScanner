package com.funkyidol.mlkitbarcodescanner.api

import com.funkyidol.mlkitbarcodescanner.model.BarcodeResultPojo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BuycottApiService {
    @GET("api/v4/products/lookup/")
    fun lookupBarcode(
        @Query("barcode") barcode: String?,
        @Query("access_token") accessToken: String = "0d4IttQC8lSdzR-hao8uPB3ezTRh0AgkZ9OhKXDL"): Call<BarcodeResultPojo>
}