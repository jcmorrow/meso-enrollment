package org.watsi.enrollment.domain.usecases

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.watsi.enrollment.domain.entities.Delta
import org.watsi.enrollment.domain.factories.DeltaFactory
import org.watsi.enrollment.domain.repositories.DeltaRepository
import org.watsi.enrollment.domain.repositories.HouseholdRepository

@RunWith(MockitoJUnitRunner::class)
class SyncHouseholdUseCaseTest {

    @Mock lateinit var householdRepo: HouseholdRepository
    @Mock lateinit var deltaRepo: DeltaRepository
    lateinit var useCase: SyncHouseholdUseCase
    @Mock lateinit var onErrorCallback: (throwable: Throwable) -> Boolean

    val exception = Exception("Oh no there is an error")
    val delta = DeltaFactory.build(action = Delta.Action.ADD,
        modelName = Delta.ModelName.HOUSEHOLD,
        synced = false)
    val deltaList = listOf(delta)
    val unsyncedSingle = Single.just(deltaList)

    @Before
    fun setup() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }

        useCase = SyncHouseholdUseCase(householdRepo, deltaRepo)
        whenever(onErrorCallback.invoke(any())).thenReturn(true)
        whenever(deltaRepo.unsynced(Delta.ModelName.HOUSEHOLD)).thenReturn(unsyncedSingle)
        whenever(deltaRepo.markAsSynced(deltaList)).thenReturn(Completable.complete())
    }

    @Test
    fun execute_success() {
        whenever(householdRepo.sync(deltaList)).thenReturn(Completable.complete())
        useCase.execute(onErrorCallback).test().assertComplete()
        verify(onErrorCallback, never()).invoke(any())
        verify(householdRepo, times(1)).sync(deltaList)
        verify(deltaRepo, times(1)).markAsSynced(deltaList)
    }

    @Test
    fun execute_onError() {
        whenever(householdRepo.sync(deltaList)).thenReturn(Completable.error(exception))
        useCase.execute(onErrorCallback).test().assertComplete()
        verify(onErrorCallback, times(1)).invoke(exception)
        verify(deltaRepo, never()).markAsSynced(any())
    }
}
