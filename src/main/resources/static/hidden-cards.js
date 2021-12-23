const wrapper = document.querySelector('#wrapper')
const hiddenClass = 'hidden'

function showElements(elements) {
    wrapper.style.display = 'flex'
    elements.forEach(element => element.classList.remove(hiddenClass))
}

function hideElements(elements) {
    wrapper.style.display = 'none'
    elements.forEach(element => element.classList.add(hiddenClass))
}

///////////////////////////////       ABOUT         ///////////////////////////////

const about = document.querySelector('#about')
const aboutButton = document.querySelector('#about-bt')
const closeAboutButton = document.querySelector('#about button')

aboutButton.onclick = () => showElements([about])
closeAboutButton.onclick = () => hideElements([about])

///////////////////////////////       ALERT         ///////////////////////////////

const alertCard = document.querySelector('#alert')
const alertMessage = document.querySelector('#alert-message')
const closeAlertButton = document.querySelector('#alert button')

function errorAlert(error) {
    showElements([alertCard])
    alertMessage.innerHTML = !error.response ? 'Connection to server failed' : error
    // alertMessage.innerHTML = error
}

closeAlertButton.onclick = () => hideElements([alertCard])