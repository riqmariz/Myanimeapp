package com.riqsphere.myapplication.cache

object Cache {
    private const val CACHE_SIZE = 20

    val data: Array<CacheItem> = Array(CACHE_SIZE) {
        CacheItem()
    }

    inline fun <reified T> get(fetch: () -> T, default: T, hashCode: Long): T {
        val itemIndex = data.indexOfFirst {
            it.hashEquals(hashCode)
        }

        return if (itemIndex >= 0) {
            data[itemIndex].attemptToCastStoredItem<T>() ?: fetchAndStoreWithSetIndex(fetch, default, hashCode, itemIndex)
        } else {
            fetchAndStore(fetch, default, hashCode)
        }
    }

    inline fun <T>fetchAndStore(fetch: () -> T, default: T, hashCode: Long): T {
        val oldest = oldestUseItem()
        val result = fetch()
        result?.let { oldest.newData(hashCode, result as Any) }
        return result ?: default
    }

    inline fun <T>fetchAndStoreWithSetIndex(fetch: () -> T, default: T, hashCode: Long, withIndex: Int): T {
        val result = fetch()
        result?.let { data[withIndex].newData(hashCode, result as Any) }
        return result ?: default
    }

    fun oldestUseItem(): CacheItem = data.minBy {
        it.lastUsed
    }!!
}