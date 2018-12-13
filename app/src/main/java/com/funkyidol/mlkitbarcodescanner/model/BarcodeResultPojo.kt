package com.funkyidol.mlkitbarcodescanner.model

import com.squareup.moshi.Json

data class BarcodeResultPojo(

    @field:Json(name = "status_code")
    val statusCode: Int? = null,

    @field:Json(name = "success")
    val success: Boolean? = null,

    @field:Json(name = "product_count")
    val productCount: Int? = null,

    @field:Json(name = "products")
    val products: List<ProductsItem?>? = null
)