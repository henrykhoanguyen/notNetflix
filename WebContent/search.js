/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleSearchResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle search response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login success, redirect to index.html page
    if (resultDataJson["status"] === "success") {
        window.location.replace("movieList.html?id=search&perPage=10&pgNum=1&sTitle=Norm&sRating=Norm&title=" + 
        		resultDataJson["movie_title"] + "&year=" + resultDataJson["movie_year"] + 
        		"&director="+ resultDataJson["movie_director"] + "&starName=" + resultDataJson["movie_star"]);
        //console.log("sup " + resultDataJson["movie_title"]);
    }
    // If search fail, display error message on <div> with id "login_error_message"
    else {

        console.log("show error message");
        console.log(resultDataJson["message"]);
        jQuery("#search_error_message").append("<header class=\"major\" style=\"color:red\" align=\"center\"><h2><strong>" 
        		+ resultDataJson["message"] + "</strong></h2></header></section>");
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitSearchForm(formSubmitEvent) {
    console.log("submit search form");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/search",
        // Serialize the login form to the data sent by POST request
        jQuery("#search_form").serialize(),
        (resultDataString) => handleSearchResult(resultDataString));

}

// Bind the submit action of the form to a handler function
jQuery("#search_form").submit((event) => submitSearchForm(event));

