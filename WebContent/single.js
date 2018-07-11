/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


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

function handleStarResult(resultData) {

    console.log("handleStarResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#single_title");
    let starTitleElement = jQuery("#single_header");
    let starIconElement = jQuery("#font_awesome");

    let titleHTML = "Fabflix - " + resultData[0]["star_name"];
    
    starTitleElement.append(titleHTML);
    
    titleHTML = "<span class=\"icon fa-user\"></span>";
    starIconElement.append(titleHTML);

    if(resultData[0]["star_dob"] == null){
    	 // append two html <p> created to the h3 body, which will refresh the page
        starInfoElement.append("<h3><strong>" + resultData[0]["star_name"] + "</strong><br><sup><b>Birth Year: </b>No Records</sup></h3>");
    }else{
    	 // append two html <p> created to the h3 body, which will refresh the page
        starInfoElement.append("<h3><strong>" + resultData[0]["star_name"] + "</strong><br><sup><b>Birth Year: </b>" + resultData[0]["star_dob"] + "</sup></h3>");
    }

    console.log("handleStarResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "single_content"
    let movieTableBodyElement = jQuery("#single_content");
    let rowHTML = "<section><p><b>List of Movies: </b>";
    		
    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        rowHTML += '<a href="single.html?id=' + resultData[i]["movie_id"] + '"><b>' + resultData[i]["movie_title"] + '</b></a>';
        if(i+1 != resultData.length){
        	rowHTML += ", ";
        }

    }
	rowHTML += "</p></section>";
    // Append the row created to the table body, which will refresh the page
    movieTableBodyElement.append(rowHTML);
}

function handleMovieResult(resultData) {

    console.log("handleMovieResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let movieInfoElement = jQuery("#single_title");
    let movieTitleElement = jQuery("#single_header");
    let movieIconElement = jQuery("#font_awesome");
    
    let titleHTML = "Fabflix - " + resultData[0]["movie_title"];
    
    movieTitleElement.append(titleHTML);
    
    titleHTML = "<span class=\"icon fa-film\"></span>";
    movieIconElement.append(titleHTML);

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<h3><sub><b>ID: </b>" + resultData[0]["movie_id"] + "</sub><br><strong>" + resultData[0]["movie_title"] + "</strong></h3>");

    console.log("handleMovieResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "single_content"
    let movieTableBodyElement = jQuery("#single_content");
    let rowHTML = "";
    rowHTML = "<section><p>";
    		
    // Concatenate the html tags with resultData jsonObject to create table rows
    rowHTML += "<b>Year: </b>" + resultData[0]["movie_year"] + "<br>";
    rowHTML += "<b>Director: </b>" + resultData[0]["movie_director"] + "<br>";
    rowHTML += "<b>List of Genres: </b>" + resultData[0]["movie_genres"] + "<br>";
    rowHTML += "<b>List of Stars: </b>" + resultData[0]["movie_stars"] + "<br>";
    rowHTML += "<b>Rating: </b>" + resultData[0]["movie_rating"];

	rowHTML += '</p><footer><ul class="buttons"><li><a href="cart.html?movie_id=' + resultData[0]["movie_id"] + '&quantity=1"' 
			+ 'class="button small">Add to Cart</a></li></ul></footer></section>';
    // Append the row created to the table body, which will refresh the page
    movieTableBodyElement.append(rowHTML);
}

function handleStarsMovies(resultData, id){
	if(id.search("nm") == 0){
		handleStarResult(resultData);	
	}else if(id.search("tt") == 0){
		handleMovieResult(resultData);
	}
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let singleId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single?id=" + singleId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarsMovies(resultData, singleId) // Setting callback function to handle data returned successfully by the SingleServlet
});