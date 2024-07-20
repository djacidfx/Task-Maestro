package com.mhss.app.di

import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coroutinesModule = module {
    single(named(namedDefaultDispatcher)) { Dispatchers.Default }
    single(named(namedIoDispatcher)) { Dispatchers.IO }
}