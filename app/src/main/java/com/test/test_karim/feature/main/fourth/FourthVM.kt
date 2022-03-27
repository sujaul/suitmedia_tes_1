package com.test.test_karim.feature.main.fourth

import androidx.lifecycle.*
import com.galee.core.BaseVM
import com.galee.core.data.BaseResponse
import com.test.test_karim.Repository.guestsRepository
import com.test.test_karim.data.model.Guests
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FourthVM(private val guestsRepository: guestsRepository): BaseVM() {
    override fun getTagName(): String = javaClass.simpleName

    private val _get = MutableLiveData<BaseResponse<List<Guests>>>()
    val getGuests: LiveData<BaseResponse<List<Guests>>> = _get

    fun getGuestToApi(){
        viewModelScope.launch {
            kotlin.runCatching {
                _get.value = BaseResponse.Loading()
                guestsRepository.getGuestsToApi()
            }.onSuccess { data ->
                data?.let {
                  it.collect { it2  ->
                      _get.postValue(BaseResponse.Success(it2))
                  }
                } ?: _get.postValue(BaseResponse.Success(listOf()))
            }.onFailure {
                _get.postValue(BaseResponse.Error(it, 2))
            }

        }
    }
}
