package com.idon.emergencmanagement.model

class SliderItem {

    private var description: String? = null
    private var imageUrl: String? = null

    fun getDescription(): String? {
        return description
    }

    fun setDescription(description: String?) {
        this.description = description
    }

    fun getImageUrl(): String? {
        return imageUrl
    }

    fun setImageUrl(imageUrl: String?) {
        this.imageUrl = imageUrl
    }

}