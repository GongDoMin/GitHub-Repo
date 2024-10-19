package com.prac.data.source.local.datastore.user

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.prac.data.datastore.User
import kotlinx.coroutines.flow.first
import java.io.InputStream
import java.io.OutputStream

internal class UserDataStoreManagerImpl(
    private val mContext: Context
) : UserDataStoreManager {

    companion object {
        private const val USER_DATA_STORE_NAME = "userDataStore"
    }

    private class TokenSerializer : Serializer<User> {
        override val defaultValue: User = User.getDefaultInstance()
        override suspend fun readFrom(input: InputStream): User {
            try {
                return User.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", exception)
            }
        }

        override suspend fun writeTo(t: User, output: OutputStream) = t.writeTo(output)
    }

    private val Context.userDataStore: DataStore<User> by dataStore(
        fileName = USER_DATA_STORE_NAME,
        serializer = TokenSerializer(),
    )

    override suspend fun setUserName(userName: String) {
        mContext.userDataStore.updateData {
            it.toBuilder()
                .setUserName(userName)
                .build()
        }
    }

    override suspend fun getUserName(): String {
        return mContext.userDataStore.data.first().userName
    }

    override suspend fun clearUserName() {
        mContext.userDataStore.updateData {
            it.toBuilder()
                .clear()
                .build()
        }
    }
}