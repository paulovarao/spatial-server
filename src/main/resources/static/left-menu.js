const toggleMenuButton = document.querySelector('aside > button')
const toggleMenu = document.querySelector('aside')

function toggle() {
    const current = toggleMenu.classList[0]
    toggleMenu.classList.remove(current)
    const isHidden = current == 'aside-hidden'
    toggleMenuButton.innerHTML = isHidden ? '<' : '>'
    toggleMenu.classList.add(isHidden ? 'aside-shown' : 'aside-hidden')
}

toggleMenuButton.onclick = toggle