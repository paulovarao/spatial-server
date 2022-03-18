const toggleButton = document.querySelector('aside > button')

function toggle() {
    const toggleMenu = document.querySelector('aside')
    const current = toggleMenu.classList[0]
    toggleMenu.classList.remove(current)
    const isHidden = current == 'aside-hidden'
    toggleButton.innerHTML = isHidden ? '<' : '>'
    toggleMenu.classList.add(isHidden ? 'aside-shown' : 'aside-hidden')
}

toggleButton.onclick = toggle