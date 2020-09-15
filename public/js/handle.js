function openCity(evt, cTabContent) {
    // Tab Control
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    document.getElementById(cTabContent).style.display = "block";
    evt.currentTarget.className += " active";

    let heightContent = document.getElementsByClassName(cTabContent).clientHeight;
    if (heightContent < 100) {
        document.getElementById(cTabContent).style.overflow = 'hidden';
    } else {
        document.getElementById(cTabContent).style.overflowY = 'scroll';
    }


}

// One of the Tab Show as Default
document.getElementById("defaultOpen").click();


let objNote = [];
let objSubtitle = [];

//load subtitles.txt 
function subtitleFile() {
    let content = document.getElementById('note');
    fetch('subtitles.sub')
        .then((res) => {
            return res.text();
        })
        .then((data) => {
            var lines = data.split('\n');
            for (var i = 0; i < lines.length - 1; i++) {
                // split Time
                var dPosition = lines[i].lastIndexOf(":");
                var time = lines[i].substring(0, dPosition);
                // Split Text
                var text = lines[i].substring(lines[i].lastIndexOf(':') + 1);

                var customObj = {
                    te: text,
                    ti: time,
                };
                objSubtitle.push(customObj);
            }
            objSubtitle.sort((a, b) => a.ti.localeCompare(b.ti));
            for (var i = 0; i < objSubtitle.length; i++) {
                content.insertAdjacentHTML('beforeend', `<p id="getid"> <i class="circle"></i> <span>${objSubtitle[i].ti}</span>${objSubtitle[i].te}</p>`);
            }

        })
        .catch((error) => {
            console.log(error);
        })
}

//load annotations.txt 
function annotationsTextFile() {
    let content = document.getElementById('transcript');
    fetch('annotations.txt')
        .then((res) => {
            console.log(res);
            return res.text();
        })
        .then((data) => {
            var lines = data.split('\n');
            for (var i = 0; i < lines.length - 1; i++) {
                // split Time
                var dPosition = lines[i].lastIndexOf(":");
                var time = lines[i].substring(0, dPosition);
                // Split Text
                var text = lines[i].substring(lines[i].lastIndexOf(':') + 1);
                var customObj = {
                    te: text,
                    ti: time,
                };
                objNote.push(customObj);
            }
            objNote.sort((a, b) => a.ti.localeCompare(b.ti));
            for (var i = 0; i < objNote.length; i++) {
                content.insertAdjacentHTML('beforeend', `<p id="getid"> <i class="circle"></i> <span>${objNote[i].ti}</span>${objNote[i].te}</p>`);
            }
        })
        .catch((error) => {
            console.log(error);
        })
}



document.addEventListener("DOMContentLoaded", function () {
    hiddenCircle();
    subtitleFile();
    annotationsTextFile();
});




document.addEventListener('click', function (event) {
    hiddenCircle();
    if (event.target.matches('#getid')) {
        event.preventDefault();
        if (event.target.closest('p')) {
            var currentTime = event.target.querySelector('span').innerText;
        }

        let circle = event.target.querySelector('i');
        circle.style.visibility = "visible";

        var a = currentTime.split(':');
        var seconds = (+a[0]) * 60 * 60 + (+a[1]) * 60 + (+a[2]);

        var player = document.getElementById("player");

        // check before Jumping which you put
        if (seconds > player.duration) {
            alert('invalid Time to Select')
        } else {
            player.currentTime = seconds;
            player.play();
            if (player.duration > 0 && !player.paused) {
                var str = document.getElementById('starter');
                str.classList.add('hide');

                player.addEventListener("timeupdate", function () {
                    const played = player.currentTime;
                    document.querySelector("#currentVideoTime").innerHTML = new Date(played * 1000).toISOString().substr(11, 8);
                    //const duration = player.duration.toFixed(1);
                    // document.querySelector("#currentVideoTime").innerHTML += "Zeit bis Ende " + (duration - played).toFixed(1);
                }, false);
            }
        }
    }

}, false);



function hiddenCircle() {
    let circle = document.querySelectorAll('#getid i');
    circle.forEach(element => {
        element.style.visibility = "hidden";
    });
}




// objSubtitle
var isSub = true;
function subtitleSort() {
    var content1 = document.querySelector('#note');
    content1.innerHTML = '';
    content1.insertAdjacentHTML('afterbegin', `<div class="Order1"> <i class="fas fa-sort" onclick="subtitleSort()"></i> </div>`);

    if (isSub) {
        objSubtitle.sort((a, b) => b.ti.localeCompare(a.ti));
        for (var i = 0; i < objSubtitle.length; i++) {
            content1.insertAdjacentHTML('beforeend', `<p id="getid"> <i class="circle"></i> <span>${objSubtitle[i].ti}</span>${objSubtitle[i].te}</p>`);
        }
        isSub = false;
    }
    else {
        objSubtitle.sort((a, b) => a.ti.localeCompare(b.ti));
        for (var i = 0; i < objSubtitle.length; i++) {
            content1.insertAdjacentHTML('beforeend', `<p id="getid"> <i class="circle"></i> <span>${objSubtitle[i].ti}</span>${objSubtitle[i].te}</p>`);
        }
        isSub = true;
    }
}


// objNote
var isNote = true;
function transcriptSort() {
    var content = document.querySelector('#transcript');
    content.innerHTML = '';
    content.insertAdjacentHTML('afterbegin', `<div class="Order2"> <i class="fas fa-sort" onclick="transcriptSort()"></i> </div>`);
    if (isNote) {
        objNote.sort((a, b) => b.ti.localeCompare(a.ti));
        for (var i = 0; i < objNote.length; i++) {
            content.insertAdjacentHTML('beforeend', `<p id="getid"> <i class="circle"></i> <span>${objNote[i].ti}</span>${objNote[i].te}</p>`);
        }
        isNote = false;
    }
    else {
        objNote.sort((a, b) => a.ti.localeCompare(b.ti));
        for (var i = 0; i < objNote.length; i++) {
            content.insertAdjacentHTML('beforeend', `<p id="getid"> <i class="circle"></i> <span>${objNote[i].ti}</span>${objNote[i].te}</p>`);
        }
        isNote = true;
    }
}






