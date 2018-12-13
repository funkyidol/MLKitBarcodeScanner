package com.funkyidol.mlkitbarcodescanner.model

import com.squareup.moshi.Json

data class ProductsItem(

    @field:Json(name = "product_gtin")
    val productGtin: String? = null,

    @field:Json(name = "brand_image_url")
    val brandImageUrl: Any? = null,

    @field:Json(name = "category_name")
    val categoryName: Any? = null,

    @field:Json(name = "product_asin")
    val productAsin: Any? = null,

    @field:Json(name = "country_of_origin")
    val countryOfOrigin: String? = null,

    @field:Json(name = "brand_name")
    val brandName: String? = null,

    @field:Json(name = "product_image_url")
    val productImageUrl: String? = null,

    @field:Json(name = "category_browse_node_id")
    val categoryBrowseNodeId: Any? = null,

    @field:Json(name = "manufacturer_name")
    val manufacturerName: Any? = null,

    @field:Json(name = "product_description")
    val productDescription: Any? = null,

    @field:Json(name = "product_name")
    val productName: String? = null,

    @field:Json(name = "manufacturer_image_url")
    val manufacturerImageUrl: Any? = null
)