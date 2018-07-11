

function handleMetaData(resultData){
	console.log(resultData[0]["status"]);
    let metaDataElement = jQuery("#database_metadata");

	if (resultData[0]["status"] === "metadata") {
    	console.log("show dashboard success");
    	 let rowHTML = "";
		 rowHTML += '<section class="wrapper style2 container special">'+
			'<header class="major"><h2><strong>Database Metadata</strong></h2></header>'+
			'<table class="default"><thead><th>Table Name</th><th>Attribute</th><th>Type</th></thead>';
    	 for(let i = 0; i < resultData.length; i++){
    		 
    		 rowHTML += '<tr><th>' + resultData[i]["table_name"] + '</th><th>' + 
    		 			resultData[i]["column_name"] + '</th><th>' + resultData[i]["data_type"] + '</th>';
    		 
    	 }
    	 
    	 rowHTML += '</table></section>';
    	 metaDataElement.append(rowHTML);
	}
}


//Makes the HTTP Post request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/metadata", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMetaData(resultData) // Setting callback function to handle data returned successfully by the SingleServlet
});