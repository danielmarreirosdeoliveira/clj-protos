var appContainer = document.getElementById('app-container')

const scrollBehaviour = {behavior: "smooth", block: "center", inline: "nearest"}

var state = {
    currentId: undefined,
    previousId: undefined,
    searchActive: false
}

function getActiveSearchChildren() {
    return Array.from(document.getElementById('active-search-list').children)
}

function deselectOthers() {
    getActiveSearchChildren().forEach(child => {
        child.classList.remove('selected')
    })
}

function getIndexOfActiveChild() {
    return getActiveSearchChildren()
        .findIndex(child => Array.from(child.classList).includes('selected'))
}

function selectChild(child) {
    if (!child) return
    deselectOthers()
    child.classList.add('selected')
    child.scrollIntoView(scrollBehaviour)
    window.state.currentId = child.id
    return child
}

function deselectChildren() {
    deselectOthers()
    window.state.currentId = false
}

function selectIssueByIndex(newTargetIndex) {
    return selectChild(getActiveSearchChildren()[newTargetIndex])
}

function selectChildAndOpenOnSidebar(id) {
    selectChild(getActiveSearchChildren().find(child => child.id === id))
    return post('ANTI_FORGERY_TOKEN', '/select-child', new FormData(), '')
}

function deselectChildrenAndCloseOnSidebar() {
    deselectChildren()
    return post('ANTI_FORGERY_TOKEN', '/deselect-children', new FormData(), '')
}

function selectChildAndOpenOnSidebarAndCloseSearch(id) {
    window.state.searchActive = false
    return Promise.resolve()
        .then(_ => {
            return post('ANTI_FORGERY_TOKEN', '/close-active-search', new FormData(), '')
        })
        .then(_ => {
            return selectChildAndOpenOnSidebar(id)
        })
}

function closeSearchAndSelectChildAndOpenOnSidebar(id) {
    window.state.searchActive = false
    window.state.currentId = id
    return Promise.resolve()
        .then(_ => {
            return post('ANTI_FORGERY_TOKEN', '/select-child', new FormData(), '')
        })
        .then(_ => {
            return post('ANTI_FORGERY_TOKEN', '/close-active-search', new FormData(), '')
        })
        .then(_ => {
            appContainer.focus()
            selectChild(getActiveSearchChildren().find(child => child.id === id))
        })
}

function deselectChildrenAndCloseOnSidebarAndCloseSearch() {
    window.state.searchActive = false
    return deselectChildrenAndCloseOnSidebar()
        .then(_ => {
            return post('ANTI_FORGERY_TOKEN', '/close-active-search', new FormData(), '')
        })
}

function selectIssueByIdAndCloseActiveSearchIfOpen(id) {
    if (window.state.searchActive) {
        return closeSearchAndSelectChildAndOpenOnSidebar(id)
    } else {
        return selectChildAndOpenOnSidebar(id)
    }
}

function openActiveSearch() {
    window.state.previousId = window.state.currentId
    window.state.currentId = undefined
    deselectOthers()
    window.state.searchActive = true
}

function selectNext() {
    var index = getIndexOfActiveChild()
    if (index + 1 < getActiveSearchChildren().length) {
        const child = selectIssueByIndex(index + 1)
        post('ANTI_FORGERY_TOKEN', '/select-child', new FormData(), child.id)
    }
}

function selectPrevious() {
    var index = getIndexOfActiveChild()
    if (index > 0) {
        const child = selectIssueByIndex(index - 1)
        post('ANTI_FORGERY_TOKEN', '/select-child', new FormData(), child.id)
    }
}

function closeActiveSearchAndRevertSelection() {
    appContainer.focus()
    const res = window.state.previousId
        ? selectChildAndOpenOnSidebarAndCloseSearch(window.state.previousId) 
        : deselectChildrenAndCloseOnSidebarAndCloseSearch()
    return res
}

appContainer.addEventListener('keydown', (e) => {
    var key = {
        code: e.code,
        altKey: e.altKey,
        ctrlKey: e.ctrlKey
    }
    if (e.code === 'ArrowUp' 
        || e.code === 'ArrowDown'
        || e.code === 'Escape'
        || (e.code === 'KeyH' && !window.state.searchActive)
        || e.code === 'Enter') {

        if (e.code === 'ArrowUp') {
            selectPrevious()
        }
        if (e.code === 'ArrowDown') {
            selectNext()
        }
        if (e.code === 'KeyH') {
            post('ANTI_FORGERY_TOKEN', '/open-active-search', new FormData(), '')
                .then(_ => {
                    return openActiveSearch()
                })
        }
        if (e.code === 'Enter') {
            if (window.state.currentId) {
                // for some reason, for the issue to be selected, we need to call this first, not after everything is done
                appContainer.focus()
                selectChildAndOpenOnSidebarAndCloseSearch(window.state.currentId)
            }
            // TODO if selection got stricter and currentId is not in selection anymore, enter should not select it
        }
        if (e.code === 'Escape') {
            if (window.state.searchActive) {
                const previousId = window.state.previousId
                window.state.previousId = undefined
                appContainer.focus()
                if (previousId) {
                    selectChildAndOpenOnSidebarAndCloseSearch(previousId)
                } else {
                    deselectChildrenAndCloseOnSidebarAndCloseSearch()
                }
            } else {
                window.state.currentId = undefined
                window.state.previousId = undefined
                deselectOthers()
                post('ANTI_FORGERY_TOKEN', '/deselect-children', new FormData(), '')
            }
        }
        return;
    }

    post('ANTI_FORGERY_TOKEN', '/keys', new FormData(), key)
})
