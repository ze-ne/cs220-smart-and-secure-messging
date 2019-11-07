package com.cs220.ssmessaging.frontendUnitTests
import com.cs220.ssmessaging.frontend.presenters.SearchActivityPresenter
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

// TODO: Implementation moved to iteration 2
class SearchUnitTests {
    private lateinit var presenter : SearchActivityPresenter
    private lateinit var view : SearchActivityPresenter.View

    @Before
    fun setup() {
        presenter = SearchActivityPresenter()
        view = mock()
        presenter.attachView(view)
    }

    @Test
    fun search_withEmptyQuery_callsShowQueryRequiredMessage() {
        presenter.search("")
        verify(view).showQueryRequiredMessage()
    }

    @Test
    fun search_withEmptyQuery_doesNotCallsShowSearchResults() {
        presenter.search("")
        verify(view, never()).showSearchResults(anyString())
    }

    @Test
    fun search_withQueryPresent_callsShowSearchResults() {
        presenter.search("john")
        verify(view).showSearchResults("john")
    }

    @Test
    fun search_withShortQuery_callsShowSearchResults() {
        presenter.search("a")
        verify(view).showSearchResults("a")
    }

    @Test
    fun search_withQueryNotPresent_callsShowSearchResults() {
        presenter.search("bob")
        verify(view).showSearchResults("bob")
    }
}