let currentIndex = 0;
let container = $('#container');
let dialogs = $('#dialogs');
let screenshots = data;
let currentScreenshot = screenshots[currentIndex];
let isSidebarOpen = false;

function render() {
    currentScreenshot = screenshots[currentIndex];
    container.html("<img src='img/" + currentScreenshot.id + ".png'  alt=''/>");
    renderDialogs();
    showDialog(1);
}

function renderNext() {
    setNextCurrentIndex(1);
    render();
}

function renderPrevious() {
    setNextCurrentIndex(-1);
    render();
}

function jumpTo(index) {
    currentIndex = index;
    render();
}

function setNextCurrentIndex(val) {
    currentIndex = currentIndex + val;
    if (currentIndex > screenshots.length - 1) {
        currentIndex = 0;
    }
    if (currentIndex < 0) {
        currentIndex = screenshots.length - 1;
    }
}

function renderDialogs() {
    let html = "";
    $(currentScreenshot.dataColumns).each(function (index, value) {
        index = index + 1;
        let json = syntaxHighlight(value);
        html += "<div id=\"" + index + "\" class=\"dialog\" style=\"min-width:400px; min-height:200px;\">\n" +
            "    <div class=\"titlebar\">" +
            "       <a href='" + currentScreenshot.url + "' target='_blank'>" + currentScreenshot.url + "</a>" +
            "    </div>\n" +
            "    <button name=\"close\"><!-- enter symbol here like &times; or &#x1f6c8; or use the default X if empty --></button>\n" +
            "    <div class=\"content\"><pre>" + json + "</pre></div>\n" +
            "</div>\n";
    });
    dialogs.html(html);
}

function syntaxHighlight(json) {
    if (typeof json != 'string') {
        json = JSON.stringify(json, undefined, 2);
    }
    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
        let cls = 'number';
        if (/^"/.test(match)) {
            if (/:$/.test(match)) {
                cls = 'key';
            } else {
                cls = 'string';
            }
        } else if (/true|false/.test(match)) {
            cls = 'boolean';
        } else if (/null/.test(match)) {
            cls = 'null';
        }
        return '<span class="' + cls + '">' + match + '</span>';
    });
}

function showDialog(id) {
    if (currentScreenshot.dataColumns.length < id) {
        console.log("No data available in column: " + id + " - for " + currentScreenshot.id)
        return;
    }
    let dialog = new DialogBox(id, callbackDialog);
    dialog.showDialog();

    function callbackDialog(btnName) {
        // not used. What does it do anyway??
    }
}

function renderSidebarContent() {
    var html = "";
    $(screenshots).each(function (index, entry) {
        html += "<a onclick='jumpTo(\"" + index + "\")'>" + entry.id + "</a>";
    });
    $('#sidebar-entries').html(html);
}

function openNav() {
    document.getElementById("panel").style.width = "300px";
}

function closeNav() {
    document.getElementById("panel").style.width = "0";
}

/***********************************************************************/
// ON STARTUP
/***********************************************************************/

document.onkeyup = function (e) {
    if (e.code === "Digit1") {
        showDialog(1);
    }
    if (e.code === "Digit2") {
        showDialog(2);
    }
    if (e.code === "Digit3") {
        showDialog(3);
    }
    if (e.code === "Digit4") {
        showDialog(4);
    }
    if (e.code === "Digit5") {
        showDialog(5);
    }
    if (e.code === "ArrowRight") {
        renderNext();
    }
    if (e.code === "ArrowLeft") {
        renderPrevious();
    }
    if (e.code === "Backquote") {
        isSidebarOpen ? closeNav() : openNav();
        isSidebarOpen = !isSidebarOpen;
    }
}

render();
renderDialogs();
renderSidebarContent();
