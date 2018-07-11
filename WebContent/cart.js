
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
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleCartResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle cart response");
    console.log(resultDataJson);
    console.log(resultDataJson[0]["status"]);
    
    // If there is movies in cart, then display everything
    if (resultDataJson[0]["status"] === "success") {
        let cartTableBodyElement = jQuery("#cart_info");
        
        let rowHTML = "<table class=\"default\"><thead><th>Movie ID</th><th>Movie Name</th><th>Quantity</th><th></th><th></th></thead>";
        
        for(let i = 0; i < resultDataJson.length; i++){
        	//console.log("wassup " + resultDataJson[i]["movie_id"]);
        	rowHTML += "<tr><th>" + resultDataJson[i]["movie_id"] + "</th><th>" + resultDataJson[i]["movie_title"] + "</th>" +
        			"<th>" + resultDataJson[i]["movie_quantity"] + "</th>" + 
        			"<th><a href=\"cart.html?movie_id=" + resultDataJson[i]["movie_id"] + "&quantity=1\" class=\"button small\">Add One</a></th>" +
        			"<th><a href=\"cart.html?movie_id=" + resultDataJson[i]["movie_id"] + "&quantity=0\" class=\"button small\">Remove</a></th></tr>";			   			
        }
        rowHTML += "</table><ul class=\"buttons\"><li><a href=\"checkout.html\" class=\"button special\">Check Out</a></li></ul>";
        
        cartTableBodyElement.append(rowHTML);
    }
    // If there is NO movies in cart, display error message on <div> with id "submit_error_message"
    else {

        console.log("show error message");
        console.log(resultDataJson[0]["errorMessage"]);
        jQuery("#submit_error_message").text(resultDataJson[0]["errorMessage"]);
    }
}

let movie_id = getParameterByName("movie_id");
let quantity = getParameterByName("quantity");

if(movie_id == null){
	
	//Makes the HTTP POST request and registers on success callback function handleCartResult
	jQuery.ajax({
	    //dataType: "json", // Setting return data type
	    method: "POST", // Setting request method
	    url: "api/cart", // Setting request url, which is mapped by CartServlet in CartServlet.java
	    success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully by the CartServlet
	});
}else{
	//Makes the HTTP POST request and registers on success callback function handleCartResult
	jQuery.ajax({
	    //dataType: "json", // Setting return data type
	    method: "POST", // Setting request method
	    url: "api/cart?movie_id=" + movie_id + "&quantity=" + quantity, // Setting request url, which is mapped by CartServlet in CartServlet.java
	    success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully by the CartServlet
	});
}



