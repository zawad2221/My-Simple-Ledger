package com.example.mysimpleledger.di

import android.app.Application
import androidx.room.Room
import com.example.mysimpleledger.data.repository.TransactionRepository
import com.example.mysimpleledger.data.room.TransactionDatabase
import com.example.mysimpleledger.network.api.Api
import com.example.mysimpleledger.network.api.TransactionApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(Api.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTransactionApi(retrofit: Retrofit): TransactionApi =
        retrofit.create(TransactionApi::class.java)

    @Provides
    @Singleton
    fun provideTransactionRepository(transactionApi: TransactionApi,
                                     db: TransactionDatabase): TransactionRepository =
            TransactionRepository(transactionApi, db)

    @Provides
    @Singleton
    fun provideTransactionDatabase(app: Application) : TransactionDatabase =
            Room.databaseBuilder(app, TransactionDatabase::class.java, "transaction_database")
                    .build()


}