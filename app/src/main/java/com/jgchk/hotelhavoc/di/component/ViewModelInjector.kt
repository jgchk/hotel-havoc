package com.jgchk.hotelhavoc.di.component

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface ViewModelInjector {

    @Component.Builder
    interface Builder {

        fun build(): ViewModelInjector
    }
}