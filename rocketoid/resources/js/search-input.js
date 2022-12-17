var element = document.getElementById('active-search')

element.focus()

element.addEventListener('input',  function(e) {
    var formData = new FormData()
    post('ANTI_FORGERY_TOKEN', '/active-search', formData, element.value)
})
