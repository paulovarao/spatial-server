const geometryTab = document.getElementById('geometry-tab')
const satelliteTab = document.getElementById('satellite-tab')
const selectedTabClass = 'selected-tab'

function updateSelectedTabStyle(tabOption) {
    tabOption.classList.add(selectedTabClass)
    const selectedTab = tabOption.innerHTML

    const tabOptions = document.querySelectorAll('div[tab-menu] button')
    tabOptions.forEach(o => {
        if (o.innerHTML != selectedTab) o.classList.remove(selectedTabClass)
    })
}

function updateDrawAndOperation(show) {
    const draw = document.querySelector('div[draw]')
    const operation = document.querySelector('div[operation]')
    const targets = [draw, operation]
    if (show) targets.forEach(t => t.classList.remove('hidden'))
    else targets.forEach(t => t.classList.add('hidden'))
}

function selectTab(tab, show, callback) {
    updateSelectedTabStyle(tab)
    updateDrawAndOperation(show)
    clearTables()
    operationSelect.value = 'None'
    if (!show) buildSatelliteSection()
}

geometryTab.onclick = () => selectTab(geometryTab, true)
satelliteTab.onclick = () => selectTab(satelliteTab, false)
