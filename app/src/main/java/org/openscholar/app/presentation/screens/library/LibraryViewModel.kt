package org.openscholar.app.presentation.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.openscholar.app.domain.repository.DocumentRepository
import org.openscholar.app.model.Document
import org.openscholar.app.model.FileType
import javax.inject.Inject

data class LibraryUiState(
    val documents: List<Document> = emptyList(),
    val selectedFilter: FileType? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val documentRepository: DocumentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadDocuments()
    }

    private fun loadDocuments() {
        viewModelScope.launch {
            documentRepository.getAllDocuments().collect { documents ->
                _uiState.value = _uiState.value.copy(
                    documents = applyFilters(documents),
                    isLoading = false
                )
            }
        }
    }

    fun setFilter(fileType: FileType?) {
        _uiState.value = _uiState.value.copy(selectedFilter = fileType)
        refreshFilter()
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        refreshFilter()
    }

    private fun refreshFilter() {
        viewModelScope.launch {
            val query = _uiState.value.searchQuery
            if (query.isBlank()) {
                documentRepository.getAllDocuments().collect { docs ->
                    _uiState.value = _uiState.value.copy(
                        documents = applyFilters(docs)
                    )
                }
            } else {
                documentRepository.searchDocuments(query).collect { docs ->
                    _uiState.value = _uiState.value.copy(
                        documents = applyFilters(docs)
                    )
                }
            }
        }
    }

    private fun applyFilters(documents: List<Document>): List<Document> {
        var result = documents
        _uiState.value.selectedFilter?.let { filter ->
            result = result.filter { it.fileType == filter }
        }
        return result
    }

    fun deleteDocument(id: String) {
        viewModelScope.launch {
            documentRepository.deleteDocument(id)
        }
    }
}
