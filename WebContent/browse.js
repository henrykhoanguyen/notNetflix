/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleGenreResult(resultData) {
    console.log("handleGenreResult: populating genre table from resultData");

    // Populate the star table
    // Find the empty table body by id "genre_table_body"
    let genreTableBodyElement = jQuery("#genres_table_body");
    let titleTableBodyElement = jQuery("#title_table_body");
    
    let counter = 0;
    // Concatenate the html tags with resultData jsonObject
    let rowHTML = "";
    // Iterate through resultData
    // Genres List
    for (let i = 0; i < resultData.length; i++) {

        if(counter == 0){
        	rowHTML = "";
            rowHTML += "<tr>";
        }
        rowHTML +=
            "<th>" +
            // Add a link to movieList.html with id passed with GET url parameter
            '<a style="color:white" href="movieList.html?id=' + resultData[i]['genre_id'] + '&perPage=10&pgNum=1&sTitle=Norm&sRating=Norm"><b>'
            + resultData[i]["genre_name"] +     // display genre_name for the link text
            '</b></a>' +
            "</th>";
        if((counter == 2) || ( i+1 == resultData.length)){
            rowHTML += "</tr>";
            // Append the row created to the table body, which will refresh the page
            genreTableBodyElement.append(rowHTML);
            counter = 0;
        }else {
        	counter++;
        }
        
    }
    rowHTML = "";
    counter = 0;
    
    // Title by Number
    for(let i = 48; i < 58; i++){
        if(counter == 0){
        	rowHTML = "";
            rowHTML += "<tr>";
        }
        rowHTML +=
            "<th>" +
            // Add a link to movieList.html with id passed with GET url parameter
            '<a style="color:inherit" href="movieList.html?id=3' + String.fromCharCode(i) + '&perPage=10&pgNum=1&sTitle=Norm&sRating=Norm"><b>'
            + String.fromCharCode(i) +     // display genre_name for the link text
            '</b></a>' +
            "</th>";
        if((counter == 3) || ( i+1 == 58)){
            rowHTML += "</tr>";
            // Append the row created to the table body, which will refresh the page
            titleTableBodyElement.append(rowHTML);
            counter = 0;
        }else {
        	counter++;
        }
    }
    rowHTML = "";
    counter = 0;
    
    //Title by Alphabet
    for(let i = 65; i < 91; i++){
        if(counter == 0){
        	rowHTML = "";
            rowHTML += "<tr>";
        }
        rowHTML +=
            "<th>" +
            // Add a link to movieList.html with id passed with GET url parameter
            '<a style="color:inherit" href="movieList.html?id=' + String.fromCharCode(i) + '&perPage=10&pgNum=1&sTitle=Norm&sRating=Norm"><b>'
            + String.fromCharCode(i) +     // display genre_name for the link text
            '</b></a>' +
            "</th>";
        if((counter == 3) || ( i+1 == 91)){
            rowHTML += "</tr>";
            // Append the row created to the table body, which will refresh the page
            titleTableBodyElement.append(rowHTML);
            counter = 0;
        }else {
        	counter++;
        }
    }
}
/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleGenreResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/browse", // Setting request url, which is mapped by BrowseServlet in browseServlet.java
    success: (resultData) => handleGenreResult(resultData) // Setting callback function to handle data returned successfully by the BrowseServlet
});