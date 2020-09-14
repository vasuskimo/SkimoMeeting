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


let noteFile = [];
let subTitleFile = [];

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
                subTitleFile.push(lines[i]);
                // split Time
                var dPosition = lines[i].lastIndexOf(":");
                var time = lines[i].substring(0, dPosition);
                // Split Text
                var text = lines[i].substring(lines[i].lastIndexOf(':') + 1);
                content.insertAdjacentHTML('beforeend', `<p id="getid"> <i class="circle"></i> <span>${time}</span>${text}</p>`);
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
                noteFile.push(lines[i]);
                // split Time
                var dPosition = lines[i].lastIndexOf(":");
                var time = lines[i].substring(0, dPosition);
                // Split Text
                var text = lines[i].substring(lines[i].lastIndexOf(':') + 1);
                content.insertAdjacentHTML('beforeend', `<p id="getid"> <i class="circle"></i> <span>${time}</span>${text}</p>`);
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
        let currentTime = event.target.querySelector('span').innerText;

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






// Sort Note 
function transcriptAscending() {
    let content = document.getElementById('transcript');
    const wrapper = document.querySelector('.Order2');

    let clone = document.querySelector('.Order2').cloneNode(true);
    content.innerHTML = "";
    document.querySelector('#transcript').appendChild(clone);

    wrapper.outerHTML = wrapper.innerHTML;
    let lines = noteFile.reverse();
    for (var i = 0; i < lines.length; i++) {
        // split Time
        var dPosition = lines[i].lastIndexOf(":");
        var time = lines[i].substring(0, dPosition);
        // Split Text
        var text = lines[i].substring(lines[i].lastIndexOf(':') + 1);

        content.insertAdjacentHTML('beforeend', `<p id="getid"> <i class="circle"></i> <span>${time}</span>${text}</p>`);

    }
}


// Sort Note 
function subtitleAscending() {
    let content = document.getElementById('note');
    const wrapper = document.querySelector('.Order1');

    let clone = document.querySelector('.Order1').cloneNode(true);
    content.innerHTML = "";
    document.querySelector('#note').appendChild(clone);

    wrapper.outerHTML = wrapper.innerHTML;
    let lines = subTitleFile.reverse();
    for (var i = 0; i < lines.length; i++) {
        // split Time
        var dPosition = lines[i].lastIndexOf(":");
        var time = lines[i].substring(0, dPosition);
        // Split Text
        var text = lines[i].substring(lines[i].lastIndexOf(':') + 1);

        content.insertAdjacentHTML('beforeend', `<p id="getid"> <i class="circle"></i> <span>${time}</span>${text}</p>`);

    }
}
