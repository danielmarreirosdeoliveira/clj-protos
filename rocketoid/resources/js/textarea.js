var textarea = document.getElementsByTagName('textarea')[0]
textarea.focus()
window.editor.create(textarea)

window.setups(textarea, 'ANTI_FORGERY_TOKEN')
