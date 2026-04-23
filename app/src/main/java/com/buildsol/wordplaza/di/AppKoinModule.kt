package com.buildsol.wordplaza.di

import kotlinx.serialization.json.Json
import org.koin.dsl.module


val appKoinModule = module {

    single<Json> {
        Json {
            encodeDefaults = false
            ignoreUnknownKeys = true
        }
    }






}
