package paul.barthuel.go4lunch.ui.workmates

class WorkmatesInfo(val name: String?,
                    val image: String?,
                    val id: String?,
                    val placeId: String?,
                    val restaurantName: String?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as WorkmatesInfo
        if (if (name != null) name != that.name else that.name != null) return false
        if (if (image != null) image != that.image else that.image != null) return false
        if (if (id != null) id != that.id else that.id != null) return false
        if (if (placeId != null) placeId != that.placeId else that.placeId != null) return false
        return if (restaurantName != null) restaurantName == that.restaurantName else that.restaurantName == null
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (placeId?.hashCode() ?: 0)
        result = 31 * result + (restaurantName?.hashCode() ?: 0)
        return result
    }

}