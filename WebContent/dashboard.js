/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataJson) {
    //resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson[0]["status"]);
    
    // If login success, redirect to index.html page
    if (resultDataJson[0]["status"] === "success") {
    	console.log("show dashboard success");
    	
        let rowHTML = "";
        let errorMessageElement = jQuery("#insert_message");
        errorMessageElement.empty();
        
        rowHTML += "<section class=\"wrapper style1 container special\"" +
					"style=\"color:red\"><header class=\"major\"><h2><strong>" + 
					resultDataJson[0]["message"] + "</strong></h2></header></section>";
        
        console.log( "rowHTML "+ rowHTML);
        errorMessageElement.append(rowHTML);
        
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {
        console.log("show error message");
        console.log(resultDataJson[0]["message"]);
        
        let rowHTML = "";
        let errorMessageElement = jQuery("#insert_message");
        errorMessageElement.empty();
        rowHTML += "<section class=\"wrapper style1 container special\"" +
					"style=\"color:red\"><header class=\"major\"><h2><strong>" + 
					resultDataJson[0]["message"] + "</strong></h2></header></section>";
        
        console.log( "rowHTML "+ rowHTML);
        errorMessageElement.append(rowHTML);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit employee login form");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/_dashboard",
        // Serialize the login form to the data sent by POST request
        jQuery("#new_movie_form").serialize(),
        (resultDataJson) => handleLoginResult(resultDataJson));

}

// Bind the submit action of the form to a handler function
jQuery("#new_movie_form").submit((event) => submitLoginForm(event));


