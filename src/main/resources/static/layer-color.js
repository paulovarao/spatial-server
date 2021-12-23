
function hexToInt(hex) {
    const rgb = [0, 1, 2].map(i => parseInt(hex.substring(2 * i, 2 * (i + 1)), 16))
    return rgb
}

function randomColor() {

    function intToHex(c) {
        const hex = c.toString(16);
        return hex.length == 1 ? "0" + hex : hex;
    }

    const rgb = [0, 1, 2].map(i => Math.floor(Math.random() * 256))
    return rgb.reduce((hex, c) => hex + intToHex(c), '')
}