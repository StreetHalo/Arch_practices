package com.example.arch_practices.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.arch_practices.model.Coin
import retrofit2.HttpException
import java.io.IOException

class PagingCoin: PagingSource<Int, Coin>() {
    private val step = 10
    override fun getRefreshKey(state: PagingState<Int, Coin>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Coin> {
        return try {
            val nextPage = params.key ?: 0
            val userList = Api.getInstance().getCoins(offset = nextPage).data
            LoadResult.Page(
                data = userList,
                prevKey = if (nextPage == 0) null else nextPage - step,
                nextKey = if (userList.isEmpty()) null else nextPage + step
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}