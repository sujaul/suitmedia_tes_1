package com.galee.core.data

/*data class BaseResponse<out T>(
    val status: Status,
    var case: Int = 0,
    val data: T?,
    var message: String
) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,
    }

    companion object {
        fun <T> loading(data: T? = null): BaseResponse<T> {
            return BaseResponse(Status.LOADING, 0, data, message = "")
        }

        fun <T> success(message: String, data: T): BaseResponse<T> {
            return BaseResponse(Status.SUCCESS, 0, data, message)
        }

        fun <T> error(message: String, case: Int = 0, data: T? = null): BaseResponse<T> {
            return BaseResponse(Status.ERROR, case, data, message)
        }
    }
}*/

sealed class BaseResponse<out T : Any> {
    class Loading(val messsage: String? = null) : BaseResponse<Nothing>()
    class Success<out T : Any>(val data: T) : BaseResponse<T>()
    /**
     * @param case
     * 0 = Masalah Pada Permission, 1 = GPS Tidak Aktif, 2 = Koneksi Internet Bermasalah,
     * 3 = Error Bugs, 4 = Tidak Ada Data, else = Unknown Error ()
     *
     **/
    class Error(val exception: Throwable, val case: Int = 4, val message: String = if (exception.localizedMessage == null) exception.message ?: "Internal server error, periksa jaringan anda!" else exception.localizedMessage!!) :
        BaseResponse<Nothing>()
}
