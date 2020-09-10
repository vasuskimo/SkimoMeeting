
// var links = document.getElementsByClassName('link');
// for(var i = 0; i < links.length; i++ ){
// 	links[i].onclick = function(){
// 		var link = this.getAttribute('link');
// 		window.open(link, '_blank');
// 	}
// }

function link(elem){
	var link = elem.getAttribute('link');
	window.open(link, '_blank');
}



/*
	Add a "Share" link to every slide
*/
// Turns a string into an element
function makeElement(str){
	var el = document.createElement("div");
	el.innerHTML = str;
	return el.children[0];
}



/*
	Update URL every 100ms to match the slide
*/
var intervalID = window.setInterval(updateURL, 100);
var prevURL = "";
function updateURL(){
	var newURL = window.location.pathname + "#/" + Reveal.getIndices().h;
	if (newURL !== prevURL){
		window.location.href = newURL;
		prevURL = newURL;
	} else {
		// No Need to renavigate
	}
}
var subTitleMap;
var noteMap;
function convertSubFileToPairs(filePath) {
	var rawFile = new XMLHttpRequest();
	rawFile.open("GET", filePath, false);
	rawFile.onreadystatechange = function ()
	{
	    if(rawFile.readyState === 4)
	    {
	        if(rawFile.status === 200 || rawFile.status == 0)
	        {
	            var fileText = rawFile.responseText;
	            // split into lines
	            var lines = fileText.split("\n");
	            // Split lines into time,text pairs
	            subTitleMap = lines
	            	// split into an array right after the time
	            	.map(line => line.split(/(?<=\d\d:\d\d:\d\d):/))
	            	// convert the array into an object
	            	.map(pair => {return {"time": pair[0] || "", "text": pair[1] || ""}})
	            	// Make sure the whole thing is properly sorted
	            	.sort((a, b) => convertTimeToSeconds(a.time) - convertTimeToSeconds(b.time))
	        }
	    }

	}
	rawFile.send(null);
	console.log(subTitleMap);
}

function convertAnnotationFileToPairs(filePath) {
	var rawFile = new XMLHttpRequest();
	rawFile.open("GET", filePath, false);
	rawFile.onreadystatechange = function ()
	{
	    if(rawFile.readyState === 4)
	    {
	        if(rawFile.status === 200 || rawFile.status == 0)
	        {
	            var fileText = rawFile.responseText;
	            // split into lines
	            var lines = fileText.split("\n");
	            // Split lines into time,text pairs
	            noteMap = lines
	            	// split into an array right after the time
	            	.map(line => line.split(/(?<=\d\d:\d\d:\d\d):/))
	            	// convert the array into an object
	            	.map(pair => {return {"time": pair[0] || "", "text": pair[1] || ""}})
	            	// Make sure the whole thing is properly sorted
	            	.sort((a, b) => convertTimeToSeconds(a.time) - convertTimeToSeconds(b.time))
	        }
	    }

	}
	rawFile.send(null);
	var html = '';
	for (var i = 0; i < noteMap.length ; i++) {

		 html +=  '<p class="link" style="text-decoration: underline" onclick="link(this)" link="source.mp4#t='+toSecs(noteMap[i].time)+'" >'+noteMap[i].time+' '+noteMap[i].text+'</p>';
	}
	document.getElementById('notes').innerHTML = html;

}

function toSecs(hms){
	var a = hms.split(':'); // split it at the colons
	// minutes are worth 60 seconds. Hours are worth 60 minutes.
	return (+a[0]) * 60 * 60 + (+a[1]) * 60 + (+a[2]);

}

function start()
{
	convertSubFileToPairs("subtitles.sub");
	convertAnnotationFileToPairs("annotations.txt");
}
// window.onload = () => convertSubFileToPairs("subtitles.sub");
window.onload = start;

function getTimeStampsOfQuery(matcher){
	// take the subtitle map
	return subTitleMap
		// filter out any where the text doesn't match
		.filter(pair => pair.text.match(matcher))
		// Then only return the times
		.map(pair => pair.time);
}

function convertTimeToSeconds(time){
	// Time should be hh:mm:ss
	var nums = time
		.split(":")
		.map(str => parseInt(str));

	return nums[0]*3600 + nums[1]*60 + nums[2];
}

function findSlidesFromTimes(times){
	// If only one time was given instead of an array
	if (times.length == undefined){
		// turn it into an array
		times = [times];
	}

	// Convert times to times in seconds
	var timesInSecs = times.map(convertTimeToSeconds);

	// Get all source times
	var sourceTimes =
		// Get the source elements
		Array.from(document.querySelectorAll('source'))
		// get their source strings
		.map(source => source.src)
		// get the time off of those strings
		.map(source => source.match(/(?<=#t=).+$/)[0]);

	// Find the slides that contain this time stamp
	return timesInSecs
		// find the slide
		.map(time => {
			// for each slide
			for (var i = 0; i < sourceTimes.length; i++){
				// if the source time is after the time stamp
				if (sourceTimes[i] > time){
					// return this index, it's the slide number
					return i-1;
				}
			}
			// If we couldn't find the time, then it's the last slide
			return i;
		});
}

function removeDuplicates(arr){
	return  Array.from(new Set(arr));
}

function getSlidesFromSubtitleQuery(matcher){
	// get times
	var times = getTimeStampsOfQuery(matcher);
	// get slides from times
	var slides = findSlidesFromTimes(times);
	// remove duplicates
	return removeDuplicates(slides);
}


	Reveal.initialize({
		transition: 'linear'
	});

	/*
	Search Modal
	*/
	// Constants
	var slideSearchIndex = 0, slideSearchResults = []
	var slidesLength = document.querySelector(".slides").children.length;

	// DOM References
	var banner = document.querySelector("header");
	var searchModal = document.getElementById("searchModal");
	var searchBar = document.getElementById("searchBar");
	var moveSearchBack = document.getElementById("moveSearchBack");
	var moveSearchForward = document.getElementById("moveSearchForward");
	var searchSlideNumber = document.getElementById("searchSlideNumber");

	// Configure the modal position
	searchModal.style.top = banner.offsetHeight + "px";

	// move Right
	moveSearchForward.onclick = SearchForward;

	// move Left
	moveSearchBack.onclick = SearchBack;

	// make the search
	searchBar.oninput = executeSearch;

	function executeSearch(){
		updateSearchResults();
		slideSearchIndex = 0;
		updateSearchSlideNumberText();
		gotoSearchSlide()
	}

	function getSearchQuery(){
		return searchBar.value.trim().toLowerCase();
	}

	function updateSearchResults(){
		query = getSearchQuery();

		if (query === ""){
			slideSearchResults = [];
		} else {
			// Make an array...
			slideSearchResults = Array
			// ...from all of the hidden inputs
			.from(document.querySelectorAll("input[type=hidden]"))
				// Convert elements to objects
				.map((element, index) => {
					return {
						value: element.value,
						index: index
					}
				})
				// Keep objects that contain the query
				.filter(o => {
					return o.value.indexOf(query) != -1;
				})
				// Convert objects to just the indexes
				.map(o => o.index);
		}

		// Search the subtitles as well
		var subSlides = getSlidesFromSubtitleQuery(query);

		var combinedSlides = [...subSlides, ...slideSearchResults];

		slideSearchResults = removeDuplicates(combinedSlides)
	}

	function gotoSearchSlide(){
		Reveal.slide(slideSearchResults[slideSearchIndex]);
	}

	function updateSearchSlideNumberText(){
		searchSlideNumber.innerText = `${slideSearchResults.length > 0 ? slideSearchIndex + 1 : 0}/${slideSearchResults.length}`;
	}

	function SearchBack(){
		searchDiff(-1);
	}

	function SearchForward(){
		searchDiff(1);
	}

	function searchDiff(diff){
		slideSearchIndex += diff;
		// Wrap around
		slideSearchIndex = (slideSearchIndex + slideSearchResults.length) % slideSearchResults.length;
		gotoSearchSlide();
		updateSearchSlideNumberText();
	}
