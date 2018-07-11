
/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMovieListResult(resultData, startIndex, maxResults) {
    console.log("handleMovieListResult: populating movies table from resultData");
    console.log("hey " + resultData[0]["status"]);
    
    if(resultData[0]["status"] == "success"){
    	
	    // Populate the star table
	    // Find the empty table body by id "movies_table_body"
	    let moviesTableBodyElement = jQuery("#movies_table_body");
	    
	    // Concatenate the html tags with resultData jsonObject
	    let rowHTML = "";
	    // Iterate through resultData    
	    for (let i = startIndex; i < maxResults; i++) {
	
	    	rowHTML = "";
	
	        rowHTML += "<div class=\"4u 12u(narrower)\">" +
	        		"<div class=\"sidebar\"><section>" +
	        		"<header>" +
	        		"<h3><sub>" + resultData[i]["movie_id"] + "</sub><br>" + 
	        		'<a style="color:black" href="single.html?id=' + resultData[i]['movie_id'] + '"><b>'
	                + resultData[i]["movie_title"] +     // display genre_name for the link text
	                '</b></a>' + "</h3>" +
	        		"</header>"+
						"<p><b>Year:</b> " + resultData[i]["movie_year"] + "<br>" +
						"<b>Director:</b> " + resultData[i]["movie_director"] + "<br>" +
						"<b>List of Genres:</b> " + resultData[i]["movie_genres"] + "<br>" +
						"<b>List of Stars:</b> " + resultData[i]["movie_stars"] + "<br>" +
						"<b>Rating:</b> " + resultData[i]["movie_rating"] + "</p>"+
						"<footer><ul class=\"buttons\"><li>" +
						"<a href=\"cart.html?movie_id=" + resultData[i]["movie_id"] + "&quantity=1\" class=\"button small\">Add to Cart</a>" +
						"</li></ul></footer></section></div></div>";
	
	        // Append the row created to the table body, which will refresh the page
	        moviesTableBodyElement.append(rowHTML);
	        
	    }
    }else{
    	// Populate the star table
	    // Find the empty table body by id "movies_table_body"
	    let moviesTableBodyElement = jQuery("#movies_table_body");
	    let preNextElement1 = jQuery("#prev_next1");
	    let preNextElement2 = jQuery("#prev_next2");
        preNextElement1.empty();
        preNextElement2.empty();
	    // Concatenate the html tags with resultData jsonObject
	    let rowHTML = '<header class=\"major\" style=\"color:red\" align=\"center\"><h2><strong>' + resultData[0]["message"] + '</strong></h2></header>';
	    
        // Append the row created to the table body, which will refresh the page
        moviesTableBodyElement.append(rowHTML);
    }
}

function handlePrevNextPage(resultData, movie_list_id, perPage, pgNum, sortTitle, sortRating){
	console.log("handlePrevNextPage: generating pages from resultData and records per page");
    
	// Find the empty tag by id "prev_next"
    let preNextElement1 = jQuery("#prev_next1");
    let preNextElement2 = jQuery("#prev_next2");
    pgNum = Number(pgNum);
    perPage = Number(perPage);
    
	var startIndex = (pgNum * perPage) - perPage;
	var maxResults = startIndex + perPage;
	
	handlePerPageAndSorting(movie_list_id, perPage, pgNum, sortTitle, sortRating);
    handleMovieListResult(resultData, startIndex, maxResults);
    
    // Concatenate the html tags with resultData jsonObject
    let rowHTML = "";
    var totalRecords = resultData.length;
    var numOfPage = Number(totalRecords / perPage);
    numOfPage = Math.ceil(numOfPage);
    
    if(pgNum == 1 && numOfPage > pgNum){
    	pgNum += 1
    	rowHTML += '<li><a href="movieList.html?id=' + movie_list_id + '&perPage=' + perPage + '&pgNum=' + pgNum + '&sTitle=' + sortTitle + '&sRating=' + sortRating + '" class="button small special">Next</a></li>';
    }else if(pgNum > 1 && pgNum < numOfPage){
    	var prev = pgNum - 1;
    	var next = pgNum + 1;
    	rowHTML += '<li><a href="movieList.html?id=' + movie_list_id + '&perPage=' + perPage + '&pgNum=' + prev + '&sTitle=' + sortTitle + '&sRating=' + sortRating + '" class="button small special">Previous</a></li>';
    	rowHTML += '<li><a href="movieList.html?id=' + movie_list_id + '&perPage=' + perPage + '&pgNum=' + next + '&sTitle=' + sortTitle + '&sRating=' + sortRating + '" class="button small special">Next</a></li>';
    }else if (pgNum >= numOfPage){
    	pgNum -=1
    	rowHTML += '<li><a href="movieList.html?id=' + movie_list_id + '&perPage=' + perPage + '&pgNum=' + pgNum + '&sTitle=' + sortTitle + '&sRating=' + sortRating + '" class="button small special">Previous</a></li>';
    }
    
    preNextElement1.append(rowHTML);
    preNextElement2.append(rowHTML);
}

function handlePerPageAndSorting(movie_list_id, perPage, pgNum, sortTitle, sortRating){
	console.log("handlePerPage: generating how many result will be show per page");
	
	let perPageElement = jQuery("#records_per_page");
	let rowHTML = "";
	rowHTML = "<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=10&pgNum=" + pgNum + '&sTitle=' + sortTitle + '&sRating=' + sortRating + "\"><b>10</b></a></li>" +
				"<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=25&pgNum=" + pgNum + '&sTitle=' + sortTitle + '&sRating=' + sortRating + "\"><b>25</b></a></li>" +
				"<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=50&pgNum=" + pgNum + '&sTitle=' + sortTitle + '&sRating=' + sortRating + "\"><b>50</b></a></li>" +
				"<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=100&pgNum=" + pgNum + '&sTitle=' + sortTitle + '&sRating=' + sortRating + "\"><b>100</b></a></li><br>";
	
	// Search Title ASC or DESC
	if(sortTitle.search("Norm") == 0){
		
		rowHTML += "<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=ASC' + '&sRating=Norm' + "\"><b>Title v</b></a></li>" +
		 "<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=DESC' + '&sRating=Norm' + "\"><b>Title ^</b></a></li>";
		
	}else if(sortTitle.search("ASC") == 0){
		
		rowHTML += "<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=Norm&sRating=Norm' + "\"><b>Default</b></a></li>" +
			"<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=DESC' + '&sRating=Norm'+ "\"><b>Title ^</b></a></li>";
				
		
	}else{
		rowHTML += "<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=ASC' + '&sRating=Norm' + "\"><b>Title v</b></a></li>" +
				"<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=Norm&sRating=Norm' + "\"><b>Default</b></a></li>";
	}
	
	// Search Rating ASC or DESC
	if(sortRating.search("Norm") == 0){
		rowHTML += "<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=Norm' + '&sRating=ASC' + "\"><b>Rating v</b></a></li>" +
		 "<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=Norm' + '&sRating=DESC' + "\"><b>Rating ^</b></a></li>";
	}else if(sortRating.search("ASC") == 0){
		rowHTML += "<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=Norm&sRating=Norm' + "\"><b>Default</b></a></li>" +
			"<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=Norm' + '&sRating=DESC' + "\"><b>Rating ^</b></a></li>";
				
	}else{
		rowHTML += "<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=Norm' + '&sRating=ASC' + "\"><b>Rating v</b></a></li>" +
				"<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + "&sTitle=Norm&sRating=Norm\"><b>Default</b></a></li>";
	}

	perPageElement.append(rowHTML);
}

/** From Search **/
function handleSearchPrevNextPage(resultData, movie_list_id, perPage, pgNum, sortTitle, sortRating, title, year, director, starName){
	console.log("handlePrevNextPage: generating pages from resultData and records per page");
    
	// Find the empty tag by id "prev_next"
    let preNextElement1 = jQuery("#prev_next1");
    let preNextElement2 = jQuery("#prev_next2");
    pgNum = Number(pgNum);
    perPage = Number(perPage);
    
	var startIndex = (pgNum * perPage) - perPage;
	var maxResults = startIndex + perPage;
	
	handleSearchPerPageAndSorting(movie_list_id, perPage, pgNum, sortTitle, sortRating, title, year, director, starName);
    handleMovieListResult(resultData, startIndex, maxResults);
    
    // Concatenate the html tags with resultData jsonObject
    let rowHTML = "";
    var totalRecords = resultData.length;
    var numOfPage = Number(totalRecords / perPage);
    numOfPage = Math.ceil(numOfPage);
    
    if(pgNum == 1 && numOfPage > pgNum){
    	
    	pgNum += 1
    	rowHTML += '<li><a href="movieList.html?id=' + movie_list_id + '&perPage=' + perPage + '&pgNum=' + pgNum 
    	+ '&sTitle=' + sortTitle + '&sRating=' + sortRating + "&title=" 
    	+ title + "&year=" + year + "&director=" + director + "&starName=" 
    	+ starName + '" class="button small special">Next</a></li>';
    	
    }else if(pgNum > 1 && pgNum < numOfPage){
    	var prev = pgNum - 1;
    	var next = pgNum + 1;
    	
    	rowHTML += '<li><a href="movieList.html?id=' + movie_list_id + '&perPage=' + perPage + '&pgNum=' + prev 
    	+ '&sTitle=' + sortTitle + '&sRating=' + sortRating + "&title="
    	+ title + "&year=" + year + "&director=" + director + "&starName=" 
    	+ starName + '" class="button small special">Previous</a></li>';
    	
    	rowHTML += '<li><a href="movieList.html?id=' + movie_list_id + '&perPage=' + perPage + '&pgNum=' + next 
    	+ '&sTitle=' + sortTitle + '&sRating=' + sortRating + "&title=" + 
    	title + "&year=" + year + "&director=" + director + "&starName=" + starName + '" class="button small special">Next</a></li>';
    
    }else if (pgNum >= numOfPage){
    	pgNum -=1
    	rowHTML += '<li><a href="movieList.html?id=' + movie_list_id + '&perPage=' + perPage + '&pgNum=' + pgNum 
    	+ '&sTitle=' + sortTitle + '&sRating=' + sortRating + "&title=" + 
    	title + "&year=" + year + "&director=" + director + "&starName=" + starName + '" class="button small special">Previous</a></li>';
    	
    }
    
    preNextElement1.append(rowHTML);
    preNextElement2.append(rowHTML);
	
}

function handleSearchPerPageAndSorting(movie_list_id, perPage, pgNum, sortTitle, sortRating, title, year, director, starName){
	console.log("handlePerPage: generating how many result will be show per page");
	
	let perPageElement = jQuery("#records_per_page");
	let rowHTML = "";
	rowHTML = "<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=10&pgNum=" + pgNum + '&sTitle=' + sortTitle + '&sRating=' + sortRating + "&title=" + 
				title + "&year=" + year + "&director=" + director + "&starName=" + starName + "\"><b>10</b></a></li>" +
				"<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=25&pgNum=" + pgNum + '&sTitle=' + sortTitle + '&sRating=' + sortRating + "&title=" + 
		    	title + "&year=" + year + "&director=" + director + "&starName=" + starName +"\"><b>25</b></a></li>" +
				"<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=50&pgNum=" + pgNum + '&sTitle=' + sortTitle + '&sRating=' + sortRating + "&title=" + 
		    	title + "&year=" + year + "&director=" + director + "&starName=" + starName +"\"><b>50</b></a></li>" +
				"<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=100&pgNum=" + pgNum + '&sTitle=' + sortTitle + '&sRating=' + sortRating + "&title=" + 
		    	title + "&year=" + year + "&director=" + director + "&starName=" + starName +"\"><b>100</b></a></li><br>";
	
	// Search Title ASC or DESC
	if(sortTitle.search("Norm") == 0){
		
		rowHTML += "<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=ASC' + '&sRating=Norm' + "&title=" + 
    				title + "&year=" + year + "&director=" + director + "&starName=" + starName +"\"><b>Title v</b></a></li>" +
    				"<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=DESC' + '&sRating=Norm' + "&title=" + 
    				title + "&year=" + year + "&director=" + director + "&starName=" + starName + "\"><b>Title ^</b></a></li>";
		
	}else if(sortTitle.search("ASC") == 0){
		
		rowHTML += "<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=Norm&sRating=Norm' + "&title=" + 
    				title + "&year=" + year + "&director=" + director + "&starName=" + starName + "\"><b>Default</b></a></li>" +
    				"<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=DESC' + '&sRating=Norm'+ "&title=" + 
    		    	title + "&year=" + year + "&director=" + director + "&starName=" + starName + "\"><b>Title ^</b></a></li>";
				
		
	}else{
		rowHTML += "<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=ASC' + '&sRating=Norm' + "&title=" + 
    				title + "&year=" + year + "&director=" + director + "&starName=" + starName +"\"><b>Title v</b></a></li>" +
    				"<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=Norm&sRating=Norm' + "&title=" + 
    		    	title + "&year=" + year + "&director=" + director + "&starName=" + starName + "\"><b>Default</b></a></li>";
	}
	
	// Search Rating ASC or DESC
	if(sortRating.search("Norm") == 0){
		
		rowHTML += "<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=Norm' + '&sRating=ASC' + "&title=" + 
    				title + "&year=" + year + "&director=" + director + "&starName=" + starName + "\"><b>Rating v</b></a></li>" +
		 			"<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=Norm' + '&sRating=DESC' + "&title=" + 
		 	    	title + "&year=" + year + "&director=" + director + "&starName=" + starName + "\"><b>Rating ^</b></a></li>";
		
	}else if(sortRating.search("ASC") == 0){
		rowHTML += "<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=Norm&sRating=Norm' + "&title=" + 
    				title + "&year=" + year + "&director=" + director + "&starName=" + starName + "\"><b>Default</b></a></li>" +
    				"<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=Norm' + '&sRating=DESC' + "&title=" + 
    		    	title + "&year=" + year + "&director=" + director + "&starName=" + starName + "\"><b>Rating ^</b></a></li>";
				
	}else{
		rowHTML += "<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + '&sTitle=Norm' + '&sRating=ASC' + "&title=" + 
    				title + "&year=" + year + "&director=" + director + "&starName=" + starName + "\"><b>Rating v</b></a></li>" +
    				"<li><a href=\"movieList.html?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + "&sTitle=Norm&sRating=Norm" + "&title=" + 
    				title + "&year=" + year + "&director=" + director + "&starName=" + starName + "\"><b>Default</b></a></li>";
	}

	perPageElement.append(rowHTML);
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

//Get parameter from URL
let movie_list_id = getParameterByName('id');
let perPage = getParameterByName('perPage');
let pgNum = getParameterByName('pgNum');
let sortTitle = getParameterByName('sTitle');
let sortRating = getParameterByName('sRating');

//Get parameter from Search
let title = getParameterByName('title');
let year = getParameterByName('year');
let director = getParameterByName('director');
let starName = getParameterByName('starName');

if(movie_list_id.search("search") == -1){
	// Makes the HTTP GET request and registers on success callback function handleMovieListResult
	jQuery.ajax({
	    dataType: "json", // Setting return data type
	    method: "GET", // Setting request method
	    url: "api/movies-list?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + pgNum + "&sTitle=" + sortTitle + "&sRating=" + sortRating, // Setting request url, which is mapped by MovieListServlet in MovieListServlet.java
	    //success: (resultData) => handleMovieListResult(resultData, perPage), // Setting callback function to handle data returned successfully by the MovieListServlet
	    success: (resultData) => handlePrevNextPage(resultData, movie_list_id, perPage, pgNum, sortTitle, sortRating)
	});
	
}else{
	
	// Makes the HTTP GET request and registers on success callback function handleMovieListResult
	jQuery.ajax({
	    dataType: "json", // Setting return data type
	    method: "GET", // Setting request method
	    url: "api/movies-list?id=" + movie_list_id + "&perPage=" + perPage + "&pgNum=" + 
	    	pgNum + "&sTitle=" + sortTitle + "&sRating=" + sortRating + "&title=" + 
	    	title + "&year=" + year + "&director=" + director + "&starName=" + starName, // Setting request url, which is mapped by MovieListServlet in MovieListServlet.java
	    //success: (resultData) => handleMovieListResult(resultData, perPage), // Setting callback function to handle data returned successfully by the MovieListServlet
	    success: (resultData) => handleSearchPrevNextPage(resultData, movie_list_id, perPage, pgNum, sortTitle, sortRating, title, year, director, starName)
	});
}
