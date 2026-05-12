function showNotification(message, type = 'success') {
    const container = document.getElementById('notifications');
    const notif = document.createElement('div');

    notif.className = `notification ${type}`;
    notif.textContent = message;

    container.appendChild(notif);

    setTimeout(() => notif.classList.add('show'), 50);

    setTimeout(() => {
        notif.classList.remove('show');
        setTimeout(() => notif.remove(), 400);
    }, 5000);
}

const svg = document.getElementById("graph")

function addPoint(x, y, color) {
    const circle = document.createElementNS("http://www.w3.org/2000/svg", "circle")
    circle.setAttribute("cx", x)
    circle.setAttribute("cy", -y)
    circle.setAttribute("r", 0.035)
    circle.setAttribute("fill", color)//"#2A2A72"
    svg.appendChild(circle)
}

function getSelectedRadioValue() {
    var radioInputs = document.querySelectorAll('input[name="main_form:select_r"]');
    for (var i = 0; i < radioInputs.length; i++) {
        if (radioInputs[i].checked) {
            return radioInputs[i].value;
        }
    }
    return null;
}

svg.addEventListener("click", e => {
    try {
        
        const pt = svg.createSVGPoint()
        pt.x = e.clientX
        pt.y = e.clientY
        const loc = pt.matrixTransform(svg.getScreenCTM().inverse())

        let r = getSelectedRadioValue()
        if (r !== null && r !== undefined && r !== "" && Number.isFinite(Number(r))) {
            r = Number(r)
            console.log(r)
            const x = loc.x * r
            const y = -loc.y * r
            document.getElementById('main_form:x_value').value = x;
            document.getElementById('main_form:input_y').value = y;
            document.getElementById('main_form:submit-btn').click();
        }
    } catch (error) {
        showNotification(error.message, 'error')
        console.error('Error details:', error)
    }
})

function rotateSvg() {
    document.querySelectorAll('.reverse').forEach(el => {
        el.setAttribute("transform", "scale(-1,-1)")
    });
}

function updateSvg(ref) { //plot table points
    const circles = svg.querySelectorAll('circle');
    circles.forEach(circle => circle.remove());

    document.querySelectorAll('.r').forEach(el => {
        let value = ref;

        if (el.classList.contains('neg')) {
            value = -value;
        }

        if (el.classList.contains('half')) {
            value = value / 2;
        }
        el.textContent = value;
    });

    const table = document.getElementById('result');
    if (!table) return;

    const rows = table.querySelectorAll('tbody tr');

    rows.forEach(row => {
        if (row.cells.length === 1) return;
        const xCell = row.cells[0].textContent;
        const yCell = row.cells[1].textContent;
        const rCell = row.cells[2].textContent;
        const resCell = row.cells[3].textContent;
        const x = parseFloat(xCell);
        const y = parseFloat(yCell);
        const r = parseFloat(rCell);
        const res = resCell == "hit"

        if (!isNaN(x) && !isNaN(y) && !isNaN(r) && r !== 0) {
            let xNormalized = x / ref
            let yNormalized = y / ref
            if (ref < 0) {
                xNormalized *= -1
                yNormalized *= -1
            }
            addPoint(xNormalized, yNormalized, res ? "#2A2A72" : "#722A2A")
        }
    });
}

updateSvg(1)

function onRadioChange(val) {
    updateSvg(val)
}