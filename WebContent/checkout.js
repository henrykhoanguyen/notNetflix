/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleCheckoutResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson[0]["status"]);
    
    let purchase_message = jQuery("#purchase_message");
    purchase_message.empty();
    let rowHTML = "";

    // If login success, redirect to index.html page
    if (resultDataJson[0]["status"] === "success") {
    	rowHTML += '<section class="wrapper style2 container special"><header class="major">'+
		'<h2><strong>Thank You For Your Purchase!</strong></h2>' +
		'<h3><strong>Your Purchase Information</strong></h3></header>'+
		'<table class="default"><thead><th>Sale ID</th>'+
		'<th>Movie Name</th><th>Quantity</th></thead>';
    	
    	for (let i = 0; i < resultDataJson.length; i++){
            rowHTML += '<tr><th>' + resultDataJson[i]["sale_id"] + '</th><th>' + resultDataJson[i]["movie_title"] + '</th><th>' + resultDataJson[i]["quantity"] + '</th></tr>';
    	}
    	
    	rowHTML += '</table></section>';
    	
        purchase_message.append(rowHTML);
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {
        console.log("show error message");
        console.log(resultDataJson[0]["message"]);
        rowHTML = "<section class=\"wrapper style1 container special\">" + 
						"<header class=\"major\" style=\"color:red\"><h2><strong>Error Message!</strong></h2>" + 
						"<h3><strong>" + resultDataJson[0]["message"] + "</strong></h3></header></section>";
        purchase_message.append(rowHTML);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitCheckoutForm(formSubmitEvent) {
    console.log("submit checkout form");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/checkout",
        // Serialize the login form to the data sent by POST request
        jQuery("#checkout_form").serialize(),
        (resultDataString) => handleCheckoutResult(resultDataString));

}

// Bind the submit action of the form to a handler function
jQuery("#checkout_form").submit((event) => submitCheckoutForm(event));