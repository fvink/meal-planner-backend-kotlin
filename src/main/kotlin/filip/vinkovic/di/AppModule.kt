package filip.vinkovic.di

import filip.vinkovic.fatsecret.FatSecretHttpClientHolder
import filip.vinkovic.fatsecret.FatSecretService
import filip.vinkovic.fatsecret.FatSecretTokenManager
import org.koin.dsl.module

val appModule = module {
    single { FatSecretHttpClientHolder(get()) }
    single { FatSecretTokenManager() }
    single { FatSecretService(get()) }
}