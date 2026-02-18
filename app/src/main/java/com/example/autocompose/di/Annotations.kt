package com.example.autocompose.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PayPalRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PayPalOkHttp