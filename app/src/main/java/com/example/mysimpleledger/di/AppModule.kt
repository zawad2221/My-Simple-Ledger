package com.example.mysimpleledger.di

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import com.example.mysimpleledger.data.PrefManager
import com.example.mysimpleledger.data.repository.AuthRepository
import com.example.mysimpleledger.data.repository.TransactionRepository
import com.example.mysimpleledger.data.room.TransactionDatabase
import com.example.mysimpleledger.data.api.Api
import com.example.mysimpleledger.data.api.AuthApi
import com.example.mysimpleledger.data.api.TransactionApi
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
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
            retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideAuthRepository(authApi: AuthApi): AuthRepository =
            AuthRepository(authApi)

    @Provides
    @Singleton
    fun provideTransactionRepository(transactionApi: TransactionApi,
                                     db: TransactionDatabase, prefManager: PrefManager): TransactionRepository =
            TransactionRepository(transactionApi, db, prefManager)

    @Provides
    @Singleton
    fun provideTransactionDatabase(app: Application) : TransactionDatabase =
            Room.databaseBuilder(app, TransactionDatabase::class.java, "transaction_database")
                    .build()

    @Provides
    @Singleton
    fun provideSharedPreferences( androidApplication: Application) =
            getSharedPrefs(androidApplication)

    @Provides
    @Singleton
    fun provideSharedPreferencesEditor( androidApplication: Application): SharedPreferences.Editor =
            getSharedPrefs(androidApplication).edit()

    @Provides
    @Singleton
    fun providePrefManager(sharedPref: SharedPreferences,
                           sharedPreferencesEditor: SharedPreferences.Editor) = PrefManager(
        sharedPref,
        sharedPreferencesEditor
    )


}
fun getSharedPrefs(androidApplication: Application): SharedPreferences {
    return androidApplication.getSharedPreferences("default", android.content.Context.MODE_PRIVATE)
}