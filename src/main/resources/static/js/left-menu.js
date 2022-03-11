const toggleMenuButton = document.querySelector('aside > button')
const toggleMenu = document.querySelector('aside')
const tabOptions = document.querySelectorAll('div[tab-menu] button')

function toggle() {
    const current = toggleMenu.classList[0]
    toggleMenu.classList.remove(current)
    const isHidden = current == 'aside-hidden'
    toggleMenuButton.innerHTML = isHidden ? '<' : '>'
    toggleMenu.classList.add(isHidden ? 'aside-shown' : 'aside-hidden')
}

function selectTabContent(tabOption) {
    const selectedTabClass = 'selected-tab'
    tabOption.classList.add(selectedTabClass)
    const selectedTab = tabOption.innerHTML
    
    tabOptions.forEach(o => {
        if (o.innerHTML != selectedTab) o.classList.remove(selectedTabClass)
    })
    
    const tabContents = document.querySelectorAll('div[tab-content] > div')
    const hiddenContentClass = 'hidden'
    const contentId = selectedTab.toLowerCase()
    
    tabContents.forEach(c => {
        c.id == contentId ? c.classList.remove(hiddenContentClass) : c.classList.add(hiddenContentClass)
    })
}

toggleMenuButton.onclick = toggle

tabOptions.forEach(o => {
    o.onclick = () => selectTabContent(o)
})