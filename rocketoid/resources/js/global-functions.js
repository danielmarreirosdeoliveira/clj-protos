function post(antiForgeryToken, url, formData, value) {
    formData.append('__anti-forgery-token', antiForgeryToken)
    formData.append('value', typeof value === 'string' ? value : JSON.stringify(value))
    formData.append('state', JSON.stringify(window.state))
    return fetch(url, {
        method: 'POST',
        body: formData
        })
        .then(resp => {
            if (resp.status === 500) {
                location.replace(
                    new URL(window.location.href).origin 
                        + '/?q=' + window.state.currentId
                )
                throw "got 500"
            } else {
                return resp
            }
        })
}

function setups(el, antiForgeryToken) {
    el.addEventListener('keydown', (e) => {
        if (e.code === "Escape" || 
            (e.ctrlKey === true && e.code === "KeyS")) {
            
            e.preventDefault()

            var formData = new FormData();
            formData.append('save?', e.ctrlKey === true && e.code === "KeyS")
            post(antiForgeryToken, '/close-editor', formData, el.value)
                .then((e) => {
                    var appContainer = document.getElementById('app-container')
                    appContainer.focus()
                })
        }
    })
}
